// SPDX-License-Identifier: Apache-2.0
package io.netty.jk;

import cc.jumpkick.plugin.buildlogic.BuildLogicAnchor;
import cc.jumpkick.plugin.buildlogic.BuildLogicContributor;
import cc.jumpkick.plugin.buildlogic.BuildLogicGraph;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

/**
 * BEFORE_COMPILE: run Netty's upstream {@code common/src/main/script/codegen.groovy} (gmaven /
 * Mill {@code common.generatedSources}) so collection templates become Java sources before javac.
 *
 * <p>Uses only the build-logic SPI + JDK + downloaded Groovy/Ant jars (same coords as
 * {@code scripts/generate-common.sh}). No setup.sh required for {@code jk build -m common}.
 */
public final class NettyCollectionsCodegen implements BuildLogicContributor {

    private static final String GROOVY_VER = "3.0.9";
    private static final String ANT_VER = "1.10.14";
    private static final String ANT_OPTIONAL_VER = "1.5.3-1";

    @Override
    public void register(BuildLogicGraph g) {
        g.task("netty-collection-codegen", BuildLogicAnchor.BEFORE_COMPILE, ctx -> {
            Path common = ctx.projectDir();
            Path script = common.resolve("src/main/script/codegen.groovy");
            if (!Files.isRegularFile(script)) {
                // Not a full Netty common tree (or codegen already removed) — no-op.
                return;
            }
            Path templates = common.resolve("src/main/templates");
            Path testTemplates = common.resolve("src/test/templates");
            Path outMain = common.resolve("src/main/java");
            Path outTest = common.resolve("src/test/java");
            Files.createDirectories(outMain);
            Files.createDirectories(outTest);

            Path cache = toolCache();
            Files.createDirectories(cache);
            Path groovy = fetch(
                    cache,
                    "org/codehaus/groovy/groovy/" + GROOVY_VER + "/groovy-" + GROOVY_VER + ".jar");
            Path groovyAnt = fetch(
                    cache,
                    "org/codehaus/groovy/groovy-ant/"
                            + GROOVY_VER
                            + "/groovy-ant-"
                            + GROOVY_VER
                            + ".jar");
            Path ant = fetch(cache, "org/apache/ant/ant/" + ANT_VER + "/ant-" + ANT_VER + ".jar");
            Path antLauncher = fetch(
                    cache, "org/apache/ant/ant-launcher/" + ANT_VER + "/ant-launcher-" + ANT_VER + ".jar");
            Path antOptional = fetch(
                    cache, "ant/ant-optional/" + ANT_OPTIONAL_VER + "/ant-optional-" + ANT_OPTIONAL_VER + ".jar");

            URL[] urls = {
                groovy.toUri().toURL(),
                groovyAnt.toUri().toURL(),
                ant.toUri().toURL(),
                antLauncher.toUri().toURL(),
                antOptional.toUri().toURL()
            };
            try (URLClassLoader cl = new URLClassLoader(urls, ClassLoader.getPlatformClassLoader())) {
                Class<?> shellCl = Class.forName("groovy.lang.GroovyShell", true, cl);
                Class<?> antCl = Class.forName("groovy.ant.AntBuilder", true, cl);
                Object shell = shellCl.getConstructor().newInstance();
                Object antBuilder = antCl.getConstructor().newInstance();
                Map<String, Object> props = new HashMap<>();
                props.put("collection.template.dir", templates.toString());
                props.put("collection.template.test.dir", testTemplates.toString());
                props.put("collection.src.dir", outMain.toString());
                props.put("collection.testsrc.dir", outTest.toString());
                Method setProperty = shellCl.getMethod("setProperty", String.class, Object.class);
                setProperty.invoke(shell, "properties", props);
                setProperty.invoke(shell, "ant", antBuilder);
                Method evaluate = shellCl.getMethod("evaluate", java.io.File.class);
                evaluate.invoke(shell, script.toFile());
            }
            Files.writeString(ctx.outDir().resolve("netty-codegen.stamp"), "ok\n");
        });
    }

    private static Path toolCache() {
        String home = System.getProperty("user.home", ".");
        String override = System.getenv("JK_CACHE_DIR");
        if (override != null && !override.isBlank()) {
            return Path.of(override, "tools", "netty-codegen");
        }
        return Path.of(home, ".jk", "tools", "netty-codegen");
    }

    private static Path fetch(Path cache, String mavenPath) throws IOException {
        String fileName = mavenPath.substring(mavenPath.lastIndexOf('/') + 1);
        Path out = cache.resolve(fileName);
        if (Files.isRegularFile(out) && Files.size(out) > 0) return out;
        URI uri = URI.create("https://repo1.maven.org/maven2/" + mavenPath);
        try (InputStream in = uri.toURL().openStream()) {
            Files.copy(in, out, StandardCopyOption.REPLACE_EXISTING);
        }
        return out;
    }
}
