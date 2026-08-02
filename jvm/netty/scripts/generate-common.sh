#!/usr/bin/env bash
# Run Netty common/src/main/script/codegen.groovy (collection templates → Java).
# Same script Maven's groovy-maven-plugin and Mill's common.generatedSources invoke.
set -euo pipefail

CHECKOUT="${1:-checkout}"
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
CHECKOUT="$ROOT/$CHECKOUT"
SCRIPT="$CHECKOUT/common/src/main/script/codegen.groovy"
TEMPLATES="$CHECKOUT/common/src/main/templates"
TEST_TEMPLATES="$CHECKOUT/common/src/test/templates"
OUT_MAIN="$CHECKOUT/common/src/main/java"
OUT_TEST="$CHECKOUT/common/src/test/java"

if [ ! -f "$SCRIPT" ]; then
  echo "missing $SCRIPT — run setup.sh first" >&2
  exit 1
fi

CACHE="${JK_CACHE_DIR:-$HOME/.jk}/tools/netty-codegen"
mkdir -p "$CACHE"
GROOVY_VER=3.0.9
ANT_VER=1.5.3-1

fetch() {
  local coord=$1 out=$2
  if [ -f "$out" ]; then return 0; fi
  local g a v
  g=$(echo "$coord" | cut -d: -f1 | tr . /)
  a=$(echo "$coord" | cut -d: -f2)
  v=$(echo "$coord" | cut -d: -f3)
  local url="https://repo1.maven.org/maven2/$g/$a/$v/$a-$v.jar"
  echo "fetch $url"
  curl -fsSL -o "$out" "$url"
}

fetch "org.codehaus.groovy:groovy:$GROOVY_VER" "$CACHE/groovy-$GROOVY_VER.jar"
fetch "org.codehaus.groovy:groovy-ant:$GROOVY_VER" "$CACHE/groovy-ant-$GROOVY_VER.jar"
fetch "ant:ant-optional:$ANT_VER" "$CACHE/ant-optional-$ANT_VER.jar"
# ant core for AntBuilder
fetch "org.apache.ant:ant:1.10.14" "$CACHE/ant-1.10.14.jar"
fetch "org.apache.ant:ant-launcher:1.10.14" "$CACHE/ant-launcher-1.10.14.jar"

CP="$CACHE/groovy-$GROOVY_VER.jar:$CACHE/groovy-ant-$GROOVY_VER.jar:$CACHE/ant-1.10.14.jar:$CACHE/ant-launcher-1.10.14.jar:$CACHE/ant-optional-$ANT_VER.jar"

# Emit a small Java driver that mirrors Mill's GroovyShell invocation (properties map + AntBuilder).
DRIVER="$CACHE/RunNettyCodegen.java"
cat > "$DRIVER" <<'JAVA'
import groovy.lang.GroovyShell;
import groovy.ant.AntBuilder;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class RunNettyCodegen {
  public static void main(String[] args) throws Exception {
    if (args.length != 5) {
      System.err.println("usage: RunNettyCodegen script templates testTemplates outMain outTest");
      System.exit(2);
    }
    File script = new File(args[0]);
    Map<String, Object> props = new HashMap<>();
    props.put("collection.template.dir", args[1]);
    props.put("collection.template.test.dir", args[2]);
    props.put("collection.src.dir", args[3]);
    props.put("collection.testsrc.dir", args[4]);
    GroovyShell shell = new GroovyShell();
    shell.setProperty("properties", props);
    shell.setProperty("ant", new AntBuilder());
    shell.evaluate(script);
    System.out.println("netty common codegen ok → " + args[3]);
  }
}
JAVA

javac -cp "$CP" -d "$CACHE" "$DRIVER"
# Generate into the traditional source roots so jk's layout=traditional picks them up.
mkdir -p "$OUT_MAIN" "$OUT_TEST"
java -cp "$CACHE:$CP" RunNettyCodegen \
  "$SCRIPT" "$TEMPLATES" "$TEST_TEMPLATES" "$OUT_MAIN" "$OUT_TEST"

echo "generated collection sources under common/src/{main,test}/java"
