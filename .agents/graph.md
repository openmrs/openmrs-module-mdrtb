# openmrs-module-mdrtb — Module Knowledge Graph
# Java OpenMRS module(Maven). Packaged as modules/mdrtb-x.y.z.omod. Requires OpenMRS ≥2.3.1.
# Shared facts (domain model, concept/encounter-type/identifier UUIDs, topology): ../.agents/graph.md
# Usage + notation legend + maintenance rules: ../.agents/instructions.md
# Last updated: 2026-10-19 (mdrtb/endoftreatment resource, scaffold only)

## § MODULE LAYOUT
```
pom.xml                             # parent Maven build (api + omod)
config.xml                          # OpenMRS module descriptor (id, version, required modules)
api/src/main/java/.../mdrtb/        # Domain logic: MdrtbConstants, MdrtbConcepts, patient/location models, services
omod/src/main/java/                 # Controllers + REST resource classes (ws endpoints under mdrtb/*)
omod/src/main/webapp/               # JSP views: chart, contacts, dashboard, drugforecast, form, program,
                                    #            regimen, reporting, specimen
omod/                               # builds the .omod artifact
commonlab_migrations.sql            # CommonLab schema migration helper
redeploy.bat                        # local Windows redeploy helper
changes.txt / instructions.txt      # historical notes
```

## § MAVEN DEPENDENCIES (compile-time)
```
mdrtb → openmrs-core 2.4.0
mdrtb → address-hierarchy 2.21.0
mdrtb → reporting 2.0.0
mdrtb → html-form-entry 2.1.0
mdrtb → webservices.rest 2.51.0
mdrtb → calculation 2.0.0
mdrtb → metadatamapping 1.7.0
mdrtb → idgen 5.0.4
mdrtb → cohort 3.7.3
mdrtb → legacyui 2.1.0          # aware-of only
```

## § REST RESOURCES EXPOSED (consumed by mdrtb-web at {base}/ws/rest/v1/…)
```
mdrtb/tb03 / mdrtb/tb03/{uuid}
mdrtb/tb03u / mdrtb/tb03u/{uuid}
mdrtb/form89 / mdrtb/form89/{uuid}
mdrtb/transferout / mdrtb/transferout/{uuid}
mdrtb/endoftreatment / mdrtb/endoftreatment/{uuid}   # End of Treatment form; ?q={patientUuid}; POST needs patientProgramUuid
    # SCAFFOLD ONLY (2026-10-19): fields pending -> grep "TODO (EoT" for every place a field must be added
    # chain: EndOfTreatmentForm (api form/custom) -> SimpleEndOfTreatmentForm (dto) -> EndOfTreatmentFormResourceController
    #        legacy EndOfTreatmentFormController (controller/form, /module/mdrtb/form/endOfTreatment.form, NO JSP on purpose)
    # save: MdrtbFormServiceImpl.processEndOfTreatmentForm -> rejects if no location / no program / program of
    #       another patient / no TB03 (DOTS) / no TB03u-MDR (MDR-TB). Does NOT touch program workflow or patient.dead.
    # getters: MdrtbService.getEndOfTreatmentFormsForProgram(patient, ppId), getEndOfTreatmentFormsFilled(locs, period)
    # report: EndOfTreatmentData (api reporting/custom) exists; DTO + mdrtb/endoftreatmentreport resource NOT yet created
    # OPEN: XDR (TB03u - XDR) accepted?  duplicate EoT per program allowed?  outcome sync with TB03/TB03u?
    # TESTS: api EndOfTreatmentDataTest (runs), EndOfTreatmentFormProcessTest (@Ignore), MdrtbServiceTest 2 smoke;
    #        omod EndOfTreatmentFormResourceControllerTest (@Ignore)
mdrtb/drugresistance / mdrtb/drugresistance/{uuid}
mdrtb/adverseevents / mdrtb/adverseevents/{uuid}
mdrtb/regimen / mdrtb/regimen/{uuid}
mdrtb/patientlist
mdrtb/tb03report / mdrtb/tb03ureport / mdrtb/form89report
mdrtb/tb07report / mdrtb/tb07ureport / mdrtb/tb08report / mdrtb/tb08ureport
mdrtb/dataquality
mdrtb/yellowcard?formUuid={AE encounter uuid} | ?year=&quarter=|month=&location=   # Yellow Card (Annex 4), read only
    # YellowCardData (api reporting/pv) -> SimpleYellowCardData (dto) -> YellowCardDataResourceController
    # one card per filled causality drug (0 filled -> 1 card, causalityIndex=0)
    # RULE: only 1:1 recorded obs; checkboxes returned as CODES; no-box answers -> NOT_ON_CARD/OTHER + warnings[]
    # never filled: 6-14, 17, 19-29, 35, 43, 45-47, "reported first time"; SUSPECTED causality and
    # hospitalization (initial vs prolonged) tick nothing. Dates yyyy-MM-dd.
    # TESTS (no DB): api reporting/pv/YellowCardDataTest (68), omod web/dto/SimpleYellowCardDataTest (4).
    #   YellowCardData.Lookups is the seam: tests pass fakes instead of Context/MdrtbService.
```

## § GLOBAL PROPERTIES (mdrtb.* namespace — stored in global_property table)
```
mdrtb.programName
mdrtb.dotsProgramName
mdrtb.primaryPatientIdentifierType
mdrtb.mdrtbPatientIdentifierType
mdrtb.dotsPatientIdentifierType
mdrtb.encounterType.tb03
mdrtb.encounterType.tb03u.mdr
mdrtb.encounterType.tb03u.xdr
mdrtb.encounterType.form89
mdrtb.encounterType.adverseEvent
mdrtb.encounterType.transferIn
mdrtb.encounterType.transferOut
mdrtb.encounterType.specimenCollection
mdrtb.encounterType.labResult
mdrtb.encounterType.resistanceDuringTreatment
mdrtb.encounterType.pvRegimen
mdrtb.encounterType.endOfTreatment   # default "End of Treatment" (MdrtbActivator) -> MdrtbConstants.ET_END_OF_TREATMENT
mdrtb.dst.formId
mdrtb.hain.formId
mdrtb.culture.formId
mdrtb.smear.formId
mdrtb.xpert.formId
labtest.mdrtbTestTypeUuid
```

## § LOCALIZATION
```
api/src/main/resources/messages.properties
api/src/main/resources/messages_{tj,ru,fr,id_ID}.properties
omod/src/main/resources/messages.properties
omod/src/main/resources/messages_{tj,ru,fr,id_ID}.properties
```
