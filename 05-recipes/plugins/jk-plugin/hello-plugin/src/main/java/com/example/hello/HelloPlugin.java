// SPDX-License-Identifier: Apache-2.0
package com.example.hello;

import cc.jumpkick.plugin.Plugin;
import cc.jumpkick.plugin.PluginManifest;
import cc.jumpkick.plugin.build.BuildPlugin;
import cc.jumpkick.plugin.build.BuildPluginContext;
import cc.jumpkick.plugin.build.BuildPluginHarness;
import cc.jumpkick.plugin.build.In;
import cc.jumpkick.plugin.build.TaskExec;
import cc.jumpkick.plugin.build.TaskSpec;
import cc.jumpkick.plugin.protocol.ProtocolWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

/**
 * The code layer of the {@code [hello]} plugin. jk forks the jar as a worker and the SDK's
 * harness answers the protocol. {@link #register} adds one step: {@code hello-greeting} writes
 * {@code greeting.txt} from {@code [hello] greeting} into a directory jk adds to the consumer's
 * resources, so the consumer's app can read it off its classpath.
 */
public final class HelloPlugin implements Plugin, BuildPlugin {

    @Override
    public PluginManifest manifest() {
        return new PluginManifest("hello", "##HELLO:");
    }

    @Override
    public int run(List<String> args, ProtocolWriter out) throws Exception {
        return BuildPluginHarness.run(this, args, out);
    }

    @Override
    public void register(BuildPluginContext ctx) {
        ctx.task(TaskSpec.named("hello-greeting")
                .inputs(In.config())
                .outputs("res")
                .contributesResources("res")
                .run(HelloPlugin::writeGreeting));
    }

    private static void writeGreeting(TaskExec exec) throws Exception {
        String greeting = exec.config().stringOpt("greeting").orElse("hello");
        Files.writeString(exec.outputDir("res").resolve("greeting.txt"), greeting + "\n", StandardCharsets.UTF_8);
        exec.label("greeting.txt: " + greeting);
    }
}
