# openmrs-module-mdrtb

An [OpenMRS](https://openmrs.org) module that extends the EMR with clinical workflows for **Multi-Drug Resistant Tuberculosis (MDR-TB)** and TB DOTS programs: patient enrollment, specimen and laboratory test tracking (smear, culture, DST, GeneXpert, HAIN/HAIN2), drug regimen management, adverse event capture, drug-needs forecasting, WHO/national reporting forms (TB03, TB03u, Form89), and cohort-based reporting.

Originally developed for WHO/national TB programs, this fork (`omrs2-upgrade-tjk-2.8` branch) targets a Tajikistan deployment and tracks upgrades toward newer OpenMRS platform versions.

## Module layout

This is a multi-module Maven project:

| Module | Artifact | Packaging | Purpose |
|---|---|---|---|
| [api/](api/) | `mdrtb-api` | jar | Domain model, services, DAOs, Liquibase changesets, reporting definitions |
| [omod/](omod/) | `mdrtb-omod` | omod | Spring MVC controllers, JSPs/portlets, REST resources, module config/activator |
| [test/](test/) | `test` | jar | REST-assured / Spring Boot integration tests against a running instance |

Build orchestration lives in the root [pom.xml](pom.xml) (artifact `mdrtb`), which inherits from the `maven-parent-openmrs-module` parent POM.

## Requirements & compatibility

- **Java**: 11 (`maven-compiler-plugin` release 11)
- **OpenMRS platform**: default build targets the version set by the `openMRSVersion` property in [pom.xml](pom.xml); a Maven profile builds against a newer platform version for upgrade testing.
- **Required OpenMRS modules** (declared in [omod/src/main/resources/config.xml](omod/src/main/resources/config.xml)): `reporting`, `webservices.rest`, `addresshierarchy`, `labtest` — see config.xml for the exact minimum versions required.
- **Aware of**: `legacyui` (optional, for legacy UI extension points)
- Also depends on (provided scope, see [pom.xml](pom.xml)): `calculation-api`, `htmlwidgets-api/omod`, `reportingcompatibility-api`, `serialization.xstream-api`, plus third-party libs `poi` (Excel export) and `xmlworker` (PDF/HTML rendering).

## Building

```
mvn clean install
```

Produces an `.omod` file under `omod/target/`, installable via the OpenMRS admin module-management UI or by dropping it into the server's `modules` directory.

To build against the newer OpenMRS platform version instead of the default:

```
mvn clean install -P 2.8
```

Code style is enforced by `formatter-maven-plugin` using `OpenMRSFormatter.xml`.

### Building just the omod

The root build compiles `api` first and packages `omod` against it. To build only the `omod` module (and its `api` dependency) without building `test`:

```
mvn clean package
```

The `omod`'s `maven-openmrs-plugin` (`initialize-module`/`package-module` goals, bound in [omod/pom.xml](omod/pom.xml)) generates the module's `config.xml` and assembles the final `.omod` archive under `omod/target/`. Install it by uploading that file through **Administration → Manage Modules → Add or Upgrade Module** in OpenMRS, or by copying it into the server's `modules` directory and restarting.

For faster iteration on JSPs/webapp resources against an already-running, already-unpacked module on a local server, `omod/pom.xml` defines a `deploy-web` profile that copies `src/main/webapp` straight into the deployed module's view directory:

```
mvn -pl omod package -P deploy-web -DdeployPath=<path-to-deployed-module-dir>
```

## Architecture

### Domain model (`api/src/main/java/org/openmrs/module/mdrtb/`)

- **Program enrollment** (`program/`) — `MdrtbPatientProgram` and `TbPatientProgram` wrap OpenMRS `PatientProgram` for MDR-TB and DOTS-TB enrollment, with hospitalization validators.
- **Specimens & lab results** (`specimen/`) — `Specimen` is the root collection encounter; `Smear`, `Culture`, `Dst`, and custom assays `Xpert`, `HAIN`, `HAIN2` (with `*Impl` implementations) model individual test results. `ScannedLabReport` supports attaching scanned paper results.
- **Lab infrastructure** (`lab/`) — generic `LabTest`, `LabTestSample`, `LabTestType`, `LabTestAttribute(Type)`, `LabTestGroup`, and a `LabTestSampleStatus` workflow (`COLLECTED → PROCESSED → REJECTED`), built on top of the external `labtest` module.
- **Regimens & drugs** (`regimen/`, `drugneeds/`) — `Regimen`, `RegimenChange`, `RegimenHistory`, `RegimenType`, `DrugSuggestion`, and `DrugForecastUtil` for inventory/usage forecasting.
- **Clinical forms** (`form/`) — `AbstractSimpleForm`/`SimpleForm` base classes with concrete forms under `form/custom/`: `SmearForm`, `CultureForm`, `DSTForm`, `XpertForm`, `HAINForm`/`HAIN2Form`, `AdverseEventsForm`, `TransferInForm`/`TransferOutForm`, `DrugResistanceDuringTreatmentForm`, and WHO reporting forms `Form89`, `TB03Form`, `TB03uForm`.
- **Config & shared types** — `MdrtbConfig`, `MdrtbConstants`, `MdrtbConcepts` (cached concept lookups), `MdrtbPatient`, geo-hierarchy helpers `Region`/`District`/`Facility`, and `ReportData`/`ReportType` for persisted report metadata.

### Service layer

- **`MdrtbService`** (`api/.../api/MdrtbService.java`) — the primary service facade: patient/program enrollment, specimen and test lifecycle, form retrieval, location/concept lookups. Methods are `@Transactional` and access-controlled by the `Edit DOTS-MDR Data` privilege. Implemented by `MdrtbServiceImpl` (bean `mdrtbService`); form-specific logic lives in `MdrtbFormServiceImpl`.
- **`LabTestService`** (`api/.../api/LabTestService.java`) — CRUD and query operations over `LabTest`/`LabTestSample`/`LabTestType`/`LabTestAttribute(Type)`, including retire/void patterns and protection of the built-in `UNKNOWN` test type.
- **DAOs** — `MdrtbDao` and `LabDao` under `api/.../api/dao/`.

### Persistence

- **Liquibase** (`api/src/main/resources/liquibase.xml`) manages schema changes, including the `report_data` table (location, report name/type/status, period, audit columns, UUID for REST) and the `labtest_type` table family.
- **Hibernate mapping**: `labtestAttribute.hbm.xml`.
- **`MdrtbActivator`** (extends `BaseModuleActivator`) runs on `started()`/`contextRefreshed()` to apply migrations, configure global properties/address templates, and run integrity checks; `shutdown()` handles cleanup.

### Web layer (`omod/`)

- **Controllers** (`omod/.../web/controller/`) — Spring MVC controllers for the patient chart (`ChartController`), drug forecasting (`DrugForecastController`), program enrollment, specimen collection/reporting, and one form controller per clinical form (e.g. `SmearFormController`, `DSTFormController`, `XpertFormController`, `Form89Controller`, `TB03Controller`/`TB03uController`), built on a shared `AbstractFormController`. Portlet controllers back the patient chart/summary/header widgets.
- **JSPs** (`omod/src/main/webapp/`) — dashboard pages, per-form pages under `form/`, lab pages (`labtest.jsp`, `addLabTest*.jsp`, `manageLabTestTypes.jsp`), chart portlets under `chart/`, and contact pages.
- **REST resources** (`omod/.../web/resource/`) — `BaseReportResource` (report CRUD) and `LabIntegrationResource` plus lab-specific resources, built on `webservices.rest`.
- **Extension point** — registers `MdrtbGutterItem` on `org.openmrs.gutter.tools` to surface MDR-TB links in the legacy UI gutter; legacy admin-list/patient-dashboard-tab extensions exist commented out in `config.xml` for reference.
- **Privilege** — single declared privilege `Edit DOTS-MDR Data`, required to access form entry pages/functions.
- **Localization** — `messages*.properties` in `omod/src/main/resources` (declared in `config.xml`: `en`, `fr`, `es`) plus many additional community-contributed translations shipped in the resources directory (e.g. `ru`, `pt`, `de`, `it`, `pl`, `ar`, `fa`, `hi`, `id`, `el`, `hy`, `ku`, `lt`, `si`, `sw`, `te`, `ht`).

### Reporting

Built on the OpenMRS `reporting`/`reportingcompatibility` modules:

- **Cohort definitions** (`api/.../reporting/definition/custom/`) — e.g. `AgeAtMDRRegistrationCohortDefinition`, `DstResultExistsCohortDefinition`, `MdrtbAfterTreatmentStartedCohortDefinition`, `MdrtbPreviousProgramOutcomeCohortDefinition`, and others for patient stratification.
- **National/WHO report data builders** (`api/.../reporting/custom/`) — `TB03Data`/`TB03Util`, `TB03uData`/`TB03uUtil`, `Form89Data`, `DSTReportTJK`, `MOHReportTJK`, plus supporting multi-table structures (`Form8Table1Data`, `Form8Table2Data`, ...).
- **Output**: PDF via `PDFHelper`/`xmlworker`, Excel via Apache POI, validated by `BacteriologyValidator` and aggregated via `SpecimenReportingTools`.

## Testing

- **Unit/integration tests** under `api/src/test/java` and `omod/src/test/java` use the standard OpenMRS test framework (in-memory H2 + Spring test context). `LabTestServiceTest` covers the common-lab-test service surface.
- **`test/` module** runs black-box REST API tests (Spring Boot test starter + REST-assured) against a live server; it explicitly excludes a few slower/flaky tests (`WHOForm05Test`, `MOHReportTest`, `DSTResultCohortDefinitionEvaluatorTest`, `MdrtbTreatmentStartedCohortDefinitionEvaluatorTest`) from the default `omod` build run.

Run all tests:

```
mvn test
```

## License

Licensed under the [OpenMRS Public License 1.0](license.txt) (based on, but distinct from, the Mozilla Public License 1.1).
