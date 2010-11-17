package org.openmrs.module.mdrtb.reporting;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNameTag;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.OpenmrsMetadata;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonName;
import org.openmrs.Program;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicCriteriaImpl;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.datasource.ObsDataSource;
import org.openmrs.logic.op.OperandConcept;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.rule.ReferenceRule;
import org.openmrs.module.mdrtb.MdrtbFactory;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.allergy.MdrtbAllergyStringObj;
import org.openmrs.module.mdrtb.allergy.MdrtbAllergyUtils;
import org.openmrs.module.mdrtb.regimen.Regimen;
import org.openmrs.module.mdrtb.regimen.RegimenHistory;
import org.openmrs.module.mdrtb.regimen.RegimenUtils;
import org.openmrs.module.mdrtb.reporting.excel.SheetHelper;
import org.openmrs.module.mdrtb.reporting.excel.StyleHelper;
import org.openmrs.module.mdrtb.reporting.logic.GetLatestEnrollmentDateRule;
import org.openmrs.module.mdrtb.reporting.logic.ProgramDataSourceMDRTB;
import org.openmrs.module.reporting.common.MessageUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.util.OpenmrsUtil;

public class PatientSummaryUtil {
	
	protected static Log log = LogFactory.getLog(PatientSummaryUtil.class);

    public static final String TYPE_OF_PATIENT = "3289";
    public static final String HIV_STATUS = "3753";
    public static final String WEIGHT_KG = "5089";
    public static final String TREATMENT_START_DATE = "1453";
    public static final String SMEAR_RESULT = "3052";
    public static final String CULTURE_RESULT = "3046";
    public static final String X_RAY_THORAX = "1342";
    public static final String THYROID = "6376";
    public static final String DST_RESULT_PARENT = "3025";  //just to get the date
    public static final String TYPE_OF_ORGANISM = "3024";
    public static final String GUARDIAN_FIRST = "2927";
    public static final String GUARDIAN_LAST = "2928";
    public static final String GUARDIAN_PHONE = "6330";
    public static final String EXTRA_PULMONARY = "1547";
    public static final String PULMONARY = "1549";
    public static final String RETURN_VISIT_DATE = "5096";
    public static final String CLINICAL_PROGRESS = "1463";
    
    private static final String[] toRegister = { 
    	TYPE_OF_PATIENT, HIV_STATUS, WEIGHT_KG,TREATMENT_START_DATE, SMEAR_RESULT,CULTURE_RESULT, X_RAY_THORAX, THYROID, CLINICAL_PROGRESS,
    	DST_RESULT_PARENT, TYPE_OF_ORGANISM, GUARDIAN_FIRST, GUARDIAN_LAST, GUARDIAN_PHONE, PULMONARY, EXTRA_PULMONARY,RETURN_VISIT_DATE
    };
    
    public static final Integer REGIMEN_TYPE = 6345; 
    public static final Integer REGIMEN_TYPE_DATE = 3974;
    public static final Integer STANDARDIZED = 6339;
    public static final Integer EMPIRIC = 6340;
    public static final Integer INDIVIDUALIZED = 6341;
    public static final Integer HOSPITALIZED = 3389;
    public static final Integer AMBULATORY = 1664;
    public static final Integer PREVIOUS_DIAGNOSIS_CONSTRUCT = 991;
    public static final Integer PREVIOUS_DIAGNOSIS = 992;
    
    public static final int DDB_FORM_ID = 13;

    private static Map<String, List<Concept>> concepts = new HashMap<String, List<Concept>>();
    private static Map<String, LogicCriteria> criteria = new LinkedHashMap<String, LogicCriteria>();
    private static Map<String, Boolean> criteriaToTrimByProgramStartDate = new HashMap<String, Boolean>();
    
    public static final String PATIENT_ID = "patientId";
    public static final String FULL_NAME = "fullName";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String GENDER = "gender";
    public static final String AGE = "age";
    public static final String BIRTHDATE = "birthdate";
    public static final String DEAD = "dead";
    public static final String DEATH_DATE = "deathDate";
    public static final String CAUSE_OF_DEATH = "causeOfDeath";
    
    public static final String MDR_TB_IDENTIFIER = "mdrtbId";
    public static final String HIV_IDENTIFIER = "hivId";
    public static final String MDR_STATUS = "mdrStatus";
    
    public static final String TELEPHONE_NUMBER = "telephoneNumber";
    public static final String BIRTHPLACE = "birthplace";
    public static final String CIVIL_STATUS = "civilStatus";
    public static final String HEALTH_CENTER = "healthCenter";
    public static final String HEALTH_DISTRICT = "healthDistrict";
    public static final String MOTHERS_NAME = "mothersName";
    public static final String TREATMENT_SUPPORTER = "treatmentSupporter";	
    
    public static final String CURRENT_REGIMEN = "currentRegimen";
    public static final String CURRENT_REGIMEN_DATE = "currentRegimenDate";
    public static final String EMPIRIC_REGIMEN = "empiricRegimen";
    public static final String EMPIRIC_REGIMEN_DATE = "empiricRegimenDate";
    public static final String INDIVIDUALIZED_REGIMEN = "individualizedRegimen";
    public static final String INDIVIDUALIZED_REGIMEN_DATE = "individualizedRegimenDate";
    public static final String ADDRESS_1 = "address1";
    public static final String CITY_VILLAGE = "cityVillage";
    public static final String FULL_ADDRESS = "fullAddress";
    public static final String ANTECEDENTS = "antecedents";
    public static final String ALLERGIES = "allergies";
    public static final String SMEAR_AT_ADMISSION = "smearAtAdmission";
    public static final String RESISTANCE_LIST = "resistanceList";
    
    private static final String[] NON_LOGIC_KEYS = {
    	PATIENT_ID, MDR_TB_IDENTIFIER, HIV_IDENTIFIER, 
    	FULL_NAME, FIRST_NAME, LAST_NAME, GENDER, AGE, BIRTHDATE, FULL_ADDRESS, ADDRESS_1, CITY_VILLAGE, 
    	TELEPHONE_NUMBER, BIRTHPLACE, CIVIL_STATUS, HEALTH_CENTER, HEALTH_DISTRICT, MOTHERS_NAME, TREATMENT_SUPPORTER,
    	DEAD, DEATH_DATE, CAUSE_OF_DEATH, MDR_STATUS, 
    	CURRENT_REGIMEN, CURRENT_REGIMEN_DATE, EMPIRIC_REGIMEN, EMPIRIC_REGIMEN_DATE, INDIVIDUALIZED_REGIMEN, INDIVIDUALIZED_REGIMEN_DATE,
    	ANTECEDENTS, ALLERGIES, SMEAR_AT_ADMISSION,  RESISTANCE_LIST
    };
    
    private static final String[] LOGIC_KEYS = {
    	"weight", "hospitalized", "ambulatory", "hivStatus",
    	"treatmentStartDate", "initialSmear", "initialCulture", "xRayLast", "smearLast", "cultureLast", "typeOfOrganism",
    	"thyroidLast", "guardianFirst", "guardianLast", "guardianPhone", "returnVisit", "pulmonary", "extraPulmonary", "clinicalProgress"
    };
    
    private static final String[] ATTRIBUTE_KEYS = {
    	TELEPHONE_NUMBER, BIRTHPLACE, CIVIL_STATUS, HEALTH_CENTER, HEALTH_DISTRICT, MOTHERS_NAME, TREATMENT_SUPPORTER
    };
    
    static {
        LogicService ls = Context.getLogicService();
        ConceptService cs = Context.getConceptService();

        concepts.put("smearResult", Arrays.asList(cs.getConcept(SMEAR_RESULT)));
        concepts.put("prevDiagnosisConst", Arrays.asList(cs.getConcept(PREVIOUS_DIAGNOSIS_CONSTRUCT)));
        concepts.put("regimenType", Arrays.asList(cs.getConcept(REGIMEN_TYPE), cs.getConcept(REGIMEN_TYPE_DATE)));
        
        List<Concept> resistanceConcepts = new ArrayList<Concept>();
        String redSt = Context.getAdministrationService().getGlobalProperty("mdrtb.dst_color_coding_red");
        String yellowSt = Context.getAdministrationService().getGlobalProperty("mdrtb.dst_color_coding_yellow");
        resistanceConcepts.add(MdrtbUtil.getMDRTBConceptByName(redSt, new Locale("en", "US")));
        resistanceConcepts.add(MdrtbUtil.getMDRTBConceptByName(yellowSt, new Locale("en", "US")));
        concepts.put("resistanceConcepts", resistanceConcepts);
        
        ObsDataSource ods = (ObsDataSource) Context.getLogicService().getLogicDataSource("obs");

        try {
	        for (String s : toRegister) {
	        	ods.addKey(s);
	        	ls.updateRule(s, new ReferenceRule("obs." + s));  
	        }
	        ls.registerLogicDataSource("pihprogram", new ProgramDataSourceMDRTB());
	        ProgramDataSourceMDRTB progDS = (ProgramDataSourceMDRTB) Context.getLogicService().getLogicDataSource("pihprogram");
	        progDS.addKey("MDR-TB PROGRAM");
	        ls.updateRule("MDR-TB PROGRAM", new GetLatestEnrollmentDateRule());
        }
        catch (LogicException le) {
        	throw new RuntimeException("Error initializing rules", le);
        }
        
        criteria.put("mdrTbProgram", new LogicCriteriaImpl("MDR-TB PROGRAM"));
        criteria.put("weight", new LogicCriteriaImpl(WEIGHT_KG).last());
        criteria.put("hospitalized", new LogicCriteriaImpl(TYPE_OF_PATIENT).equalTo(new OperandConcept(loadConcept(HOSPITALIZED))).last());
        criteria.put("ambulatory", new LogicCriteriaImpl(TYPE_OF_PATIENT).equalTo(new OperandConcept(loadConcept(AMBULATORY))).last());
        criteria.put("hivStatus", new LogicCriteriaImpl(HIV_STATUS).last());
        criteria.put("treatmentStartDate", new LogicCriteriaImpl(TREATMENT_START_DATE).last());
        criteria.put("initialSmear", new LogicCriteriaImpl(SMEAR_RESULT));
        criteria.put("initialCulture", new LogicCriteriaImpl(CULTURE_RESULT));
        criteria.put("xRayLast", new LogicCriteriaImpl(X_RAY_THORAX).last());
        criteria.put("smearLast", new LogicCriteriaImpl(SMEAR_RESULT).last());
        criteria.put("cultureLast", new LogicCriteriaImpl(CULTURE_RESULT).last());
        criteria.put("typeOfOrganism", new LogicCriteriaImpl(TYPE_OF_ORGANISM).last());
        criteria.put("thyroidLast", new LogicCriteriaImpl(THYROID).last());
        criteria.put("dstResultParent", new LogicCriteriaImpl(DST_RESULT_PARENT).last());
        criteria.put("guardianFirst", new LogicCriteriaImpl(GUARDIAN_FIRST).last());
        criteria.put("guardianLast", new LogicCriteriaImpl(GUARDIAN_LAST).last());
        criteria.put("guardianPhone", new LogicCriteriaImpl(GUARDIAN_PHONE).last());
        criteria.put("returnVisit", new LogicCriteriaImpl(RETURN_VISIT_DATE).last());
        criteria.put("pulmonary", new LogicCriteriaImpl(PULMONARY).last());
        criteria.put("extraPulmonary", new LogicCriteriaImpl(EXTRA_PULMONARY).last());
        criteria.put("clinicalProgress", new LogicCriteriaImpl(CLINICAL_PROGRESS).last());
        
        criteriaToTrimByProgramStartDate.put("initialSmear", true);
        criteriaToTrimByProgramStartDate.put("initialCulture", true);
        criteriaToTrimByProgramStartDate.put("pulmonary", true);
        criteriaToTrimByProgramStartDate.put("extraPulmonary", true);
    }
    
    public static final Map<String, String> getAvailableKeys() {
    	Map<String, String> keys = new LinkedHashMap<String, String>();
    	
    	for (String s : NON_LOGIC_KEYS) {
    		keys.put(s, MessageUtil.translate("mdrtb."+s));
    	}
    	
    	for (String s : LOGIC_KEYS) {
    		keys.put(s, MessageUtil.translate("mdrtb."+s));
    		keys.put(s+"Date", MessageUtil.translate("mdrtb."+s) + " (" + MessageUtil.translate("mdrtb.date") + ")");
    	}
    	
    	return keys;
    }
    
    public static Cohort getCohort(Integer patientId) {
    	Cohort c = new Cohort();
    	c.addMember(patientId);
    	return c;
    }
    
    public static Cohort getCohort(Location location) {
        Program mdrtbProgram = MdrtbFactory.getInstance().getMDRTBProgram();
    	Date now = new Date();
    	
        Cohort cohort = Context.getPatientSetService().getPatientsInProgram(mdrtbProgram, now, now);
        if (location != null) {
	        Cohort atLocation = Context.getPatientSetService().getPatientsHavingLocation(location);
	        cohort = Cohort.intersect(cohort, atLocation);
        }
        return cohort;
    }

    public static Map<Integer, Map<String, Object>> getPatientSummaryData(Cohort cohort, List<String> columns, Date effectiveDate) {
    	
    	long ms = System.currentTimeMillis();
    	Map<Integer, Map<String, Object>> byPatient = new HashMap<Integer, Map<String, Object>>();
    	
    	MdrtbFactory mu = MdrtbFactory.getInstance();
    	
    	if (columns == null) {
    		columns = new ArrayList<String>(getAvailableKeys().keySet());
    	}
    	if (effectiveDate == null) {
    		effectiveDate = new Date();
    	}
    	if (cohort == null) {
    		cohort = new Cohort();
    	}
    	
        List<Patient> patients = Context.getPatientSetService().getPatients(cohort.getMemberIds());

        List<Person> persons = new ArrayList<Person>();
        for (Patient p : patients) {
        	persons.add(p);
        }
        
        log.warn("Retrieved Patients in: " + ((System.currentTimeMillis() - ms)/1000) + " seconds");
        ms = System.currentTimeMillis();
        
        try {
        	
            Map<Integer, Result> latestMdrPrograms = Context.getLogicService().eval(cohort, criteria.get("mdrTbProgram"));
            
            for (Map.Entry<String, LogicCriteria> entry : criteria.entrySet()) {
            	String token = entry.getKey();
            	if (columns.contains(token) || columns.contains(token+"Date")) {
            		Map<Integer, Result> results = Context.getLogicService().eval(cohort, entry.getValue());
            		for (Map.Entry<Integer, Result> resultEntry : results.entrySet()) {
            			
            			Integer pId = resultEntry.getKey();
            			Result r = resultEntry.getValue();
            			
	                    Map<String, Object> p = byPatient.get(pId);
	                    if (p == null) {
	                        p = new HashMap<String, Object>();
	                        byPatient.put(pId, p);
	                    }
	                    
	                    // Remove certain results that are > 2 months before program start date
	                    if (criteriaToTrimByProgramStartDate.containsKey(token)) {
	                    	Date sd = latestMdrPrograms.get(pId).getResultDate();
	                    	boolean subtractTwoMonths = criteriaToTrimByProgramStartDate.get(token).booleanValue();
	                        r = getFirstResultAfterProgramStartDate(r, sd, subtractTwoMonths);
	                    }
	                    
	                    p.put(token, r.toString());
	                    if (r != null) {
	                        p.put(token+"Date", r.getResultDate());
	            		}
            		}
                }
            }
            
            log.warn("Retrieved Results from Logic in: " + ((System.currentTimeMillis() - ms)/1000) + " seconds");
            ms = System.currentTimeMillis();
            
            // Add stuff we can't get from logic
            
            Map<Patient, List<MdrtbAllergyStringObj>> allergyMap = new HashMap<Patient, List<MdrtbAllergyStringObj>>();
            if (ObjectUtil.containsAny(columns, ALLERGIES)) {
            	allergyMap = MdrtbAllergyUtils.getPatientAllergies(patients, mu);
            }
            
            Map<Integer, Obs> initialSmearMap = new HashMap<Integer, Obs>();
        	if (ObjectUtil.containsAny(columns, SMEAR_AT_ADMISSION)) {
        		List<Obs> smearResults = Context.getObsService().getObservations(persons, null, concepts.get("smearResult"), null, null, null, null, null, null, null, null, false);
                for (Obs o : smearResults) {
                	Encounter e = o.getEncounter();
                	if (e != null && e.getForm() != null && e.getForm().getFormId().intValue() == DDB_FORM_ID) {
                		Integer pId = o.getPersonId();
                    	Obs curr = initialSmearMap.get(pId);
                    	if (curr == null || e.getEncounterDatetime().after(curr.getEncounter().getEncounterDatetime())) {
                    		initialSmearMap.put(pId, o);
                    	}
                	}
                }
        	}

        	Map<Integer, Map<Integer, List<Obs>>> regObs = new HashMap<Integer, Map<Integer, List<Obs>>>();
        	if (ObjectUtil.containsAny(columns, EMPIRIC_REGIMEN, INDIVIDUALIZED_REGIMEN)) {
                for (Obs o : Context.getObsService().getObservations(persons, null, concepts.get("regimenType"), null, null, null, null, null, null, null, null, false)) {
                	Map<Integer, List<Obs>> m = regObs.get(o.getPersonId());
                	if (m == null) {
                		m = new HashMap<Integer, List<Obs>>();
                		regObs.put(o.getPersonId(), m);
                	}
                	List<Obs> l = m.get(o.getConcept().getConceptId());
                	if (l == null) {
                		l = new ArrayList<Obs>();
                		m.put(o.getConcept().getConceptId(), l);
                	}
                	l.add(o);
                }
        	}

            for (Patient p : patients) {
            	log.debug("Evaluating: " + p.getPatientId());
                Map<String, Object> map = byPatient.get(p.getPatientId());
                if (map == null) {
                    map = new HashMap<String, Object>();
                    byPatient.put(p.getPatientId(), map);
                }
                
                map.put(PATIENT_ID, p.getPatientId());
            	
            	if (ObjectUtil.containsAny(columns, FULL_NAME, FIRST_NAME, LAST_NAME)) {
                    PersonName pn = p.getPersonName();
                    if (pn != null) {
	                    String lastNameUpper = ObjectUtil.nvl(pn.getFamilyName(), "").toUpperCase();
	                    String firstName = ObjectUtil.toString(" ", pn.getGivenName(), pn.getMiddleName());
	                    map.put(FIRST_NAME, firstName);
	                    map.put(LAST_NAME, lastNameUpper);
	                    map.put(FULL_NAME, ObjectUtil.toString(", ", lastNameUpper, firstName));
	            	}
            	}
            	
            	if (ObjectUtil.containsAny(columns, GENDER)) {
            		map.put(GENDER, MessageUtil.translate("mdrtb.gender."+p.getGender()));
            	}
            	
            	if (ObjectUtil.containsAny(columns, BIRTHDATE, AGE)) {
            		map.put(BIRTHDATE, formatDate(p.getBirthdate(), p.getBirthdateEstimated(), "mdrtb.unknown"));
            		map.put(AGE, p.getAge(effectiveDate));
            	}
            	
            	if (ObjectUtil.containsAny(columns, DEAD, DEATH_DATE, CAUSE_OF_DEATH)) {
            		if (p.getDead() && (p.getDeathDate() == null || p.getDeathDate().compareTo(effectiveDate) <= 0)) {
                		map.put(DEAD, MessageUtil.translate("general.true"));
                		map.put(DEATH_DATE, formatDate(p.getDeathDate(), null, "mdrtb.unknown"));
                		map.put(CAUSE_OF_DEATH, formatConcept(p.getCauseOfDeath()));
            		}
            		else {
            			map.put(DEAD, MessageUtil.translate("general.false"));
            		}
            	}
            	
            	if (ObjectUtil.containsAny(columns, FULL_ADDRESS, ADDRESS_1, CITY_VILLAGE)) {
            		PersonAddress pa = p.getPersonAddress();
            		if (pa != null) {
            			map.put(ADDRESS_1, pa.getAddress1());
            			map.put(CITY_VILLAGE, pa.getCityVillage());
            			map.put(FULL_ADDRESS, ObjectUtil.toString(", ", pa.getAddress1(), pa.getCityVillage()));
            		}
            	}
            	
            	if (ObjectUtil.containsAny(columns, MDR_TB_IDENTIFIER, HIV_IDENTIFIER)) {
            		String hivIdName = Context.getAdministrationService().getGlobalProperty("mdrtb.hivEmrIdentifierTypeName");
            		String mdrIdName = Context.getAdministrationService().getGlobalProperty("mdrtb.mdrTbProgramIdentifierTypeName");
            		for (PatientIdentifier pi : p.getIdentifiers()) {
            			if (ObjectUtil.areEqual(pi.getIdentifierType().getName(), mdrIdName)) {
            				map.put(MDR_TB_IDENTIFIER, pi.getIdentifier());
            			}
            			else if (ObjectUtil.areEqual(pi.getIdentifierType().getName(), hivIdName)) {
            				map.put(HIV_IDENTIFIER, pi.getIdentifier());
            			}
            		}
            	}
            	
            	if (ObjectUtil.containsAny(columns, ATTRIBUTE_KEYS)) {
            		for (PersonAttribute pa : p.getActiveAttributes()) {
            			Object o = pa.getHydratedObject();
            			map.put(ObjectUtil.toCamelCase(pa.getAttributeType().getName()), ObjectUtil.nvlStr(o, ""));
            		}
            	}
                
            	if (ObjectUtil.containsAny(columns, MDR_STATUS)) {
            		ProgramWorkflowService pws = Context.getProgramWorkflowService();
	                for (PatientProgram ps : pws.getPatientPrograms(p, mu.getMDRTBProgram(), null, null, null, null, false)) {
	                    PatientState patientState = ps.getCurrentState(mu.getPatientStatusWorkflow());
	                    if (patientState != null) {
	                        map.put(MDR_STATUS, formatConcept(patientState.getState().getConcept()));
	                    }
	                }
            	}
            	
            	RegimenHistory rh = RegimenUtils.getTbRegimenHistory(p);

            	if (ObjectUtil.containsAny(columns, CURRENT_REGIMEN, CURRENT_REGIMEN_DATE)) {
            		map.put(CURRENT_REGIMEN, getRegimenAsString(rh, effectiveDate, null));
            		if (rh != null) {
            			Regimen r = rh.getRegimenOnDate(effectiveDate);
            			if (r != null) {
            				map.put(CURRENT_REGIMEN_DATE, r.getStartDate());
            			}
            		}
            	}
            	
            	Date empiricDate = null;
            	Date personalizedDate = null;
            	
            	if (ObjectUtil.containsAny(columns, EMPIRIC_REGIMEN, EMPIRIC_REGIMEN_DATE, INDIVIDUALIZED_REGIMEN, INDIVIDUALIZED_REGIMEN_DATE)) {
	            	Map<Integer, List<Obs>> patRegObs = regObs.get(p.getPatientId());
	            	if (patRegObs != null && !patRegObs.isEmpty()) {
	            		Map<Date, Date> obsDateToDate = new HashMap<Date, Date>();
	            		List<Obs> typeDateObs = patRegObs.get(REGIMEN_TYPE_DATE);
	            		if (typeDateObs != null) {
	            			for (Obs o : typeDateObs) {
	            				obsDateToDate.put(o.getObsDatetime(), o.getValueDatetime());
	            			}
	            		}
	            		List<Obs> typeObs = patRegObs.get(REGIMEN_TYPE);
	            		if (typeObs != null) {
	            			for (Obs o : typeObs) {
	            				Date d = ObjectUtil.nvl(obsDateToDate.get(o.getObsDatetime()), o.getObsDatetime());
	            				if (o.getValueCoded().getConceptId().equals(INDIVIDUALIZED)) {
	            					if (personalizedDate == null || compareDates(personalizedDate, d, "yyyy-MM-dd") > 0) {
	            						personalizedDate = d;
	            					}
	            				}
	            				else {
	            					if (empiricDate == null || empiricDate.after(d)) {
	            						empiricDate = d;
	            					}
	            				}
	            			}
	            		}
	            	}
	            	map.put(EMPIRIC_REGIMEN_DATE, empiricDate);
	            	map.put(INDIVIDUALIZED_REGIMEN_DATE, personalizedDate);
            	
	            	if (ObjectUtil.containsAny(columns, EMPIRIC_REGIMEN)) {
	            		if (empiricDate != null && empiricDate.compareTo(effectiveDate) <= 0) {
		                    map.put(EMPIRIC_REGIMEN, getRegimenAsString(rh, empiricDate, null));
		                }
	            	}
	            	
	            	if (ObjectUtil.containsAny(columns, INDIVIDUALIZED_REGIMEN)) {
	            		if (personalizedDate != null && personalizedDate.compareTo(effectiveDate) <= 0) {
		                    map.put(INDIVIDUALIZED_REGIMEN, getRegimenAsString(rh, personalizedDate, null));
		                }
	            	}
            	}

            	if (ObjectUtil.containsAny(columns, ANTECEDENTS)) {
            		map.put(ANTECEDENTS, getAntecedentsPersonnelsAsString(concepts.get("prevDiagnosisConst").get(0), p));
            	}
            	
            	if (ObjectUtil.containsAny(columns, ALLERGIES)) {
            		map.put(ALLERGIES, allergyMap.get(p));
            	}
                
            	if (ObjectUtil.containsAny(columns, SMEAR_AT_ADMISSION)) {
            		map.put(SMEAR_AT_ADMISSION, initialSmearMap.get(p.getPatientId()));
            	}
                
            	if (ObjectUtil.containsAny(columns, RESISTANCE_LIST)) {
            		Date programDate = latestMdrPrograms.get(p.getPatientId()).getResultDate();
            		Concept dst = mu.getConceptDSTParent();
            		Concept dstResult = mu.getConceptDSTResultParent();
            		List<Concept> drugs = MdrtbUtil.getDstDrugList(true);
            		List<Concept> rcs = concepts.get("resistanceConcepts");
            		
	                Set<String> names = new TreeSet<String>();
	                for (Concept c: MdrtbUtil.getResistantToDrugConcepts(programDate, dst, dstResult, drugs, rcs, p, false)){
	                    names.add(getConceptDisplay(c, null));
	                }
	                map.put(RESISTANCE_LIST, OpenmrsUtil.join(names, ", "));
            	}
            }  
        } 
        catch (LogicException e) {
            throw new RuntimeException("Error evaluating logic", e);
        }
        
        ms = System.currentTimeMillis() - ms;
        log.warn("Retrieved remaining results in : " + ms + " ms");
        return byPatient;
    }
    
    
    private static Result trimResultsToAfterProgramStartDate(Result originalResult,  Date programStartDate, boolean subtractTwoMonths){
            Result ret = new Result();
            
            if (programStartDate != null && subtractTwoMonths){
                Calendar cal = Calendar.getInstance();
                cal.setTime(programStartDate);
                cal.add(Calendar.MONTH, -2);
                programStartDate = cal.getTime();
            }
            
            if (programStartDate == null)
                programStartDate = new Date(0);
            if (originalResult != null){
                Iterator<Result> resIterator = originalResult.listIterator();
                while (resIterator.hasNext()){
                    Result resInner = resIterator.next();
                    if (resInner.getResultDate().after(programStartDate) || resInner.getResultDate().getTime() == programStartDate.getTime()){
                        ret.add(resInner);
                    }
                }
            }
            //sort by result date:
            if (ret != null)
                Collections.sort(ret, new Comparator<Result>() {
                    public int compare(Result left, Result right) {
                        return (left.getResultDate()).compareTo(right.getResultDate());
                    }
                    
                });
            
            return ret;
    }
    
    private static Result getFirstResultAfterProgramStartDate(Result originalResult,  Date programStartDate, boolean subtractTwoMonths){
        if (originalResult.isEmpty())
            return originalResult;
        else {
            Result ret = trimResultsToAfterProgramStartDate(originalResult,  programStartDate, subtractTwoMonths);
            if (ret.isEmpty())
                return new Result();
            else
                return ret.iterator().next();
        }
    }
    
    private static String getAntecedentsPersonnelsAsString(Concept c, Patient patient){
        Set<String> names = new HashSet<String>();
        List<Person> pList = new ArrayList<Person>();
        pList.add(patient);
        List<Concept> questions = new ArrayList<Concept>();
        questions.add(c);
        
        List<Obs> oList = Context.getObsService().getObservations(pList, null, questions, null, null,null, null, 1, null, null, null, false);
        if (oList.size() > 0){
            Obs parentObs = oList.get(0);
            for (Obs oInner : parentObs.getGroupMembers()){
                if (oInner.getConcept().getConceptId().intValue() == PREVIOUS_DIAGNOSIS.intValue()){
                    Concept cAnswer = oInner.getValueCoded();
                    if (cAnswer != null){
                        names.add(cAnswer.getBestName(Context.getLocale()).getName());
                    }
                }
            }
        }        
        return OpenmrsUtil.join(names, ", ");
    }  
    
    public static String getRegimenAsString(RegimenHistory rh, Date regDate, ConceptNameTag tag) {
    	if (rh != null) {
	    	Regimen r = rh.getRegimenOnDate(regDate);
	    	if (r != null && r.getDrugOrders() != null) {
	    		Set<String> components = new TreeSet<String>();
	    		for (DrugOrder rc : r.getDrugOrders()) {
	    			components.add(getConceptDisplay(rc.getConcept(), tag));
	    		}
	    		return OpenmrsUtil.join(components, "+");
	    	}
    	}
    	return "";
    }
    	
    public static String getConceptDisplay(Concept c, ConceptNameTag tag) {
		String s = c.getDisplayString();
		ConceptName name = null;
		if (tag != null) {
			name = c.findNameTaggedWith(tag);
		}
		if (name == null) {
			name = c.getShortestName(Context.getLocale(), false);
		}
		if (name != null) {
			s = name.getName();
		}
		return s;
    }
    
	public static void outputToExcel(HttpServletResponse response, Cohort c, List<String> columnList) throws IOException {
    	
		Date runDate = new Date();
    	Map<String, String> availableKeys = PatientSummaryUtil.getAvailableKeys();
    	Map<Integer, Map<String, Object>> data = PatientSummaryUtil.getPatientSummaryData(c, columnList, runDate);
    	
		String title = "export";

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(title);
		SheetHelper sh = new SheetHelper(sheet);
		StyleHelper helper = new StyleHelper(wb);
		
		sh.addCell("Patient Export " + " (" + data.size() + "), generated " + formatDate(runDate, null, null), helper.getStyle("bold,italic"));
		sh.nextRow();
		sh.nextRow();
		
		for (String column : columnList) {
			sh.addCell(availableKeys.get(column), helper.getStyle("bold"));
		}
		sh.nextRow();
		
		for (Map<String, Object> row : data.values()) {
			for (String column : columnList) {
				Object val = row.get(column);
				if (val != null && val instanceof Date) {
					sh.addCell((Date)val, helper.getStyle("date"));
				}
				else {
					sh.addCell(formatObject(val, ""));
				}
			}
			sh.nextRow();
		}
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename=export-" + System.currentTimeMillis() + ".xls");
		wb.write(response.getOutputStream());
	}
	
	public static Concept loadConcept(Integer conceptId) {
		Concept c = Context.getConceptService().getConcept(conceptId);
		MdrtbFactory.getInstance().initializeEverythingAboutConcept(c);
		return c;
	}
	
    public static String formatDate(Date d, Boolean estimated, String emptyMessage) {
    	if (d != null) {
    		DateFormat df = new SimpleDateFormat("dd/MMM/yyyy", Context.getLocale());
    		return (estimated == Boolean.TRUE ? "~" : "") + df.format(d);
    	}
    	return (emptyMessage == null ? "" : MessageUtil.translate(emptyMessage));
    }
    
    public static String formatConcept(Concept c) {
    	if (c == null) {
    		return "";
    	}
    	return c.getName().getName();
    }
    
	public static int compareDates(Date d1, Date d2, String format) {
		DateFormat df = new SimpleDateFormat(format, Context.getLocale());
		String s1 = df.format(d1);
		String s2 = df.format(d2);
		return s1.compareTo(s2);
	}
	
	public static String formatObject(Object o, String defaultVal) {
		if (o == null) {
			return defaultVal;
		}
		if (o instanceof Concept) {
			return formatConcept((Concept)o);
		}
		if (o instanceof OpenmrsMetadata) {
			return ((OpenmrsMetadata)o).getName();
		}
		if (o instanceof Collection) {
			return OpenmrsUtil.join((Collection<?>)o, ", ");
		}
		if (o instanceof Result) {
			return ((Result)o).toString();
		}
		return o.toString();
	}
}
