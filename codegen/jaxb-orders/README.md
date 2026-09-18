# codegen/jaxb-orders — XML schemas through xjc

The `[jaxb]` preset runs the JAXB schema compiler, xjc, over `src/main/xsd` in the generate stage
— every `.xsd` in one invocation, `-no-header` so an unchanged schema is a byte-identical output —
and the generated `com.acme.order.Order` and `ObjectFactory` join the compile. xjc is not in
`[dependencies]`: the preset fetches `org.glassfish.jaxb:jaxb-xjc` at its pinned release into the
store and hashes it into the step's key. The two dependencies are what the generated classes need
at run time: the `jakarta.xml.bind` annotations and the JAXB implementation on xjc's line.

```toml
[jaxb]
package = "com.acme.order"     # one package for every class (-p), in place of the namespace's

[dependencies]
jakarta-xml-bind-api = "4.0.5"
jaxb-runtime         = "4.0.9"
```

The step is `generate-jaxb` (`jk explain` shows it). A schema edit re-runs it and the compile; an
unchanged schema is a cache hit, so a second `jk build` is up to date.

```sh
jk build     # generates com.acme.order.{Order,ObjectFactory}, compiles, tests, packages target/jaxb-orders-1.0.0.jar
jk run       # <?xml version="1.0" encoding="UTF-8" standalone="yes"?><order xmlns="http://acme.com/order"><id>A-1</id><quantity>3</quantity></order>
jk test      # an Order marshals with the schema's namespace and unmarshals to the same values
jk guard
```
