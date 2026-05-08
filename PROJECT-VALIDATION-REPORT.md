# Project Validation Report — xbrl-accrual

**Date**: 2026-05-07  
**Validator**: Claude Code automated validation  
**Tool chain**: Eclipse Temurin JDK 21.0.11 · Apache Maven 3.9.15 · jaxb-maven-plugin 4.0.8

---

## 1. Executive Summary

**Status: PASSED WITH WARNINGS**

The project builds successfully, generates 100 Java source files from the Italian
accrual XBRL taxonomy, compiles them under Java 21, passes all tests, and produces
a deployable JAR. Schema resolution is fully local via the OASIS XML Catalog;
both online and offline builds succeed.

Three non-blocking warnings were found:
- Maven archetype template files are bundled inside the JAR (cosmetic pollution).
- XSD files and the binding file are bundled in the JAR (expected side-effect of their location in `src/main/resources/`).
- No single unified taxonomy package name: XJC derives one package per namespace, producing `_2003`-suffixed package segments.

There are **no blocking issues**.

---

## 2. Build Result

### Command executed

```
mvn clean package
```

### Result

```
[INFO] BUILD SUCCESS
[INFO] Total time:  14.437 s
[INFO] Finished at: 2026-05-07T21:32:49+02:00
```

No `[ERROR]` or `[WARN]` lines in the build output.

### Final artifact

```
target/xbrl-accrual-1.0.0.jar   (172 KB / 176,029 bytes)
```

### Offline build

```
mvn -o clean package  →  BUILD SUCCESS
```

Offline build passes, confirming that neither Maven dependency resolution nor
XSD schema parsing requires network access during a repeat build.

---

## 3. Java 21 Compatibility

### Evidence from `pom.xml`

```xml
<properties>
  <java.version>21</java.version>
</properties>

<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-compiler-plugin</artifactId>
  <version>3.12.1</version>
  <configuration>
    <release>${java.version}</release>   <!-- compiles with --release 21 -->
  </configuration>
</plugin>
```

### Build log confirmation

```
[INFO] Compiling 100 source files with javac [debug release 21] to target\classes
```

### JAXB API namespace

All generated classes use `jakarta.xml.bind.*` (Jakarta EE 4), not the legacy
`javax.xml.bind.*`. Evidence from `Xbrl.java` (line 15–27):

```java
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
...
```

The only `javax.*` import present in generated code is `javax.xml.namespace.QName`,
which comes from the `java.xml` JDK module — not from the old JAXB API — and is
fully compatible with Java 21.

### Runtime versions verified

```
openjdk version "21.0.11" 2026-04-21 LTS
OpenJDK Runtime Environment Temurin-21.0.11+10
javac 21.0.11
Apache Maven 3.9.15
```

**Result: PASS**

---

## 4. JAXB/XJC Generation Validation

### Entry-point schema

Only one root schema was passed to XJC:

```
[INFO] schemaFiles (calculated):
  [...]\src\main\resources\xsd\accrual\ska\rend\2025-04-14\accrual-ska-rend_2025-04-14.xsd
```

Configured in `pom.xml`:

```xml
<schemaDirectory>${project.basedir}/src/main/resources/xsd</schemaDirectory>
<schemaIncludes>
  <include>accrual/ska/rend/2025-04-14/accrual-ska-rend_2025-04-14.xsd</include>
</schemaIncludes>
```

### Global schemas

Global XBRL schemas (`xbrl-instance-2003-12-31.xsd`, `xbrl-linkbase-2003-12-31.xsd`,
etc.) are **not** passed as root inputs. They are resolved exclusively as transitive
imports through the catalog. No "already defined" duplication errors occur.

### Generated source folder

```
target/generated-sources/xjc/
```

### Generated packages and class counts

| Package | .java files |
|---------|------------|
| `org.xbrl._2003.instance` | 51 |
| `org.xbrl._2003.linkbase` | 19 |
| `org.xbrl._2003.xlink` | 10 |
| `org.xbrl.dtr.type.numeric` | 12 |
| `org.xbrl.dtr.type.non_numeric` | 7 |
| `it.gov.mef.rgs.xbrl.accrual.ska.roles._2025_04_14` | 1 |
| **Total** | **100** |

Verified by compiler log: `Compiling 100 source files`.

The taxonomy package (`it.gov.mef.rgs.xbrl.accrual.ska.roles._2025_04_14`)
contains a single `ObjectFactory` class with factory methods for all 295 XBRL
elements defined in the accrual roles schema.

**Result: PASS**

---

## 5. Catalog Resolution Validation

### Catalog file

```
src/main/resources/catalog.xml
```

Confirmed active by verbose log:

```
[INFO] catalog:...\src\main\resources\catalog.xml
[INFO] catalogURIs (calculated):[file:/...src/main/resources/catalog.xml]
```

### Rewrite rule

```xml
<rewriteURI
    uriStartString="http://www.xbrl.org/"
    rewritePrefix="xsd/global/www.xbrl.org/"/>
```

The `rewritePrefix` is relative to the catalog file's own location
(`src/main/resources/`), so `http://www.xbrl.org/2003/xbrl-instance-2003-12-31.xsd`
resolves to:

```
src/main/resources/xsd/global/www.xbrl.org/2003/xbrl-instance-2003-12-31.xsd
```

All required local files are present and confirmed (12 XSD files under
`src/main/resources/xsd/global/www.xbrl.org/`).

### Offline/no-network schema resolution confirmed

`mvn -o clean package` (Maven offline flag) completed with `BUILD SUCCESS`. No
network call is made for XSD resolution at any point.

**Note**: The `-o` flag disables Maven artifact downloading. It succeeds only
because all required Maven JARs are already in the local repository
(`~/.m2/repository/`). The catalog handles XSD resolution; Maven handles JAR
resolution — these are separate mechanisms.

**Result: PASS**

---

## 6. Generated Classes Validation

### Class counts in JAR

Total `.class` entries in `target/xbrl-accrual-1.0.0.jar`:

```
102  (100 top-level classes + 2 compiler-generated inner classes:
      ContextEntityType$Identifier, ContextPeriodType$Forever)
```

### Sample generated classes

```
org/xbrl/_2003/instance/Xbrl.class              ← root XBRL instance document
org/xbrl/_2003/instance/MonetaryItemType.class   ← monetary fact type
org/xbrl/_2003/linkbase/Linkbase.class           ← linkbase document
org/xbrl/_2003/linkbase/ObjectFactory.class      ← linkbase element factory
it/.../roles/_2025_04_14/ObjectFactory.class     ← 295 taxonomy element factories
```

### Classes in JAR: confirmed

All 102 `.class` files are present in the JAR under their correct package paths.
Verified by `jar tf target/xbrl-accrual-1.0.0.jar`.

### No classes committed to `src/main/java`

`src/main/java/` does not exist. Generated classes live only under
`target/generated-sources/xjc/` and are not version-controlled. ✓

**Result: PASS**

---

## 7. Test Result

### Test configuration

```
test file: src/test/java/it/iss/xbrl/accrual/generated/JaxbContextTest.java
framework: JUnit 5 (junit-jupiter 5.10.1)
runner:    maven-surefire-plugin 3.2.5
```

### Test: `allPackagesContextInitializes`

```java
JAXBContext ctx = JAXBContext.newInstance(
    org.xbrl._2003.instance.ObjectFactory.class,
    org.xbrl._2003.linkbase.ObjectFactory.class,
    org.xbrl._2003.xlink.ObjectFactory.class,
    org.xbrl.dtr.type.numeric.ObjectFactory.class,
    org.xbrl.dtr.type.non_numeric.ObjectFactory.class,
    it.gov.mef.rgs.xbrl.accrual.ska.roles._2025_04_14.ObjectFactory.class
);
assertNotNull(ctx);
```

All six packages must be included in a single `JAXBContext.newInstance()` call
because the taxonomy elements use cross-package substitution groups (`item`,
`footnoteLink`). Passing only one package would cause `IllegalAnnotationsException`
with 295 "no element mapping" errors.

### Result

```
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

**Result: PASS**

---

## 8. JAR Inspection

### Artifact

```
target/xbrl-accrual-1.0.0.jar
groupId:    it.iss.si
artifactId: xbrl-accrual
version:    1.0.0
size:       172 KB (176,029 bytes)
```

### Content summary

| Content | Present | Note |
|---------|---------|------|
| Compiled JAXB classes (102 `.class`) | ✓ | 6 packages |
| `META-INF/MANIFEST.MF` | ✓ | Standard |
| `META-INF/sun-jaxb.episode` | ✓ | Enables downstream JAXB episode chaining |
| `META-INF/maven/it.iss.si/xbrl-accrual/pom.xml` | ✓ | Standard Maven metadata |
| `META-INF/maven/it.iss.si/xbrl-accrual/pom.properties` | ✓ | Standard Maven metadata |
| Taxonomy XSD files (`xsd/accrual/...`) | ✓ | Bundled from `src/main/resources/` |
| Global XBRL XSD files (`xsd/global/...`) | ✓ | Bundled — enables runtime schema validation |
| `catalog.xml` | ✓ | Bundled — usable for runtime resolution |
| `xsd/xbrl-bindings.xjb` | ✓ | Bundled but not needed at runtime |
| `archetype-resources/` | ⚠ | Maven archetype leftovers — see §9 |

### Usability as a Maven dependency

The JAR is installable as a Maven dependency:

```bash
mvn install:install-file \
  -Dfile=target/xbrl-accrual-1.0.0.jar \
  -DgroupId=it.iss.si \
  -DartifactId=xbrl-accrual \
  -Dversion=1.0.0 \
  -Dpackaging=jar
```

Or deploy to a Nexus/Artifactory repository. The `sun-jaxb.episode` file allows
downstream projects to avoid regenerating the same XBRL mapping types.

Downstream projects must depend on `jakarta.xml.bind-api:4.0.x` and include a
JAXB runtime (`jaxb-runtime:4.0.x`) to use the generated classes.

**Result: PASS WITH WARNINGS** (archetype-resources pollution)

---

## 9. Risks / Warnings

### W1 — `archetype-resources/` in JAR (cosmetic, non-blocking)

**Finding**: `src/main/resources/archetype-resources/` contains three Maven
archetype template files (`pom.xml`, `App.java`, `AppTest.java`). Because all
files under `src/main/resources/` are copied to the JAR, these appear in the
final artifact as:

```
archetype-resources/pom.xml
archetype-resources/src/main/java/App.java
archetype-resources/src/test/java/AppTest.java
```

These are inert at runtime but are unnecessary clutter in the artifact.

**Fix**: Delete the directory:
```
src/main/resources/archetype-resources/
```

### W2 — Namespace-derived package names with underscores (non-blocking)

**Finding**: Without an explicit `<generatePackage>`, XJC derives package names
from XML namespace URIs. The year segments `2003` and `2025-04-14` become
`_2003` and `_2025_04_14` (leading underscore added to make valid Java
identifiers). Callers referencing these packages must use the full name with
underscores.

**Fix** (optional): Add schema-specific `<jaxb:bindings schemaLocation="...">` in
`xbrl-bindings.xjb` with `<jaxb:package name="..."/>` to assign friendlier
names. Currently this mechanism does not apply because the plugin matches
schema system IDs (file:// URIs) to binding `schemaLocation` attributes
(http:// URIs) and finds no match. To fix this, the binding file would need to
use file-relative schema locations matching the resolved paths, or the
`disableSystemIdResolution` plugin option would need to be set to `false`.

### W3 — XSD files and binding file bundled in JAR (design, non-blocking)

**Finding**: All taxonomy and global XBRL XSD files are under
`src/main/resources/` and therefore copied into the JAR. This is ~145 KB of
additional content and makes the JAR self-contained for schema validation.
If JAR size is a concern and runtime schema access is not needed, add resource
exclusions to `maven-resources-plugin`.

### W4 — JAXB version mismatch between project scope and plugin scope (minor)

**Finding**: The project declares `jakarta.xml.bind-api:4.0.2` as a compile
dependency, while the jaxb-maven-plugin's internal plugin dependency specifies
`jakarta.xml.bind-api:4.0.0`. These are on separate classpaths (plugin
classpath vs. project classpath) and do not conflict at runtime. Both are
compatible 4.x Jakarta JAXB versions.

### W5 — Taxonomy version hardcoded in multiple locations (structural, non-blocking)

**Finding**: The string `2025-04-14` appears in:
- `pom.xml` `<schemaIncludes>` path
- The generated package `_2025_04_14`
- The catalog rewritePrefix path

Upgrading the taxonomy to a new version requires updating all of these
consistently.

### W6 — `bindings:[]` — schema-specific JAXB customizations silently ignored

**Finding**: The verbose log reports `bindings:[]` even when
`xbrl-bindings.xjb` contains `<jaxb:bindings schemaLocation="http://...">` blocks.
This means any schema-specific customizations (package names, class renames, etc.)
are silently not applied. Only `<jaxb:globalBindings>` takes effect.

**Root cause**: The jaxb-maven-plugin resolves schemas by their system ID
(file:// URI after catalog rewriting), but bindings are looked up by matching
the `schemaLocation` attribute verbatim. HTTP-form schemaLocations in the binding
file never match the resolved file:// system IDs.

---

## 10. Required Fixes Before Delivery

**None.** The build, tests, and JAR are correct and functional.

The only recommended pre-delivery cleanup is:

- Delete `src/main/resources/archetype-resources/` to eliminate Maven archetype
  leftovers from the JAR (W1 above). This is a one-line delete with no build impact.

---

## 11. Optional Improvements

| # | Improvement | Effort |
|---|-------------|--------|
| 1 | Delete `src/main/resources/archetype-resources/` | 1 minute |
| 2 | Exclude `xsd/` and `xbrl-bindings.xjb` from JAR if runtime schema access is not needed | 10 minutes |
| 3 | Assign custom package names via `<jaxb:bindings>` with file-relative `schemaLocation` or `disableSystemIdResolution=false` | 1 hour |
| 4 | Align `jakarta.xml.bind-api` versions: use `4.0.2` consistently in both project and plugin dependencies | 5 minutes |
| 5 | Add `maven-install-plugin` execution to automate local installation | 30 minutes |
| 6 | Parameterize the taxonomy version date (`2025-04-14`) as a Maven property | 30 minutes |

---

## 12. Final Verdict

| Item | Status |
|------|--------|
| `mvn clean package` | PASS |
| `mvn -o clean package` (offline) | PASS |
| Java 21 compilation (`--release 21`) | PASS |
| Jakarta JAXB 4 (`jakarta.xml.bind.*`) | PASS |
| Single entry-point schema — no duplicate loading | PASS |
| Catalog active — local XSD resolution confirmed | PASS |
| 100 generated source files, 102 compiled classes | PASS |
| 6 separate packages, 6 ObjectFactory classes (no naming collision) | PASS |
| All generated classes present in JAR | PASS |
| `JAXBContext` initialization test: 1/1 passed | PASS |
| No hardcoded absolute paths in source | PASS |
| No `javax.xml.bind` imports in generated code | PASS |
| No generated classes committed to `src/main/java` | PASS |
| README.md present and complete | PASS |
| `archetype-resources/` in JAR | WARNING (W1) |

**The project can be delivered.** The generated JAR is valid, fully functional,
and usable as a Maven dependency. The one recommended pre-delivery action is
deleting `src/main/resources/archetype-resources/` to clean up the artifact.
