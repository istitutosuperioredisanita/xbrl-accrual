# xbrl-accrual

Generates Java JAXB mapping classes from the Italian accrual XBRL taxonomy
(`accrual/ska/rend`, version 2025-04-14) published by RGS — Ragioneria Generale
dello Stato (MEF).

## Requirements

| Tool | Minimum version |
|------|----------------|
| Java | **21** (Eclipse Temurin recommended) |
| Maven | 3.9.x |

The build does **not** require internet access. All XBRL standard schemas are
bundled locally under `src/main/resources/xsd/global/` and resolved offline via
the OASIS XML Catalog.

## How to build

```bash
mvn clean package
```

The build runs in roughly 15 seconds on a standard laptop. It will:

1. Generate Java sources from the taxonomy XSD using XJC (JAXB 4 / Jakarta EE).
2. Compile all 100 generated source files against Java 21.
3. Run the JAXB context validation test.
4. Package the compiled classes into a JAR.

### Offline build

Because all Maven dependencies and XSD schemas are already cached/local,
the build also works fully offline:

```bash
mvn -o clean package
```

## Taxonomy location

| Item | Path |
|------|------|
| Taxonomy entry-point XSD | `src/main/resources/xsd/accrual/ska/rend/2025-04-14/accrual-ska-rend_2025-04-14.xsd` |
| Roles schema | `src/main/resources/xsd/accrual/ska/rend/2025-04-14/accrual-ska-roles_2025-04-14.xsd` |
| Global XBRL schemas | `src/main/resources/xsd/global/www.xbrl.org/` |
| OASIS XML Catalog | `src/main/resources/catalog.xml` |

## Catalog usage

`catalog.xml` rewrites every `http://www.xbrl.org/...` URL to the corresponding
local file under `src/main/resources/xsd/global/www.xbrl.org/`. This prevents
XJC from making network requests during code generation.

```xml
<rewriteURI
    uriStartString="http://www.xbrl.org/"
    rewritePrefix="xsd/global/www.xbrl.org/"/>
```

## Generated packages

XJC generates one Java package per XML namespace. No `<generatePackage>` override
is applied, so package names are derived from the namespace URIs:

| Java package | XML namespace |
|-------------|---------------|
| `org.xbrl._2003.instance` | `http://www.xbrl.org/2003/instance` |
| `org.xbrl._2003.linkbase` | `http://www.xbrl.org/2003/linkbase` |
| `org.xbrl._2003.xlink` | `http://www.xbrl.org/2003/XLink` |
| `org.xbrl.dtr.type.numeric` | `http://www.xbrl.org/dtr/type/numeric` |
| `org.xbrl.dtr.type.non_numeric` | `http://www.xbrl.org/dtr/type/non-numeric` |
| `it.gov.mef.rgs.xbrl.accrual.ska.roles._2025_04_14` | `http://www.rgs.mef.gov.it/xbrl/accrual/ska/roles/2025-04-14` |

Generated sources are written to `target/generated-sources/xjc/` and are NOT
committed to version control.

## Final artifact

```
target/xbrl-accrual-1.0.0.jar
```

The JAR contains all compiled mapping classes and the JAXB episode file
(`META-INF/sun-jaxb.episode`), which allows downstream projects to reference
these classes without regenerating them.

## Using JAXBContext

All six packages must be loaded together because the taxonomy elements use
substitution groups that span package boundaries:

```java
JAXBContext ctx = JAXBContext.newInstance(
    org.xbrl._2003.instance.ObjectFactory.class,
    org.xbrl._2003.linkbase.ObjectFactory.class,
    org.xbrl._2003.xlink.ObjectFactory.class,
    org.xbrl.dtr.type.numeric.ObjectFactory.class,
    org.xbrl.dtr.type.non_numeric.ObjectFactory.class,
    it.gov.mef.rgs.xbrl.accrual.ska.roles._2025_04_14.ObjectFactory.class
);
```

## Known limitations

1. **Package names contain underscores**: `_2003` and `_2025_04_14` segments
   result from namespace-to-Java identifier conversion rules. These are
   syntactically valid Java identifiers but unconventional.

2. **Taxonomy version is hardcoded**: The entry-point XSD path and generated
   package name embed `2025-04-14`. Upgrading the taxonomy requires updating
   `pom.xml` and the source set.

3. **`archetype-resources/` in the JAR** (cosmetic): Three Maven archetype
   template files (`App.java`, `AppTest.java`, `pom.xml`) are present in
   `src/main/resources/archetype-resources/` and are copied into the JAR.
   They have no runtime effect and can be safely removed from the project.

4. **XSD files bundled in the JAR**: The taxonomy XSDs and global XBRL schemas
   are included in the JAR (since they live under `src/main/resources/`). This
   makes the artifact self-contained for validation use cases but increases size.
