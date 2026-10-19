package org.openmrs.module.mdrtb.web.controller.reporting;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.District;
import org.openmrs.module.mdrtb.Facility;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbConstants;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.Region;
import org.openmrs.module.mdrtb.ReportType;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.form.custom.Form89;
import org.openmrs.module.mdrtb.form.custom.TB03Form;
import org.openmrs.module.mdrtb.reporting.custom.Form8Table1Data;
import org.openmrs.module.mdrtb.reporting.custom.Form8Table2Data;
import org.openmrs.module.mdrtb.reporting.custom.Form8Table3Data;
import org.openmrs.module.mdrtb.reporting.custom.Form8Table4Data;
import org.openmrs.module.mdrtb.reporting.custom.Form8Table5aData;
import org.openmrs.module.mdrtb.reporting.custom.TB08Data;
import org.openmrs.propertyeditor.ConceptEditor;
import org.openmrs.propertyeditor.LocationEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@SuppressWarnings("unused")
@Controller
public class Form8Controller {
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(Context.getDateFormat(), true, 10));
		binder.registerCustomEditor(Concept.class, new ConceptEditor());
		binder.registerCustomEditor(Location.class, new LocationEditor());
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/module/mdrtb/reporting/form8")
	public ModelAndView showRegimenOptions(@RequestParam(value = "loc", required = false) String district,
	        @RequestParam(value = "ob", required = false) String oblast,
	        @RequestParam(value = "yearSelected", required = false) Integer year,
	        @RequestParam(value = "quarterSelected", required = false) String quarter,
	        @RequestParam(value = "monthSelected", required = false) String month, ModelMap model) {
		
		List<Region> oblasts;
		List<Facility> facilities;
		List<District> districts;
		
		if (oblast == null) {
			oblasts = Context.getService(MdrtbService.class).getRegions();
			model.addAttribute("oblasts", oblasts);
		}
		
		else if (district == null) {
			//DUSHANBE
			if (Integer.parseInt(oblast) == 186) {
				oblasts = Context.getService(MdrtbService.class).getRegions();
				districts = Context.getService(MdrtbService.class).getDistrictsByParent(Integer.parseInt(oblast));
				District d = districts.get(0);
				facilities = Context.getService(MdrtbService.class).getFacilitiesByParent(d.getId());
				model.addAttribute("oblastSelected", oblast);
				model.addAttribute("oblasts", oblasts);
				model.addAttribute("districts", districts);
				model.addAttribute("facilities", facilities);
				model.addAttribute("dushanbe", 186);
			} else {
				oblasts = Context.getService(MdrtbService.class).getRegions();
				districts = Context.getService(MdrtbService.class).getDistrictsByParent(Integer.parseInt(oblast));
				model.addAttribute("oblastSelected", oblast);
				model.addAttribute("oblasts", oblasts);
				model.addAttribute("districts", districts);
			}
		} else {
			if (Integer.parseInt(oblast) == 186) {
				oblasts = Context.getService(MdrtbService.class).getRegions();
				districts = Context.getService(MdrtbService.class).getDistrictsByParent(Integer.parseInt(oblast));
				District d = districts.get(0);
				facilities = Context.getService(MdrtbService.class).getFacilitiesByParent(d.getId());
				model.addAttribute("oblastSelected", oblast);
				model.addAttribute("oblasts", oblasts);
				model.addAttribute("districts", districts);
				model.addAttribute("facilities", facilities);
				model.addAttribute("dushanbe", 186);
			} else {
				oblasts = Context.getService(MdrtbService.class).getRegions();
				districts = Context.getService(MdrtbService.class).getDistrictsByParent(Integer.parseInt(oblast));
				facilities = Context.getService(MdrtbService.class).getFacilitiesByParent(Integer.parseInt(district));
				model.addAttribute("oblastSelected", oblast);
				model.addAttribute("oblasts", oblasts);
				model.addAttribute("districtSelected", district);
				model.addAttribute("districts", districts);
				model.addAttribute("facilities", facilities);
			}
		}
		
		model.addAttribute("yearSelected", year);
		model.addAttribute("monthSelected", month);
		model.addAttribute("quarterSelected", quarter);
		
		return new ModelAndView("/module/mdrtb/reporting/form8", model);
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/module/mdrtb/reporting/form8")
	public static String doTB08(@RequestParam("district") Integer districtId, @RequestParam("oblast") Integer oblastId,
	        @RequestParam("facility") Integer facilityId, @RequestParam(value = "year", required = true) Integer year,
	        @RequestParam(value = "quarter", required = false) String quarter,
	        @RequestParam(value = "month", required = false) String month, ModelMap model) {
		
		List<Location> locList = null;
		if (oblastId != null) {
			if (oblastId == 186) {
				locList = Context.getService(MdrtbService.class)
				        .getLocationListForDushanbe(oblastId, districtId, facilityId);
			} else {
				Region region = Context.getService(MdrtbService.class).getRegion(oblastId);
				District district = Context.getService(MdrtbService.class).getDistrict(districtId);
				Facility facility = Context.getService(MdrtbService.class).getFacility(facilityId);
				locList = Context.getService(MdrtbService.class).getLocations(region, district, facility);
			}
		}
		
		Integer quarterInt = quarter == null ? null : Integer.parseInt(quarter);
		Integer monthInt = month == null ? null : Integer.parseInt(month);
		Map<String, Object> tableMap = getForm8TableMap(locList, year, quarterInt, monthInt, monthInt);
		
		for (Entry<String, Object> table : tableMap.entrySet()) {
			model.addAttribute(table.getKey(), table.getValue());
		}
		
		boolean reportStatus = Context.getService(MdrtbService.class).getReportArchived(oblastId, districtId, facilityId,
		    year, quarterInt, monthInt, "TB-08", ReportType.DOTSTB);
		
		String oName = null;
		String dName = null;
		String fName = null;
		
		if (oblastId != null) {
			Region o = Context.getService(MdrtbService.class).getRegion(oblastId);
			if (o != null) {
				oName = o.getName();
			}
		}
		
		if (districtId != null) {
			District d = Context.getService(MdrtbService.class).getDistrict(districtId);
			if (d != null) {
				dName = d.getName();
			}
		}
		
		if (facilityId != null) {
			Facility f = Context.getService(MdrtbService.class).getFacility(facilityId);
			if (f != null) {
				fName = f.getName();
			}
		}
		model.addAttribute("oblast", oblastId);
		model.addAttribute("district", districtId);
		model.addAttribute("facility", facilityId);
		model.addAttribute("year", year);
		if (month != null && month.length() != 0)
			model.addAttribute("month", month.replace("\"", ""));
		else
			model.addAttribute("month", "");
		
		if (quarter != null && quarter.length() != 0)
			model.addAttribute("quarter", quarter.replace("\"", "'"));
		else
			model.addAttribute("quarter", "");
		model.addAttribute("oName", oName);
		model.addAttribute("dName", dName);
		model.addAttribute("fName", fName);
		model.addAttribute("reportDate", Context.getDateTimeFormat().format(new Date()));
		model.addAttribute("reportStatus", reportStatus);
		return "/module/mdrtb/reporting/form8Results";
	}
	
	public static Map<String, Object> getForm8TableMap(List<Location> locList, Integer year, Integer quarter, Integer month, Integer month2) {
		List<TB03Form> tb03List = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year, quarter, month, month2);
		Integer ageAtRegistration = 0;
		
		Concept pulmonaryConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PULMONARY_TB);
		Concept extrapulmonaryConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.EXTRA_PULMONARY_TB);
		Concept positiveConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.POSITIVE);
		Concept negativeConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NEGATIVE);
		
		String gender = null;
		Boolean pulmonary = null;
		Boolean bacPositive = null;
		Boolean cured = null;
		Boolean txCompleted = null;
		Boolean diedTB = null;
		Boolean diedNotTB = null;
		Boolean failed = null;
		Boolean defaulted = null;
		Boolean transferOut = null;
		Boolean canceled = null;
		Boolean sld = null;
		Boolean male = null;
		Boolean female = null;
		Boolean hospitalised = null;
		
		Form8Table1Data table1 = new Form8Table1Data();
		Form8Table2Data table2 = new Form8Table2Data();
		Form8Table3Data table3 = new Form8Table3Data();
		Form8Table4Data table4 = new Form8Table4Data();
		Form8Table5aData table5a = new Form8Table5aData();
		TB08Data tb08TableData = new TB08Data();
		
		Concept regGroup = null;
		Form89 f89 = null;
		
		Boolean rural = null;
		Concept ruralConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.VILLAGE);
		int ruralId = ruralConcept.getConceptId();
		Concept locationType = null;
		
		Boolean miliary = null;
		Boolean focal = null;
		Boolean infiltrative = null;
		Boolean disseminated = null;
		Boolean cavernous = null;
		Boolean fibroCav = null;
		Boolean cirrhotic = null;
		Boolean tbComplex = null;
		Boolean tuberculoma = null;
		Boolean bronchi = null;
		
		Boolean plevritis = null;
		Boolean itLymph = null;
		Boolean cns = null;
		Boolean osteoArticular = null;
		Boolean urogenital = null;
		Boolean peripheralLymphNodes = null;
		Boolean abdominal = null;
		Boolean eye = null;
		Boolean liver = null;
		Boolean skin = null;
		
		Boolean resistant = null;
		Boolean hivPositive = null;
		
		Boolean phcFacility = null;
		Boolean tbFacility = null;
		Boolean privateSectorFacility = null;
		Boolean otherFacility = null;
		Boolean phcWorker = null;
		Boolean tbServicesWorker = null;
		Boolean contact = null;
		Boolean migrant = null;
		
		Boolean decay = null;
		
		//PULMONARY
		Concept fibroCavConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.FIBROUS_CAVERNOUS);
		int fibroCavId = fibroCavConcept.getConceptId();
		Concept miliaryConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MILITARY_SERVANT);
		int miliaryId = miliaryConcept.getConceptId();
		Concept focalConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.FOCAL);
		int focalId = focalConcept.getConceptId();
		Concept infiltrativeConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.INFILTRATIVE);
		int infiltrativeId = infiltrativeConcept.getConceptId();
		Concept disseminatedConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DISSEMINATED);
		int disseminatedId = disseminatedConcept.getConceptId();
		Concept cavernousConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAVERNOUS);
		int cavernousId = cavernousConcept.getConceptId();
		Concept cirrhoticConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CIRRHOTIC);
		int cirrhoticId = cirrhoticConcept.getConceptId();
		Concept primaryComplexConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TB_PRIMARY_COMPLEX);
		int primaryComplexId = primaryComplexConcept.getConceptId();
		Concept tuberculomaConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TUBERCULOMA);
		int tuberculomaId = tuberculomaConcept.getConceptId();
		Concept bronchiConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.BRONCHUS);
		int bronchiId = bronchiConcept.getConceptId();
		
		//EP TB
		Concept plevConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PLEVRITIS);
		int plevId = plevConcept.getConceptId();
		Concept itLymphConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OF_LYMPH_NODES);
		int itLymphId = itLymphConcept.getConceptId();
		Concept cnsConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OF_CNS);
		int cnsId = cnsConcept.getConceptId();
		Concept osteoArticularConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OSTEOARTICULAR);
		int osteoArticularId = osteoArticularConcept.getConceptId();
		Concept urogenitalConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.GENITOURINARY);
		int urogenitalId = urogenitalConcept.getConceptId();
		Concept peripheralLymphNodesConcept = Context.getService(MdrtbService.class)
		        .getConcept(MdrtbConcepts.OF_LYMPH_NODES);
		int peripheralLymphNodesId = peripheralLymphNodesConcept.getConceptId();
		Concept abdominalConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ABDOMINAL);
		int abdominalId = abdominalConcept.getConceptId();
		Concept eyeConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OCULAR);
		int eyeId = eyeConcept.getConceptId();
		Concept skinConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TUBERCULODERMA);
		int skinId = skinConcept.getConceptId();
		Concept liverConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OF_LIVER);
		int liverId = liverConcept.getConceptId();
		
		//Other
		Concept phcFacilityConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PHC_FACILITY);
		int phcFacilityId = phcFacilityConcept.getConceptId();
		Concept tbFacilityConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TB_FACILITY);
		int tbFacilityId = tbFacilityConcept.getConceptId();
		Concept privateSectorFacilityConcept = Context.getService(MdrtbService.class).getConcept(
		    MdrtbConcepts.PRIVATE_SECTOR_FACILITY);
		int privateSectorFacilityId = privateSectorFacilityConcept.getConceptId();
		Concept otherFacilityConcept = Context.getService(MdrtbService.class).getConcept(
		    MdrtbConcepts.OTHER_MEDICAL_FACILITY);
		int otherFacilityId = otherFacilityConcept.getConceptId();
		
		Concept phcWorkerConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PHC_WORKER);
		int phcWorkerId = phcWorkerConcept.getConceptId();
		Concept tbWorkerConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.TB_SERVICES_WORKER);
		int tbWorkerId = tbWorkerConcept.getConceptId();
		Concept contactConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CONTACT_INVESTIGATION);
		int contactId = contactConcept.getConceptId();
		Concept migrantConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MIGRANT);
		int migrantId = migrantConcept.getConceptId();
		Concept yesConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.YES);
		int yesId = yesConcept.getConceptId();
		Concept hospConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HOSPITAL);
		int hospId = hospConcept.getConceptId();
		
		Concept anatomicalSite = null;
		Concept epulLocation = null;
		
		int age = 0;
		int resId = 0;
		
		int noResId = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NONE).getConceptId();
		int unknownId = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.UNKNOWN).getConceptId();
		int monoId = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MONO).getConceptId();
		
		Boolean pregnant = null;
		Concept detectedAt = null;
		Concept circOf = null;
		Concept prof = null;
		
		//table1
		for (TB03Form tf : tb03List) {
			if (tf.getPatient() == null || tf.getPatient().getVoided()) {
				continue;
			}
			System.out.println("Processing: " + tf.getPatient().toString());
			ageAtRegistration = -1;
			
			gender = null;
			regGroup = locationType = anatomicalSite = epulLocation = detectedAt = circOf = prof = null;
			pulmonary = bacPositive = male = female = rural = plevritis = itLymph = fibroCav = cns = osteoArticular 
					= urogenital = peripheralLymphNodes = abdominal = eye = miliary = resistant = hivPositive = phcFacility 
					= phcWorker = tbFacility = tbServicesWorker = migrant = contact = otherFacility = privateSectorFacility 
					= focal = infiltrative = disseminated = cavernous = cirrhotic = tbComplex = tuberculoma = bronchi 
					= skin = liver = pregnant = hospitalised = null;
						
			ageAtRegistration = tf.getAgeAtTB03Registration();
			age = ageAtRegistration;
			bacPositive = MdrtbUtil.isDiagnosticBacPositive(tf);
			regGroup = tf.getRegistrationGroup();
			
			if ((tf.getTreatmentSiteIP() != null && tf.getTreatmentSiteIP().getConceptId() == hospId)
			        || (tf.getTreatmentSiteCP() != null && tf.getTreatmentSiteCP().getConceptId() == hospId)) {
				hospitalised = Boolean.TRUE;
				table4.setHospitalised(table4.getHospitalised() + 1);
				table4.setInHospital(table4.getInHospital() + 1);
				if (age >= 0 && age < 15) {
					table4.setHospitalised014(table4.getHospitalised014() + 1);
					table4.setInHospital014(table4.getInHospital014() + 1);
				} else if (age >= 15 && age < 18) {
					table4.setHospitalised1517(table4.getHospitalised1517() + 1);
					table4.setInHospital1517(table4.getInHospital1517() + 1);
				} else if (age >= 18 && age < 19) {
					table4.setHospitalised1819(table4.getHospitalised1819() + 1);
					table4.setInHospital1819(table4.getInHospital1819() + 1);
				}
				if (regGroup != null
				        && regGroup.getConceptId().equals(
				            Integer.parseInt(Context.getAdministrationService().getGlobalProperty(
				                MdrtbConstants.GP_NEW_CONCEPT_ID)))) {
					table4.setFirstNew(table4.getFirstNew() + 1);
					if (age >= 0 && age < 15) {
						table4.setFirstNew014(table4.getFirstNew014() + 1);
					} else if (age >= 15 && age < 18) {
						table4.setFirstNew1517(table4.getFirstNew1517() + 1);
					} else if (age >= 18 && age < 19) {
						table4.setFirstNew1819(table4.getFirstNew1819() + 1);
					}
					if (bacPositive) {
						table4.setNewBac(table4.getNewBac() + 1);
						if (age >= 0 && age < 15) {
							table4.setNewBac014(table4.getNewBac014() + 1);
						} else if (age >= 15 && age < 18) {
							table4.setNewBac1517(table4.getNewBac1517() + 1);
						} else if (age >= 18 && age < 19) {
							table4.setNewBac1819(table4.getNewBac1819() + 1);
						}
					} else {
						table4.setNewOther(table4.getNewOther() + 1);
						if (age >= 0 && age < 15) {
							table4.setNewOther014(table4.getNewOther014() + 1);
						} else if (age >= 15 && age < 18) {
							table4.setNewOther1517(table4.getNewOther1517() + 1);
						} else if (age >= 18 && age < 19) {
							table4.setNewOther1819(table4.getNewOther1819() + 1);
						}
					}
				}
				
			}
			
			List<Form89> fList = Context.getService(MdrtbService.class).getForm89FormsFilledForPatientProgram(
			    tf.getPatient(), null, tf.getPatientProgramId(), null, null, null, null);
			
			if (!fList.isEmpty()) {
				rural = fList.get(0).getLocationType().getConceptId().equals(ruralId) ? true : false;
			}
			
			Concept q = tf.getAnatomicalSite();
			
			if (q != null) {
				if (q.getConceptId().intValue() == pulmonaryConcept.getConceptId().intValue()) {
					pulmonary = Boolean.TRUE;
				}
			}
			
			if (bacPositive != null && bacPositive && pulmonary != null && pulmonary) {
				if (regGroup != null
				        && regGroup.getConceptId().equals(
				            Integer.parseInt(Context.getAdministrationService().getGlobalProperty(
				                MdrtbConstants.GP_NEW_CONCEPT_ID)))) {
					table5a.setRespBacNew(table5a.getRespBacNew() + 1);
					
					if (rural != null && rural) {
						table5a.setRespBacNewVillager(table5a.getRespBacNewVillager() + 1);
					}
				} else if (regGroup != null) {
					table5a.setRespBacOther(table5a.getRespBacOther() + 1);
					
					if (rural != null && rural) {
						table5a.setRespBacOtherVillager(table5a.getRespBacOtherVillager() + 1);
					}
				}
			}
			
			if (regGroup == null
			        || !regGroup.getConceptId().equals(
			            Integer.parseInt(Context.getAdministrationService().getGlobalProperty(
			                MdrtbConstants.GP_NEW_CONCEPT_ID)))) {
				if (regGroup != null && 
						(regGroup.getConceptId().equals(Integer.parseInt(Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_AFTER_RELAPSE1_CONCEPT_ID))))) {
					table2.setRelapseCount(table2.getRelapseCount() + 1);
					table3.setGroup2To1(table3.getGroup2To1() + 1);
					table3.setRelapse(table3.getRelapse() + 1);
				} else if (regGroup != null && 
						(regGroup.getConceptId().equals(Integer.parseInt(Context.getAdministrationService()
				                .getGlobalProperty(MdrtbConstants.GP_AFTER_FAILURE1_CONCEPT_ID))))) {
					table2.setFailCount(table2.getFailCount() + 1);
					table3.setGroup2To1(table3.getGroup2To1() + 1);
				} else if (regGroup != null && 
						(regGroup.getConceptId().equals(Integer.parseInt(Context.getAdministrationService()
				                .getGlobalProperty(MdrtbConstants.GP_AFTER_DEFAULT1_CONCEPT_ID))))) {
					table2.setLtfuCount(table2.getLtfuCount() + 1);
					table3.setGroup2To1(table3.getGroup2To1() + 1);
				} else {
					table2.setOtherCount(table2.getOtherCount() + 1);
					table3.setGroup2To1(table3.getGroup2To1() + 1);
				}
				continue;
			}
			
			fList = Context.getService(MdrtbService.class).getForm89FormsFilledForPatientProgram(tf.getPatient(), null,
			    tf.getPatientProgramId(), null, null, null, null);
			
			if (fList == null || fList.size() != 1) {
				continue;
			}
			f89 = fList.get(0);
			f89.setTB03(tf);
			locationType = f89.getLocationType();
			if (locationType != null && locationType.getConceptId().equals(ruralId)) {
				rural = Boolean.TRUE;
			} else if (locationType != null) {
				rural = Boolean.FALSE;
			}
			
			gender = tf.getPatient().getGender();
			if ("M".equals(gender)) {
				male = Boolean.TRUE;
			} else if ("F".equals(gender)) {
				female = Boolean.TRUE;
			}
			
			//get disease site
			q = tf.getAnatomicalSite();
			
			if (q != null) {
				if (q.getConceptId().intValue() == pulmonaryConcept.getConceptId().intValue()) {
					pulmonary = Boolean.TRUE;
					anatomicalSite = f89.getAnatomicalSite();
					if (anatomicalSite != null && anatomicalSite.getConceptId() == fibroCavId) {
						fibroCav = Boolean.TRUE;
						table2.setFibrousTotal(table2.getFibrousTotal() + 1);
					}
					if (anatomicalSite != null && anatomicalSite.getConceptId() == miliaryId) {
						miliary = Boolean.TRUE;
						table2.setMiliaryTotal(table2.getMiliaryTotal() + 1);
					}
					if (anatomicalSite != null && anatomicalSite.getConceptId() == focalId) {
						focal = Boolean.TRUE;
						table2.setFocalTotal(table2.getFocalTotal() + 1);
					}
					if (anatomicalSite != null && anatomicalSite.getConceptId() == infiltrativeId) {
						infiltrative = Boolean.TRUE;
						table2.setInfiltrativeTotal(table2.getInfiltrativeTotal() + 1);
					}
					if (anatomicalSite != null && anatomicalSite.getConceptId() == disseminatedId) {
						disseminated = Boolean.TRUE;
						table2.setDisseminatedTotal(table2.getDisseminatedTotal() + 1);
					}
					if (anatomicalSite != null && anatomicalSite.getConceptId() == cavernousId) {
						cavernous = Boolean.TRUE;
						table2.setCavernousTotal(table2.getCavernousTotal() + 1);
					}
					if (anatomicalSite != null && anatomicalSite.getConceptId() == cirrhoticId) {
						cirrhotic = Boolean.TRUE;
						table2.setCirrhoticTotal(table2.getCirrhoticTotal() + 1);
					}
					if (anatomicalSite != null && anatomicalSite.getConceptId() == primaryComplexId) {
						tbComplex = Boolean.TRUE;
						table2.setTbComplexTotal(table2.getTbComplexTotal() + 1);
					}
					if (anatomicalSite != null && anatomicalSite.getConceptId() == tuberculomaId) {
						tuberculoma = Boolean.TRUE;
						table2.setTuberculomaTotal(table2.getTuberculomaTotal() + 1);
					}
					if (anatomicalSite != null && anatomicalSite.getConceptId() == bronchiId) {
						bronchi = Boolean.TRUE;
						table2.setBronchiTotal(table2.getBronchiTotal() + 1);
					}
				}
				
				else if (q.getConceptId().intValue() == extrapulmonaryConcept.getConceptId().intValue()) {
					pulmonary = Boolean.FALSE;
					epulLocation = f89.getEptbLocation();
					if (epulLocation != null) {
						if (epulLocation.getConceptId() == cnsId) {
							cns = Boolean.TRUE;
							table2.setNervousSystemTotal(table2.getNervousSystemTotal() + 1);
						} else if (epulLocation.getConceptId() == osteoArticularId) {
							osteoArticular = Boolean.TRUE;
							table2.setOsteoarticularTotal(table2.getOsteoarticularTotal() + 1);
						} else if (epulLocation.getConceptId() == urogenitalId) {
							urogenital = Boolean.TRUE;
							table2.setUrogenitalTotal(table2.getUrogenitalTotal() + 1);
						} else if (epulLocation.getConceptId() == peripheralLymphNodesId) {
							peripheralLymphNodes = Boolean.TRUE;
							table2.setPeripheralLymphNodesTotal(table2.getPeripheralLymphNodesTotal() + 1);
						} else if (epulLocation.getConceptId() == abdominalId) {
							abdominal = Boolean.TRUE;
							table2.setAbdominalTotal(table2.getAbdominalTotal() + 1);
						} else if (epulLocation.getConceptId() == eyeId) {
							eye = Boolean.TRUE;
							table2.setEyeTotal(table2.getEyeTotal() + 1);
						} else if (epulLocation.getConceptId() == plevId) {
							plevritis = Boolean.TRUE;
							table2.setPleurisyTotal(table2.getPleurisyTotal() + 1);
						} else if (epulLocation.getConceptId() == itLymphId) {
							itLymph = Boolean.TRUE;
							table2.setHilarLymphNodesTotal(table2.getHilarLymphNodesTotal() + 1);
						} else if (epulLocation.getConceptId() == liverId) {
							liver = Boolean.TRUE;
							table2.setLiverTotal(table2.getLiverTotal() + 1);
						} else if (epulLocation.getConceptId() == skinId) {
							skin = Boolean.TRUE;
							table2.setSkinTotal(table2.getSkinTotal() + 1);
						}
					}
				} else {
					pulmonary = null;
				}
			}
			
			q = tf.getHivStatus();
			
			if (q != null) {
				if (q.getConceptId().intValue() == positiveConcept.getConceptId().intValue()) {
					hivPositive = Boolean.TRUE;
				} else if (q.getConceptId().intValue() == negativeConcept.getConceptId().intValue()) {
					hivPositive = Boolean.FALSE;
				} else {
					hivPositive = Boolean.FALSE;
				}
			} else {
				hivPositive = Boolean.FALSE;
			}
			
			q = tf.getResistanceType();
			
			if (q != null) {
				resId = q.getConceptId();
				if (resId != noResId && resId != unknownId && resId != monoId) {
					resistant = Boolean.TRUE;
				} else if (resId == noResId) {
					resistant = Boolean.FALSE;
				}
			}
			
			detectedAt = f89.getPlaceOfDetection();
			if (detectedAt != null) {
				int detId = detectedAt.getConceptId();
				table2.setDetectedByRoutineCheckups(table2.getDetectedByRoutineCheckups() + 1);
				if (age >= 0 && age < 15) {
					table2.setRoutine014(table2.getRoutine014() + 1);
				} else if (age >= 15 && age < 18) {
					table2.setRoutine1517(table2.getRoutine1517() + 1);
				} else if (age >= 18 && age < 20) {
					table2.setRoutine1819(table2.getRoutine1819() + 1);
				}
				if (detId == phcFacilityId) {
					table2.setDetectedBySpecialistsTotal(table2.getDetectedBySpecialistsTotal() + 1);
					phcFacility = Boolean.TRUE;
				} else if (detId == tbFacilityId) {
					table2.setDetectedBySpecialistsTotal(table2.getDetectedByTBDoctors() + 1);
					tbFacility = Boolean.TRUE;
				} else if (detId == privateSectorFacilityId) {
					table2.setDetectedBySpecialistsTotal(table2.getDetectedBySpecialistsTotal() + 1);
					privateSectorFacility = Boolean.TRUE;
				} else if (detId == otherFacilityId) {
					table2.setDetectedByOtherSpecialists(table2.getDetectedByOtherSpecialists() + 1);
					otherFacility = Boolean.TRUE;
				}
			}
			
			circOf = f89.getCircumstancesOfDetection();
			if (circOf != null) {
				int circId = circOf.getConceptId();
				if (circId == contactId) {
					contact = Boolean.TRUE;
					table2.setContact(table2.getContact() + 1);
				} else if (circId == migrantId) {
					migrant = Boolean.TRUE;
					table2.setMigrants(table2.getMigrants() + 1);
				}
			}
			
			prof = f89.getProfession();
			
			if (prof != null) {
				int profId = prof.getConceptId();
				
				if (profId == phcWorkerId) {
					phcWorker = Boolean.TRUE;
					table2.setPhcWorkers(table2.getPhcWorkers() + 1);
				} else if (profId == tbWorkerId) {
					tbServicesWorker = Boolean.TRUE;
					table2.setTbServiceWorkers(table2.getTbServiceWorkers() + 1);
				}
			}
			
			if (f89.getPregnant() != null) {
				if (f89.getPregnant().getConceptId() == yesId) {
					pregnant = Boolean.TRUE;
				}
			}
			
			//decay
			if (f89.getPresenceOfDecay() != null) {
				if (f89.getPresenceOfDecay().getConceptId() == yesId) {
					table2.setDecayPhaseTotal(table2.getDecayPhaseTotal() + 1);
					if (phcFacility != null && phcFacility) {
						table2.setDecayPhasePHCTotal(table2.getDecayPhasePHCTotal() + 1);
					}
					if (age >= 0 && age < 15) {
						table2.setDecayPhase014(table2.getDecayPhase014() + 1);
					} else if (age >= 15 && age < 18) {
						table2.setDecayPhase1517(table2.getDecayPhase1517() + 1);
					} else if (age >= 18 && age < 20) {
						table2.setDecayPhase1819(table2.getDecayPhase1819() + 1);
					}
				}
			}
			
			if (male != null && male) {
				table1.setActiveTBTotalMale(table1.getActiveTBTotalMale() + 1);
				
				if (phcFacility != null && phcFacility) {
					table2.setActivePHCTotal(table2.getActivePHCTotal() + 1);
				}
				
				if ((pulmonary != null && pulmonary) || (plevritis != null && plevritis) || (itLymph != null && itLymph)) {
					table1.setRespiratoryTBTotalMale(table1.getRespiratoryTBTotalMale() + 1);
					
					if (phcFacility != null && phcFacility) {
						table2.setRespiratoryPHCTotal(table2.getRespiratoryPHCTotal() + 1);
					}
				}
				
				if (pulmonary != null && pulmonary) {
					table1.setPulmonaryTBTotalMale(table1.getPulmonaryTBTotalMale() + 1);
					if (phcFacility != null && phcFacility) {
						table2.setPulmonaryPHCTotal(table2.getPulmonaryPHCTotal() + 1);
					}
					
					if (MdrtbUtil.isDiagnosticBacPositive(tf)) {
						table1.setBacExTBTotalMale(table1.getBacExTBTotalMale() + 1);
						
						if (phcFacility != null && phcFacility) {
							table2.setBacExPHCTotal(table2.getBacExPHCTotal() + 1);
						}
					}
					
					if (miliary != null && miliary) {
						table1.setMiliaryTBTotalMale(table1.getMiliaryTBTotalMale() + 1);
						
						if (phcFacility != null && phcFacility) {
							table2.setMiliaryPHCTotal(table2.getMiliaryPHCTotal() + 1);
						}
					}
				}
				
				if (fibroCav != null && fibroCav) {
					table1.setFibCavTBTotalMale(table1.getFibCavTBTotalMale() + 1);
				}
				
				else if (cns != null && cns) {
					table1.setNervousSystemTBTotalMale(table1.getNervousSystemTBTotalMale() + 1);
					
					if (phcFacility != null && phcFacility) {
						table2.setNervousSystemPHCTotal(table2.getNervousSystemPHCTotal() + 1);
					}
				}
				
				else if (osteoArticular != null && osteoArticular) {
					table1.setOtherOrgansTBTotalMale(table1.getOtherOrgansTBTotalMale() + 1);
					if (phcFacility != null && phcFacility) {
						table2.setOtherOrgansPHCTotal(table2.getOtherOrgansPHCTotal() + 1);
					}
					table1.setOsteoarticularTBTotalMale(table1.getOsteoarticularTBTotalMale() + 1);
				}
				
				else if (urogenital != null && urogenital) {
					table1.setOtherOrgansTBTotalMale(table1.getOtherOrgansTBTotalMale() + 1);
					if (phcFacility != null && phcFacility) {
						table2.setOtherOrgansPHCTotal(table2.getOtherOrgansPHCTotal() + 1);
					}
					table1.setUrogenitalTBTotalMale(table1.getUrogenitalTBTotalMale() + 1);
				}
				
				else if (peripheralLymphNodes != null && peripheralLymphNodes) {
					table1.setOtherOrgansTBTotalMale(table1.getOtherOrgansTBTotalMale() + 1);
					if (phcFacility != null && phcFacility) {
						table2.setOtherOrgansPHCTotal(table2.getOtherOrgansPHCTotal() + 1);
					}
					table1.setLymphNodesTBTotalMale(table1.getLymphNodesTBTotalMale() + 1);
				}
				
				else if (abdominal != null && abdominal) {
					table1.setOtherOrgansTBTotalMale(table1.getOtherOrgansTBTotalMale() + 1);
					if (phcFacility != null && phcFacility) {
						table2.setOtherOrgansPHCTotal(table2.getOtherOrgansPHCTotal() + 1);
					}
					table1.setAbdominalTBTotalMale(table1.getAbdominalTBTotalMale() + 1);
				}
				
				else if (eye != null && eye) {
					table1.setOtherOrgansTBTotalMale(table1.getOtherOrgansTBTotalMale() + 1);
					if (phcFacility != null && phcFacility) {
						table2.setOtherOrgansPHCTotal(table2.getOtherOrgansPHCTotal() + 1);
					}
					table1.setEyeTBTotalMale(table1.getEyeTBTotalMale() + 1);
				}
				
				if (resistant != null && resistant) {
					table1.setResistantTBTotalMale(table1.getResistantTBTotalMale() + 1);
				}
				
				if (hivPositive != null && hivPositive) {
					table1.setTbhivTBTotalMale(table1.getTbhivTBTotalMale() + 1);
				}
				
				if (rural != null && rural) {
					table1.setRuralTBTotalMale(table1.getRuralTBTotalMale() + 1);
				}
				
				if (age >= 0 && age < 5) {
					table1.setActiveTB04Male(table1.getActiveTB04Male() + 1);
					if ((pulmonary != null && pulmonary) || (plevritis != null && plevritis) || (itLymph != null && itLymph)) {
						table1.setRespiratoryTB04Male(table1.getRespiratoryTB04Male() + 1);
					}
					
					if (pulmonary != null && pulmonary) {
						table1.setPulmonaryTB04Male(table1.getPulmonaryTB04Male() + 1);
						
						if (MdrtbUtil.isDiagnosticBacPositive(tf)) {
							table1.setBacExTB04Male(table1.getBacExTB04Male() + 1);
						}
						
						if (miliary != null && miliary) {
							table1.setMiliaryTB04Male(table1.getMiliaryTB04Male() + 1);
						}
					}
					
					if (fibroCav != null && fibroCav) {
						table1.setFibCavTB04Male(table1.getFibCavTB04Male() + 1);
					}
					
					else if (cns != null && cns) {
						table1.setNervousSystemTB04Male(table1.getNervousSystemTB04Male() + 1);
					}
					
					else if (osteoArticular != null && osteoArticular) {
						table1.setOtherOrgansTB04Male(table1.getOtherOrgansTB04Male() + 1);
						table1.setOsteoarticularTB04Male(table1.getOsteoarticularTB04Male() + 1);
					}
					
					else if (urogenital != null && urogenital) {
						table1.setOtherOrgansTB04Male(table1.getOtherOrgansTB04Male() + 1);
						table1.setUrogenitalTB04Male(table1.getUrogenitalTB04Male() + 1);
					}
					
					else if (peripheralLymphNodes != null && peripheralLymphNodes) {
						table1.setOtherOrgansTB04Male(table1.getOtherOrgansTB04Male() + 1);
						table1.setLymphNodesTB04Male(table1.getLymphNodesTB04Male() + 1);
					}
					
					else if (abdominal != null && abdominal) {
						table1.setOtherOrgansTB04Male(table1.getOtherOrgansTB04Male() + 1);
						table1.setAbdominalTB04Male(table1.getAbdominalTB04Male() + 1);
					}
					
					else if (eye != null && eye) {
						table1.setOtherOrgansTB04Male(table1.getOtherOrgansTB04Male() + 1);
						table1.setEyeTB04Male(table1.getEyeTB04Male() + 1);
					}
					
					if (resistant != null && resistant) {
						table1.setResistantTB04Male(table1.getResistantTB04Male() + 1);
					}
					
					if (hivPositive != null && hivPositive) {
						table1.setTbhivTB04Male(table1.getTbhivTB04Male() + 1);
					}
					
					if (rural != null && rural) {
						table1.setRuralTB04Male(table1.getRuralTB04Male() + 1);
					}
				}
				
				else if (age >= 5 && age < 15) {
					table1.setActiveTB0514Male(table1.getActiveTB0514Male() + 1);
					
					if ((pulmonary != null && pulmonary) || (plevritis != null && plevritis) || (itLymph != null && itLymph)) {
						table1.setRespiratoryTB0514Male(table1.getRespiratoryTB0514Male() + 1);
					}
					
					if (pulmonary != null && pulmonary) {
						table1.setPulmonaryTB0514Male(table1.getPulmonaryTB0514Male() + 1);
						
						if (MdrtbUtil.isDiagnosticBacPositive(tf)) {
							table1.setBacExTB0514Male(table1.getBacExTB0514Male() + 1);
						}
						
						if (miliary != null && miliary) {
							table1.setMiliaryTB0514Male(table1.getMiliaryTB0514Male() + 1);
						}
					}
					
					if (fibroCav != null && fibroCav) {
						table1.setFibCavTB0514Male(table1.getFibCavTB0514Male() + 1);
					}
					
					else if (cns != null && cns) {
						table1.setNervousSystemTB0514Male(table1.getNervousSystemTB0514Male() + 1);
					}
					
					else if (osteoArticular != null && osteoArticular) {
						table1.setOtherOrgansTB0514Male(table1.getOtherOrgansTB0514Male() + 1);
						table1.setOsteoarticularTB0514Male(table1.getOsteoarticularTB0514Male() + 1);
					}
					
					else if (urogenital != null && urogenital) {
						table1.setOtherOrgansTB0514Male(table1.getOtherOrgansTB0514Male() + 1);
						table1.setUrogenitalTB0514Male(table1.getUrogenitalTB0514Male() + 1);
					}
					
					else if (peripheralLymphNodes != null && peripheralLymphNodes) {
						table1.setOtherOrgansTB0514Male(table1.getOtherOrgansTB0514Male() + 1);
						table1.setLymphNodesTB0514Male(table1.getLymphNodesTB0514Male() + 1);
					}
					
					else if (abdominal != null && abdominal) {
						table1.setOtherOrgansTB0514Male(table1.getOtherOrgansTB0514Male() + 1);
						table1.setAbdominalTB0514Male(table1.getAbdominalTB0514Male() + 1);
					}
					
					else if (eye != null && eye) {
						table1.setOtherOrgansTB0514Male(table1.getOtherOrgansTB0514Male() + 1);
						table1.setEyeTB0514Male(table1.getEyeTB0514Male() + 1);
					}
					
					if (resistant != null && resistant) {
						table1.setResistantTB0514Male(table1.getResistantTB0514Male() + 1);
					}
					
					if (hivPositive != null && hivPositive) {
						table1.setTbhivTB0514Male(table1.getTbhivTB0514Male() + 1);
					}
					
					if (rural != null && rural) {
						table1.setRuralTB0514Male(table1.getRuralTB0514Male() + 1);
					}
				}
				
				else if (age >= 15 && age < 18) {
					table1.setActiveTB1517Male(table1.getActiveTB1517Male() + 1);
					
					if ((pulmonary != null && pulmonary) || (plevritis != null && plevritis) || (itLymph != null && itLymph)) {
						table1.setRespiratoryTB1517Male(table1.getRespiratoryTB1517Male() + 1);
					}
					
					if (pulmonary != null && pulmonary) {
						table1.setPulmonaryTB1517Male(table1.getPulmonaryTB1517Male() + 1);
						
						if (MdrtbUtil.isDiagnosticBacPositive(tf)) {
							table1.setBacExTB1517Male(table1.getBacExTB1517Male() + 1);
						}
						
						if (miliary != null && miliary) {
							table1.setMiliaryTB1517Male(table1.getMiliaryTB1517Male() + 1);
						}
					}
					
					if (fibroCav != null && fibroCav) {
						table1.setFibCavTB1517Male(table1.getFibCavTB1517Male() + 1);
					}
					
					else if (cns != null && cns) {
						table1.setNervousSystemTB1517Male(table1.getNervousSystemTB1517Male() + 1);
					}
					
					else if (osteoArticular != null && osteoArticular) {
						table1.setOtherOrgansTB1517Male(table1.getOtherOrgansTB1517Male() + 1);
						table1.setOsteoarticularTB1517Male(table1.getOsteoarticularTB1517Male() + 1);
					}
					
					else if (urogenital != null && urogenital) {
						table1.setOtherOrgansTB1517Male(table1.getOtherOrgansTB1517Male() + 1);
						table1.setUrogenitalTB1517Male(table1.getUrogenitalTB1517Male() + 1);
					}
					
					else if (peripheralLymphNodes != null && peripheralLymphNodes) {
						table1.setOtherOrgansTB1517Male(table1.getOtherOrgansTB1517Male() + 1);
						table1.setLymphNodesTB1517Male(table1.getLymphNodesTB1517Male() + 1);
					}
					
					else if (abdominal != null && abdominal) {
						table1.setOtherOrgansTB1517Male(table1.getOtherOrgansTB1517Male() + 1);
						table1.setAbdominalTB1517Male(table1.getAbdominalTB1517Male() + 1);
					}
					
					else if (eye != null && eye) {
						table1.setOtherOrgansTB1517Male(table1.getOtherOrgansTB1517Male() + 1);
						table1.setEyeTB1517Male(table1.getEyeTB1517Male() + 1);
					}
					
					if (resistant != null && resistant) {
						table1.setResistantTB1517Male(table1.getResistantTB1517Male() + 1);
					}
					
					if (hivPositive != null && hivPositive) {
						table1.setTbhivTB1517Male(table1.getTbhivTB1517Male() + 1);
					}
					
					if (rural != null && rural) {
						table1.setRuralTB1517Male(table1.getRuralTB1517Male() + 1);
					}
				}
				
				else if (age >= 18 && age < 20) {
					table1.setActiveTB1819Male(table1.getActiveTB1819Male() + 1);
					
					if ((pulmonary != null && pulmonary) || (plevritis != null && plevritis) || (itLymph != null && itLymph)) {
						table1.setRespiratoryTB1819Male(table1.getRespiratoryTB1819Male() + 1);
					}
					
					if (pulmonary != null && pulmonary) {
						table1.setPulmonaryTB1819Male(table1.getPulmonaryTB1819Male() + 1);
						
						if (MdrtbUtil.isDiagnosticBacPositive(tf)) {
							table1.setBacExTB1819Male(table1.getBacExTB1819Male() + 1);
						}
						
						if (miliary != null && miliary) {
							table1.setMiliaryTB1819Male(table1.getMiliaryTB1819Male() + 1);
						}
					}
					
					if (fibroCav != null && fibroCav) {
						table1.setFibCavTB1819Male(table1.getFibCavTB1819Male() + 1);
					}
					
					else if (cns != null && cns) {
						table1.setNervousSystemTB1819Male(table1.getNervousSystemTB1819Male() + 1);
					}
					
					else if (osteoArticular != null && osteoArticular) {
						table1.setOtherOrgansTB1819Male(table1.getOtherOrgansTB1819Male() + 1);
						table1.setOsteoarticularTB1819Male(table1.getOsteoarticularTB1819Male() + 1);
					}
					
					else if (urogenital != null && urogenital) {
						table1.setOtherOrgansTB1819Male(table1.getOtherOrgansTB1819Male() + 1);
						table1.setUrogenitalTB1819Male(table1.getUrogenitalTB1819Male() + 1);
					}
					
					else if (peripheralLymphNodes != null && peripheralLymphNodes) {
						table1.setOtherOrgansTB1819Male(table1.getOtherOrgansTB1819Male() + 1);
						table1.setLymphNodesTB1819Male(table1.getLymphNodesTB1819Male() + 1);
					}
					
					else if (abdominal != null && abdominal) {
						table1.setOtherOrgansTB1819Male(table1.getOtherOrgansTB1819Male() + 1);
						table1.setAbdominalTB1819Male(table1.getAbdominalTB1819Male() + 1);
					}
					
					else if (eye != null && eye) {
						table1.setOtherOrgansTB1819Male(table1.getOtherOrgansTB1819Male() + 1);
						table1.setEyeTB1819Male(table1.getEyeTB1819Male() + 1);
					}
					
					if (resistant != null && resistant) {
						table1.setResistantTB1819Male(table1.getResistantTB1819Male() + 1);
					}
					
					if (hivPositive != null && hivPositive) {
						table1.setTbhivTB1819Male(table1.getTbhivTB1819Male() + 1);
					}
					
					if (rural != null && rural) {
						table1.setRuralTB1819Male(table1.getRuralTB1819Male() + 1);
					}
				}
				
				else if (age >= 20 && age < 25) {
					table1.setActiveTB2024Male(table1.getActiveTB2024Male() + 1);
					
					if ((pulmonary != null && pulmonary) || (plevritis != null && plevritis) || (itLymph != null && itLymph)) {
						table1.setRespiratoryTB2024Male(table1.getRespiratoryTB2024Male() + 1);
					}
					
					if (pulmonary != null && pulmonary) {
						table1.setPulmonaryTB2024Male(table1.getPulmonaryTB2024Male() + 1);
						
						if (MdrtbUtil.isDiagnosticBacPositive(tf)) {
							table1.setBacExTB2024Male(table1.getBacExTB2024Male() + 1);
						}
						
						if (miliary != null && miliary) {
							table1.setMiliaryTB2024Male(table1.getMiliaryTB2024Male() + 1);
						}
					}
					
					if (fibroCav != null && fibroCav) {
						table1.setFibCavTB2024Male(table1.getFibCavTB2024Male() + 1);
					}
					
					else if (cns != null && cns) {
						table1.setNervousSystemTB2024Male(table1.getNervousSystemTB2024Male() + 1);
					}
					
					else if (osteoArticular != null && osteoArticular) {
						table1.setOtherOrgansTB2024Male(table1.getOtherOrgansTB2024Male() + 1);
						table1.setOsteoarticularTB2024Male(table1.getOsteoarticularTB2024Male() + 1);
					}
					
					else if (urogenital != null && urogenital) {
						table1.setOtherOrgansTB2024Male(table1.getOtherOrgansTB2024Male() + 1);
						table1.setUrogenitalTB2024Male(table1.getUrogenitalTB2024Male() + 1);
					}
					
					else if (peripheralLymphNodes != null && peripheralLymphNodes) {
						table1.setOtherOrgansTB2024Male(table1.getOtherOrgansTB2024Male() + 1);
						table1.setLymphNodesTB2024Male(table1.getLymphNodesTB2024Male() + 1);
					}
					
					else if (abdominal != null && abdominal) {
						table1.setOtherOrgansTB2024Male(table1.getOtherOrgansTB2024Male() + 1);
						table1.setAbdominalTB2024Male(table1.getAbdominalTB2024Male() + 1);
					}
					
					else if (eye != null && eye) {
						table1.setOtherOrgansTB2024Male(table1.getOtherOrgansTB2024Male() + 1);
						table1.setEyeTB2024Male(table1.getEyeTB2024Male() + 1);
					}
					
					if (resistant != null && resistant) {
						table1.setResistantTB2024Male(table1.getResistantTB2024Male() + 1);
					}
					
					if (hivPositive != null && hivPositive) {
						table1.setTbhivTB2024Male(table1.getTbhivTB2024Male() + 1);
					}
					
					if (rural != null && rural) {
						table1.setRuralTB2024Male(table1.getRuralTB2024Male() + 1);
					}
				}
				
				else if (age >= 25 && age < 35) {
					table1.setActiveTB2534Male(table1.getActiveTB2534Male() + 1);
					
					if ((pulmonary != null && pulmonary) || (plevritis != null && plevritis) || (itLymph != null && itLymph)) {
						table1.setRespiratoryTB2534Male(table1.getRespiratoryTB2534Male() + 1);
					}
					
					if (pulmonary != null && pulmonary) {
						table1.setPulmonaryTB2534Male(table1.getPulmonaryTB2534Male() + 1);
						
						if (MdrtbUtil.isDiagnosticBacPositive(tf)) {
							table1.setBacExTB2534Male(table1.getBacExTB2534Male() + 1);
						}
						
						if (miliary != null && miliary) {
							table1.setMiliaryTB2534Male(table1.getMiliaryTB2534Male() + 1);
						}
					}
					
					if (fibroCav != null && fibroCav) {
						table1.setFibCavTB2534Male(table1.getFibCavTB2534Male() + 1);
					}
					
					else if (cns != null && cns) {
						table1.setNervousSystemTB2534Male(table1.getNervousSystemTB2534Male() + 1);
					}
					
					else if (osteoArticular != null && osteoArticular) {
						table1.setOtherOrgansTB2534Male(table1.getOtherOrgansTB2534Male() + 1);
						table1.setOsteoarticularTB2534Male(table1.getOsteoarticularTB2534Male() + 1);
					}
					
					else if (urogenital != null && urogenital) {
						table1.setOtherOrgansTB2534Male(table1.getOtherOrgansTB2534Male() + 1);
						table1.setUrogenitalTB2534Male(table1.getUrogenitalTB2534Male() + 1);
					}
					
					else if (peripheralLymphNodes != null && peripheralLymphNodes) {
						table1.setOtherOrgansTB2534Male(table1.getOtherOrgansTB2534Male() + 1);
						table1.setLymphNodesTB2534Male(table1.getLymphNodesTB2534Male() + 1);
					}
					
					else if (abdominal != null && abdominal) {
						table1.setOtherOrgansTB2534Male(table1.getOtherOrgansTB2534Male() + 1);
						table1.setAbdominalTB2534Male(table1.getAbdominalTB2534Male() + 1);
					}
					
					else if (eye != null && eye) {
						table1.setOtherOrgansTB2534Male(table1.getOtherOrgansTB2534Male() + 1);
						table1.setEyeTB2534Male(table1.getEyeTB2534Male() + 1);
					}
					
					if (resistant != null && resistant) {
						table1.setResistantTB2534Male(table1.getResistantTB2534Male() + 1);
					}
					
					if (hivPositive != null && hivPositive) {
						table1.setTbhivTB2534Male(table1.getTbhivTB2534Male() + 1);
					}
					
					if (rural != null && rural) {
						table1.setRuralTB2534Male(table1.getRuralTB2534Male() + 1);
					}
				}
				
				else if (age >= 35 && age < 45) {
					table1.setActiveTB3544Male(table1.getActiveTB3544Male() + 1);
					
					if ((pulmonary != null && pulmonary) || (plevritis != null && plevritis) || (itLymph != null && itLymph)) {
						table1.setRespiratoryTB3544Male(table1.getRespiratoryTB3544Male() + 1);
					}
					
					if (pulmonary != null && pulmonary) {
						table1.setPulmonaryTB3544Male(table1.getPulmonaryTB3544Male() + 1);
						
						if (MdrtbUtil.isDiagnosticBacPositive(tf)) {
							table1.setBacExTB3544Male(table1.getBacExTB3544Male() + 1);
						}
						
						if (miliary != null && miliary) {
							table1.setMiliaryTB3544Male(table1.getMiliaryTB3544Male() + 1);
						}
					}
					
					if (fibroCav != null && fibroCav) {
						table1.setFibCavTB3544Male(table1.getFibCavTB3544Male() + 1);
					}
					
					else if (cns != null && cns) {
						table1.setNervousSystemTB3544Male(table1.getNervousSystemTB3544Male() + 1);
					}
					
					else if (osteoArticular != null && osteoArticular) {
						table1.setOtherOrgansTB3544Male(table1.getOtherOrgansTB3544Male() + 1);
						table1.setOsteoarticularTB3544Male(table1.getOsteoarticularTB3544Male() + 1);
					}
					
					else if (urogenital != null && urogenital) {
						table1.setOtherOrgansTB3544Male(table1.getOtherOrgansTB3544Male() + 1);
						table1.setUrogenitalTB3544Male(table1.getUrogenitalTB3544Male() + 1);
					}
					
					else if (peripheralLymphNodes != null && peripheralLymphNodes) {
						table1.setOtherOrgansTB3544Male(table1.getOtherOrgansTB3544Male() + 1);
						table1.setLymphNodesTB3544Male(table1.getLymphNodesTB3544Male() + 1);
					}
					
					else if (abdominal != null && abdominal) {
						table1.setOtherOrgansTB3544Male(table1.getOtherOrgansTB3544Male() + 1);
						table1.setAbdominalTB3544Male(table1.getAbdominalTB3544Male() + 1);
					}
					
					else if (eye != null && eye) {
						table1.setOtherOrgansTB3544Male(table1.getOtherOrgansTB3544Male() + 1);
						table1.setEyeTB3544Male(table1.getEyeTB3544Male() + 1);
					}
					
					if (resistant != null && resistant) {
						table1.setResistantTB3544Male(table1.getResistantTB3544Male() + 1);
					}
					
					if (hivPositive != null && hivPositive) {
						table1.setTbhivTB3544Male(table1.getTbhivTB3544Male() + 1);
					}
					
					if (rural != null && rural) {
						table1.setRuralTB3544Male(table1.getRuralTB3544Male() + 1);
					}
				}
				
				else if (age >= 45 && age < 55) {
					table1.setActiveTB4554Male(table1.getActiveTB4554Male() + 1);
					
					if ((pulmonary != null && pulmonary) || (plevritis != null && plevritis) || (itLymph != null && itLymph)) {
						table1.setRespiratoryTB4554Male(table1.getRespiratoryTB4554Male() + 1);
					}
					
					if (pulmonary != null && pulmonary) {
						table1.setPulmonaryTB4554Male(table1.getPulmonaryTB4554Male() + 1);
						
						if (MdrtbUtil.isDiagnosticBacPositive(tf)) {
							table1.setBacExTB4554Male(table1.getBacExTB4554Male() + 1);
						}
						
						if (miliary != null && miliary) {
							table1.setMiliaryTB4554Male(table1.getMiliaryTB4554Male() + 1);
						}
					}
					
					if (fibroCav != null && fibroCav) {
						table1.setFibCavTB4554Male(table1.getFibCavTB4554Male() + 1);
					}
					
					else if (cns != null && cns) {
						table1.setNervousSystemTB4554Male(table1.getNervousSystemTB4554Male() + 1);
					}
					
					else if (osteoArticular != null && osteoArticular) {
						table1.setOtherOrgansTB4554Male(table1.getOtherOrgansTB4554Male() + 1);
						table1.setOsteoarticularTB4554Male(table1.getOsteoarticularTB4554Male() + 1);
					}
					
					else if (urogenital != null && urogenital) {
						table1.setOtherOrgansTB4554Male(table1.getOtherOrgansTB4554Male() + 1);
						table1.setUrogenitalTB4554Male(table1.getUrogenitalTB4554Male() + 1);
					}
					
					else if (peripheralLymphNodes != null && peripheralLymphNodes) {
						table1.setOtherOrgansTB4554Male(table1.getOtherOrgansTB4554Male() + 1);
						table1.setLymphNodesTB4554Male(table1.getLymphNodesTB4554Male() + 1);
					}
					
					else if (abdominal != null && abdominal) {
						table1.setOtherOrgansTB4554Male(table1.getOtherOrgansTB4554Male() + 1);
						table1.setAbdominalTB4554Male(table1.getAbdominalTB4554Male() + 1);
					}
					
					else if (eye != null && eye) {
						table1.setOtherOrgansTB4554Male(table1.getOtherOrgansTB4554Male() + 1);
						table1.setEyeTB4554Male(table1.getEyeTB4554Male() + 1);
					}
					
					if (resistant != null && resistant) {
						table1.setResistantTB4554Male(table1.getResistantTB4554Male() + 1);
					}
					
					if (hivPositive != null && hivPositive) {
						table1.setTbhivTB4554Male(table1.getTbhivTB4554Male() + 1);
					}
					
					if (rural != null && rural) {
						table1.setRuralTB4554Male(table1.getRuralTB4554Male() + 1);
					}
				}
				
				else if (age >= 55 && age < 65) {
					table1.setActiveTB5564Male(table1.getActiveTB5564Male() + 1);
					
					if ((pulmonary != null && pulmonary) || (plevritis != null && plevritis) || (itLymph != null && itLymph)) {
						table1.setRespiratoryTB5564Male(table1.getRespiratoryTB5564Male() + 1);
					}
					
					if (pulmonary != null && pulmonary) {
						table1.setPulmonaryTB5564Male(table1.getPulmonaryTB5564Male() + 1);
						
						if (MdrtbUtil.isDiagnosticBacPositive(tf)) {
							table1.setBacExTB5564Male(table1.getBacExTB5564Male() + 1);
						}
						
						if (miliary != null && miliary) {
							table1.setMiliaryTB5564Male(table1.getMiliaryTB5564Male() + 1);
						}
					}
					
					if (fibroCav != null && fibroCav) {
						table1.setFibCavTB5564Male(table1.getFibCavTB5564Male() + 1);
					}
					
					else if (cns != null && cns) {
						table1.setNervousSystemTB5564Male(table1.getNervousSystemTB5564Male() + 1);
					}
					
					else if (osteoArticular != null && osteoArticular) {
						table1.setOtherOrgansTB5564Male(table1.getOtherOrgansTB5564Male() + 1);
						table1.setOsteoarticularTB5564Male(table1.getOsteoarticularTB5564Male() + 1);
					}
					
					else if (urogenital != null && urogenital) {
						table1.setOtherOrgansTB5564Male(table1.getOtherOrgansTB5564Male() + 1);
						table1.setUrogenitalTB5564Male(table1.getUrogenitalTB5564Male() + 1);
					}
					
					else if (peripheralLymphNodes != null && peripheralLymphNodes) {
						table1.setOtherOrgansTB5564Male(table1.getOtherOrgansTB5564Male() + 1);
						table1.setLymphNodesTB5564Male(table1.getLymphNodesTB5564Male() + 1);
					}
					
					else if (abdominal != null && abdominal) {
						table1.setOtherOrgansTB5564Male(table1.getOtherOrgansTB5564Male() + 1);
						table1.setAbdominalTB5564Male(table1.getAbdominalTB5564Male() + 1);
					}
					
					else if (eye != null && eye) {
						table1.setOtherOrgansTB5564Male(table1.getOtherOrgansTB5564Male() + 1);
						table1.setEyeTB5564Male(table1.getEyeTB5564Male() + 1);
					}
					
					if (resistant != null && resistant) {
						table1.setResistantTB5564Male(table1.getResistantTB5564Male() + 1);
					}
					
					if (hivPositive != null && hivPositive) {
						table1.setTbhivTB5564Male(table1.getTbhivTB5564Male() + 1);
					}
					
					if (rural != null && rural) {
						table1.setRuralTB5564Male(table1.getRuralTB5564Male() + 1);
					}
				}
				
				else if (age >= 65) {
					table1.setActiveTB65Male(table1.getActiveTB65Male() + 1);
					
					if ((pulmonary != null && pulmonary) || (plevritis != null && plevritis) || (itLymph != null && itLymph)) {
						table1.setRespiratoryTB65Male(table1.getRespiratoryTB65Male() + 1);
					}
					
					if (pulmonary != null && pulmonary) {
						table1.setPulmonaryTB65Male(table1.getPulmonaryTB65Male() + 1);
						
						if (MdrtbUtil.isDiagnosticBacPositive(tf)) {
							table1.setBacExTB65Male(table1.getBacExTB65Male() + 1);
						}
						
						if (miliary != null && miliary) {
							table1.setMiliaryTB65Male(table1.getMiliaryTB65Male() + 1);
						}
					}
					
					if (fibroCav != null && fibroCav) {
						table1.setFibCavTB65Male(table1.getFibCavTB65Male() + 1);
					}
					
					else if (cns != null && cns) {
						table1.setNervousSystemTB65Male(table1.getNervousSystemTB65Male() + 1);
					}
					
					else if (osteoArticular != null && osteoArticular) {
						table1.setOtherOrgansTB65Male(table1.getOtherOrgansTB65Male() + 1);
						table1.setOsteoarticularTB65Male(table1.getOsteoarticularTB65Male() + 1);
					}
					
					else if (urogenital != null && urogenital) {
						table1.setOtherOrgansTB65Male(table1.getOtherOrgansTB65Male() + 1);
						table1.setUrogenitalTB65Male(table1.getUrogenitalTB65Male() + 1);
					}
					
					else if (peripheralLymphNodes != null && peripheralLymphNodes) {
						table1.setOtherOrgansTB65Male(table1.getOtherOrgansTB65Male() + 1);
						table1.setLymphNodesTB65Male(table1.getLymphNodesTB65Male() + 1);
					}
					
					else if (abdominal != null && abdominal) {
						table1.setOtherOrgansTB65Male(table1.getOtherOrgansTB65Male() + 1);
						table1.setAbdominalTB65Male(table1.getAbdominalTB65Male() + 1);
					}
					
					else if (eye != null && eye) {
						table1.setOtherOrgansTB65Male(table1.getOtherOrgansTB65Male() + 1);
						table1.setEyeTB65Male(table1.getEyeTB65Male() + 1);
					}
					
					if (resistant != null && resistant) {
						table1.setResistantTB65Male(table1.getResistantTB65Male() + 1);
					}
					
					if (hivPositive != null && hivPositive) {
						table1.setTbhivTB65Male(table1.getTbhivTB65Male() + 1);
					}
					
					if (rural != null && rural) {
						table1.setRuralTB65Male(table1.getRuralTB65Male() + 1);
					}
				}
				
				if (rural != null && rural) {
					table1.setActiveTBRuralMale(table1.getActiveTBRuralMale() + 1);
					
					if ((pulmonary != null && pulmonary) || (plevritis != null && plevritis) || (itLymph != null && itLymph)) {
						table1.setRespiratoryTBRuralMale(table1.getRespiratoryTBRuralMale() + 1);
					}
					
					if (pulmonary != null && pulmonary) {
						table1.setPulmonaryTBRuralMale(table1.getPulmonaryTBRuralMale() + 1);
						
						if (MdrtbUtil.isDiagnosticBacPositive(tf)) {
							table1.setBacExTBRuralMale(table1.getBacExTBRuralMale() + 1);
						}
						
						if (miliary != null && miliary) {
							table1.setMiliaryTBRuralMale(table1.getMiliaryTBRuralMale() + 1);
						}
					}
					
					if (fibroCav != null && fibroCav) {
						table1.setFibCavTBRuralMale(table1.getFibCavTBRuralMale() + 1);
					}
					
					else if (cns != null && cns) {
						table1.setNervousSystemTBRuralMale(table1.getNervousSystemTBRuralMale() + 1);
					}
					
					else if (osteoArticular != null && osteoArticular) {
						table1.setOtherOrgansTBRuralMale(table1.getOtherOrgansTBRuralMale() + 1);
						table1.setOsteoarticularTBRuralMale(table1.getOsteoarticularTBRuralMale() + 1);
					}
					
					else if (urogenital != null && urogenital) {
						table1.setOtherOrgansTBRuralMale(table1.getOtherOrgansTBRuralMale() + 1);
						table1.setUrogenitalTBRuralMale(table1.getUrogenitalTBRuralMale() + 1);
					}
					
					else if (peripheralLymphNodes != null && peripheralLymphNodes) {
						table1.setOtherOrgansTBRuralMale(table1.getOtherOrgansTBRuralMale() + 1);
						table1.setLymphNodesTBRuralMale(table1.getLymphNodesTBRuralMale() + 1);
					}
					
					else if (abdominal != null && abdominal) {
						table1.setOtherOrgansTBRuralMale(table1.getOtherOrgansTBRuralMale() + 1);
						table1.setAbdominalTBRuralMale(table1.getAbdominalTBRuralMale() + 1);
					}
					
					else if (eye != null && eye) {
						table1.setOtherOrgansTBRuralMale(table1.getOtherOrgansTBRuralMale() + 1);
						table1.setEyeTBRuralMale(table1.getEyeTBRuralMale() + 1);
					}
					
					if (resistant != null && resistant) {
						table1.setResistantTBRuralMale(table1.getResistantTBRuralMale() + 1);
					}
					
					if (hivPositive != null && hivPositive) {
						table1.setTbhivTBRuralMale(table1.getTbhivTBRuralMale() + 1);
					}
					
				}
				
			}
			
			else if (female != null && female) {
				
				if (age >= 15 && age <= 49) {
					table2.setChildbearing(table2.getChildbearing() + 1);
					
					if (pregnant != null && pregnant) {
						table2.setPregnant(table2.getPregnant() + 1);
					}
					
				}
				
				table1.setActiveTBTotalFemale(table1.getActiveTBTotalFemale() + 1);
				
				if (phcFacility != null && phcFacility) {
					table2.setActivePHCTotal(table2.getActivePHCTotal() + 1);
				}
				
				if ((pulmonary != null && pulmonary) || (plevritis != null && plevritis) || (itLymph != null && itLymph)) {
					table1.setRespiratoryTBTotalFemale(table1.getRespiratoryTBTotalFemale() + 1);
					
					if (phcFacility != null && phcFacility) {
						table2.setRespiratoryPHCTotal(table2.getRespiratoryPHCTotal() + 1);
					}
				}
				
				if (pulmonary != null && pulmonary) {
					table1.setPulmonaryTBTotalFemale(table1.getPulmonaryTBTotalFemale() + 1);
					if (phcFacility != null && phcFacility) {
						table2.setPulmonaryPHCTotal(table2.getPulmonaryPHCTotal() + 1);
					}
					
					if (MdrtbUtil.isDiagnosticBacPositive(tf)) {
						table1.setBacExTBTotalFemale(table1.getBacExTBTotalFemale() + 1);
						
						if (phcFacility != null && phcFacility) {
							table2.setBacExPHCTotal(table2.getBacExPHCTotal() + 1);
						}
					}
					
					if (miliary != null && miliary) {
						table1.setMiliaryTBTotalFemale(table1.getMiliaryTBTotalFemale() + 1);
						
						if (phcFacility != null && phcFacility) {
							table2.setMiliaryPHCTotal(table2.getMiliaryPHCTotal() + 1);
						}
					}
				}
				
				if (fibroCav != null && fibroCav) {
					table1.setFibCavTBTotalFemale(table1.getFibCavTBTotalFemale() + 1);
				}
				
				else if (cns != null && cns) {
					table1.setNervousSystemTBTotalFemale(table1.getNervousSystemTBTotalFemale() + 1);
					
					if (phcFacility != null && phcFacility) {
						table2.setNervousSystemPHCTotal(table2.getNervousSystemPHCTotal() + 1);
					}
				}
				
				else if (osteoArticular != null && osteoArticular) {
					table1.setOtherOrgansTBTotalFemale(table1.getOtherOrgansTBTotalFemale() + 1);
					if (phcFacility != null && phcFacility) {
						table2.setOtherOrgansPHCTotal(table2.getOtherOrgansPHCTotal() + 1);
					}
					table1.setOsteoarticularTBTotalFemale(table1.getOsteoarticularTBTotalFemale() + 1);
				}
				
				else if (urogenital != null && urogenital) {
					table1.setOtherOrgansTBTotalFemale(table1.getOtherOrgansTBTotalFemale() + 1);
					if (phcFacility != null && phcFacility) {
						table2.setOtherOrgansPHCTotal(table2.getOtherOrgansPHCTotal() + 1);
					}
					table1.setUrogenitalTBTotalFemale(table1.getUrogenitalTBTotalFemale() + 1);
				}
				
				else if (peripheralLymphNodes != null && peripheralLymphNodes) {
					table1.setOtherOrgansTBTotalFemale(table1.getOtherOrgansTBTotalFemale() + 1);
					if (phcFacility != null && phcFacility) {
						table2.setOtherOrgansPHCTotal(table2.getOtherOrgansPHCTotal() + 1);
					}
					table1.setLymphNodesTBTotalFemale(table1.getLymphNodesTBTotalFemale() + 1);
				}
				
				else if (abdominal != null && abdominal) {
					table1.setOtherOrgansTBTotalFemale(table1.getOtherOrgansTBTotalFemale() + 1);
					if (phcFacility != null && phcFacility) {
						table2.setOtherOrgansPHCTotal(table2.getOtherOrgansPHCTotal() + 1);
					}
					table1.setAbdominalTBTotalFemale(table1.getAbdominalTBTotalFemale() + 1);
				}
				
				else if (eye != null && eye) {
					table1.setOtherOrgansTBTotalFemale(table1.getOtherOrgansTBTotalFemale() + 1);
					if (phcFacility != null && phcFacility) {
						table2.setOtherOrgansPHCTotal(table2.getOtherOrgansPHCTotal() + 1);
					}
					table1.setEyeTBTotalFemale(table1.getEyeTBTotalFemale() + 1);
				}
				
				if (resistant != null && resistant) {
					table1.setResistantTBTotalFemale(table1.getResistantTBTotalFemale() + 1);
				}
				
				if (hivPositive != null && hivPositive) {
					table1.setTbhivTBTotalFemale(table1.getTbhivTBTotalFemale() + 1);
				}
				
				if (rural != null && rural) {
					table1.setRuralTBTotalFemale(table1.getRuralTBTotalFemale() + 1);
				}
				
				if (age >= 0 && age < 5) {
					table1.setActiveTB04Female(table1.getActiveTB04Female() + 1);
					if ((pulmonary != null && pulmonary) || (plevritis != null && plevritis) || (itLymph != null && itLymph)) {
						table1.setRespiratoryTB04Female(table1.getRespiratoryTB04Female() + 1);
					}
					
					if (pulmonary != null && pulmonary) {
						table1.setPulmonaryTB04Female(table1.getPulmonaryTB04Female() + 1);
						
						if (MdrtbUtil.isDiagnosticBacPositive(tf)) {
							table1.setBacExTB04Female(table1.getBacExTB04Female() + 1);
						}
						
						if (miliary != null && miliary) {
							table1.setMiliaryTB04Female(table1.getMiliaryTB04Female() + 1);
						}
					}
					
					if (fibroCav != null && fibroCav) {
						table1.setFibCavTB04Female(table1.getFibCavTB04Female() + 1);
					}
					
					else if (cns != null && cns) {
						table1.setNervousSystemTB04Female(table1.getNervousSystemTB04Female() + 1);
					}
					
					else if (osteoArticular != null && osteoArticular) {
						table1.setOtherOrgansTB04Female(table1.getOtherOrgansTB04Female() + 1);
						table1.setOsteoarticularTB04Female(table1.getOsteoarticularTB04Female() + 1);
					}
					
					else if (urogenital != null && urogenital) {
						table1.setOtherOrgansTB04Female(table1.getOtherOrgansTB04Female() + 1);
						table1.setUrogenitalTB04Female(table1.getUrogenitalTB04Female() + 1);
					}
					
					else if (peripheralLymphNodes != null && peripheralLymphNodes) {
						table1.setOtherOrgansTB04Female(table1.getOtherOrgansTB04Female() + 1);
						table1.setLymphNodesTB04Female(table1.getLymphNodesTB04Female() + 1);
					}
					
					else if (abdominal != null && abdominal) {
						table1.setOtherOrgansTB04Female(table1.getOtherOrgansTB04Female() + 1);
						table1.setAbdominalTB04Female(table1.getAbdominalTB04Female() + 1);
					}
					
					else if (eye != null && eye) {
						table1.setOtherOrgansTB04Female(table1.getOtherOrgansTB04Female() + 1);
						table1.setEyeTB04Female(table1.getEyeTB04Female() + 1);
					}
					
					if (resistant != null && resistant) {
						table1.setResistantTB04Female(table1.getResistantTB04Female() + 1);
					}
					
					if (hivPositive != null && hivPositive) {
						table1.setTbhivTB04Female(table1.getTbhivTB04Female() + 1);
					}
					
					if (rural != null && rural) {
						table1.setRuralTB04Female(table1.getRuralTB04Female() + 1);
					}
				}
				
				else if (age >= 5 && age < 15) {
					table1.setActiveTB0514Female(table1.getActiveTB0514Female() + 1);
					
					if ((pulmonary != null && pulmonary) || (plevritis != null && plevritis) || (itLymph != null && itLymph)) {
						table1.setRespiratoryTB0514Female(table1.getRespiratoryTB0514Female() + 1);
					}
					
					if (pulmonary != null && pulmonary) {
						table1.setPulmonaryTB0514Female(table1.getPulmonaryTB0514Female() + 1);
						
						if (MdrtbUtil.isDiagnosticBacPositive(tf)) {
							table1.setBacExTB0514Female(table1.getBacExTB0514Female() + 1);
						}
						
						if (miliary != null && miliary) {
							table1.setMiliaryTB0514Female(table1.getMiliaryTB0514Female() + 1);
						}
					}
					
					if (fibroCav != null && fibroCav) {
						table1.setFibCavTB0514Female(table1.getFibCavTB0514Female() + 1);
					}
					
					else if (cns != null && cns) {
						table1.setNervousSystemTB0514Female(table1.getNervousSystemTB0514Female() + 1);
					}
					
					else if (osteoArticular != null && osteoArticular) {
						table1.setOtherOrgansTB0514Female(table1.getOtherOrgansTB0514Female() + 1);
						table1.setOsteoarticularTB0514Female(table1.getOsteoarticularTB0514Female() + 1);
					}
					
					else if (urogenital != null && urogenital) {
						table1.setOtherOrgansTB0514Female(table1.getOtherOrgansTB0514Female() + 1);
						table1.setUrogenitalTB0514Female(table1.getUrogenitalTB0514Female() + 1);
					}
					
					else if (peripheralLymphNodes != null && peripheralLymphNodes) {
						table1.setOtherOrgansTB0514Female(table1.getOtherOrgansTB0514Female() + 1);
						table1.setLymphNodesTB0514Female(table1.getLymphNodesTB0514Female() + 1);
					}
					
					else if (abdominal != null && abdominal) {
						table1.setOtherOrgansTB0514Female(table1.getOtherOrgansTB0514Female() + 1);
						table1.setAbdominalTB0514Female(table1.getAbdominalTB0514Female() + 1);
					}
					
					else if (eye != null && eye) {
						table1.setOtherOrgansTB0514Female(table1.getOtherOrgansTB0514Female() + 1);
						table1.setEyeTB0514Female(table1.getEyeTB0514Female() + 1);
					}
					
					if (resistant != null && resistant) {
						table1.setResistantTB0514Female(table1.getResistantTB0514Female() + 1);
					}
					
					if (hivPositive != null && hivPositive) {
						table1.setTbhivTB0514Female(table1.getTbhivTB0514Female() + 1);
					}
					
					if (rural != null && rural) {
						table1.setRuralTB0514Female(table1.getRuralTB0514Female() + 1);
					}
				}
				
				else if (age >= 15 && age < 18) {
					table1.setActiveTB1517Female(table1.getActiveTB1517Female() + 1);
					
					if ((pulmonary != null && pulmonary) || (plevritis != null && plevritis) || (itLymph != null && itLymph)) {
						table1.setRespiratoryTB1517Female(table1.getRespiratoryTB1517Female() + 1);
					}
					
					if (pulmonary != null && pulmonary) {
						table1.setPulmonaryTB1517Female(table1.getPulmonaryTB1517Female() + 1);
						
						if (MdrtbUtil.isDiagnosticBacPositive(tf)) {
							table1.setBacExTB1517Female(table1.getBacExTB1517Female() + 1);
						}
						
						if (miliary != null && miliary) {
							table1.setMiliaryTB1517Female(table1.getMiliaryTB1517Female() + 1);
						}
					}
					
					if (fibroCav != null && fibroCav) {
						table1.setFibCavTB1517Female(table1.getFibCavTB1517Female() + 1);
					}
					
					else if (cns != null && cns) {
						table1.setNervousSystemTB1517Female(table1.getNervousSystemTB1517Female() + 1);
					}
					
					else if (osteoArticular != null && osteoArticular) {
						table1.setOtherOrgansTB1517Female(table1.getOtherOrgansTB1517Female() + 1);
						table1.setOsteoarticularTB1517Female(table1.getOsteoarticularTB1517Female() + 1);
					}
					
					else if (urogenital != null && urogenital) {
						table1.setOtherOrgansTB1517Female(table1.getOtherOrgansTB1517Female() + 1);
						table1.setUrogenitalTB1517Female(table1.getUrogenitalTB1517Female() + 1);
					}
					
					else if (peripheralLymphNodes != null && peripheralLymphNodes) {
						table1.setOtherOrgansTB1517Female(table1.getOtherOrgansTB1517Female() + 1);
						table1.setLymphNodesTB1517Female(table1.getLymphNodesTB1517Female() + 1);
					}
					
					else if (abdominal != null && abdominal) {
						table1.setOtherOrgansTB1517Female(table1.getOtherOrgansTB1517Female() + 1);
						table1.setAbdominalTB1517Female(table1.getAbdominalTB1517Female() + 1);
					}
					
					else if (eye != null && eye) {
						table1.setOtherOrgansTB1517Female(table1.getOtherOrgansTB1517Female() + 1);
						table1.setEyeTB1517Female(table1.getEyeTB1517Female() + 1);
					}
					
					if (resistant != null && resistant) {
						table1.setResistantTB1517Female(table1.getResistantTB1517Female() + 1);
					}
					
					if (hivPositive != null && hivPositive) {
						table1.setTbhivTB1517Female(table1.getTbhivTB1517Female() + 1);
					}
					
					if (rural != null && rural) {
						table1.setRuralTB1517Female(table1.getRuralTB1517Female() + 1);
					}
				}
				
				else if (age >= 18 && age < 20) {
					table1.setActiveTB1819Female(table1.getActiveTB1819Female() + 1);
					
					if ((pulmonary != null && pulmonary) || (plevritis != null && plevritis) || (itLymph != null && itLymph)) {
						table1.setRespiratoryTB1819Female(table1.getRespiratoryTB1819Female() + 1);
					}
					
					if (pulmonary != null && pulmonary) {
						table1.setPulmonaryTB1819Female(table1.getPulmonaryTB1819Female() + 1);
						
						if (MdrtbUtil.isDiagnosticBacPositive(tf)) {
							table1.setBacExTB1819Female(table1.getBacExTB1819Female() + 1);
						}
						
						if (miliary != null && miliary) {
							table1.setMiliaryTB1819Female(table1.getMiliaryTB1819Female() + 1);
						}
					}
					
					if (fibroCav != null && fibroCav) {
						table1.setFibCavTB1819Female(table1.getFibCavTB1819Female() + 1);
					}
					
					else if (cns != null && cns) {
						table1.setNervousSystemTB1819Female(table1.getNervousSystemTB1819Female() + 1);
					}
					
					else if (osteoArticular != null && osteoArticular) {
						table1.setOtherOrgansTB1819Female(table1.getOtherOrgansTB1819Female() + 1);
						table1.setOsteoarticularTB1819Female(table1.getOsteoarticularTB1819Female() + 1);
					}
					
					else if (urogenital != null && urogenital) {
						table1.setOtherOrgansTB1819Female(table1.getOtherOrgansTB1819Female() + 1);
						table1.setUrogenitalTB1819Female(table1.getUrogenitalTB1819Female() + 1);
					}
					
					else if (peripheralLymphNodes != null && peripheralLymphNodes) {
						table1.setOtherOrgansTB1819Female(table1.getOtherOrgansTB1819Female() + 1);
						table1.setLymphNodesTB1819Female(table1.getLymphNodesTB1819Female() + 1);
					}
					
					else if (abdominal != null && abdominal) {
						table1.setOtherOrgansTB1819Female(table1.getOtherOrgansTB1819Female() + 1);
						table1.setAbdominalTB1819Female(table1.getAbdominalTB1819Female() + 1);
					}
					
					else if (eye != null && eye) {
						table1.setOtherOrgansTB1819Female(table1.getOtherOrgansTB1819Female() + 1);
						table1.setEyeTB1819Female(table1.getEyeTB1819Female() + 1);
					}
					
					if (resistant != null && resistant) {
						table1.setResistantTB1819Female(table1.getResistantTB1819Female() + 1);
					}
					
					if (hivPositive != null && hivPositive) {
						table1.setTbhivTB1819Female(table1.getTbhivTB1819Female() + 1);
					}
					
					if (rural != null && rural) {
						table1.setRuralTB1819Female(table1.getRuralTB1819Female() + 1);
					}
				}
				
				else if (age >= 20 && age < 25) {
					table1.setActiveTB2024Female(table1.getActiveTB2024Female() + 1);
					
					if ((pulmonary != null && pulmonary) || (plevritis != null && plevritis) || (itLymph != null && itLymph)) {
						table1.setRespiratoryTB2024Female(table1.getRespiratoryTB2024Female() + 1);
					}
					
					if (pulmonary != null && pulmonary) {
						table1.setPulmonaryTB2024Female(table1.getPulmonaryTB2024Female() + 1);
						
						if (MdrtbUtil.isDiagnosticBacPositive(tf)) {
							table1.setBacExTB2024Female(table1.getBacExTB2024Female() + 1);
						}
						
						if (miliary != null && miliary) {
							table1.setMiliaryTB2024Female(table1.getMiliaryTB2024Female() + 1);
						}
					}
					
					if (fibroCav != null && fibroCav) {
						table1.setFibCavTB2024Female(table1.getFibCavTB2024Female() + 1);
					}
					
					else if (cns != null && cns) {
						table1.setNervousSystemTB2024Female(table1.getNervousSystemTB2024Female() + 1);
					}
					
					else if (osteoArticular != null && osteoArticular) {
						table1.setOtherOrgansTB2024Female(table1.getOtherOrgansTB2024Female() + 1);
						table1.setOsteoarticularTB2024Female(table1.getOsteoarticularTB2024Female() + 1);
					}
					
					else if (urogenital != null && urogenital) {
						table1.setOtherOrgansTB2024Female(table1.getOtherOrgansTB2024Female() + 1);
						table1.setUrogenitalTB2024Female(table1.getUrogenitalTB2024Female() + 1);
					}
					
					else if (peripheralLymphNodes != null && peripheralLymphNodes) {
						table1.setOtherOrgansTB2024Female(table1.getOtherOrgansTB2024Female() + 1);
						table1.setLymphNodesTB2024Female(table1.getLymphNodesTB2024Female() + 1);
					}
					
					else if (abdominal != null && abdominal) {
						table1.setOtherOrgansTB2024Female(table1.getOtherOrgansTB2024Female() + 1);
						table1.setAbdominalTB2024Female(table1.getAbdominalTB2024Female() + 1);
					}
					
					else if (eye != null && eye) {
						table1.setOtherOrgansTB2024Female(table1.getOtherOrgansTB2024Female() + 1);
						table1.setEyeTB2024Female(table1.getEyeTB2024Female() + 1);
					}
					
					if (resistant != null && resistant) {
						table1.setResistantTB2024Female(table1.getResistantTB2024Female() + 1);
					}
					
					if (hivPositive != null && hivPositive) {
						table1.setTbhivTB2024Female(table1.getTbhivTB2024Female() + 1);
					}
					
					if (rural != null && rural) {
						table1.setRuralTB2024Female(table1.getRuralTB2024Female() + 1);
					}
				}
				
				else if (age >= 25 && age < 35) {
					table1.setActiveTB2534Female(table1.getActiveTB2534Female() + 1);
					
					if ((pulmonary != null && pulmonary) || (plevritis != null && plevritis) || (itLymph != null && itLymph)) {
						table1.setRespiratoryTB2534Female(table1.getRespiratoryTB2534Female() + 1);
					}
					
					if (pulmonary != null && pulmonary) {
						table1.setPulmonaryTB2534Female(table1.getPulmonaryTB2534Female() + 1);
						
						if (MdrtbUtil.isDiagnosticBacPositive(tf)) {
							table1.setBacExTB2534Female(table1.getBacExTB2534Female() + 1);
						}
						
						if (miliary != null && miliary) {
							table1.setMiliaryTB2534Female(table1.getMiliaryTB2534Female() + 1);
						}
					}
					
					if (fibroCav != null && fibroCav) {
						table1.setFibCavTB2534Female(table1.getFibCavTB2534Female() + 1);
					}
					
					else if (cns != null && cns) {
						table1.setNervousSystemTB2534Female(table1.getNervousSystemTB2534Female() + 1);
					}
					
					else if (osteoArticular != null && osteoArticular) {
						table1.setOtherOrgansTB2534Female(table1.getOtherOrgansTB2534Female() + 1);
						table1.setOsteoarticularTB2534Female(table1.getOsteoarticularTB2534Female() + 1);
					}
					
					else if (urogenital != null && urogenital) {
						table1.setOtherOrgansTB2534Female(table1.getOtherOrgansTB2534Female() + 1);
						table1.setUrogenitalTB2534Female(table1.getUrogenitalTB2534Female() + 1);
					}
					
					else if (peripheralLymphNodes != null && peripheralLymphNodes) {
						table1.setOtherOrgansTB2534Female(table1.getOtherOrgansTB2534Female() + 1);
						table1.setLymphNodesTB2534Female(table1.getLymphNodesTB2534Female() + 1);
					}
					
					else if (abdominal != null && abdominal) {
						table1.setOtherOrgansTB2534Female(table1.getOtherOrgansTB2534Female() + 1);
						table1.setAbdominalTB2534Female(table1.getAbdominalTB2534Female() + 1);
					}
					
					else if (eye != null && eye) {
						table1.setOtherOrgansTB2534Female(table1.getOtherOrgansTB2534Female() + 1);
						table1.setEyeTB2534Female(table1.getEyeTB2534Female() + 1);
					}
					
					if (resistant != null && resistant) {
						table1.setResistantTB2534Female(table1.getResistantTB2534Female() + 1);
					}
					
					if (hivPositive != null && hivPositive) {
						table1.setTbhivTB2534Female(table1.getTbhivTB2534Female() + 1);
					}
					
					if (rural != null && rural) {
						table1.setRuralTB2534Female(table1.getRuralTB2534Female() + 1);
					}
				}
				
				else if (age >= 35 && age < 45) {
					table1.setActiveTB3544Female(table1.getActiveTB3544Female() + 1);
					
					if ((pulmonary != null && pulmonary) || (plevritis != null && plevritis) || (itLymph != null && itLymph)) {
						table1.setRespiratoryTB3544Female(table1.getRespiratoryTB3544Female() + 1);
					}
					
					if (pulmonary != null && pulmonary) {
						table1.setPulmonaryTB3544Female(table1.getPulmonaryTB3544Female() + 1);
						
						if (MdrtbUtil.isDiagnosticBacPositive(tf)) {
							table1.setBacExTB3544Female(table1.getBacExTB3544Female() + 1);
						}
						
						if (miliary != null && miliary) {
							table1.setMiliaryTB3544Female(table1.getMiliaryTB3544Female() + 1);
						}
					}
					
					if (fibroCav != null && fibroCav) {
						table1.setFibCavTB3544Female(table1.getFibCavTB3544Female() + 1);
					}
					
					else if (cns != null && cns) {
						table1.setNervousSystemTB3544Female(table1.getNervousSystemTB3544Female() + 1);
					}
					
					else if (osteoArticular != null && osteoArticular) {
						table1.setOtherOrgansTB3544Female(table1.getOtherOrgansTB3544Female() + 1);
						table1.setOsteoarticularTB3544Female(table1.getOsteoarticularTB3544Female() + 1);
					}
					
					else if (urogenital != null && urogenital) {
						table1.setOtherOrgansTB3544Female(table1.getOtherOrgansTB3544Female() + 1);
						table1.setUrogenitalTB3544Female(table1.getUrogenitalTB3544Female() + 1);
					}
					
					else if (peripheralLymphNodes != null && peripheralLymphNodes) {
						table1.setOtherOrgansTB3544Female(table1.getOtherOrgansTB3544Female() + 1);
						table1.setLymphNodesTB3544Female(table1.getLymphNodesTB3544Female() + 1);
					}
					
					else if (abdominal != null && abdominal) {
						table1.setOtherOrgansTB3544Female(table1.getOtherOrgansTB3544Female() + 1);
						table1.setAbdominalTB3544Female(table1.getAbdominalTB3544Female() + 1);
					}
					
					else if (eye != null && eye) {
						table1.setOtherOrgansTB3544Female(table1.getOtherOrgansTB3544Female() + 1);
						table1.setEyeTB3544Female(table1.getEyeTB3544Female() + 1);
					}
					
					if (resistant != null && resistant) {
						table1.setResistantTB3544Female(table1.getResistantTB3544Female() + 1);
					}
					
					if (hivPositive != null && hivPositive) {
						table1.setTbhivTB3544Female(table1.getTbhivTB3544Female() + 1);
					}
					
					if (rural != null && rural) {
						table1.setRuralTB3544Female(table1.getRuralTB3544Female() + 1);
					}
				}
				
				else if (age >= 45 && age < 55) {
					table1.setActiveTB4554Female(table1.getActiveTB4554Female() + 1);
					
					if ((pulmonary != null && pulmonary) || (plevritis != null && plevritis) || (itLymph != null && itLymph)) {
						table1.setRespiratoryTB4554Female(table1.getRespiratoryTB4554Female() + 1);
					}
					
					if (pulmonary != null && pulmonary) {
						table1.setPulmonaryTB4554Female(table1.getPulmonaryTB4554Female() + 1);
						
						if (MdrtbUtil.isDiagnosticBacPositive(tf)) {
							table1.setBacExTB4554Female(table1.getBacExTB4554Female() + 1);
						}
						
						if (miliary != null && miliary) {
							table1.setMiliaryTB4554Female(table1.getMiliaryTB4554Female() + 1);
						}
					}
					
					if (fibroCav != null && fibroCav) {
						table1.setFibCavTB4554Female(table1.getFibCavTB4554Female() + 1);
					}
					
					else if (cns != null && cns) {
						table1.setNervousSystemTB4554Female(table1.getNervousSystemTB4554Female() + 1);
					}
					
					else if (osteoArticular != null && osteoArticular) {
						table1.setOtherOrgansTB4554Female(table1.getOtherOrgansTB4554Female() + 1);
						table1.setOsteoarticularTB4554Female(table1.getOsteoarticularTB4554Female() + 1);
					}
					
					else if (urogenital != null && urogenital) {
						table1.setOtherOrgansTB4554Female(table1.getOtherOrgansTB4554Female() + 1);
						table1.setUrogenitalTB4554Female(table1.getUrogenitalTB4554Female() + 1);
					}
					
					else if (peripheralLymphNodes != null && peripheralLymphNodes) {
						table1.setOtherOrgansTB4554Female(table1.getOtherOrgansTB4554Female() + 1);
						table1.setLymphNodesTB4554Female(table1.getLymphNodesTB4554Female() + 1);
					}
					
					else if (abdominal != null && abdominal) {
						table1.setOtherOrgansTB4554Female(table1.getOtherOrgansTB4554Female() + 1);
						table1.setAbdominalTB4554Female(table1.getAbdominalTB4554Female() + 1);
					}
					
					else if (eye != null && eye) {
						table1.setOtherOrgansTB4554Female(table1.getOtherOrgansTB4554Female() + 1);
						table1.setEyeTB4554Female(table1.getEyeTB4554Female() + 1);
					}
					
					if (resistant != null && resistant) {
						table1.setResistantTB4554Female(table1.getResistantTB4554Female() + 1);
					}
					
					if (hivPositive != null && hivPositive) {
						table1.setTbhivTB4554Female(table1.getTbhivTB4554Female() + 1);
					}
					
					if (rural != null && rural) {
						table1.setRuralTB4554Female(table1.getRuralTB4554Female() + 1);
					}
				}
				
				else if (age >= 55 && age < 65) {
					table1.setActiveTB5564Female(table1.getActiveTB5564Female() + 1);
					
					if ((pulmonary != null && pulmonary) || (plevritis != null && plevritis) || (itLymph != null && itLymph)) {
						table1.setRespiratoryTB5564Female(table1.getRespiratoryTB5564Female() + 1);
					}
					
					if (pulmonary != null && pulmonary) {
						table1.setPulmonaryTB5564Female(table1.getPulmonaryTB5564Female() + 1);
						
						if (MdrtbUtil.isDiagnosticBacPositive(tf)) {
							table1.setBacExTB5564Female(table1.getBacExTB5564Female() + 1);
						}
						
						if (miliary != null && miliary) {
							table1.setMiliaryTB5564Female(table1.getMiliaryTB5564Female() + 1);
						}
					}
					
					if (fibroCav != null && fibroCav) {
						table1.setFibCavTB5564Female(table1.getFibCavTB5564Female() + 1);
					}
					
					else if (cns != null && cns) {
						table1.setNervousSystemTB5564Female(table1.getNervousSystemTB5564Female() + 1);
					}
					
					else if (osteoArticular != null && osteoArticular) {
						table1.setOtherOrgansTB5564Female(table1.getOtherOrgansTB5564Female() + 1);
						table1.setOsteoarticularTB5564Female(table1.getOsteoarticularTB5564Female() + 1);
					}
					
					else if (urogenital != null && urogenital) {
						table1.setOtherOrgansTB5564Female(table1.getOtherOrgansTB5564Female() + 1);
						table1.setUrogenitalTB5564Female(table1.getUrogenitalTB5564Female() + 1);
					}
					
					else if (peripheralLymphNodes != null && peripheralLymphNodes) {
						table1.setOtherOrgansTB5564Female(table1.getOtherOrgansTB5564Female() + 1);
						table1.setLymphNodesTB5564Female(table1.getLymphNodesTB5564Female() + 1);
					}
					
					else if (abdominal != null && abdominal) {
						table1.setOtherOrgansTB5564Female(table1.getOtherOrgansTB5564Female() + 1);
						table1.setAbdominalTB5564Female(table1.getAbdominalTB5564Female() + 1);
					}
					
					else if (eye != null && eye) {
						table1.setOtherOrgansTB5564Female(table1.getOtherOrgansTB5564Female() + 1);
						table1.setEyeTB5564Female(table1.getEyeTB5564Female() + 1);
					}
					
					if (resistant != null && resistant) {
						table1.setResistantTB5564Female(table1.getResistantTB5564Female() + 1);
					}
					
					if (hivPositive != null && hivPositive) {
						table1.setTbhivTB5564Female(table1.getTbhivTB5564Female() + 1);
					}
					
					if (rural != null && rural) {
						table1.setRuralTB5564Female(table1.getRuralTB5564Female() + 1);
					}
				}
				
				else if (age >= 65) {
					table1.setActiveTB65Female(table1.getActiveTB65Female() + 1);
					
					if ((pulmonary != null && pulmonary) || (plevritis != null && plevritis) || (itLymph != null && itLymph)) {
						table1.setRespiratoryTB65Female(table1.getRespiratoryTB65Female() + 1);
					}
					
					if (pulmonary != null && pulmonary) {
						table1.setPulmonaryTB65Female(table1.getPulmonaryTB65Female() + 1);
						
						if (MdrtbUtil.isDiagnosticBacPositive(tf)) {
							table1.setBacExTB65Female(table1.getBacExTB65Female() + 1);
						}
						
						if (miliary != null && miliary) {
							table1.setMiliaryTB65Female(table1.getMiliaryTB65Female() + 1);
						}
					}
					
					if (fibroCav != null && fibroCav) {
						table1.setFibCavTB65Female(table1.getFibCavTB65Female() + 1);
					}
					
					else if (cns != null && cns) {
						table1.setNervousSystemTB65Female(table1.getNervousSystemTB65Female() + 1);
					}
					
					else if (osteoArticular != null && osteoArticular) {
						table1.setOtherOrgansTB65Female(table1.getOtherOrgansTB65Female() + 1);
						table1.setOsteoarticularTB65Female(table1.getOsteoarticularTB65Female() + 1);
					}
					
					else if (urogenital != null && urogenital) {
						table1.setOtherOrgansTB65Female(table1.getOtherOrgansTB65Female() + 1);
						table1.setUrogenitalTB65Female(table1.getUrogenitalTB65Female() + 1);
					}
					
					else if (peripheralLymphNodes != null && peripheralLymphNodes) {
						table1.setOtherOrgansTB65Female(table1.getOtherOrgansTB65Female() + 1);
						table1.setLymphNodesTB65Female(table1.getLymphNodesTB65Female() + 1);
					}
					
					else if (abdominal != null && abdominal) {
						table1.setOtherOrgansTB65Female(table1.getOtherOrgansTB65Female() + 1);
						table1.setAbdominalTB65Female(table1.getAbdominalTB65Female() + 1);
					}
					
					else if (eye != null && eye) {
						table1.setOtherOrgansTB65Female(table1.getOtherOrgansTB65Female() + 1);
						table1.setEyeTB65Female(table1.getEyeTB65Female() + 1);
					}
					
					if (resistant != null && resistant) {
						table1.setResistantTB65Female(table1.getResistantTB65Female() + 1);
					}
					
					if (hivPositive != null && hivPositive) {
						table1.setTbhivTB65Female(table1.getTbhivTB65Female() + 1);
					}
					
					if (rural != null && rural) {
						table1.setRuralTB65Female(table1.getRuralTB65Female() + 1);
					}
				}
				
				if (rural != null && rural) {
					table1.setActiveTBRuralFemale(table1.getActiveTBRuralFemale() + 1);
					
					if ((pulmonary != null && pulmonary) || (plevritis != null && plevritis) || (itLymph != null && itLymph)) {
						table1.setRespiratoryTBRuralFemale(table1.getRespiratoryTBRuralFemale() + 1);
					}
					
					if (pulmonary != null && pulmonary) {
						table1.setPulmonaryTBRuralFemale(table1.getPulmonaryTBRuralFemale() + 1);
						
						if (MdrtbUtil.isDiagnosticBacPositive(tf)) {
							table1.setBacExTBRuralFemale(table1.getBacExTBRuralFemale() + 1);
						}
						
						if (miliary != null && miliary) {
							table1.setMiliaryTBRuralFemale(table1.getMiliaryTBRuralFemale() + 1);
						}
					}
					
					if (fibroCav != null && fibroCav) {
						table1.setFibCavTBRuralFemale(table1.getFibCavTBRuralFemale() + 1);
					}
					
					else if (cns != null && cns) {
						table1.setNervousSystemTBRuralFemale(table1.getNervousSystemTBRuralFemale() + 1);
					}
					
					else if (osteoArticular != null && osteoArticular) {
						table1.setOtherOrgansTBRuralFemale(table1.getOtherOrgansTBRuralFemale() + 1);
						table1.setOsteoarticularTBRuralFemale(table1.getOsteoarticularTBRuralFemale() + 1);
					}
					
					else if (urogenital != null && urogenital) {
						table1.setOtherOrgansTBRuralFemale(table1.getOtherOrgansTBRuralFemale() + 1);
						table1.setUrogenitalTBRuralFemale(table1.getUrogenitalTBRuralFemale() + 1);
					}
					
					else if (peripheralLymphNodes != null && peripheralLymphNodes) {
						table1.setOtherOrgansTBRuralFemale(table1.getOtherOrgansTBRuralFemale() + 1);
						table1.setLymphNodesTBRuralFemale(table1.getLymphNodesTBRuralFemale() + 1);
					}
					
					else if (abdominal != null && abdominal) {
						table1.setOtherOrgansTBRuralFemale(table1.getOtherOrgansTBRuralFemale() + 1);
						table1.setAbdominalTBRuralFemale(table1.getAbdominalTBRuralFemale() + 1);
					}
					
					else if (eye != null && eye) {
						table1.setOtherOrgansTBRuralFemale(table1.getOtherOrgansTBRuralFemale() + 1);
						table1.setEyeTBRuralFemale(table1.getEyeTBRuralFemale() + 1);
					}
					
					if (resistant != null && resistant) {
						table1.setResistantTBRuralFemale(table1.getResistantTBRuralFemale() + 1);
					}
					
					if (hivPositive != null && hivPositive) {
						table1.setTbhivTBRuralFemale(table1.getTbhivTBRuralFemale() + 1);
					}
				}
			}
		}
		
		//Table 6
		tb03List = Context.getService(MdrtbService.class).getTB03FormsFilled(locList, year - 1, quarter, month, month);
		
		for (TB03Form tf : tb03List) {//for (Integer i : idSet) {
		
			ageAtRegistration = -1;
			pulmonary = null;
			bacPositive = null;
			
			cured = null;
			txCompleted = null;
			diedTB = null;
			diedNotTB = null;
			failed = null;
			defaulted = null;
			transferOut = null;
			canceled = null;
			sld = null;
			
			Patient patient = tf.getPatient();
			if (patient == null || patient.getVoided()) {
				continue;
				
			}
			
			ageAtRegistration = tf.getAgeAtTB03Registration();
			
			//get disease site
			Concept q = tf.getAnatomicalSite();
			
			if (q != null) {
				if (q.getConceptId().intValue() == pulmonaryConcept.getConceptId().intValue()) {
					pulmonary = Boolean.TRUE;
				}
				
				else if (q.getConceptId().intValue() == extrapulmonaryConcept.getConceptId().intValue()) {
					pulmonary = Boolean.FALSE;
				}
				
				else {
					pulmonary = null;
				}
				
			}
			
			else {
				continue;
			}
			
			bacPositive = MdrtbUtil.isDiagnosticBacPositive(tf);
			
			//OUTCOMES
			q = tf.getTreatmentOutcome();
			
			if (q != null) {
				
				if (q.getId().equals(
				    Integer.parseInt(Context.getAdministrationService().getGlobalProperty(
				        MdrtbConstants.GP_OUTCOME_CURED_CONCEPT_ID)))) {
					cured = Boolean.TRUE;
				}
				
				else if (q.getId().equals(
				    Integer.parseInt(Context.getAdministrationService().getGlobalProperty(
				        MdrtbConstants.GP_OUTCOME_TX_COMPLETED_CONCEPT_ID)))) {
					txCompleted = Boolean.TRUE;
				}
				
				else if (q.getId().equals(
				    Integer.parseInt(Context.getAdministrationService().getGlobalProperty(
				        MdrtbConstants.GP_OUTCOME_TX_FAILURE_CONCEPT_ID)))) {
					failed = Boolean.TRUE;
				}
				
				else if (q.getId().equals(
				    Integer.parseInt(Context.getAdministrationService().getGlobalProperty(
				        MdrtbConstants.GP_OUTCOME_DIED_CONCEPT_ID)))) {
					q = tf.getCauseOfDeath();//Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAUSE_OF_DEATH);
					
					if (q != null) {
						if (q.getId().intValue() == Context.getService(MdrtbService.class)
						        .getConcept(MdrtbConcepts.DEATH_BY_TB).getConceptId().intValue())
							diedTB = Boolean.TRUE;
						else
							diedNotTB = Boolean.TRUE;
					}
				}
				
				else if (q.getId().equals(
				    Integer.parseInt(Context.getAdministrationService().getGlobalProperty(
				        MdrtbConstants.GP_OUTCOME_LTFU_CONCEPT_ID)))) {
					defaulted = Boolean.TRUE;
				} else if (q.getId().equals(
				    Integer.parseInt(Context.getAdministrationService().getGlobalProperty(
				        MdrtbConstants.GP_OUTCOME_CANCELED_CONCEPT_ID)))) {
					canceled = Boolean.TRUE;
				}
				
				else if (q.getId().equals(
				    Integer.parseInt(Context.getAdministrationService().getGlobalProperty(
				        MdrtbConstants.GP_OUTCOME_TRANSFER_OUT_CONCEPT_ID)))) {
					transferOut = Boolean.TRUE;
				}
				
				else if (q.getId().equals(
				    Integer.parseInt(Context.getAdministrationService().getGlobalProperty(
				        MdrtbConstants.GP_OUTCOME_STARTED_SLD_CONCEPT_ID)))) {
					sld = Boolean.TRUE;
				}
			}
			
			//get registration group
			//REGISTRATION GROUP
			q = tf.getRegistrationGroup();
			
			if (q != null) {
				
				if (!q.getId().equals(
				    Integer.parseInt(Context.getAdministrationService().getGlobalProperty(
				        MdrtbConstants.GP_TRANSFER_IN_CONCEPT_ID)))) {
					
					tb08TableData.setAllDetected(tb08TableData.getAllDetected() + 1);
					if (cured != null && cured) {
						tb08TableData.setAllCured(tb08TableData.getAllCured() + 1);
						tb08TableData.setAllEligible(tb08TableData.getAllEligible() + 1);
					} else if (txCompleted != null && txCompleted) {
						tb08TableData.setAllCompleted(tb08TableData.getAllCompleted() + 1);
						tb08TableData.setAllEligible(tb08TableData.getAllEligible() + 1);
					} else if (diedTB != null && diedTB) {
						tb08TableData.setAllDiedTB(tb08TableData.getAllDiedTB() + 1);
						tb08TableData.setAllEligible(tb08TableData.getAllEligible() + 1);
					} else if (diedNotTB != null && diedNotTB) {
						tb08TableData.setAllDiedNotTB(tb08TableData.getAllDiedNotTB() + 1);
						tb08TableData.setAllEligible(tb08TableData.getAllEligible() + 1);
					} else if (failed != null && failed) {
						tb08TableData.setAllFailed(tb08TableData.getAllFailed() + 1);
						tb08TableData.setAllEligible(tb08TableData.getAllEligible() + 1);
					} else if (defaulted != null && defaulted) {
						tb08TableData.setAllDefaulted(tb08TableData.getAllDefaulted() + 1);
						tb08TableData.setAllEligible(tb08TableData.getAllEligible() + 1);
					} else if (transferOut != null && transferOut) {
						tb08TableData.setAllTransferOut(tb08TableData.getAllTransferOut() + 1);
					} else if (canceled != null && canceled) {
						tb08TableData.setAllCanceled(tb08TableData.getAllCanceled() + 1);
					} else if (sld != null && sld) {
						tb08TableData.setAllSLD(tb08TableData.getAllSLD() + 1);
					}
				}
				//NEW
				if (q.getId()
				        .equals(
				            Integer.parseInt(Context.getAdministrationService().getGlobalProperty(
				                MdrtbConstants.GP_NEW_CONCEPT_ID)))) {
					tb08TableData.setNewAllDetected(tb08TableData.getNewAllDetected() + 1);
					//P
					if (pulmonary != null && pulmonary) {
						//BC
						if (bacPositive) {
							tb08TableData.setNewPulmonaryBCDetected(tb08TableData.getNewPulmonaryBCDetected() + 1);
							if (ageAtRegistration >= 0 && ageAtRegistration < 5) {
								tb08TableData.setNewPulmonaryBCDetected04(tb08TableData.getNewPulmonaryBCDetected04() + 1);
								
								if (cured != null && cured) {
									tb08TableData.setNewAllCured(tb08TableData.getNewAllCured() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryBCCured04(tb08TableData.getNewPulmonaryBCCured04() + 1);
									tb08TableData.setNewPulmonaryBCEligible04(tb08TableData.getNewPulmonaryBCEligible04() + 1);
									tb08TableData.setNewPulmonaryBCCured(tb08TableData.getNewPulmonaryBCCured() + 1);
									tb08TableData.setNewPulmonaryBCEligible(tb08TableData.getNewPulmonaryBCEligible() + 1);
									
								}
								
								else if (txCompleted != null && txCompleted) {
									tb08TableData.setNewAllCompleted(tb08TableData.getNewAllCompleted() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryBCCompleted04(tb08TableData.getNewPulmonaryBCCompleted04() + 1);
									tb08TableData.setNewPulmonaryBCEligible04(tb08TableData.getNewPulmonaryBCEligible04() + 1);
									tb08TableData.setNewPulmonaryBCCompleted(tb08TableData.getNewPulmonaryBCCompleted() + 1);
									tb08TableData.setNewPulmonaryBCEligible(tb08TableData.getNewPulmonaryBCEligible() + 1);
								}
								
								else if (diedTB != null && diedTB) {
									tb08TableData.setNewAllDiedTB(tb08TableData.getNewAllDiedTB() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryBCDiedTB04(tb08TableData.getNewPulmonaryBCDiedTB04() + 1);
									tb08TableData.setNewPulmonaryBCEligible04(tb08TableData.getNewPulmonaryBCEligible04() + 1);
									tb08TableData.setNewPulmonaryBCDiedTB(tb08TableData.getNewPulmonaryBCDiedTB() + 1);
									tb08TableData.setNewPulmonaryBCEligible(tb08TableData.getNewPulmonaryBCEligible() + 1);
								}
								
								else if (diedNotTB != null && diedNotTB) {
									tb08TableData.setNewAllDiedNotTB(tb08TableData.getNewAllDiedNotTB() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryBCDiedNotTB04(tb08TableData.getNewPulmonaryBCDiedNotTB04() + 1);
									tb08TableData.setNewPulmonaryBCEligible04(tb08TableData.getNewPulmonaryBCEligible04() + 1);
									tb08TableData.setNewPulmonaryBCDiedNotTB(tb08TableData.getNewPulmonaryBCDiedNotTB() + 1);
									tb08TableData.setNewPulmonaryBCEligible(tb08TableData.getNewPulmonaryBCEligible() + 1);
								}
								
								else if (failed != null && failed) {
									tb08TableData.setNewAllFailed(tb08TableData.getNewAllFailed() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryBCFailed04(tb08TableData.getNewPulmonaryBCFailed04() + 1);
									tb08TableData.setNewPulmonaryBCEligible04(tb08TableData.getNewPulmonaryBCEligible04() + 1);
									tb08TableData.setNewPulmonaryBCFailed(tb08TableData.getNewPulmonaryBCFailed() + 1);
									tb08TableData.setNewPulmonaryBCEligible(tb08TableData.getNewPulmonaryBCEligible() + 1);
								}
								
								else if (defaulted != null && defaulted) {
									tb08TableData.setNewAllDefaulted(tb08TableData.getNewAllDefaulted() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryBCDefaulted04(tb08TableData.getNewPulmonaryBCDefaulted04() + 1);
									tb08TableData.setNewPulmonaryBCEligible04(tb08TableData.getNewPulmonaryBCEligible04() + 1);
									tb08TableData.setNewPulmonaryBCDefaulted(tb08TableData.getNewPulmonaryBCDefaulted() + 1);
									tb08TableData.setNewPulmonaryBCEligible(tb08TableData.getNewPulmonaryBCEligible() + 1);
								}
								
								else if (transferOut != null && transferOut) {
									tb08TableData.setNewAllTransferOut(tb08TableData.getNewAllTransferOut() + 1);
									tb08TableData.setNewPulmonaryBCTransferOut04(tb08TableData.getNewPulmonaryBCTransferOut04() + 1);
									tb08TableData.setNewPulmonaryBCTransferOut(tb08TableData.getNewPulmonaryBCTransferOut() + 1);
									
								}
								
								else if (canceled != null && canceled) {
									tb08TableData.setNewAllCanceled(tb08TableData.getNewAllCanceled() + 1);
									tb08TableData.setNewPulmonaryBCCanceled04(tb08TableData.getNewPulmonaryBCCanceled04() + 1);
									tb08TableData.setNewPulmonaryBCCanceled(tb08TableData.getNewPulmonaryBCCanceled() + 1);
								}
								
								else if (sld != null && sld) {
									tb08TableData.setNewAllSLD(tb08TableData.getNewAllSLD() + 1);
									tb08TableData.setNewPulmonaryBCSLD04(tb08TableData.getNewPulmonaryBCSLD04() + 1);
									tb08TableData.setNewPulmonaryBCSLD(tb08TableData.getNewPulmonaryBCSLD() + 1);
								}
								
							}
							
							else if (ageAtRegistration >= 5 && ageAtRegistration < 15) {
								
								tb08TableData.setNewPulmonaryBCDetected0514(tb08TableData.getNewPulmonaryBCDetected0514() + 1);
								
								if (cured != null && cured) {
									tb08TableData.setNewAllCured(tb08TableData.getNewAllCured() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryBCCured0514(tb08TableData.getNewPulmonaryBCCured0514() + 1);
									tb08TableData.setNewPulmonaryBCEligible0514(tb08TableData.getNewPulmonaryBCEligible0514() + 1);
									tb08TableData.setNewPulmonaryBCCured(tb08TableData.getNewPulmonaryBCCured() + 1);
									tb08TableData.setNewPulmonaryBCEligible(tb08TableData.getNewPulmonaryBCEligible() + 1);
								}
								
								else if (txCompleted != null && txCompleted) {
									tb08TableData.setNewAllCompleted(tb08TableData.getNewAllCompleted() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryBCCompleted0514(tb08TableData.getNewPulmonaryBCCompleted0514() + 1);
									tb08TableData.setNewPulmonaryBCEligible0514(tb08TableData.getNewPulmonaryBCEligible0514() + 1);
									tb08TableData.setNewPulmonaryBCCompleted(tb08TableData.getNewPulmonaryBCCompleted() + 1);
									tb08TableData.setNewPulmonaryBCEligible(tb08TableData.getNewPulmonaryBCEligible() + 1);
								}
								
								else if (diedTB != null && diedTB) {
									tb08TableData.setNewAllDiedTB(tb08TableData.getNewAllDiedTB() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryBCDiedTB0514(tb08TableData.getNewPulmonaryBCDiedTB0514() + 1);
									tb08TableData.setNewPulmonaryBCEligible0514(tb08TableData.getNewPulmonaryBCEligible0514() + 1);
									tb08TableData.setNewPulmonaryBCDiedTB(tb08TableData.getNewPulmonaryBCDiedTB() + 1);
									tb08TableData.setNewPulmonaryBCEligible(tb08TableData.getNewPulmonaryBCEligible() + 1);
								}
								
								else if (diedNotTB != null && diedNotTB) {
									tb08TableData.setNewAllDiedNotTB(tb08TableData.getNewAllDiedNotTB() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryBCDiedNotTB0514(tb08TableData.getNewPulmonaryBCDiedNotTB0514() + 1);
									tb08TableData.setNewPulmonaryBCEligible0514(tb08TableData.getNewPulmonaryBCEligible0514() + 1);
									tb08TableData.setNewPulmonaryBCDiedNotTB(tb08TableData.getNewPulmonaryBCDiedNotTB() + 1);
									tb08TableData.setNewPulmonaryBCEligible(tb08TableData.getNewPulmonaryBCEligible() + 1);
								}
								
								else if (failed != null && failed) {
									tb08TableData.setNewAllFailed(tb08TableData.getNewAllFailed() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryBCFailed0514(tb08TableData.getNewPulmonaryBCFailed0514() + 1);
									tb08TableData.setNewPulmonaryBCEligible0514(tb08TableData.getNewPulmonaryBCEligible0514() + 1);
									tb08TableData.setNewPulmonaryBCFailed(tb08TableData.getNewPulmonaryBCFailed() + 1);
									tb08TableData.setNewPulmonaryBCEligible(tb08TableData.getNewPulmonaryBCEligible() + 1);
								}
								
								else if (defaulted != null && defaulted) {
									tb08TableData.setNewAllDefaulted(tb08TableData.getNewAllDefaulted() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryBCDefaulted0514(tb08TableData.getNewPulmonaryBCDefaulted0514() + 1);
									tb08TableData.setNewPulmonaryBCEligible0514(tb08TableData.getNewPulmonaryBCEligible0514() + 1);
									tb08TableData.setNewPulmonaryBCDefaulted(tb08TableData.getNewPulmonaryBCDefaulted() + 1);
									tb08TableData.setNewPulmonaryBCEligible(tb08TableData.getNewPulmonaryBCEligible() + 1);
								}
								
								else if (transferOut != null && transferOut) {
									tb08TableData.setNewAllTransferOut(tb08TableData.getNewAllTransferOut() + 1);
									tb08TableData.setNewPulmonaryBCTransferOut0514(tb08TableData.getNewPulmonaryBCTransferOut0514() + 1);
									tb08TableData.setNewPulmonaryBCTransferOut(tb08TableData.getNewPulmonaryBCTransferOut() + 1);
									
								}
								
								else if (canceled != null && canceled) {
									tb08TableData.setNewAllCanceled(tb08TableData.getNewAllCanceled() + 1);
									tb08TableData.setNewPulmonaryBCCanceled0514(tb08TableData.getNewPulmonaryBCCanceled0514() + 1);
									tb08TableData.setNewPulmonaryBCCanceled(tb08TableData.getNewPulmonaryBCCanceled() + 1);
								}
								
								else if (sld != null && sld) {
									tb08TableData.setNewAllSLD(tb08TableData.getNewAllSLD() + 1);
									tb08TableData.setNewPulmonaryBCSLD0514(tb08TableData.getNewPulmonaryBCSLD0514() + 1);
									tb08TableData.setNewPulmonaryBCSLD(tb08TableData.getNewPulmonaryBCSLD() + 1);
								}
								
							}
							
							else if (ageAtRegistration >= 15 && ageAtRegistration < 18) {
								
								tb08TableData.setNewPulmonaryBCDetected1517(tb08TableData.getNewPulmonaryBCDetected1517() + 1);
								
								if (cured != null && cured) {
									tb08TableData.setNewAllCured(tb08TableData.getNewAllCured() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryBCCured1517(tb08TableData.getNewPulmonaryBCCured1517() + 1);
									tb08TableData.setNewPulmonaryBCEligible1517(tb08TableData.getNewPulmonaryBCEligible1517() + 1);
									tb08TableData.setNewPulmonaryBCCured(tb08TableData.getNewPulmonaryBCCured() + 1);
									tb08TableData.setNewPulmonaryBCEligible(tb08TableData.getNewPulmonaryBCEligible() + 1);
								}
								
								else if (txCompleted != null && txCompleted) {
									tb08TableData.setNewAllCompleted(tb08TableData.getNewAllCompleted() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryBCCompleted1517(tb08TableData.getNewPulmonaryBCCompleted1517() + 1);
									tb08TableData.setNewPulmonaryBCEligible1517(tb08TableData.getNewPulmonaryBCEligible1517() + 1);
									tb08TableData.setNewPulmonaryBCCompleted(tb08TableData.getNewPulmonaryBCCompleted() + 1);
									tb08TableData.setNewPulmonaryBCEligible(tb08TableData.getNewPulmonaryBCEligible() + 1);
								}
								
								else if (diedTB != null && diedTB) {
									tb08TableData.setNewAllDiedTB(tb08TableData.getNewAllDiedTB() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryBCDiedTB1517(tb08TableData.getNewPulmonaryBCDiedTB1517() + 1);
									tb08TableData.setNewPulmonaryBCEligible1517(tb08TableData.getNewPulmonaryBCEligible1517() + 1);
									tb08TableData.setNewPulmonaryBCDiedTB(tb08TableData.getNewPulmonaryBCDiedTB() + 1);
									tb08TableData.setNewPulmonaryBCEligible(tb08TableData.getNewPulmonaryBCEligible() + 1);
								}
								
								else if (diedNotTB != null && diedNotTB) {
									tb08TableData.setNewAllDiedNotTB(tb08TableData.getNewAllDiedNotTB() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryBCDiedNotTB1517(tb08TableData.getNewPulmonaryBCDiedNotTB1517() + 1);
									tb08TableData.setNewPulmonaryBCEligible1517(tb08TableData.getNewPulmonaryBCEligible1517() + 1);
									tb08TableData.setNewPulmonaryBCDiedNotTB(tb08TableData.getNewPulmonaryBCDiedNotTB() + 1);
									tb08TableData.setNewPulmonaryBCEligible(tb08TableData.getNewPulmonaryBCEligible() + 1);
								}
								
								else if (failed != null && failed) {
									tb08TableData.setNewAllFailed(tb08TableData.getNewAllFailed() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryBCFailed1517(tb08TableData.getNewPulmonaryBCFailed1517() + 1);
									tb08TableData.setNewPulmonaryBCEligible1517(tb08TableData.getNewPulmonaryBCEligible1517() + 1);
									tb08TableData.setNewPulmonaryBCFailed(tb08TableData.getNewPulmonaryBCFailed() + 1);
									tb08TableData.setNewPulmonaryBCEligible(tb08TableData.getNewPulmonaryBCEligible() + 1);
								}
								
								else if (defaulted != null && defaulted) {
									tb08TableData.setNewAllDefaulted(tb08TableData.getNewAllDefaulted() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryBCDefaulted1517(tb08TableData.getNewPulmonaryBCDefaulted1517() + 1);
									tb08TableData.setNewPulmonaryBCEligible1517(tb08TableData.getNewPulmonaryBCEligible1517() + 1);
									tb08TableData.setNewPulmonaryBCDefaulted(tb08TableData.getNewPulmonaryBCDefaulted() + 1);
									tb08TableData.setNewPulmonaryBCEligible(tb08TableData.getNewPulmonaryBCEligible() + 1);
								}
								
								else if (transferOut != null && transferOut) {
									tb08TableData.setNewAllTransferOut(tb08TableData.getNewAllTransferOut() + 1);
									tb08TableData.setNewPulmonaryBCTransferOut1517(tb08TableData.getNewPulmonaryBCTransferOut1517() + 1);
									tb08TableData.setNewPulmonaryBCTransferOut(tb08TableData.getNewPulmonaryBCTransferOut() + 1);
									
								}
								
								else if (canceled != null && canceled) {
									tb08TableData.setNewAllCanceled(tb08TableData.getNewAllCanceled() + 1);
									tb08TableData.setNewPulmonaryBCCanceled1517(tb08TableData.getNewPulmonaryBCCanceled1517() + 1);
									tb08TableData.setNewPulmonaryBCCanceled(tb08TableData.getNewPulmonaryBCCanceled() + 1);
								}
								
								else if (sld != null && sld) {
									tb08TableData.setNewAllSLD(tb08TableData.getNewAllSLD() + 1);
									tb08TableData.setNewPulmonaryBCSLD1517(tb08TableData.getNewPulmonaryBCSLD1517() + 1);
									tb08TableData.setNewPulmonaryBCSLD(tb08TableData.getNewPulmonaryBCSLD() + 1);
								}
								
							}
							
							else {
								
								if (cured != null && cured) {
									tb08TableData.setNewAllCured(tb08TableData.getNewAllCured() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryBCCured(tb08TableData.getNewPulmonaryBCCured() + 1);
									tb08TableData.setNewPulmonaryBCEligible(tb08TableData.getNewPulmonaryBCEligible() + 1);
								}
								
								else if (txCompleted != null && txCompleted) {
									tb08TableData.setNewAllCompleted(tb08TableData.getNewAllCompleted() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryBCCompleted(tb08TableData.getNewPulmonaryBCCompleted() + 1);
									tb08TableData.setNewPulmonaryBCEligible(tb08TableData.getNewPulmonaryBCEligible() + 1);
								}
								
								else if (diedTB != null && diedTB) {
									tb08TableData.setNewAllDiedTB(tb08TableData.getNewAllDiedTB() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryBCDiedTB(tb08TableData.getNewPulmonaryBCDiedTB() + 1);
									tb08TableData.setNewPulmonaryBCEligible(tb08TableData.getNewPulmonaryBCEligible() + 1);
								}
								
								else if (diedNotTB != null && diedNotTB) {
									tb08TableData.setNewAllDiedNotTB(tb08TableData.getNewAllDiedNotTB() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryBCDiedNotTB(tb08TableData.getNewPulmonaryBCDiedNotTB() + 1);
									tb08TableData.setNewPulmonaryBCEligible(tb08TableData.getNewPulmonaryBCEligible() + 1);
								}
								
								else if (failed != null && failed) {
									tb08TableData.setNewAllFailed(tb08TableData.getNewAllFailed() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryBCFailed(tb08TableData.getNewPulmonaryBCFailed() + 1);
									tb08TableData.setNewPulmonaryBCEligible(tb08TableData.getNewPulmonaryBCEligible() + 1);
								}
								
								else if (defaulted != null && defaulted) {
									tb08TableData.setNewAllDefaulted(tb08TableData.getNewAllDefaulted() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryBCDefaulted(tb08TableData.getNewPulmonaryBCDefaulted() + 1);
									tb08TableData.setNewPulmonaryBCEligible(tb08TableData.getNewPulmonaryBCEligible() + 1);
								}
								
								else if (transferOut != null && transferOut) {
									tb08TableData.setNewAllTransferOut(tb08TableData.getNewAllTransferOut() + 1);
									tb08TableData.setNewPulmonaryBCTransferOut(tb08TableData.getNewPulmonaryBCTransferOut() + 1);
									
								}
								
								else if (canceled != null && canceled) {
									tb08TableData.setNewAllCanceled(tb08TableData.getNewAllCanceled() + 1);
									tb08TableData.setNewPulmonaryBCCanceled(tb08TableData.getNewPulmonaryBCCanceled() + 1);
									
								}
								
								else if (sld != null && sld) {
									tb08TableData.setNewAllSLD(tb08TableData.getNewAllSLD() + 1);
									tb08TableData.setNewPulmonaryBCSLD(tb08TableData.getNewPulmonaryBCSLD() + 1);
									
								}
							}
							
						}
						
						//CD
						else {
							
							tb08TableData.setNewPulmonaryCDDetected(tb08TableData.getNewPulmonaryCDDetected() + 1);
							
							if (ageAtRegistration >= 0 && ageAtRegistration < 5) {
								
								tb08TableData.setNewPulmonaryCDDetected04(tb08TableData.getNewPulmonaryCDDetected04() + 1);
								
								if (cured != null && cured) {
									tb08TableData.setNewAllCured(tb08TableData.getNewAllCured() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryCDCured04(tb08TableData.getNewPulmonaryCDCured04() + 1);
									tb08TableData.setNewPulmonaryCDEligible04(tb08TableData.getNewPulmonaryCDEligible04() + 1);
									tb08TableData.setNewPulmonaryCDCured(tb08TableData.getNewPulmonaryCDCured() + 1);
									tb08TableData.setNewPulmonaryCDEligible(tb08TableData.getNewPulmonaryCDEligible() + 1);
								}
								
								else if (txCompleted != null && txCompleted) {
									tb08TableData.setNewAllCompleted(tb08TableData.getNewAllCompleted() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryCDCompleted04(tb08TableData.getNewPulmonaryCDCompleted04() + 1);
									tb08TableData.setNewPulmonaryCDEligible04(tb08TableData.getNewPulmonaryCDEligible04() + 1);
									tb08TableData.setNewPulmonaryCDCompleted(tb08TableData.getNewPulmonaryCDCompleted() + 1);
									tb08TableData.setNewPulmonaryCDEligible(tb08TableData.getNewPulmonaryCDEligible() + 1);
								}
								
								else if (diedTB != null && diedTB) {
									tb08TableData.setNewAllDiedTB(tb08TableData.getNewAllDiedTB() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryCDDiedTB04(tb08TableData.getNewPulmonaryCDDiedTB04() + 1);
									tb08TableData.setNewPulmonaryCDEligible04(tb08TableData.getNewPulmonaryCDEligible04() + 1);
									tb08TableData.setNewPulmonaryCDDiedTB(tb08TableData.getNewPulmonaryCDDiedTB() + 1);
									tb08TableData.setNewPulmonaryCDEligible(tb08TableData.getNewPulmonaryCDEligible() + 1);
								}
								
								else if (diedNotTB != null && diedNotTB) {
									tb08TableData.setNewAllDiedNotTB(tb08TableData.getNewAllDiedNotTB() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryCDDiedNotTB04(tb08TableData.getNewPulmonaryCDDiedNotTB04() + 1);
									tb08TableData.setNewPulmonaryCDEligible04(tb08TableData.getNewPulmonaryCDEligible04() + 1);
									tb08TableData.setNewPulmonaryCDDiedNotTB(tb08TableData.getNewPulmonaryCDDiedNotTB() + 1);
									tb08TableData.setNewPulmonaryCDEligible(tb08TableData.getNewPulmonaryCDEligible() + 1);
								}
								
								else if (failed != null && failed) {
									tb08TableData.setNewAllFailed(tb08TableData.getNewAllFailed() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryCDFailed04(tb08TableData.getNewPulmonaryCDFailed04() + 1);
									tb08TableData.setNewPulmonaryCDEligible04(tb08TableData.getNewPulmonaryCDEligible04() + 1);
									tb08TableData.setNewPulmonaryCDFailed(tb08TableData.getNewPulmonaryCDFailed() + 1);
									tb08TableData.setNewPulmonaryCDEligible(tb08TableData.getNewPulmonaryCDEligible() + 1);
								}
								
								else if (defaulted != null && defaulted) {
									tb08TableData.setNewAllDefaulted(tb08TableData.getNewAllDefaulted() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryCDDefaulted04(tb08TableData.getNewPulmonaryCDDefaulted04() + 1);
									tb08TableData.setNewPulmonaryCDEligible04(tb08TableData.getNewPulmonaryCDEligible04() + 1);
									tb08TableData.setNewPulmonaryCDDefaulted(tb08TableData.getNewPulmonaryCDDefaulted() + 1);
									tb08TableData.setNewPulmonaryCDEligible(tb08TableData.getNewPulmonaryCDEligible() + 1);
								}
								
								else if (transferOut != null && transferOut) {
									tb08TableData.setNewAllTransferOut(tb08TableData.getNewAllTransferOut() + 1);
									tb08TableData.setNewPulmonaryCDTransferOut04(tb08TableData.getNewPulmonaryCDTransferOut04() + 1);
									tb08TableData.setNewPulmonaryCDTransferOut(tb08TableData.getNewPulmonaryCDTransferOut() + 1);
									
								}
								
								else if (canceled != null && canceled) {
									tb08TableData.setNewAllCanceled(tb08TableData.getNewAllCanceled() + 1);
									tb08TableData.setNewPulmonaryCDCanceled04(tb08TableData.getNewPulmonaryCDCanceled04() + 1);
									tb08TableData.setNewPulmonaryCDCanceled(tb08TableData.getNewPulmonaryCDCanceled() + 1);
									
								}
								
								else if (sld != null && sld) {
									tb08TableData.setNewAllSLD(tb08TableData.getNewAllSLD() + 1);
									tb08TableData.setNewPulmonaryCDSLD04(tb08TableData.getNewPulmonaryCDSLD04() + 1);
									tb08TableData.setNewPulmonaryCDSLD(tb08TableData.getNewPulmonaryCDSLD() + 1);
									
								}
								
							}
							
							else if (ageAtRegistration >= 5 && ageAtRegistration < 15) {
								
								tb08TableData.setNewPulmonaryCDDetected0514(tb08TableData.getNewPulmonaryCDDetected0514() + 1);
								
								if (cured != null && cured) {
									tb08TableData.setNewAllCured(tb08TableData.getNewAllCured() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryCDCured0514(tb08TableData.getNewPulmonaryCDCured0514() + 1);
									tb08TableData.setNewPulmonaryCDEligible0514(tb08TableData.getNewPulmonaryCDEligible0514() + 1);
									tb08TableData.setNewPulmonaryCDCured(tb08TableData.getNewPulmonaryCDCured() + 1);
									tb08TableData.setNewPulmonaryCDEligible(tb08TableData.getNewPulmonaryCDEligible() + 1);
								}
								
								else if (txCompleted != null && txCompleted) {
									tb08TableData.setNewAllCompleted(tb08TableData.getNewAllCompleted() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryCDCompleted0514(tb08TableData.getNewPulmonaryCDCompleted0514() + 1);
									tb08TableData.setNewPulmonaryCDEligible0514(tb08TableData.getNewPulmonaryCDEligible0514() + 1);
									tb08TableData.setNewPulmonaryCDCompleted(tb08TableData.getNewPulmonaryCDCompleted() + 1);
									tb08TableData.setNewPulmonaryCDEligible(tb08TableData.getNewPulmonaryCDEligible() + 1);
								}
								
								else if (diedTB != null && diedTB) {
									tb08TableData.setNewAllDiedTB(tb08TableData.getNewAllDiedTB() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryCDDiedTB0514(tb08TableData.getNewPulmonaryCDDiedTB0514() + 1);
									tb08TableData.setNewPulmonaryCDEligible0514(tb08TableData.getNewPulmonaryCDEligible0514() + 1);
									tb08TableData.setNewPulmonaryCDDiedTB(tb08TableData.getNewPulmonaryCDDiedTB() + 1);
									tb08TableData.setNewPulmonaryCDEligible(tb08TableData.getNewPulmonaryCDEligible() + 1);
								}
								
								else if (diedNotTB != null && diedNotTB) {
									tb08TableData.setNewAllDiedNotTB(tb08TableData.getNewAllDiedNotTB() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryCDDiedNotTB0514(tb08TableData.getNewPulmonaryCDDiedNotTB0514() + 1);
									tb08TableData.setNewPulmonaryCDEligible0514(tb08TableData.getNewPulmonaryCDEligible0514() + 1);
									tb08TableData.setNewPulmonaryCDDiedNotTB(tb08TableData.getNewPulmonaryCDDiedNotTB() + 1);
									tb08TableData.setNewPulmonaryCDEligible(tb08TableData.getNewPulmonaryCDEligible() + 1);
								}
								
								else if (failed != null && failed) {
									tb08TableData.setNewAllFailed(tb08TableData.getNewAllFailed() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryCDFailed0514(tb08TableData.getNewPulmonaryCDFailed0514() + 1);
									tb08TableData.setNewPulmonaryCDEligible0514(tb08TableData.getNewPulmonaryCDEligible0514() + 1);
									tb08TableData.setNewPulmonaryCDFailed(tb08TableData.getNewPulmonaryCDFailed() + 1);
									tb08TableData.setNewPulmonaryCDEligible(tb08TableData.getNewPulmonaryCDEligible() + 1);
								}
								
								else if (defaulted != null && defaulted) {
									tb08TableData.setNewAllDefaulted(tb08TableData.getNewAllDefaulted() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryCDDefaulted0514(tb08TableData.getNewPulmonaryCDDefaulted0514() + 1);
									tb08TableData.setNewPulmonaryCDEligible0514(tb08TableData.getNewPulmonaryCDEligible0514() + 1);
									tb08TableData.setNewPulmonaryCDDefaulted(tb08TableData.getNewPulmonaryCDDefaulted() + 1);
									tb08TableData.setNewPulmonaryCDEligible(tb08TableData.getNewPulmonaryCDEligible() + 1);
								}
								
								else if (transferOut != null && transferOut) {
									tb08TableData.setNewAllTransferOut(tb08TableData.getNewAllTransferOut() + 1);
									tb08TableData.setNewPulmonaryCDTransferOut0514(tb08TableData.getNewPulmonaryCDTransferOut0514() + 1);
									tb08TableData.setNewPulmonaryCDTransferOut(tb08TableData.getNewPulmonaryCDTransferOut() + 1);
									
								}
								
								else if (canceled != null && canceled) {
									tb08TableData.setNewAllCanceled(tb08TableData.getNewAllCanceled() + 1);
									tb08TableData.setNewPulmonaryCDCanceled0514(tb08TableData.getNewPulmonaryCDCanceled0514() + 1);
									tb08TableData.setNewPulmonaryCDCanceled(tb08TableData.getNewPulmonaryCDCanceled() + 1);
									
								}
								
								else if (sld != null && sld) {
									tb08TableData.setNewAllSLD(tb08TableData.getNewAllSLD() + 1);
									tb08TableData.setNewPulmonaryCDSLD0514(tb08TableData.getNewPulmonaryCDSLD0514() + 1);
									tb08TableData.setNewPulmonaryCDSLD(tb08TableData.getNewPulmonaryCDSLD() + 1);
									
								}
								
							}
							
							else if (ageAtRegistration >= 15 && ageAtRegistration < 18) {
								
								tb08TableData.setNewPulmonaryCDDetected1517(tb08TableData.getNewPulmonaryCDDetected1517() + 1);
								
								if (cured != null && cured) {
									tb08TableData.setNewAllCured(tb08TableData.getNewAllCured() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryCDCured1517(tb08TableData.getNewPulmonaryCDCured1517() + 1);
									tb08TableData.setNewPulmonaryCDEligible1517(tb08TableData.getNewPulmonaryCDEligible1517() + 1);
									tb08TableData.setNewPulmonaryCDCured(tb08TableData.getNewPulmonaryCDCured() + 1);
									tb08TableData.setNewPulmonaryCDEligible(tb08TableData.getNewPulmonaryCDEligible() + 1);
								}
								
								else if (txCompleted != null && txCompleted) {
									tb08TableData.setNewAllCompleted(tb08TableData.getNewAllCompleted() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryCDCompleted1517(tb08TableData.getNewPulmonaryCDCompleted1517() + 1);
									tb08TableData.setNewPulmonaryCDEligible1517(tb08TableData.getNewPulmonaryCDEligible1517() + 1);
									tb08TableData.setNewPulmonaryCDCompleted(tb08TableData.getNewPulmonaryCDCompleted() + 1);
									tb08TableData.setNewPulmonaryCDEligible(tb08TableData.getNewPulmonaryCDEligible() + 1);
								}
								
								else if (diedTB != null && diedTB) {
									tb08TableData.setNewAllDiedTB(tb08TableData.getNewAllDiedTB() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryCDDiedTB1517(tb08TableData.getNewPulmonaryCDDiedTB1517() + 1);
									tb08TableData.setNewPulmonaryCDEligible1517(tb08TableData.getNewPulmonaryCDEligible1517() + 1);
									tb08TableData.setNewPulmonaryCDDiedTB(tb08TableData.getNewPulmonaryCDDiedTB() + 1);
									tb08TableData.setNewPulmonaryCDEligible(tb08TableData.getNewPulmonaryCDEligible() + 1);
								}
								
								else if (diedNotTB != null && diedNotTB) {
									tb08TableData.setNewAllDiedNotTB(tb08TableData.getNewAllDiedNotTB() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryCDDiedNotTB1517(tb08TableData.getNewPulmonaryCDDiedNotTB1517() + 1);
									tb08TableData.setNewPulmonaryCDEligible1517(tb08TableData.getNewPulmonaryCDEligible1517() + 1);
									tb08TableData.setNewPulmonaryCDDiedNotTB(tb08TableData.getNewPulmonaryCDDiedNotTB() + 1);
									tb08TableData.setNewPulmonaryCDEligible(tb08TableData.getNewPulmonaryCDEligible() + 1);
								}
								
								else if (failed != null && failed) {
									tb08TableData.setNewAllFailed(tb08TableData.getNewAllFailed() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryCDFailed1517(tb08TableData.getNewPulmonaryCDFailed1517() + 1);
									tb08TableData.setNewPulmonaryCDEligible1517(tb08TableData.getNewPulmonaryCDEligible1517() + 1);
									tb08TableData.setNewPulmonaryCDFailed(tb08TableData.getNewPulmonaryCDFailed() + 1);
									tb08TableData.setNewPulmonaryCDEligible(tb08TableData.getNewPulmonaryCDEligible() + 1);
								}
								
								else if (defaulted != null && defaulted) {
									tb08TableData.setNewAllDefaulted(tb08TableData.getNewAllDefaulted() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryCDDefaulted1517(tb08TableData.getNewPulmonaryCDDefaulted1517() + 1);
									tb08TableData.setNewPulmonaryCDEligible1517(tb08TableData.getNewPulmonaryCDEligible1517() + 1);
									tb08TableData.setNewPulmonaryCDDefaulted(tb08TableData.getNewPulmonaryCDDefaulted() + 1);
									tb08TableData.setNewPulmonaryCDEligible(tb08TableData.getNewPulmonaryCDEligible() + 1);
								}
								
								else if (transferOut != null && transferOut) {
									tb08TableData.setNewAllTransferOut(tb08TableData.getNewAllTransferOut() + 1);
									tb08TableData.setNewPulmonaryCDTransferOut1517(tb08TableData.getNewPulmonaryCDTransferOut1517() + 1);
									tb08TableData.setNewPulmonaryCDTransferOut(tb08TableData.getNewPulmonaryCDTransferOut() + 1);
									
								}
								
								else if (canceled != null && canceled) {
									tb08TableData.setNewAllCanceled(tb08TableData.getNewAllCanceled() + 1);
									tb08TableData.setNewPulmonaryCDCanceled1517(tb08TableData.getNewPulmonaryCDCanceled1517() + 1);
									tb08TableData.setNewPulmonaryCDCanceled(tb08TableData.getNewPulmonaryCDCanceled() + 1);
								}
								
								else if (sld != null && sld) {
									tb08TableData.setNewAllSLD(tb08TableData.getNewAllSLD() + 1);
									tb08TableData.setNewPulmonaryCDSLD1517(tb08TableData.getNewPulmonaryCDSLD1517() + 1);
									tb08TableData.setNewPulmonaryCDSLD(tb08TableData.getNewPulmonaryCDSLD() + 1);
								}
								
							}
							
							else {
								
								if (cured != null && cured) {
									tb08TableData.setNewAllCured(tb08TableData.getNewAllCured() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryCDCured(tb08TableData.getNewPulmonaryCDCured() + 1);
									tb08TableData.setNewPulmonaryCDEligible(tb08TableData.getNewPulmonaryCDEligible() + 1);
								}
								
								else if (txCompleted != null && txCompleted) {
									tb08TableData.setNewAllCompleted(tb08TableData.getNewAllCompleted() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryCDCompleted(tb08TableData.getNewPulmonaryCDCompleted() + 1);
									tb08TableData.setNewPulmonaryCDEligible(tb08TableData.getNewPulmonaryCDEligible() + 1);
								}
								
								else if (diedTB != null && diedTB) {
									tb08TableData.setNewAllDiedTB(tb08TableData.getNewAllDiedTB() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryCDDiedTB(tb08TableData.getNewPulmonaryCDDiedTB() + 1);
									tb08TableData.setNewPulmonaryCDEligible(tb08TableData.getNewPulmonaryCDEligible() + 1);
								}
								
								else if (diedNotTB != null && diedNotTB) {
									tb08TableData.setNewAllDiedNotTB(tb08TableData.getNewAllDiedNotTB() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryCDDiedNotTB(tb08TableData.getNewPulmonaryCDDiedNotTB() + 1);
									tb08TableData.setNewPulmonaryCDEligible(tb08TableData.getNewPulmonaryCDEligible() + 1);
								}
								
								else if (failed != null && failed) {
									tb08TableData.setNewAllFailed(tb08TableData.getNewAllFailed() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryCDFailed(tb08TableData.getNewPulmonaryCDFailed() + 1);
									tb08TableData.setNewPulmonaryCDEligible(tb08TableData.getNewPulmonaryCDEligible() + 1);
								}
								
								else if (defaulted != null && defaulted) {
									tb08TableData.setNewAllDefaulted(tb08TableData.getNewAllDefaulted() + 1);
									tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
									tb08TableData.setNewPulmonaryCDDefaulted(tb08TableData.getNewPulmonaryCDDefaulted() + 1);
									tb08TableData.setNewPulmonaryCDEligible(tb08TableData.getNewPulmonaryCDEligible() + 1);
								}
								
								else if (transferOut != null && transferOut) {
									tb08TableData.setNewAllTransferOut(tb08TableData.getNewAllTransferOut() + 1);
									tb08TableData.setNewPulmonaryCDTransferOut(tb08TableData.getNewPulmonaryCDTransferOut() + 1);
									
								}
								
								else if (canceled != null && canceled) {
									tb08TableData.setNewAllCanceled(tb08TableData.getNewAllCanceled() + 1);
									tb08TableData.setNewPulmonaryCDCanceled(tb08TableData.getNewPulmonaryCDCanceled() + 1);
									
								}
								
								else if (sld != null && sld) {
									tb08TableData.setNewAllSLD(tb08TableData.getNewAllSLD() + 1);
									tb08TableData.setNewPulmonaryCDSLD(tb08TableData.getNewPulmonaryCDSLD() + 1);
									
								}
							}
						}
					}
					
					//EP
					else if (pulmonary != null && !pulmonary) {
						
						tb08TableData.setNewExtrapulmonaryDetected(tb08TableData.getNewExtrapulmonaryDetected() + 1);
						
						if (ageAtRegistration >= 0 && ageAtRegistration < 5) {
							
							tb08TableData.setNewExtrapulmonaryDetected04(tb08TableData.getNewExtrapulmonaryDetected04() + 1);
							
							if (cured != null && cured) {
								tb08TableData.setNewAllCured(tb08TableData.getNewAllCured() + 1);
								tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
								tb08TableData.setNewExtrapulmonaryCured04(tb08TableData.getNewExtrapulmonaryCured04() + 1);
								tb08TableData.setNewExtrapulmonaryEligible04(tb08TableData.getNewExtrapulmonaryEligible04() + 1);
								tb08TableData.setNewExtrapulmonaryCured(tb08TableData.getNewExtrapulmonaryCured() + 1);
								tb08TableData.setNewExtrapulmonaryEligible(tb08TableData.getNewExtrapulmonaryEligible() + 1);
							}
							
							else if (txCompleted != null && txCompleted) {
								tb08TableData.setNewAllCompleted(tb08TableData.getNewAllCompleted() + 1);
								tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
								tb08TableData.setNewExtrapulmonaryCompleted04(tb08TableData.getNewExtrapulmonaryCompleted04() + 1);
								tb08TableData.setNewExtrapulmonaryEligible04(tb08TableData.getNewExtrapulmonaryEligible04() + 1);
								tb08TableData.setNewExtrapulmonaryCompleted(tb08TableData.getNewExtrapulmonaryCompleted() + 1);
								tb08TableData.setNewExtrapulmonaryEligible(tb08TableData.getNewExtrapulmonaryEligible() + 1);
							}
							
							else if (diedTB != null && diedTB) {
								tb08TableData.setNewAllDiedTB(tb08TableData.getNewAllDiedTB() + 1);
								tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
								tb08TableData.setNewExtrapulmonaryDiedTB04(tb08TableData.getNewExtrapulmonaryDiedTB04() + 1);
								tb08TableData.setNewExtrapulmonaryEligible04(tb08TableData.getNewExtrapulmonaryEligible04() + 1);
								tb08TableData.setNewExtrapulmonaryDiedTB(tb08TableData.getNewExtrapulmonaryDiedTB() + 1);
								tb08TableData.setNewExtrapulmonaryEligible(tb08TableData.getNewExtrapulmonaryEligible() + 1);
							}
							
							else if (diedNotTB != null && diedNotTB) {
								tb08TableData.setNewAllDiedNotTB(tb08TableData.getNewAllDiedNotTB() + 1);
								tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
								tb08TableData.setNewExtrapulmonaryDiedNotTB04(tb08TableData.getNewExtrapulmonaryDiedNotTB04() + 1);
								tb08TableData.setNewExtrapulmonaryEligible04(tb08TableData.getNewExtrapulmonaryEligible04() + 1);
								tb08TableData.setNewExtrapulmonaryDiedNotTB(tb08TableData.getNewExtrapulmonaryDiedNotTB() + 1);
								tb08TableData.setNewExtrapulmonaryEligible(tb08TableData.getNewExtrapulmonaryEligible() + 1);
							}
							
							else if (failed != null && failed) {
								tb08TableData.setNewAllFailed(tb08TableData.getNewAllFailed() + 1);
								tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
								tb08TableData.setNewExtrapulmonaryFailed04(tb08TableData.getNewExtrapulmonaryFailed04() + 1);
								tb08TableData.setNewExtrapulmonaryEligible04(tb08TableData.getNewExtrapulmonaryEligible04() + 1);
								tb08TableData.setNewExtrapulmonaryFailed(tb08TableData.getNewExtrapulmonaryFailed() + 1);
								tb08TableData.setNewExtrapulmonaryEligible(tb08TableData.getNewExtrapulmonaryEligible() + 1);
							}
							
							else if (defaulted != null && defaulted) {
								tb08TableData.setNewAllDefaulted(tb08TableData.getNewAllDefaulted() + 1);
								tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
								tb08TableData.setNewExtrapulmonaryDefaulted04(tb08TableData.getNewExtrapulmonaryDefaulted04() + 1);
								tb08TableData.setNewExtrapulmonaryEligible04(tb08TableData.getNewExtrapulmonaryEligible04() + 1);
								tb08TableData.setNewExtrapulmonaryDefaulted(tb08TableData.getNewExtrapulmonaryDefaulted() + 1);
								tb08TableData.setNewExtrapulmonaryEligible(tb08TableData.getNewExtrapulmonaryEligible() + 1);
							}
							
							else if (transferOut != null && transferOut) {
								tb08TableData.setNewAllTransferOut(tb08TableData.getNewAllTransferOut() + 1);
								tb08TableData.setNewExtrapulmonaryTransferOut04(tb08TableData.getNewExtrapulmonaryTransferOut04() + 1);
								tb08TableData.setNewExtrapulmonaryTransferOut(tb08TableData.getNewExtrapulmonaryTransferOut() + 1);
							}
							
							else if (canceled != null && canceled) {
								tb08TableData.setNewAllCanceled(tb08TableData.getNewAllCanceled() + 1);
								tb08TableData.setNewExtrapulmonaryCanceled04(tb08TableData.getNewExtrapulmonaryCanceled04() + 1);
								tb08TableData.setNewExtrapulmonaryCanceled(tb08TableData.getNewExtrapulmonaryCanceled() + 1);
							}
							
							else if (sld != null && sld) {
								tb08TableData.setNewAllSLD(tb08TableData.getNewAllSLD() + 1);
								tb08TableData.setNewExtrapulmonarySLD04(tb08TableData.getNewExtrapulmonarySLD04() + 1);
								tb08TableData.setNewExtrapulmonarySLD(tb08TableData.getNewExtrapulmonarySLD() + 1);
							}
							
						}
						
						else if (ageAtRegistration >= 5 && ageAtRegistration < 15) {
							
							tb08TableData.setNewExtrapulmonaryDetected0514(tb08TableData.getNewExtrapulmonaryDetected0514() + 1);
							
							if (cured != null && cured) {
								tb08TableData.setNewAllCured(tb08TableData.getNewAllCured() + 1);
								tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
								tb08TableData.setNewExtrapulmonaryCured0514(tb08TableData.getNewExtrapulmonaryCured0514() + 1);
								tb08TableData.setNewExtrapulmonaryEligible0514(tb08TableData.getNewExtrapulmonaryEligible0514() + 1);
								tb08TableData.setNewExtrapulmonaryCured(tb08TableData.getNewExtrapulmonaryCured() + 1);
								tb08TableData.setNewExtrapulmonaryEligible(tb08TableData.getNewExtrapulmonaryEligible() + 1);
							}
							
							else if (txCompleted != null && txCompleted) {
								tb08TableData.setNewAllCompleted(tb08TableData.getNewAllCompleted() + 1);
								tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
								tb08TableData.setNewExtrapulmonaryCompleted0514(tb08TableData.getNewExtrapulmonaryCompleted0514() + 1);
								tb08TableData.setNewExtrapulmonaryEligible0514(tb08TableData.getNewExtrapulmonaryEligible0514() + 1);
								tb08TableData.setNewExtrapulmonaryCompleted(tb08TableData.getNewExtrapulmonaryCompleted() + 1);
								tb08TableData.setNewExtrapulmonaryEligible(tb08TableData.getNewExtrapulmonaryEligible() + 1);
							}
							
							else if (diedTB != null && diedTB) {
								tb08TableData.setNewAllDiedTB(tb08TableData.getNewAllDiedTB() + 1);
								tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
								tb08TableData.setNewExtrapulmonaryDiedTB0514(tb08TableData.getNewExtrapulmonaryDiedTB0514() + 1);
								tb08TableData.setNewExtrapulmonaryEligible0514(tb08TableData.getNewExtrapulmonaryEligible0514() + 1);
								tb08TableData.setNewExtrapulmonaryDiedTB(tb08TableData.getNewExtrapulmonaryDiedTB() + 1);
								tb08TableData.setNewExtrapulmonaryEligible(tb08TableData.getNewExtrapulmonaryEligible() + 1);
							}
							
							else if (diedNotTB != null && diedNotTB) {
								tb08TableData.setNewAllDiedNotTB(tb08TableData.getNewAllDiedNotTB() + 1);
								tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
								tb08TableData.setNewExtrapulmonaryDiedNotTB0514(tb08TableData.getNewExtrapulmonaryDiedNotTB0514() + 1);
								tb08TableData.setNewExtrapulmonaryEligible0514(tb08TableData.getNewExtrapulmonaryEligible0514() + 1);
								tb08TableData.setNewExtrapulmonaryDiedNotTB(tb08TableData.getNewExtrapulmonaryDiedNotTB() + 1);
								tb08TableData.setNewExtrapulmonaryEligible(tb08TableData.getNewExtrapulmonaryEligible() + 1);
							}
							
							else if (failed != null && failed) {
								tb08TableData.setNewAllFailed(tb08TableData.getNewAllFailed() + 1);
								tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
								tb08TableData.setNewExtrapulmonaryFailed0514(tb08TableData.getNewExtrapulmonaryFailed0514() + 1);
								tb08TableData.setNewExtrapulmonaryEligible0514(tb08TableData.getNewExtrapulmonaryEligible0514() + 1);
								tb08TableData.setNewExtrapulmonaryFailed(tb08TableData.getNewExtrapulmonaryFailed() + 1);
								tb08TableData.setNewExtrapulmonaryEligible(tb08TableData.getNewExtrapulmonaryEligible() + 1);
							}
							
							else if (defaulted != null && defaulted) {
								tb08TableData.setNewAllDefaulted(tb08TableData.getNewAllDefaulted() + 1);
								tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
								tb08TableData.setNewExtrapulmonaryDefaulted0514(tb08TableData.getNewExtrapulmonaryDefaulted0514() + 1);
								tb08TableData.setNewExtrapulmonaryEligible0514(tb08TableData.getNewExtrapulmonaryEligible0514() + 1);
								tb08TableData.setNewExtrapulmonaryDefaulted(tb08TableData.getNewExtrapulmonaryDefaulted() + 1);
								tb08TableData.setNewExtrapulmonaryEligible(tb08TableData.getNewExtrapulmonaryEligible() + 1);
							}
							
							else if (transferOut != null && transferOut) {
								tb08TableData.setNewAllTransferOut(tb08TableData.getNewAllTransferOut() + 1);
								tb08TableData.setNewExtrapulmonaryTransferOut0514(tb08TableData.getNewExtrapulmonaryTransferOut0514() + 1);
								tb08TableData.setNewExtrapulmonaryTransferOut(tb08TableData.getNewExtrapulmonaryTransferOut() + 1);
							}
							
							else if (canceled != null && canceled) {
								tb08TableData.setNewAllCanceled(tb08TableData.getNewAllCanceled() + 1);
								tb08TableData.setNewExtrapulmonaryCanceled0514(tb08TableData.getNewExtrapulmonaryCanceled0514() + 1);
								tb08TableData.setNewExtrapulmonaryCanceled(tb08TableData.getNewExtrapulmonaryCanceled() + 1);
							}
							
							else if (sld != null && sld) {
								tb08TableData.setNewAllSLD(tb08TableData.getNewAllSLD() + 1);
								tb08TableData.setNewExtrapulmonarySLD0514(tb08TableData.getNewExtrapulmonarySLD0514() + 1);
								tb08TableData.setNewExtrapulmonarySLD(tb08TableData.getNewExtrapulmonarySLD() + 1);
							}
							
						}
						
						else if (ageAtRegistration >= 15 && ageAtRegistration < 18) {
							
							tb08TableData.setNewExtrapulmonaryDetected1517(tb08TableData.getNewExtrapulmonaryDetected1517() + 1);
							
							if (cured != null && cured) {
								tb08TableData.setNewAllCured(tb08TableData.getNewAllCured() + 1);
								tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
								tb08TableData.setNewExtrapulmonaryCured1517(tb08TableData.getNewExtrapulmonaryCured1517() + 1);
								tb08TableData.setNewExtrapulmonaryEligible1517(tb08TableData.getNewExtrapulmonaryEligible1517() + 1);
								tb08TableData.setNewExtrapulmonaryCured(tb08TableData.getNewExtrapulmonaryCured() + 1);
								tb08TableData.setNewExtrapulmonaryEligible(tb08TableData.getNewExtrapulmonaryEligible() + 1);
							}
							
							else if (txCompleted != null && txCompleted) {
								tb08TableData.setNewAllCompleted(tb08TableData.getNewAllCompleted() + 1);
								tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
								tb08TableData.setNewExtrapulmonaryCompleted1517(tb08TableData.getNewExtrapulmonaryCompleted1517() + 1);
								tb08TableData.setNewExtrapulmonaryEligible1517(tb08TableData.getNewExtrapulmonaryEligible1517() + 1);
								tb08TableData.setNewExtrapulmonaryCompleted(tb08TableData.getNewExtrapulmonaryCompleted() + 1);
								tb08TableData.setNewExtrapulmonaryEligible(tb08TableData.getNewExtrapulmonaryEligible() + 1);
							}
							
							else if (diedTB != null && diedTB) {
								tb08TableData.setNewAllDiedTB(tb08TableData.getNewAllDiedTB() + 1);
								tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
								tb08TableData.setNewExtrapulmonaryDiedTB1517(tb08TableData.getNewExtrapulmonaryDiedTB1517() + 1);
								tb08TableData.setNewExtrapulmonaryEligible1517(tb08TableData.getNewExtrapulmonaryEligible1517() + 1);
								tb08TableData.setNewExtrapulmonaryDiedTB(tb08TableData.getNewExtrapulmonaryDiedTB() + 1);
								tb08TableData.setNewExtrapulmonaryEligible(tb08TableData.getNewExtrapulmonaryEligible() + 1);
							}
							
							else if (diedNotTB != null && diedNotTB) {
								tb08TableData.setNewAllDiedNotTB(tb08TableData.getNewAllDiedNotTB() + 1);
								tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
								tb08TableData.setNewExtrapulmonaryDiedNotTB1517(tb08TableData.getNewExtrapulmonaryDiedNotTB1517() + 1);
								tb08TableData.setNewExtrapulmonaryEligible1517(tb08TableData.getNewExtrapulmonaryEligible1517() + 1);
								tb08TableData.setNewExtrapulmonaryDiedNotTB(tb08TableData.getNewExtrapulmonaryDiedNotTB() + 1);
								tb08TableData.setNewExtrapulmonaryEligible(tb08TableData.getNewExtrapulmonaryEligible() + 1);
							}
							
							else if (failed != null && failed) {
								tb08TableData.setNewAllFailed(tb08TableData.getNewAllFailed() + 1);
								tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
								tb08TableData.setNewExtrapulmonaryFailed1517(tb08TableData.getNewExtrapulmonaryFailed1517() + 1);
								tb08TableData.setNewExtrapulmonaryEligible1517(tb08TableData.getNewExtrapulmonaryEligible1517() + 1);
								tb08TableData.setNewExtrapulmonaryFailed(tb08TableData.getNewExtrapulmonaryFailed() + 1);
								tb08TableData.setNewExtrapulmonaryEligible(tb08TableData.getNewExtrapulmonaryEligible() + 1);
							}
							
							else if (defaulted != null && defaulted) {
								tb08TableData.setNewAllDefaulted(tb08TableData.getNewAllDefaulted() + 1);
								tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
								tb08TableData.setNewExtrapulmonaryDefaulted1517(tb08TableData.getNewExtrapulmonaryDefaulted1517() + 1);
								tb08TableData.setNewExtrapulmonaryEligible1517(tb08TableData.getNewExtrapulmonaryEligible1517() + 1);
								tb08TableData.setNewExtrapulmonaryDefaulted(tb08TableData.getNewExtrapulmonaryDefaulted() + 1);
								tb08TableData.setNewExtrapulmonaryEligible(tb08TableData.getNewExtrapulmonaryEligible() + 1);
							}
							
							else if (transferOut != null && transferOut) {
								tb08TableData.setNewAllTransferOut(tb08TableData.getNewAllTransferOut() + 1);
								tb08TableData.setNewExtrapulmonaryTransferOut1517(tb08TableData.getNewExtrapulmonaryTransferOut1517() + 1);
								tb08TableData.setNewExtrapulmonaryTransferOut(tb08TableData.getNewExtrapulmonaryTransferOut() + 1);
								
							}
							
							else if (canceled != null && canceled) {
								tb08TableData.setNewAllCanceled(tb08TableData.getNewAllCanceled() + 1);
								tb08TableData.setNewExtrapulmonaryCanceled1517(tb08TableData.getNewExtrapulmonaryCanceled1517() + 1);
								tb08TableData.setNewExtrapulmonaryCanceled(tb08TableData.getNewExtrapulmonaryCanceled() + 1);
							}
							
							else if (sld != null && sld) {
								tb08TableData.setNewAllSLD(tb08TableData.getNewAllSLD() + 1);
								tb08TableData.setNewExtrapulmonarySLD1517(tb08TableData.getNewExtrapulmonarySLD1517() + 1);
								tb08TableData.setNewExtrapulmonarySLD(tb08TableData.getNewExtrapulmonarySLD() + 1);
							}
							
						}
						
						else {
							
							if (cured != null && cured) {
								tb08TableData.setNewAllCured(tb08TableData.getNewAllCured() + 1);
								tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
								tb08TableData.setNewExtrapulmonaryCured(tb08TableData.getNewExtrapulmonaryCured() + 1);
								tb08TableData.setNewExtrapulmonaryEligible(tb08TableData.getNewExtrapulmonaryEligible() + 1);
							}
							
							else if (txCompleted != null && txCompleted) {
								tb08TableData.setNewAllCompleted(tb08TableData.getNewAllCompleted() + 1);
								tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
								tb08TableData.setNewExtrapulmonaryCompleted(tb08TableData.getNewExtrapulmonaryCompleted() + 1);
								tb08TableData.setNewExtrapulmonaryEligible(tb08TableData.getNewExtrapulmonaryEligible() + 1);
							}
							
							else if (diedTB != null && diedTB) {
								tb08TableData.setNewAllDiedTB(tb08TableData.getNewAllDiedTB() + 1);
								tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
								tb08TableData.setNewExtrapulmonaryDiedTB(tb08TableData.getNewExtrapulmonaryDiedTB() + 1);
								tb08TableData.setNewExtrapulmonaryEligible(tb08TableData.getNewExtrapulmonaryEligible() + 1);
							}
							
							else if (diedNotTB != null && diedNotTB) {
								tb08TableData.setNewAllDiedNotTB(tb08TableData.getNewAllDiedNotTB() + 1);
								tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
								tb08TableData.setNewExtrapulmonaryDiedNotTB(tb08TableData.getNewExtrapulmonaryDiedNotTB() + 1);
								tb08TableData.setNewExtrapulmonaryEligible(tb08TableData.getNewExtrapulmonaryEligible() + 1);
							}
							
							else if (failed != null && failed) {
								tb08TableData.setNewAllFailed(tb08TableData.getNewAllFailed() + 1);
								tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
								tb08TableData.setNewExtrapulmonaryFailed(tb08TableData.getNewExtrapulmonaryFailed() + 1);
								tb08TableData.setNewExtrapulmonaryEligible(tb08TableData.getNewExtrapulmonaryEligible() + 1);
							}
							
							else if (defaulted != null && defaulted) {
								tb08TableData.setNewAllDefaulted(tb08TableData.getNewAllDefaulted() + 1);
								tb08TableData.setNewAllEligible(tb08TableData.getNewAllEligible() + 1);
								tb08TableData.setNewExtrapulmonaryDefaulted(tb08TableData.getNewExtrapulmonaryDefaulted() + 1);
								tb08TableData.setNewExtrapulmonaryEligible(tb08TableData.getNewExtrapulmonaryEligible() + 1);
							}
							
							else if (transferOut != null && transferOut) {
								tb08TableData.setNewAllTransferOut(tb08TableData.getNewAllTransferOut() + 1);
								tb08TableData.setNewExtrapulmonaryTransferOut(tb08TableData.getNewExtrapulmonaryTransferOut() + 1);
								
							}
							
							else if (canceled != null && canceled) {
								tb08TableData.setNewAllCanceled(tb08TableData.getNewAllCanceled() + 1);
								tb08TableData.setNewExtrapulmonaryCanceled(tb08TableData.getNewExtrapulmonaryCanceled() + 1);
								
							}
							
							else if (sld != null && sld) {
								tb08TableData.setNewAllSLD(tb08TableData.getNewAllSLD() + 1);
								tb08TableData.setNewExtrapulmonarySLD(tb08TableData.getNewExtrapulmonarySLD() + 1);
								
							}
						}
						
					}
					
				}
				
				//RELAPSE
				else if (q.getId().equals(
				    Integer.parseInt(Context.getAdministrationService().getGlobalProperty(
				        MdrtbConstants.GP_AFTER_RELAPSE1_CONCEPT_ID)))
				        || q.getId().equals(
				            Integer.parseInt(Context.getAdministrationService().getGlobalProperty(
				                MdrtbConstants.GP_AFTER_RELAPSE2_CONCEPT_ID)))) {
					
					tb08TableData.setRelapseAllDetected(tb08TableData.getRelapseAllDetected() + 1);
					
					//P
					if (pulmonary != null && pulmonary) {
						
						//BC
						if (bacPositive) {
							
							tb08TableData.setRelapsePulmonaryBCDetected(tb08TableData.getRelapsePulmonaryBCDetected() + 1);
							
							if (ageAtRegistration >= 0 && ageAtRegistration < 5) {
								
								tb08TableData.setRelapsePulmonaryBCDetected04(tb08TableData.getRelapsePulmonaryBCDetected04() + 1);
								
								if (cured != null && cured) {
									tb08TableData.setRelapseAllCured(tb08TableData.getRelapseAllCured() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryBCCured04(tb08TableData.getRelapsePulmonaryBCCured04() + 1);
									tb08TableData.setRelapsePulmonaryBCEligible04(tb08TableData.getRelapsePulmonaryBCEligible04() + 1);
									tb08TableData.setRelapsePulmonaryBCCured(tb08TableData.getRelapsePulmonaryBCCured() + 1);
									tb08TableData.setRelapsePulmonaryBCEligible(tb08TableData.getRelapsePulmonaryBCEligible() + 1);
									
								}
								
								else if (txCompleted != null && txCompleted) {
									tb08TableData.setRelapseAllCompleted(tb08TableData.getRelapseAllCompleted() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryBCCompleted04(tb08TableData.getRelapsePulmonaryBCCompleted04() + 1);
									tb08TableData.setRelapsePulmonaryBCEligible04(tb08TableData.getRelapsePulmonaryBCEligible04() + 1);
									tb08TableData.setRelapsePulmonaryBCCompleted(tb08TableData.getRelapsePulmonaryBCCompleted() + 1);
									tb08TableData.setRelapsePulmonaryBCEligible(tb08TableData.getRelapsePulmonaryBCEligible() + 1);
								}
								
								else if (diedTB != null && diedTB) {
									tb08TableData.setRelapseAllDiedTB(tb08TableData.getRelapseAllDiedTB() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryBCDiedTB04(tb08TableData.getRelapsePulmonaryBCDiedTB04() + 1);
									tb08TableData.setRelapsePulmonaryBCEligible04(tb08TableData.getRelapsePulmonaryBCEligible04() + 1);
									tb08TableData.setRelapsePulmonaryBCDiedTB(tb08TableData.getRelapsePulmonaryBCDiedTB() + 1);
									tb08TableData.setRelapsePulmonaryBCEligible(tb08TableData.getRelapsePulmonaryBCEligible() + 1);
								}
								
								else if (diedNotTB != null && diedNotTB) {
									tb08TableData.setRelapseAllDiedNotTB(tb08TableData.getRelapseAllDiedNotTB() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryBCDiedNotTB04(tb08TableData.getRelapsePulmonaryBCDiedNotTB04() + 1);
									tb08TableData.setRelapsePulmonaryBCEligible04(tb08TableData.getRelapsePulmonaryBCEligible04() + 1);
									tb08TableData.setRelapsePulmonaryBCDiedNotTB(tb08TableData.getRelapsePulmonaryBCDiedNotTB() + 1);
									tb08TableData.setRelapsePulmonaryBCEligible(tb08TableData.getRelapsePulmonaryBCEligible() + 1);
								}
								
								else if (failed != null && failed) {
									tb08TableData.setRelapseAllFailed(tb08TableData.getRelapseAllFailed() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryBCFailed04(tb08TableData.getRelapsePulmonaryBCFailed04() + 1);
									tb08TableData.setRelapsePulmonaryBCEligible04(tb08TableData.getRelapsePulmonaryBCEligible04() + 1);
									tb08TableData.setRelapsePulmonaryBCFailed(tb08TableData.getRelapsePulmonaryBCFailed() + 1);
									tb08TableData.setRelapsePulmonaryBCEligible(tb08TableData.getRelapsePulmonaryBCEligible() + 1);
								}
								
								else if (defaulted != null && defaulted) {
									tb08TableData.setRelapseAllDefaulted(tb08TableData.getRelapseAllDefaulted() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryBCDefaulted04(tb08TableData.getRelapsePulmonaryBCDefaulted04() + 1);
									tb08TableData.setRelapsePulmonaryBCEligible04(tb08TableData.getRelapsePulmonaryBCEligible04() + 1);
									tb08TableData.setRelapsePulmonaryBCDefaulted(tb08TableData.getRelapsePulmonaryBCDefaulted() + 1);
									tb08TableData.setRelapsePulmonaryBCEligible(tb08TableData.getRelapsePulmonaryBCEligible() + 1);
								}
								
								else if (transferOut != null && transferOut) {
									tb08TableData.setRelapseAllTransferOut(tb08TableData.getRelapseAllTransferOut() + 1);
									tb08TableData.setRelapsePulmonaryBCTransferOut04(tb08TableData.getRelapsePulmonaryBCTransferOut04() + 1);
									tb08TableData.setRelapsePulmonaryBCTransferOut(tb08TableData.getRelapsePulmonaryBCTransferOut() + 1);
									
								}
								
								else if (canceled != null && canceled) {
									tb08TableData.setRelapseAllCanceled(tb08TableData.getRelapseAllCanceled() + 1);
									tb08TableData.setRelapsePulmonaryBCCanceled04(tb08TableData.getRelapsePulmonaryBCCanceled04() + 1);
									tb08TableData.setRelapsePulmonaryBCCanceled(tb08TableData.getRelapsePulmonaryBCCanceled() + 1);
								}
								
								else if (sld != null && sld) {
									tb08TableData.setRelapseAllSLD(tb08TableData.getRelapseAllSLD() + 1);
									tb08TableData.setRelapsePulmonaryBCSLD04(tb08TableData.getRelapsePulmonaryBCSLD04() + 1);
									tb08TableData.setRelapsePulmonaryBCSLD(tb08TableData.getRelapsePulmonaryBCSLD() + 1);
								}
								
							}
							
							else if (ageAtRegistration >= 5 && ageAtRegistration < 15) {
								
								tb08TableData.setRelapsePulmonaryBCDetected0514(tb08TableData.getRelapsePulmonaryBCDetected0514() + 1);
								
								if (cured != null && cured) {
									tb08TableData.setRelapseAllCured(tb08TableData.getRelapseAllCured() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryBCCured0514(tb08TableData.getRelapsePulmonaryBCCured0514() + 1);
									tb08TableData.setRelapsePulmonaryBCEligible0514(tb08TableData.getRelapsePulmonaryBCEligible0514() + 1);
									tb08TableData.setRelapsePulmonaryBCCured(tb08TableData.getRelapsePulmonaryBCCured() + 1);
									tb08TableData.setRelapsePulmonaryBCEligible(tb08TableData.getRelapsePulmonaryBCEligible() + 1);
								}
								
								else if (txCompleted != null && txCompleted) {
									tb08TableData.setRelapseAllCompleted(tb08TableData.getRelapseAllCompleted() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryBCCompleted0514(tb08TableData.getRelapsePulmonaryBCCompleted0514() + 1);
									tb08TableData.setRelapsePulmonaryBCEligible0514(tb08TableData.getRelapsePulmonaryBCEligible0514() + 1);
									tb08TableData.setRelapsePulmonaryBCCompleted(tb08TableData.getRelapsePulmonaryBCCompleted() + 1);
									tb08TableData.setRelapsePulmonaryBCEligible(tb08TableData.getRelapsePulmonaryBCEligible() + 1);
								}
								
								else if (diedTB != null && diedTB) {
									tb08TableData.setRelapseAllDiedTB(tb08TableData.getRelapseAllDiedTB() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryBCDiedTB0514(tb08TableData.getRelapsePulmonaryBCDiedTB0514() + 1);
									tb08TableData.setRelapsePulmonaryBCEligible0514(tb08TableData.getRelapsePulmonaryBCEligible0514() + 1);
									tb08TableData.setRelapsePulmonaryBCDiedTB(tb08TableData.getRelapsePulmonaryBCDiedTB() + 1);
									tb08TableData.setRelapsePulmonaryBCEligible(tb08TableData.getRelapsePulmonaryBCEligible() + 1);
								}
								
								else if (diedNotTB != null && diedNotTB) {
									tb08TableData.setRelapseAllDiedNotTB(tb08TableData.getRelapseAllDiedNotTB() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryBCDiedNotTB0514(tb08TableData.getRelapsePulmonaryBCDiedNotTB0514() + 1);
									tb08TableData.setRelapsePulmonaryBCEligible0514(tb08TableData.getRelapsePulmonaryBCEligible0514() + 1);
									tb08TableData.setRelapsePulmonaryBCDiedNotTB(tb08TableData.getRelapsePulmonaryBCDiedNotTB() + 1);
									tb08TableData.setRelapsePulmonaryBCEligible(tb08TableData.getRelapsePulmonaryBCEligible() + 1);
								}
								
								else if (failed != null && failed) {
									tb08TableData.setRelapseAllFailed(tb08TableData.getRelapseAllFailed() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryBCFailed0514(tb08TableData.getRelapsePulmonaryBCFailed0514() + 1);
									tb08TableData.setRelapsePulmonaryBCEligible0514(tb08TableData.getRelapsePulmonaryBCEligible0514() + 1);
									tb08TableData.setRelapsePulmonaryBCFailed(tb08TableData.getRelapsePulmonaryBCFailed() + 1);
									tb08TableData.setRelapsePulmonaryBCEligible(tb08TableData.getRelapsePulmonaryBCEligible() + 1);
								}
								
								else if (defaulted != null && defaulted) {
									tb08TableData.setRelapseAllDefaulted(tb08TableData.getRelapseAllDefaulted() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryBCDefaulted0514(tb08TableData.getRelapsePulmonaryBCDefaulted0514() + 1);
									tb08TableData.setRelapsePulmonaryBCEligible0514(tb08TableData.getRelapsePulmonaryBCEligible0514() + 1);
									tb08TableData.setRelapsePulmonaryBCDefaulted(tb08TableData.getRelapsePulmonaryBCDefaulted() + 1);
									tb08TableData.setRelapsePulmonaryBCEligible(tb08TableData.getRelapsePulmonaryBCEligible() + 1);
								}
								
								else if (transferOut != null && transferOut) {
									tb08TableData.setRelapseAllTransferOut(tb08TableData.getRelapseAllTransferOut() + 1);
									tb08TableData.setRelapsePulmonaryBCTransferOut0514(tb08TableData
									        .getRelapsePulmonaryBCTransferOut0514() + 1);
									tb08TableData.setRelapsePulmonaryBCTransferOut(tb08TableData.getRelapsePulmonaryBCTransferOut() + 1);
								}
								
								else if (canceled != null && canceled) {
									tb08TableData.setRelapseAllCanceled(tb08TableData.getRelapseAllCanceled() + 1);
									tb08TableData.setRelapsePulmonaryBCCanceled0514(tb08TableData.getRelapsePulmonaryBCCanceled0514() + 1);
									tb08TableData.setRelapsePulmonaryBCCanceled(tb08TableData.getRelapsePulmonaryBCCanceled() + 1);
								}
								
								else if (sld != null && sld) {
									tb08TableData.setRelapseAllSLD(tb08TableData.getRelapseAllSLD() + 1);
									tb08TableData.setRelapsePulmonaryBCSLD0514(tb08TableData.getRelapsePulmonaryBCSLD0514() + 1);
									tb08TableData.setRelapsePulmonaryBCSLD(tb08TableData.getRelapsePulmonaryBCSLD() + 1);
								}
								
							}
							
							else if (ageAtRegistration >= 15 && ageAtRegistration < 18) {
								
								tb08TableData.setRelapsePulmonaryBCDetected1517(tb08TableData.getRelapsePulmonaryBCDetected1517() + 1);
								
								if (cured != null && cured) {
									tb08TableData.setRelapseAllCured(tb08TableData.getRelapseAllCured() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryBCCured1517(tb08TableData.getRelapsePulmonaryBCCured1517() + 1);
									tb08TableData.setRelapsePulmonaryBCEligible1517(tb08TableData.getRelapsePulmonaryBCEligible1517() + 1);
									tb08TableData.setRelapsePulmonaryBCCured(tb08TableData.getRelapsePulmonaryBCCured() + 1);
									tb08TableData.setRelapsePulmonaryBCEligible(tb08TableData.getRelapsePulmonaryBCEligible() + 1);
								}
								
								else if (txCompleted != null && txCompleted) {
									tb08TableData.setRelapseAllCompleted(tb08TableData.getRelapseAllCompleted() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryBCCompleted1517(tb08TableData.getRelapsePulmonaryBCCompleted1517() + 1);
									tb08TableData.setRelapsePulmonaryBCEligible1517(tb08TableData.getRelapsePulmonaryBCEligible1517() + 1);
									tb08TableData.setRelapsePulmonaryBCCompleted(tb08TableData.getRelapsePulmonaryBCCompleted() + 1);
									tb08TableData.setRelapsePulmonaryBCEligible(tb08TableData.getRelapsePulmonaryBCEligible() + 1);
								}
								
								else if (diedTB != null && diedTB) {
									tb08TableData.setRelapseAllDiedTB(tb08TableData.getRelapseAllDiedTB() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryBCDiedTB1517(tb08TableData.getRelapsePulmonaryBCDiedTB1517() + 1);
									tb08TableData.setRelapsePulmonaryBCEligible1517(tb08TableData.getRelapsePulmonaryBCEligible1517() + 1);
									tb08TableData.setRelapsePulmonaryBCDiedTB(tb08TableData.getRelapsePulmonaryBCDiedTB() + 1);
									tb08TableData.setRelapsePulmonaryBCEligible(tb08TableData.getRelapsePulmonaryBCEligible() + 1);
								}
								
								else if (diedNotTB != null && diedNotTB) {
									tb08TableData.setRelapseAllDiedNotTB(tb08TableData.getRelapseAllDiedNotTB() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryBCDiedNotTB1517(tb08TableData.getRelapsePulmonaryBCDiedNotTB1517() + 1);
									tb08TableData.setRelapsePulmonaryBCEligible1517(tb08TableData.getRelapsePulmonaryBCEligible1517() + 1);
									tb08TableData.setRelapsePulmonaryBCDiedNotTB(tb08TableData.getRelapsePulmonaryBCDiedNotTB() + 1);
									tb08TableData.setRelapsePulmonaryBCEligible(tb08TableData.getRelapsePulmonaryBCEligible() + 1);
								}
								
								else if (failed != null && failed) {
									tb08TableData.setRelapseAllFailed(tb08TableData.getRelapseAllFailed() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryBCFailed1517(tb08TableData.getRelapsePulmonaryBCFailed1517() + 1);
									tb08TableData.setRelapsePulmonaryBCEligible1517(tb08TableData.getRelapsePulmonaryBCEligible1517() + 1);
									tb08TableData.setRelapsePulmonaryBCFailed(tb08TableData.getRelapsePulmonaryBCFailed() + 1);
									tb08TableData.setRelapsePulmonaryBCEligible(tb08TableData.getRelapsePulmonaryBCEligible() + 1);
								}
								
								else if (defaulted != null && defaulted) {
									tb08TableData.setRelapseAllDefaulted(tb08TableData.getRelapseAllDefaulted() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryBCDefaulted1517(tb08TableData.getRelapsePulmonaryBCDefaulted1517() + 1);
									tb08TableData.setRelapsePulmonaryBCEligible1517(tb08TableData.getRelapsePulmonaryBCEligible1517() + 1);
									tb08TableData.setRelapsePulmonaryBCDefaulted(tb08TableData.getRelapsePulmonaryBCDefaulted() + 1);
									tb08TableData.setRelapsePulmonaryBCEligible(tb08TableData.getRelapsePulmonaryBCEligible() + 1);
								}
								
								else if (transferOut != null && transferOut) {
									tb08TableData.setRelapseAllTransferOut(tb08TableData.getRelapseAllTransferOut() + 1);
									tb08TableData.setRelapsePulmonaryBCTransferOut1517(tb08TableData
									        .getRelapsePulmonaryBCTransferOut1517() + 1);
									tb08TableData.setRelapsePulmonaryBCTransferOut(tb08TableData.getRelapsePulmonaryBCTransferOut() + 1);
								}
								
								else if (canceled != null && canceled) {
									tb08TableData.setRelapseAllCanceled(tb08TableData.getRelapseAllCanceled() + 1);
									tb08TableData.setRelapsePulmonaryBCCanceled1517(tb08TableData.getRelapsePulmonaryBCCanceled1517() + 1);
									tb08TableData.setRelapsePulmonaryBCCanceled(tb08TableData.getRelapsePulmonaryBCCanceled() + 1);
								}
								
								else if (sld != null && sld) {
									tb08TableData.setRelapseAllSLD(tb08TableData.getRelapseAllSLD() + 1);
									tb08TableData.setRelapsePulmonaryBCSLD1517(tb08TableData.getRelapsePulmonaryBCSLD1517() + 1);
									tb08TableData.setRelapsePulmonaryBCSLD(tb08TableData.getRelapsePulmonaryBCSLD() + 1);
								}
								
							}
							
							else {
								
								if (cured != null && cured) {
									tb08TableData.setRelapseAllCured(tb08TableData.getRelapseAllCured() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryBCCured(tb08TableData.getRelapsePulmonaryBCCured() + 1);
									tb08TableData.setRelapsePulmonaryBCEligible(tb08TableData.getRelapsePulmonaryBCEligible() + 1);
								}
								
								else if (txCompleted != null && txCompleted) {
									tb08TableData.setRelapseAllCompleted(tb08TableData.getRelapseAllCompleted() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryBCCompleted(tb08TableData.getRelapsePulmonaryBCCompleted() + 1);
									tb08TableData.setRelapsePulmonaryBCEligible(tb08TableData.getRelapsePulmonaryBCEligible() + 1);
								}
								
								else if (diedTB != null && diedTB) {
									tb08TableData.setRelapseAllDiedTB(tb08TableData.getRelapseAllDiedTB() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryBCDiedTB(tb08TableData.getRelapsePulmonaryBCDiedTB() + 1);
									tb08TableData.setRelapsePulmonaryBCEligible(tb08TableData.getRelapsePulmonaryBCEligible() + 1);
								}
								
								else if (diedNotTB != null && diedNotTB) {
									tb08TableData.setRelapseAllDiedNotTB(tb08TableData.getRelapseAllDiedNotTB() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryBCDiedNotTB(tb08TableData.getRelapsePulmonaryBCDiedNotTB() + 1);
									tb08TableData.setRelapsePulmonaryBCEligible(tb08TableData.getRelapsePulmonaryBCEligible() + 1);
								}
								
								else if (failed != null && failed) {
									tb08TableData.setRelapseAllFailed(tb08TableData.getRelapseAllFailed() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryBCFailed(tb08TableData.getRelapsePulmonaryBCFailed() + 1);
									tb08TableData.setRelapsePulmonaryBCEligible(tb08TableData.getRelapsePulmonaryBCEligible() + 1);
								}
								
								else if (defaulted != null && defaulted) {
									tb08TableData.setRelapseAllDefaulted(tb08TableData.getRelapseAllDefaulted() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryBCDefaulted(tb08TableData.getRelapsePulmonaryBCDefaulted() + 1);
									tb08TableData.setRelapsePulmonaryBCEligible(tb08TableData.getRelapsePulmonaryBCEligible() + 1);
								}
								
								else if (transferOut != null && transferOut) {
									tb08TableData.setRelapseAllTransferOut(tb08TableData.getRelapseAllTransferOut() + 1);
									tb08TableData.setRelapsePulmonaryBCTransferOut(tb08TableData.getRelapsePulmonaryBCTransferOut() + 1);
									
								}
								
								else if (canceled != null && canceled) {
									tb08TableData.setRelapseAllCanceled(tb08TableData.getRelapseAllCanceled() + 1);
									tb08TableData.setRelapsePulmonaryBCCanceled(tb08TableData.getRelapsePulmonaryBCCanceled() + 1);
									
								}
								
								else if (sld != null && sld) {
									tb08TableData.setRelapseAllSLD(tb08TableData.getRelapseAllSLD() + 1);
									tb08TableData.setRelapsePulmonaryBCSLD(tb08TableData.getRelapsePulmonaryBCSLD() + 1);
									
								}
							}
						}
						
						//CD
						else {
							
							tb08TableData.setRelapsePulmonaryCDDetected(tb08TableData.getRelapsePulmonaryCDDetected() + 1);
							
							if (ageAtRegistration >= 0 && ageAtRegistration < 5) {
								
								tb08TableData.setRelapsePulmonaryCDDetected04(tb08TableData.getRelapsePulmonaryCDDetected04() + 1);
								
								if (cured != null && cured) {
									tb08TableData.setRelapseAllCured(tb08TableData.getRelapseAllCured() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryCDCured04(tb08TableData.getRelapsePulmonaryCDCured04() + 1);
									tb08TableData.setRelapsePulmonaryCDEligible04(tb08TableData.getRelapsePulmonaryCDEligible04() + 1);
									tb08TableData.setRelapsePulmonaryCDCured(tb08TableData.getRelapsePulmonaryCDCured() + 1);
									tb08TableData.setRelapsePulmonaryCDEligible(tb08TableData.getRelapsePulmonaryCDEligible() + 1);
								}
								
								else if (txCompleted != null && txCompleted) {
									tb08TableData.setRelapseAllCompleted(tb08TableData.getRelapseAllCompleted() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryCDCompleted04(tb08TableData.getRelapsePulmonaryCDCompleted04() + 1);
									tb08TableData.setRelapsePulmonaryCDEligible04(tb08TableData.getRelapsePulmonaryCDEligible04() + 1);
									tb08TableData.setRelapsePulmonaryCDCompleted(tb08TableData.getRelapsePulmonaryCDCompleted() + 1);
									tb08TableData.setRelapsePulmonaryCDEligible(tb08TableData.getRelapsePulmonaryCDEligible() + 1);
								}
								
								else if (diedTB != null && diedTB) {
									tb08TableData.setRelapseAllDiedTB(tb08TableData.getRelapseAllDiedTB() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryCDDiedTB04(tb08TableData.getRelapsePulmonaryCDDiedTB04() + 1);
									tb08TableData.setRelapsePulmonaryCDEligible04(tb08TableData.getRelapsePulmonaryCDEligible04() + 1);
									tb08TableData.setRelapsePulmonaryCDDiedTB(tb08TableData.getRelapsePulmonaryCDDiedTB() + 1);
									tb08TableData.setRelapsePulmonaryCDEligible(tb08TableData.getRelapsePulmonaryCDEligible() + 1);
								}
								
								else if (diedNotTB != null && diedNotTB) {
									tb08TableData.setRelapseAllDiedNotTB(tb08TableData.getRelapseAllDiedNotTB() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryCDDiedNotTB04(tb08TableData.getRelapsePulmonaryCDDiedNotTB04() + 1);
									tb08TableData.setRelapsePulmonaryCDEligible04(tb08TableData.getRelapsePulmonaryCDEligible04() + 1);
									tb08TableData.setRelapsePulmonaryCDDiedNotTB(tb08TableData.getRelapsePulmonaryCDDiedNotTB() + 1);
									tb08TableData.setRelapsePulmonaryCDEligible(tb08TableData.getRelapsePulmonaryCDEligible() + 1);
								}
								
								else if (failed != null && failed) {
									tb08TableData.setRelapseAllFailed(tb08TableData.getRelapseAllFailed() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryCDFailed04(tb08TableData.getRelapsePulmonaryCDFailed04() + 1);
									tb08TableData.setRelapsePulmonaryCDEligible04(tb08TableData.getRelapsePulmonaryCDEligible04() + 1);
									tb08TableData.setRelapsePulmonaryCDFailed(tb08TableData.getRelapsePulmonaryCDFailed() + 1);
									tb08TableData.setRelapsePulmonaryCDEligible(tb08TableData.getRelapsePulmonaryCDEligible() + 1);
								}
								
								else if (defaulted != null && defaulted) {
									tb08TableData.setRelapseAllDefaulted(tb08TableData.getRelapseAllDefaulted() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryCDDefaulted04(tb08TableData.getRelapsePulmonaryCDDefaulted04() + 1);
									tb08TableData.setRelapsePulmonaryCDEligible04(tb08TableData.getRelapsePulmonaryCDEligible04() + 1);
									tb08TableData.setRelapsePulmonaryCDDefaulted(tb08TableData.getRelapsePulmonaryCDDefaulted() + 1);
									tb08TableData.setRelapsePulmonaryCDEligible(tb08TableData.getRelapsePulmonaryCDEligible() + 1);
								}
								
								else if (transferOut != null && transferOut) {
									tb08TableData.setRelapseAllTransferOut(tb08TableData.getRelapseAllTransferOut() + 1);
									tb08TableData.setRelapsePulmonaryCDTransferOut04(tb08TableData.getRelapsePulmonaryCDTransferOut04() + 1);
									tb08TableData.setRelapsePulmonaryCDTransferOut(tb08TableData.getRelapsePulmonaryCDTransferOut() + 1);
								}
								
								else if (canceled != null && canceled) {
									tb08TableData.setRelapseAllCanceled(tb08TableData.getRelapseAllCanceled() + 1);
									tb08TableData.setRelapsePulmonaryCDCanceled04(tb08TableData.getRelapsePulmonaryCDCanceled04() + 1);
									tb08TableData.setRelapsePulmonaryCDCanceled(tb08TableData.getRelapsePulmonaryCDCanceled() + 1);
								}
								
								else if (sld != null && sld) {
									tb08TableData.setRelapseAllSLD(tb08TableData.getRelapseAllSLD() + 1);
									tb08TableData.setRelapsePulmonaryCDSLD04(tb08TableData.getRelapsePulmonaryCDSLD04() + 1);
									tb08TableData.setRelapsePulmonaryCDSLD(tb08TableData.getRelapsePulmonaryCDSLD() + 1);
								}
								
							}
							
							else if (ageAtRegistration >= 5 && ageAtRegistration < 15) {
								
								tb08TableData.setRelapsePulmonaryCDDetected0514(tb08TableData.getRelapsePulmonaryCDDetected0514() + 1);
								
								if (cured != null && cured) {
									tb08TableData.setRelapseAllCured(tb08TableData.getRelapseAllCured() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryCDCured0514(tb08TableData.getRelapsePulmonaryCDCured0514() + 1);
									tb08TableData.setRelapsePulmonaryCDEligible0514(tb08TableData.getRelapsePulmonaryCDEligible0514() + 1);
									tb08TableData.setRelapsePulmonaryCDCured(tb08TableData.getRelapsePulmonaryCDCured() + 1);
									tb08TableData.setRelapsePulmonaryCDEligible(tb08TableData.getRelapsePulmonaryCDEligible() + 1);
								}
								
								else if (txCompleted != null && txCompleted) {
									tb08TableData.setRelapseAllCompleted(tb08TableData.getRelapseAllCompleted() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryCDCompleted0514(tb08TableData.getRelapsePulmonaryCDCompleted0514() + 1);
									tb08TableData.setRelapsePulmonaryCDEligible0514(tb08TableData.getRelapsePulmonaryCDEligible0514() + 1);
									tb08TableData.setRelapsePulmonaryCDCompleted(tb08TableData.getRelapsePulmonaryCDCompleted() + 1);
									tb08TableData.setRelapsePulmonaryCDEligible(tb08TableData.getRelapsePulmonaryCDEligible() + 1);
								}
								
								else if (diedTB != null && diedTB) {
									tb08TableData.setRelapseAllDiedTB(tb08TableData.getRelapseAllDiedTB() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryCDDiedTB0514(tb08TableData.getRelapsePulmonaryCDDiedTB0514() + 1);
									tb08TableData.setRelapsePulmonaryCDEligible0514(tb08TableData.getRelapsePulmonaryCDEligible0514() + 1);
									tb08TableData.setRelapsePulmonaryCDDiedTB(tb08TableData.getRelapsePulmonaryCDDiedTB() + 1);
									tb08TableData.setRelapsePulmonaryCDEligible(tb08TableData.getRelapsePulmonaryCDEligible() + 1);
								}
								
								else if (diedNotTB != null && diedNotTB) {
									tb08TableData.setRelapseAllDiedNotTB(tb08TableData.getRelapseAllDiedNotTB() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryCDDiedNotTB0514(tb08TableData.getRelapsePulmonaryCDDiedNotTB0514() + 1);
									tb08TableData.setRelapsePulmonaryCDEligible0514(tb08TableData.getRelapsePulmonaryCDEligible0514() + 1);
									tb08TableData.setRelapsePulmonaryCDDiedNotTB(tb08TableData.getRelapsePulmonaryCDDiedNotTB() + 1);
									tb08TableData.setRelapsePulmonaryCDEligible(tb08TableData.getRelapsePulmonaryCDEligible() + 1);
								}
								
								else if (failed != null && failed) {
									tb08TableData.setRelapseAllFailed(tb08TableData.getRelapseAllFailed() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryCDFailed0514(tb08TableData.getRelapsePulmonaryCDFailed0514() + 1);
									tb08TableData.setRelapsePulmonaryCDEligible0514(tb08TableData.getRelapsePulmonaryCDEligible0514() + 1);
									tb08TableData.setRelapsePulmonaryCDFailed(tb08TableData.getRelapsePulmonaryCDFailed() + 1);
									tb08TableData.setRelapsePulmonaryCDEligible(tb08TableData.getRelapsePulmonaryCDEligible() + 1);
								}
								
								else if (defaulted != null && defaulted) {
									tb08TableData.setRelapseAllDefaulted(tb08TableData.getRelapseAllDefaulted() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryCDDefaulted0514(tb08TableData.getRelapsePulmonaryCDDefaulted0514() + 1);
									tb08TableData.setRelapsePulmonaryCDEligible0514(tb08TableData.getRelapsePulmonaryCDEligible0514() + 1);
									tb08TableData.setRelapsePulmonaryCDDefaulted(tb08TableData.getRelapsePulmonaryCDDefaulted() + 1);
									tb08TableData.setRelapsePulmonaryCDEligible(tb08TableData.getRelapsePulmonaryCDEligible() + 1);
								}
								
								else if (transferOut != null && transferOut) {
									tb08TableData.setRelapseAllTransferOut(tb08TableData.getRelapseAllTransferOut() + 1);
									tb08TableData.setRelapsePulmonaryCDTransferOut0514(tb08TableData
									        .getRelapsePulmonaryCDTransferOut0514() + 1);
									tb08TableData.setRelapsePulmonaryCDTransferOut(tb08TableData.getRelapsePulmonaryCDTransferOut() + 1);
									
								}
								
								else if (canceled != null && canceled) {
									tb08TableData.setRelapseAllCanceled(tb08TableData.getRelapseAllCanceled() + 1);
									tb08TableData.setRelapsePulmonaryCDCanceled0514(tb08TableData.getRelapsePulmonaryCDCanceled0514() + 1);
									tb08TableData.setRelapsePulmonaryCDCanceled(tb08TableData.getRelapsePulmonaryCDCanceled() + 1);
								}
								
								else if (sld != null && sld) {
									tb08TableData.setRelapseAllSLD(tb08TableData.getRelapseAllSLD() + 1);
									tb08TableData.setRelapsePulmonaryCDSLD0514(tb08TableData.getRelapsePulmonaryCDSLD0514() + 1);
									tb08TableData.setRelapsePulmonaryCDSLD(tb08TableData.getRelapsePulmonaryCDSLD() + 1);
								}
								
							}
							
							else if (ageAtRegistration >= 15 && ageAtRegistration < 18) {
								
								tb08TableData.setRelapsePulmonaryCDDetected1517(tb08TableData.getRelapsePulmonaryCDDetected1517() + 1);
								
								if (cured != null && cured) {
									tb08TableData.setRelapseAllCured(tb08TableData.getRelapseAllCured() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryCDCured1517(tb08TableData.getRelapsePulmonaryCDCured1517() + 1);
									tb08TableData.setRelapsePulmonaryCDEligible1517(tb08TableData.getRelapsePulmonaryCDEligible1517() + 1);
									tb08TableData.setRelapsePulmonaryCDCured(tb08TableData.getRelapsePulmonaryCDCured() + 1);
									tb08TableData.setRelapsePulmonaryCDEligible(tb08TableData.getRelapsePulmonaryCDEligible() + 1);
								}
								
								else if (txCompleted != null && txCompleted) {
									tb08TableData.setRelapseAllCompleted(tb08TableData.getRelapseAllCompleted() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryCDCompleted1517(tb08TableData.getRelapsePulmonaryCDCompleted1517() + 1);
									tb08TableData.setRelapsePulmonaryCDEligible1517(tb08TableData.getRelapsePulmonaryCDEligible1517() + 1);
									tb08TableData.setRelapsePulmonaryCDCompleted(tb08TableData.getRelapsePulmonaryCDCompleted() + 1);
									tb08TableData.setRelapsePulmonaryCDEligible(tb08TableData.getRelapsePulmonaryCDEligible() + 1);
								}
								
								else if (diedTB != null && diedTB) {
									tb08TableData.setRelapseAllDiedTB(tb08TableData.getRelapseAllDiedTB() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryCDDiedTB1517(tb08TableData.getRelapsePulmonaryCDDiedTB1517() + 1);
									tb08TableData.setRelapsePulmonaryCDEligible1517(tb08TableData.getRelapsePulmonaryCDEligible1517() + 1);
									tb08TableData.setRelapsePulmonaryCDDiedTB(tb08TableData.getRelapsePulmonaryCDDiedTB() + 1);
									tb08TableData.setRelapsePulmonaryCDEligible(tb08TableData.getRelapsePulmonaryCDEligible() + 1);
								}
								
								else if (diedNotTB != null && diedNotTB) {
									tb08TableData.setRelapseAllDiedNotTB(tb08TableData.getRelapseAllDiedNotTB() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryCDDiedNotTB1517(tb08TableData.getRelapsePulmonaryCDDiedNotTB1517() + 1);
									tb08TableData.setRelapsePulmonaryCDEligible1517(tb08TableData.getRelapsePulmonaryCDEligible1517() + 1);
									tb08TableData.setRelapsePulmonaryCDDiedNotTB(tb08TableData.getRelapsePulmonaryCDDiedNotTB() + 1);
									tb08TableData.setRelapsePulmonaryCDEligible(tb08TableData.getRelapsePulmonaryCDEligible() + 1);
								}
								
								else if (failed != null && failed) {
									tb08TableData.setRelapseAllFailed(tb08TableData.getRelapseAllFailed() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryCDFailed1517(tb08TableData.getRelapsePulmonaryCDFailed1517() + 1);
									tb08TableData.setRelapsePulmonaryCDEligible1517(tb08TableData.getRelapsePulmonaryCDEligible1517() + 1);
									tb08TableData.setRelapsePulmonaryCDFailed(tb08TableData.getRelapsePulmonaryCDFailed() + 1);
									tb08TableData.setRelapsePulmonaryCDEligible(tb08TableData.getRelapsePulmonaryCDEligible() + 1);
								}
								
								else if (defaulted != null && defaulted) {
									tb08TableData.setRelapseAllDefaulted(tb08TableData.getRelapseAllDefaulted() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryCDDefaulted1517(tb08TableData.getRelapsePulmonaryCDDefaulted1517() + 1);
									tb08TableData.setRelapsePulmonaryCDEligible1517(tb08TableData.getRelapsePulmonaryCDEligible1517() + 1);
									tb08TableData.setRelapsePulmonaryCDDefaulted(tb08TableData.getRelapsePulmonaryCDDefaulted() + 1);
									tb08TableData.setRelapsePulmonaryCDEligible(tb08TableData.getRelapsePulmonaryCDEligible() + 1);
								}
								
								else if (transferOut != null && transferOut) {
									tb08TableData.setRelapseAllTransferOut(tb08TableData.getRelapseAllTransferOut() + 1);
									tb08TableData.setRelapsePulmonaryCDTransferOut1517(tb08TableData
									        .getRelapsePulmonaryCDTransferOut1517() + 1);
									tb08TableData.setRelapsePulmonaryCDTransferOut(tb08TableData.getRelapsePulmonaryCDTransferOut() + 1);
									
								}
								
								else if (canceled != null && canceled) {
									tb08TableData.setRelapseAllCanceled(tb08TableData.getRelapseAllCanceled() + 1);
									tb08TableData.setRelapsePulmonaryCDCanceled1517(tb08TableData.getRelapsePulmonaryCDCanceled1517() + 1);
									tb08TableData.setRelapsePulmonaryCDCanceled(tb08TableData.getRelapsePulmonaryCDCanceled() + 1);
								}
								
								else if (sld != null && sld) {
									tb08TableData.setRelapseAllSLD(tb08TableData.getRelapseAllSLD() + 1);
									tb08TableData.setRelapsePulmonaryCDSLD1517(tb08TableData.getRelapsePulmonaryCDSLD1517() + 1);
									tb08TableData.setRelapsePulmonaryCDSLD(tb08TableData.getRelapsePulmonaryCDSLD() + 1);
								}
								
							}
							
							else {
								
								if (cured != null && cured) {
									tb08TableData.setRelapseAllCured(tb08TableData.getRelapseAllCured() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryCDCured(tb08TableData.getRelapsePulmonaryCDCured() + 1);
									tb08TableData.setRelapsePulmonaryCDEligible(tb08TableData.getRelapsePulmonaryCDEligible() + 1);
								}
								
								else if (txCompleted != null && txCompleted) {
									tb08TableData.setRelapseAllCompleted(tb08TableData.getRelapseAllCompleted() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryCDCompleted(tb08TableData.getRelapsePulmonaryCDCompleted() + 1);
									tb08TableData.setRelapsePulmonaryCDEligible(tb08TableData.getRelapsePulmonaryCDEligible() + 1);
								}
								
								else if (diedTB != null && diedTB) {
									tb08TableData.setRelapseAllDiedTB(tb08TableData.getRelapseAllDiedTB() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryCDDiedTB(tb08TableData.getRelapsePulmonaryCDDiedTB() + 1);
									tb08TableData.setRelapsePulmonaryCDEligible(tb08TableData.getRelapsePulmonaryCDEligible() + 1);
								}
								
								else if (diedNotTB != null && diedNotTB) {
									tb08TableData.setRelapseAllDiedNotTB(tb08TableData.getRelapseAllDiedNotTB() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryCDDiedNotTB(tb08TableData.getRelapsePulmonaryCDDiedNotTB() + 1);
									tb08TableData.setRelapsePulmonaryCDEligible(tb08TableData.getRelapsePulmonaryCDEligible() + 1);
								}
								
								else if (failed != null && failed) {
									tb08TableData.setRelapseAllFailed(tb08TableData.getRelapseAllFailed() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryCDFailed(tb08TableData.getRelapsePulmonaryCDFailed() + 1);
									tb08TableData.setRelapsePulmonaryCDEligible(tb08TableData.getRelapsePulmonaryCDEligible() + 1);
								}
								
								else if (defaulted != null && defaulted) {
									tb08TableData.setRelapseAllDefaulted(tb08TableData.getRelapseAllDefaulted() + 1);
									tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
									tb08TableData.setRelapsePulmonaryCDDefaulted(tb08TableData.getRelapsePulmonaryCDDefaulted() + 1);
									tb08TableData.setRelapsePulmonaryCDEligible(tb08TableData.getRelapsePulmonaryCDEligible() + 1);
								}
								
								else if (transferOut != null && transferOut) {
									tb08TableData.setRelapseAllTransferOut(tb08TableData.getRelapseAllTransferOut() + 1);
									tb08TableData.setRelapsePulmonaryCDTransferOut(tb08TableData.getRelapsePulmonaryCDTransferOut() + 1);
									
								}
								
								else if (canceled != null && canceled) {
									tb08TableData.setRelapseAllCanceled(tb08TableData.getRelapseAllCanceled() + 1);
									tb08TableData.setRelapsePulmonaryCDCanceled(tb08TableData.getRelapsePulmonaryCDCanceled() + 1);
									
								}
								
								else if (sld != null && sld) {
									tb08TableData.setRelapseAllSLD(tb08TableData.getRelapseAllSLD() + 1);
									tb08TableData.setRelapsePulmonaryCDSLD(tb08TableData.getRelapsePulmonaryCDSLD() + 1);
									
								}
							}
						}
					}
					
					//EP
					else if (pulmonary != null && !pulmonary) {
						
						tb08TableData.setRelapseExtrapulmonaryDetected(tb08TableData.getRelapseExtrapulmonaryDetected() + 1);
						
						if (ageAtRegistration >= 0 && ageAtRegistration < 5) {
							
							tb08TableData.setRelapseExtrapulmonaryDetected04(tb08TableData.getRelapseExtrapulmonaryDetected04() + 1);
							
							if (cured != null && cured) {
								tb08TableData.setRelapseAllCured(tb08TableData.getRelapseAllCured() + 1);
								tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
								tb08TableData.setRelapseExtrapulmonaryCured04(tb08TableData.getRelapseExtrapulmonaryCured04() + 1);
								tb08TableData.setRelapseExtrapulmonaryEligible04(tb08TableData.getRelapseExtrapulmonaryEligible04() + 1);
								tb08TableData.setRelapseExtrapulmonaryCured(tb08TableData.getRelapseExtrapulmonaryCured() + 1);
								tb08TableData.setRelapseExtrapulmonaryEligible(tb08TableData.getRelapseExtrapulmonaryEligible() + 1);
							}
							
							else if (txCompleted != null && txCompleted) {
								tb08TableData.setRelapseAllCompleted(tb08TableData.getRelapseAllCompleted() + 1);
								tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
								tb08TableData.setRelapseExtrapulmonaryCompleted04(tb08TableData.getRelapseExtrapulmonaryCompleted04() + 1);
								tb08TableData.setRelapseExtrapulmonaryEligible04(tb08TableData.getRelapseExtrapulmonaryEligible04() + 1);
								tb08TableData.setRelapseExtrapulmonaryCompleted(tb08TableData.getRelapseExtrapulmonaryCompleted() + 1);
								tb08TableData.setRelapseExtrapulmonaryEligible(tb08TableData.getRelapseExtrapulmonaryEligible() + 1);
							}
							
							else if (diedTB != null && diedTB) {
								tb08TableData.setRelapseAllDiedTB(tb08TableData.getRelapseAllDiedTB() + 1);
								tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
								tb08TableData.setRelapseExtrapulmonaryDiedTB04(tb08TableData.getRelapseExtrapulmonaryDiedTB04() + 1);
								tb08TableData.setRelapseExtrapulmonaryEligible04(tb08TableData.getRelapseExtrapulmonaryEligible04() + 1);
								tb08TableData.setRelapseExtrapulmonaryDiedTB(tb08TableData.getRelapseExtrapulmonaryDiedTB() + 1);
								tb08TableData.setRelapseExtrapulmonaryEligible(tb08TableData.getRelapseExtrapulmonaryEligible() + 1);
							}
							
							else if (diedNotTB != null && diedNotTB) {
								tb08TableData.setRelapseAllDiedNotTB(tb08TableData.getRelapseAllDiedNotTB() + 1);
								tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
								tb08TableData.setRelapseExtrapulmonaryDiedNotTB04(tb08TableData.getRelapseExtrapulmonaryDiedNotTB04() + 1);
								tb08TableData.setRelapseExtrapulmonaryEligible04(tb08TableData.getRelapseExtrapulmonaryEligible04() + 1);
								tb08TableData.setRelapseExtrapulmonaryDiedNotTB(tb08TableData.getRelapseExtrapulmonaryDiedNotTB() + 1);
								tb08TableData.setRelapseExtrapulmonaryEligible(tb08TableData.getRelapseExtrapulmonaryEligible() + 1);
							}
							
							else if (failed != null && failed) {
								tb08TableData.setRelapseAllFailed(tb08TableData.getRelapseAllFailed() + 1);
								tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
								tb08TableData.setRelapseExtrapulmonaryFailed04(tb08TableData.getRelapseExtrapulmonaryFailed04() + 1);
								tb08TableData.setRelapseExtrapulmonaryEligible04(tb08TableData.getRelapseExtrapulmonaryEligible04() + 1);
								tb08TableData.setRelapseExtrapulmonaryFailed(tb08TableData.getRelapseExtrapulmonaryFailed() + 1);
								tb08TableData.setRelapseExtrapulmonaryEligible(tb08TableData.getRelapseExtrapulmonaryEligible() + 1);
							}
							
							else if (defaulted != null && defaulted) {
								tb08TableData.setRelapseAllDefaulted(tb08TableData.getRelapseAllDefaulted() + 1);
								tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
								tb08TableData.setRelapseExtrapulmonaryDefaulted04(tb08TableData.getRelapseExtrapulmonaryDefaulted04() + 1);
								tb08TableData.setRelapseExtrapulmonaryEligible04(tb08TableData.getRelapseExtrapulmonaryEligible04() + 1);
								tb08TableData.setRelapseExtrapulmonaryDefaulted(tb08TableData.getRelapseExtrapulmonaryDefaulted() + 1);
								tb08TableData.setRelapseExtrapulmonaryEligible(tb08TableData.getRelapseExtrapulmonaryEligible() + 1);
							}
							
							else if (transferOut != null && transferOut) {
								tb08TableData.setRelapseAllTransferOut(tb08TableData.getRelapseAllTransferOut() + 1);
								tb08TableData.setRelapseExtrapulmonaryTransferOut04(tb08TableData.getRelapseExtrapulmonaryTransferOut04() + 1);
								tb08TableData.setRelapseExtrapulmonaryTransferOut(tb08TableData.getRelapseExtrapulmonaryTransferOut() + 1);
							}
							
							else if (canceled != null && canceled) {
								tb08TableData.setRelapseAllCanceled(tb08TableData.getRelapseAllCanceled() + 1);
								tb08TableData.setRelapseExtrapulmonaryCanceled04(tb08TableData.getRelapseExtrapulmonaryCanceled04() + 1);
								tb08TableData.setRelapseExtrapulmonaryCanceled(tb08TableData.getRelapseExtrapulmonaryCanceled() + 1);
							}
							
							else if (sld != null && sld) {
								tb08TableData.setRelapseAllSLD(tb08TableData.getRelapseAllSLD() + 1);
								tb08TableData.setRelapseExtrapulmonarySLD04(tb08TableData.getRelapseExtrapulmonarySLD04() + 1);
								tb08TableData.setRelapseExtrapulmonarySLD(tb08TableData.getRelapseExtrapulmonarySLD() + 1);
							}
							
						}
						
						else if (ageAtRegistration >= 5 && ageAtRegistration < 15) {
							
							tb08TableData.setRelapseExtrapulmonaryDetected0514(tb08TableData.getRelapseExtrapulmonaryDetected0514() + 1);
							
							if (cured != null && cured) {
								tb08TableData.setRelapseAllCured(tb08TableData.getRelapseAllCured() + 1);
								tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
								tb08TableData.setRelapseExtrapulmonaryCured0514(tb08TableData.getRelapseExtrapulmonaryCured0514() + 1);
								tb08TableData.setRelapseExtrapulmonaryEligible0514(tb08TableData.getRelapseExtrapulmonaryEligible0514() + 1);
								tb08TableData.setRelapseExtrapulmonaryCured(tb08TableData.getRelapseExtrapulmonaryCured() + 1);
								tb08TableData.setRelapseExtrapulmonaryEligible(tb08TableData.getRelapseExtrapulmonaryEligible() + 1);
							}
							
							else if (txCompleted != null && txCompleted) {
								tb08TableData.setRelapseAllCompleted(tb08TableData.getRelapseAllCompleted() + 1);
								tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
								tb08TableData.setRelapseExtrapulmonaryCompleted0514(tb08TableData.getRelapseExtrapulmonaryCompleted0514() + 1);
								tb08TableData.setRelapseExtrapulmonaryEligible0514(tb08TableData.getRelapseExtrapulmonaryEligible0514() + 1);
								tb08TableData.setRelapseExtrapulmonaryCompleted(tb08TableData.getRelapseExtrapulmonaryCompleted() + 1);
								tb08TableData.setRelapseExtrapulmonaryEligible(tb08TableData.getRelapseExtrapulmonaryEligible() + 1);
							}
							
							else if (diedTB != null && diedTB) {
								tb08TableData.setRelapseAllDiedTB(tb08TableData.getRelapseAllDiedTB() + 1);
								tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
								tb08TableData.setRelapseExtrapulmonaryDiedTB0514(tb08TableData.getRelapseExtrapulmonaryDiedTB0514() + 1);
								tb08TableData.setRelapseExtrapulmonaryEligible0514(tb08TableData.getRelapseExtrapulmonaryEligible0514() + 1);
								tb08TableData.setRelapseExtrapulmonaryDiedTB(tb08TableData.getRelapseExtrapulmonaryDiedTB() + 1);
								tb08TableData.setRelapseExtrapulmonaryEligible(tb08TableData.getRelapseExtrapulmonaryEligible() + 1);
							}
							
							else if (diedNotTB != null && diedNotTB) {
								tb08TableData.setRelapseAllDiedNotTB(tb08TableData.getRelapseAllDiedNotTB() + 1);
								tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
								tb08TableData.setRelapseExtrapulmonaryDiedNotTB0514(tb08TableData.getRelapseExtrapulmonaryDiedNotTB0514() + 1);
								tb08TableData.setRelapseExtrapulmonaryEligible0514(tb08TableData.getRelapseExtrapulmonaryEligible0514() + 1);
								tb08TableData.setRelapseExtrapulmonaryDiedNotTB(tb08TableData.getRelapseExtrapulmonaryDiedNotTB() + 1);
								tb08TableData.setRelapseExtrapulmonaryEligible(tb08TableData.getRelapseExtrapulmonaryEligible() + 1);
							}
							
							else if (failed != null && failed) {
								tb08TableData.setRelapseAllFailed(tb08TableData.getRelapseAllFailed() + 1);
								tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
								tb08TableData.setRelapseExtrapulmonaryFailed0514(tb08TableData.getRelapseExtrapulmonaryFailed0514() + 1);
								tb08TableData.setRelapseExtrapulmonaryEligible0514(tb08TableData.getRelapseExtrapulmonaryEligible0514() + 1);
								tb08TableData.setRelapseExtrapulmonaryFailed(tb08TableData.getRelapseExtrapulmonaryFailed() + 1);
								tb08TableData.setRelapseExtrapulmonaryEligible(tb08TableData.getRelapseExtrapulmonaryEligible() + 1);
							}
							
							else if (defaulted != null && defaulted) {
								tb08TableData.setRelapseAllDefaulted(tb08TableData.getRelapseAllDefaulted() + 1);
								tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
								tb08TableData.setRelapseExtrapulmonaryDefaulted0514(tb08TableData.getRelapseExtrapulmonaryDefaulted0514() + 1);
								tb08TableData.setRelapseExtrapulmonaryEligible0514(tb08TableData.getRelapseExtrapulmonaryEligible0514() + 1);
								tb08TableData.setRelapseExtrapulmonaryDefaulted(tb08TableData.getRelapseExtrapulmonaryDefaulted() + 1);
								tb08TableData.setRelapseExtrapulmonaryEligible(tb08TableData.getRelapseExtrapulmonaryEligible() + 1);
							}
							
							else if (transferOut != null && transferOut) {
								tb08TableData.setRelapseAllTransferOut(tb08TableData.getRelapseAllTransferOut() + 1);
								tb08TableData.setRelapseExtrapulmonaryTransferOut0514(tb08TableData
								        .getRelapseExtrapulmonaryTransferOut0514() + 1);
								tb08TableData.setRelapseExtrapulmonaryTransferOut(tb08TableData.getRelapseExtrapulmonaryTransferOut() + 1);
							}
							
							else if (canceled != null && canceled) {
								tb08TableData.setRelapseAllCanceled(tb08TableData.getRelapseAllCanceled() + 1);
								tb08TableData.setRelapseExtrapulmonaryCanceled0514(tb08TableData.getRelapseExtrapulmonaryCanceled0514() + 1);
								tb08TableData.setRelapseExtrapulmonaryCanceled(tb08TableData.getRelapseExtrapulmonaryCanceled() + 1);
							}
							
							else if (sld != null && sld) {
								tb08TableData.setRelapseAllSLD(tb08TableData.getRelapseAllSLD() + 1);
								tb08TableData.setRelapseExtrapulmonarySLD0514(tb08TableData.getRelapseExtrapulmonarySLD0514() + 1);
								tb08TableData.setRelapseExtrapulmonarySLD(tb08TableData.getRelapseExtrapulmonarySLD() + 1);
							}
							
						}
						
						else if (ageAtRegistration >= 15 && ageAtRegistration < 18) {
							
							tb08TableData.setRelapseExtrapulmonaryDetected1517(tb08TableData.getRelapseExtrapulmonaryDetected1517() + 1);
							
							if (cured != null && cured) {
								tb08TableData.setRelapseAllCured(tb08TableData.getRelapseAllCured() + 1);
								tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
								tb08TableData.setRelapseExtrapulmonaryCured1517(tb08TableData.getRelapseExtrapulmonaryCured1517() + 1);
								tb08TableData.setRelapseExtrapulmonaryEligible1517(tb08TableData.getRelapseExtrapulmonaryEligible1517() + 1);
								tb08TableData.setRelapseExtrapulmonaryCured(tb08TableData.getRelapseExtrapulmonaryCured() + 1);
								tb08TableData.setRelapseExtrapulmonaryEligible(tb08TableData.getRelapseExtrapulmonaryEligible() + 1);
							}
							
							else if (txCompleted != null && txCompleted) {
								tb08TableData.setRelapseAllCompleted(tb08TableData.getRelapseAllCompleted() + 1);
								tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
								tb08TableData.setRelapseExtrapulmonaryCompleted1517(tb08TableData.getRelapseExtrapulmonaryCompleted1517() + 1);
								tb08TableData.setRelapseExtrapulmonaryEligible1517(tb08TableData.getRelapseExtrapulmonaryEligible1517() + 1);
								tb08TableData.setRelapseExtrapulmonaryCompleted(tb08TableData.getRelapseExtrapulmonaryCompleted() + 1);
								tb08TableData.setRelapseExtrapulmonaryEligible(tb08TableData.getRelapseExtrapulmonaryEligible() + 1);
							}
							
							else if (diedTB != null && diedTB) {
								tb08TableData.setRelapseAllDiedTB(tb08TableData.getRelapseAllDiedTB() + 1);
								tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
								tb08TableData.setRelapseExtrapulmonaryDiedTB1517(tb08TableData.getRelapseExtrapulmonaryDiedTB1517() + 1);
								tb08TableData.setRelapseExtrapulmonaryEligible1517(tb08TableData.getRelapseExtrapulmonaryEligible1517() + 1);
								tb08TableData.setRelapseExtrapulmonaryDiedTB(tb08TableData.getRelapseExtrapulmonaryDiedTB() + 1);
								tb08TableData.setRelapseExtrapulmonaryEligible(tb08TableData.getRelapseExtrapulmonaryEligible() + 1);
							}
							
							else if (diedNotTB != null && diedNotTB) {
								tb08TableData.setRelapseAllDiedNotTB(tb08TableData.getRelapseAllDiedNotTB() + 1);
								tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
								tb08TableData.setRelapseExtrapulmonaryDiedNotTB1517(tb08TableData.getRelapseExtrapulmonaryDiedNotTB1517() + 1);
								tb08TableData.setRelapseExtrapulmonaryEligible1517(tb08TableData.getRelapseExtrapulmonaryEligible1517() + 1);
								tb08TableData.setRelapseExtrapulmonaryDiedNotTB(tb08TableData.getRelapseExtrapulmonaryDiedNotTB() + 1);
								tb08TableData.setRelapseExtrapulmonaryEligible(tb08TableData.getRelapseExtrapulmonaryEligible() + 1);
							}
							
							else if (failed != null && failed) {
								tb08TableData.setRelapseAllFailed(tb08TableData.getRelapseAllFailed() + 1);
								tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
								tb08TableData.setRelapseExtrapulmonaryFailed1517(tb08TableData.getRelapseExtrapulmonaryFailed1517() + 1);
								tb08TableData.setRelapseExtrapulmonaryEligible1517(tb08TableData.getRelapseExtrapulmonaryEligible1517() + 1);
								tb08TableData.setRelapseExtrapulmonaryFailed(tb08TableData.getRelapseExtrapulmonaryFailed() + 1);
								tb08TableData.setRelapseExtrapulmonaryEligible(tb08TableData.getRelapseExtrapulmonaryEligible() + 1);
							}
							
							else if (defaulted != null && defaulted) {
								tb08TableData.setRelapseAllDefaulted(tb08TableData.getRelapseAllDefaulted() + 1);
								tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
								tb08TableData.setRelapseExtrapulmonaryDefaulted1517(tb08TableData.getRelapseExtrapulmonaryDefaulted1517() + 1);
								tb08TableData.setRelapseExtrapulmonaryEligible1517(tb08TableData.getRelapseExtrapulmonaryEligible1517() + 1);
								tb08TableData.setRelapseExtrapulmonaryDefaulted(tb08TableData.getRelapseExtrapulmonaryDefaulted() + 1);
								tb08TableData.setRelapseExtrapulmonaryEligible(tb08TableData.getRelapseExtrapulmonaryEligible() + 1);
							}
							
							else if (transferOut != null && transferOut) {
								tb08TableData.setRelapseAllTransferOut(tb08TableData.getRelapseAllTransferOut() + 1);
								tb08TableData.setRelapseExtrapulmonaryTransferOut1517(tb08TableData
								        .getRelapseExtrapulmonaryTransferOut1517() + 1);
								tb08TableData.setRelapseExtrapulmonaryTransferOut(tb08TableData.getRelapseExtrapulmonaryTransferOut() + 1);
							}
							
							else if (canceled != null && canceled) {
								tb08TableData.setRelapseAllCanceled(tb08TableData.getRelapseAllCanceled() + 1);
								tb08TableData.setRelapseExtrapulmonaryCanceled1517(tb08TableData.getRelapseExtrapulmonaryCanceled1517() + 1);
								tb08TableData.setRelapseExtrapulmonaryCanceled(tb08TableData.getRelapseExtrapulmonaryCanceled() + 1);
							}
							
							else if (sld != null && sld) {
								tb08TableData.setRelapseAllSLD(tb08TableData.getRelapseAllSLD() + 1);
								tb08TableData.setRelapseExtrapulmonarySLD1517(tb08TableData.getRelapseExtrapulmonarySLD1517() + 1);
								tb08TableData.setRelapseExtrapulmonarySLD(tb08TableData.getRelapseExtrapulmonarySLD() + 1);
							}
							
						}
						
						else {
							
							if (cured != null && cured) {
								tb08TableData.setRelapseAllCured(tb08TableData.getRelapseAllCured() + 1);
								tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
								tb08TableData.setRelapseExtrapulmonaryCured(tb08TableData.getRelapseExtrapulmonaryCured() + 1);
								tb08TableData.setRelapseExtrapulmonaryEligible(tb08TableData.getRelapseExtrapulmonaryEligible() + 1);
							}
							
							else if (txCompleted != null && txCompleted) {
								tb08TableData.setRelapseAllCompleted(tb08TableData.getRelapseAllCompleted() + 1);
								tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
								tb08TableData.setRelapseExtrapulmonaryCompleted(tb08TableData.getRelapseExtrapulmonaryCompleted() + 1);
								tb08TableData.setRelapseExtrapulmonaryEligible(tb08TableData.getRelapseExtrapulmonaryEligible() + 1);
							}
							
							else if (diedTB != null && diedTB) {
								tb08TableData.setRelapseAllDiedTB(tb08TableData.getRelapseAllDiedTB() + 1);
								tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
								tb08TableData.setRelapseExtrapulmonaryDiedTB(tb08TableData.getRelapseExtrapulmonaryDiedTB() + 1);
								tb08TableData.setRelapseExtrapulmonaryEligible(tb08TableData.getRelapseExtrapulmonaryEligible() + 1);
							}
							
							else if (diedNotTB != null && diedNotTB) {
								tb08TableData.setRelapseAllDiedNotTB(tb08TableData.getRelapseAllDiedNotTB() + 1);
								tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
								tb08TableData.setRelapseExtrapulmonaryDiedNotTB(tb08TableData.getRelapseExtrapulmonaryDiedNotTB() + 1);
								tb08TableData.setRelapseExtrapulmonaryEligible(tb08TableData.getRelapseExtrapulmonaryEligible() + 1);
							}
							
							else if (failed != null && failed) {
								tb08TableData.setRelapseAllFailed(tb08TableData.getRelapseAllFailed() + 1);
								tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
								tb08TableData.setRelapseExtrapulmonaryFailed(tb08TableData.getRelapseExtrapulmonaryFailed() + 1);
								tb08TableData.setRelapseExtrapulmonaryEligible(tb08TableData.getRelapseExtrapulmonaryEligible() + 1);
							}
							
							else if (defaulted != null && defaulted) {
								tb08TableData.setRelapseAllDefaulted(tb08TableData.getRelapseAllDefaulted() + 1);
								tb08TableData.setRelapseAllEligible(tb08TableData.getRelapseAllEligible() + 1);
								tb08TableData.setRelapseExtrapulmonaryDefaulted(tb08TableData.getRelapseExtrapulmonaryDefaulted() + 1);
								tb08TableData.setRelapseExtrapulmonaryEligible(tb08TableData.getRelapseExtrapulmonaryEligible() + 1);
							}
							
							else if (transferOut != null && transferOut) {
								tb08TableData.setRelapseAllTransferOut(tb08TableData.getRelapseAllTransferOut() + 1);
								tb08TableData.setRelapseExtrapulmonaryTransferOut(tb08TableData.getRelapseExtrapulmonaryTransferOut() + 1);
								
							}
							
							else if (canceled != null && canceled) {
								tb08TableData.setRelapseAllCanceled(tb08TableData.getRelapseAllCanceled() + 1);
								tb08TableData.setRelapseExtrapulmonaryCanceled(tb08TableData.getRelapseExtrapulmonaryCanceled() + 1);
								
							}
							
							else if (sld != null && sld) {
								tb08TableData.setRelapseAllSLD(tb08TableData.getRelapseAllSLD() + 1);
								tb08TableData.setRelapseExtrapulmonarySLD(tb08TableData.getRelapseExtrapulmonarySLD() + 1);
								
							}
						}
						
					}
				}
				
				//FAILURE
				else if (q.getId().equals(
				            Integer.parseInt(Context.getAdministrationService().getGlobalProperty(
				                MdrtbConstants.GP_AFTER_FAILURE1_CONCEPT_ID)))) {
					tb08TableData.setFailureAllDetected(tb08TableData.getFailureAllDetected() + 1);
					
					//P
					if (pulmonary != null && pulmonary) {
						
						//BC
						if (bacPositive) {
							
							tb08TableData.setFailurePulmonaryBCDetected(tb08TableData.getFailurePulmonaryBCDetected() + 1);
							
							if (cured != null && cured) {
								tb08TableData.setFailureAllCured(tb08TableData.getFailureAllCured() + 1);
								tb08TableData.setFailureAllEligible(tb08TableData.getFailureAllEligible() + 1);
								tb08TableData.setFailurePulmonaryBCCured(tb08TableData.getFailurePulmonaryBCCured() + 1);
								tb08TableData.setFailurePulmonaryBCEligible(tb08TableData.getFailurePulmonaryBCEligible() + 1);
							}
							
							else if (txCompleted != null && txCompleted) {
								tb08TableData.setFailureAllCompleted(tb08TableData.getFailureAllCompleted() + 1);
								tb08TableData.setFailureAllEligible(tb08TableData.getFailureAllEligible() + 1);
								tb08TableData.setFailurePulmonaryBCCompleted(tb08TableData.getFailurePulmonaryBCCompleted() + 1);
								tb08TableData.setFailurePulmonaryBCEligible(tb08TableData.getFailurePulmonaryBCEligible() + 1);
							}
							
							else if (diedTB != null && diedTB) {
								tb08TableData.setFailureAllDiedTB(tb08TableData.getFailureAllDiedTB() + 1);
								tb08TableData.setFailureAllEligible(tb08TableData.getFailureAllEligible() + 1);
								tb08TableData.setFailurePulmonaryBCDiedTB(tb08TableData.getFailurePulmonaryBCDiedTB() + 1);
								tb08TableData.setFailurePulmonaryBCEligible(tb08TableData.getFailurePulmonaryBCEligible() + 1);
							}
							
							else if (diedNotTB != null && diedNotTB) {
								tb08TableData.setFailureAllDiedNotTB(tb08TableData.getFailureAllDiedNotTB() + 1);
								tb08TableData.setFailureAllEligible(tb08TableData.getFailureAllEligible() + 1);
								tb08TableData.setFailurePulmonaryBCDiedNotTB(tb08TableData.getFailurePulmonaryBCDiedNotTB() + 1);
								tb08TableData.setFailurePulmonaryBCEligible(tb08TableData.getFailurePulmonaryBCEligible() + 1);
							}
							
							else if (failed != null && failed) {
								tb08TableData.setFailureAllFailed(tb08TableData.getFailureAllFailed() + 1);
								tb08TableData.setFailureAllEligible(tb08TableData.getFailureAllEligible() + 1);
								tb08TableData.setFailurePulmonaryBCFailed(tb08TableData.getFailurePulmonaryBCFailed() + 1);
								tb08TableData.setFailurePulmonaryBCEligible(tb08TableData.getFailurePulmonaryBCEligible() + 1);
							}
							
							else if (defaulted != null && defaulted) {
								tb08TableData.setFailureAllDefaulted(tb08TableData.getFailureAllDefaulted() + 1);
								tb08TableData.setFailureAllEligible(tb08TableData.getFailureAllEligible() + 1);
								tb08TableData.setFailurePulmonaryBCDefaulted(tb08TableData.getFailurePulmonaryBCDefaulted() + 1);
								tb08TableData.setFailurePulmonaryBCEligible(tb08TableData.getFailurePulmonaryBCEligible() + 1);
							}
							
							else if (transferOut != null && transferOut) {
								tb08TableData.setFailureAllTransferOut(tb08TableData.getFailureAllTransferOut() + 1);
								tb08TableData.setFailurePulmonaryBCTransferOut(tb08TableData.getFailurePulmonaryBCTransferOut() + 1);
								
							}
							
							else if (canceled != null && canceled) {
								tb08TableData.setFailureAllCanceled(tb08TableData.getFailureAllCanceled() + 1);
								tb08TableData.setFailurePulmonaryBCCanceled(tb08TableData.getFailurePulmonaryBCCanceled() + 1);
								
							}
							
							else if (sld != null && sld) {
								tb08TableData.setFailureAllSLD(tb08TableData.getFailureAllSLD() + 1);
								tb08TableData.setFailurePulmonaryBCSLD(tb08TableData.getFailurePulmonaryBCSLD() + 1);
								
							}
						}
						
						//CD
						else {
							
							tb08TableData.setFailurePulmonaryCDDetected(tb08TableData.getFailurePulmonaryCDDetected() + 1);
							
							if (cured != null && cured) {
								tb08TableData.setFailureAllCured(tb08TableData.getFailureAllCured() + 1);
								tb08TableData.setFailureAllEligible(tb08TableData.getFailureAllEligible() + 1);
								tb08TableData.setFailurePulmonaryCDCured(tb08TableData.getFailurePulmonaryCDCured() + 1);
								tb08TableData.setFailurePulmonaryCDEligible(tb08TableData.getFailurePulmonaryCDEligible() + 1);
							}
							
							else if (txCompleted != null && txCompleted) {
								tb08TableData.setFailureAllCompleted(tb08TableData.getFailureAllCompleted() + 1);
								tb08TableData.setFailureAllEligible(tb08TableData.getFailureAllEligible() + 1);
								tb08TableData.setFailurePulmonaryCDCompleted(tb08TableData.getFailurePulmonaryCDCompleted() + 1);
								tb08TableData.setFailurePulmonaryCDEligible(tb08TableData.getFailurePulmonaryCDEligible() + 1);
							}
							
							else if (diedTB != null && diedTB) {
								tb08TableData.setFailureAllDiedTB(tb08TableData.getFailureAllDiedTB() + 1);
								tb08TableData.setFailureAllEligible(tb08TableData.getFailureAllEligible() + 1);
								tb08TableData.setFailurePulmonaryCDDiedTB(tb08TableData.getFailurePulmonaryCDDiedTB() + 1);
								tb08TableData.setFailurePulmonaryCDEligible(tb08TableData.getFailurePulmonaryCDEligible() + 1);
							}
							
							else if (diedNotTB != null && diedNotTB) {
								tb08TableData.setFailureAllDiedNotTB(tb08TableData.getFailureAllDiedNotTB() + 1);
								tb08TableData.setFailureAllEligible(tb08TableData.getFailureAllEligible() + 1);
								tb08TableData.setFailurePulmonaryCDDiedNotTB(tb08TableData.getFailurePulmonaryCDDiedNotTB() + 1);
								tb08TableData.setFailurePulmonaryCDEligible(tb08TableData.getFailurePulmonaryCDEligible() + 1);
							}
							
							else if (failed != null && failed) {
								tb08TableData.setFailureAllFailed(tb08TableData.getFailureAllFailed() + 1);
								tb08TableData.setFailureAllEligible(tb08TableData.getFailureAllEligible() + 1);
								tb08TableData.setFailurePulmonaryCDFailed(tb08TableData.getFailurePulmonaryCDFailed() + 1);
								tb08TableData.setFailurePulmonaryCDEligible(tb08TableData.getFailurePulmonaryCDEligible() + 1);
							}
							
							else if (defaulted != null && defaulted) {
								tb08TableData.setFailureAllDefaulted(tb08TableData.getFailureAllDefaulted() + 1);
								tb08TableData.setFailureAllEligible(tb08TableData.getFailureAllEligible() + 1);
								tb08TableData.setFailurePulmonaryCDDefaulted(tb08TableData.getFailurePulmonaryCDDefaulted() + 1);
								tb08TableData.setFailurePulmonaryCDEligible(tb08TableData.getFailurePulmonaryCDEligible() + 1);
							}
							
							else if (transferOut != null && transferOut) {
								tb08TableData.setFailureAllTransferOut(tb08TableData.getFailureAllTransferOut() + 1);
								tb08TableData.setFailurePulmonaryCDTransferOut(tb08TableData.getFailurePulmonaryCDTransferOut() + 1);
								
							}
							
							else if (canceled != null && canceled) {
								tb08TableData.setFailureAllCanceled(tb08TableData.getFailureAllCanceled() + 1);
								tb08TableData.setFailurePulmonaryCDCanceled(tb08TableData.getFailurePulmonaryCDCanceled() + 1);
								
							}
							
							else if (sld != null && sld) {
								tb08TableData.setFailureAllSLD(tb08TableData.getFailureAllSLD() + 1);
								tb08TableData.setFailurePulmonaryCDSLD(tb08TableData.getFailurePulmonaryCDSLD() + 1);
								
							}
							
						}
					}
					
					//EP
					else if (pulmonary != null && !pulmonary) {
						
						tb08TableData.setFailureExtrapulmonaryDetected(tb08TableData.getFailureExtrapulmonaryDetected() + 1);
						
						if (cured != null && cured) {
							tb08TableData.setFailureAllCured(tb08TableData.getFailureAllCured() + 1);
							tb08TableData.setFailureAllEligible(tb08TableData.getFailureAllEligible() + 1);
							tb08TableData.setFailureExtrapulmonaryCured(tb08TableData.getFailureExtrapulmonaryCured() + 1);
							tb08TableData.setFailureExtrapulmonaryEligible(tb08TableData.getFailureExtrapulmonaryEligible() + 1);
						}
						
						else if (txCompleted != null && txCompleted) {
							tb08TableData.setFailureAllCompleted(tb08TableData.getFailureAllCompleted() + 1);
							tb08TableData.setFailureAllEligible(tb08TableData.getFailureAllEligible() + 1);
							tb08TableData.setFailureExtrapulmonaryCompleted(tb08TableData.getFailureExtrapulmonaryCompleted() + 1);
							tb08TableData.setFailureExtrapulmonaryEligible(tb08TableData.getFailureExtrapulmonaryEligible() + 1);
						}
						
						else if (diedTB != null && diedTB) {
							tb08TableData.setFailureAllDiedTB(tb08TableData.getFailureAllDiedTB() + 1);
							tb08TableData.setFailureAllEligible(tb08TableData.getFailureAllEligible() + 1);
							tb08TableData.setFailureExtrapulmonaryDiedTB(tb08TableData.getFailureExtrapulmonaryDiedTB() + 1);
							tb08TableData.setFailureExtrapulmonaryEligible(tb08TableData.getFailureExtrapulmonaryEligible() + 1);
						}
						
						else if (diedNotTB != null && diedNotTB) {
							tb08TableData.setFailureAllDiedNotTB(tb08TableData.getFailureAllDiedNotTB() + 1);
							tb08TableData.setFailureAllEligible(tb08TableData.getFailureAllEligible() + 1);
							tb08TableData.setFailureExtrapulmonaryDiedNotTB(tb08TableData.getFailureExtrapulmonaryDiedNotTB() + 1);
							tb08TableData.setFailureExtrapulmonaryEligible(tb08TableData.getFailureExtrapulmonaryEligible() + 1);
						}
						
						else if (failed != null && failed) {
							tb08TableData.setFailureAllFailed(tb08TableData.getFailureAllFailed() + 1);
							tb08TableData.setFailureAllEligible(tb08TableData.getFailureAllEligible() + 1);
							tb08TableData.setFailureExtrapulmonaryFailed(tb08TableData.getFailureExtrapulmonaryFailed() + 1);
							tb08TableData.setFailureExtrapulmonaryEligible(tb08TableData.getFailureExtrapulmonaryEligible() + 1);
						}
						
						else if (defaulted != null && defaulted) {
							tb08TableData.setFailureAllDefaulted(tb08TableData.getFailureAllDefaulted() + 1);
							tb08TableData.setFailureAllEligible(tb08TableData.getFailureAllEligible() + 1);
							tb08TableData.setFailureExtrapulmonaryDefaulted(tb08TableData.getFailureExtrapulmonaryDefaulted() + 1);
							tb08TableData.setFailureExtrapulmonaryEligible(tb08TableData.getFailureExtrapulmonaryEligible() + 1);
						}
						
						else if (transferOut != null && transferOut) {
							tb08TableData.setFailureAllTransferOut(tb08TableData.getFailureAllTransferOut() + 1);
							tb08TableData.setFailureExtrapulmonaryTransferOut(tb08TableData.getFailureExtrapulmonaryTransferOut() + 1);
							
						}
						
						else if (canceled != null && canceled) {
							tb08TableData.setFailureAllCanceled(tb08TableData.getFailureAllCanceled() + 1);
							tb08TableData.setFailureExtrapulmonaryCanceled(tb08TableData.getFailureExtrapulmonaryCanceled() + 1);
						}
						else if (sld != null && sld) {
							tb08TableData.setFailureAllSLD(tb08TableData.getFailureAllSLD() + 1);
							tb08TableData.setFailureExtrapulmonarySLD(tb08TableData.getFailureExtrapulmonarySLD() + 1);
						}
					}
				}
				else if (q.getId().equals(Integer.parseInt(Context.getAdministrationService().getGlobalProperty(MdrtbConstants.GP_AFTER_DEFAULT1_CONCEPT_ID)))) {
					tb08TableData.setDefaultAllDetected(tb08TableData.getDefaultAllDetected() + 1);
					
					if (pulmonary != null && pulmonary) {
						if (bacPositive) {
							tb08TableData.setDefaultPulmonaryBCDetected(tb08TableData.getDefaultPulmonaryBCDetected() + 1);
							if (cured != null && cured) {
								tb08TableData.setDefaultAllCured(tb08TableData.getDefaultAllCured() + 1);
								tb08TableData.setDefaultAllEligible(tb08TableData.getDefaultAllEligible() + 1);
								tb08TableData.setDefaultPulmonaryBCCured(tb08TableData.getDefaultPulmonaryBCCured() + 1);
								tb08TableData.setDefaultPulmonaryBCEligible(tb08TableData.getDefaultPulmonaryBCEligible() + 1);
							}
							else if (txCompleted != null && txCompleted) {
								tb08TableData.setDefaultAllCompleted(tb08TableData.getDefaultAllCompleted() + 1);
								tb08TableData.setDefaultAllEligible(tb08TableData.getDefaultAllEligible() + 1);
								tb08TableData.setDefaultPulmonaryBCCompleted(tb08TableData.getDefaultPulmonaryBCCompleted() + 1);
								tb08TableData.setDefaultPulmonaryBCEligible(tb08TableData.getDefaultPulmonaryBCEligible() + 1);
							}
							else if (diedTB != null && diedTB) {
								tb08TableData.setDefaultAllDiedTB(tb08TableData.getDefaultAllDiedTB() + 1);
								tb08TableData.setDefaultAllEligible(tb08TableData.getDefaultAllEligible() + 1);
								tb08TableData.setDefaultPulmonaryBCDiedTB(tb08TableData.getDefaultPulmonaryBCDiedTB() + 1);
								tb08TableData.setDefaultPulmonaryBCEligible(tb08TableData.getDefaultPulmonaryBCEligible() + 1);
							}
							else if (diedNotTB != null && diedNotTB) {
								tb08TableData.setDefaultAllDiedNotTB(tb08TableData.getDefaultAllDiedNotTB() + 1);
								tb08TableData.setDefaultAllEligible(tb08TableData.getDefaultAllEligible() + 1);
								tb08TableData.setDefaultPulmonaryBCDiedNotTB(tb08TableData.getDefaultPulmonaryBCDiedNotTB() + 1);
								tb08TableData.setDefaultPulmonaryBCEligible(tb08TableData.getDefaultPulmonaryBCEligible() + 1);
							}
							else if (failed != null && failed) {
								tb08TableData.setDefaultAllFailed(tb08TableData.getDefaultAllFailed() + 1);
								tb08TableData.setDefaultAllEligible(tb08TableData.getDefaultAllEligible() + 1);
								tb08TableData.setDefaultPulmonaryBCFailed(tb08TableData.getDefaultPulmonaryBCFailed() + 1);
								tb08TableData.setDefaultPulmonaryBCEligible(tb08TableData.getDefaultPulmonaryBCEligible() + 1);
							}
							else if (defaulted != null && defaulted) {
								tb08TableData.setDefaultAllDefaulted(tb08TableData.getDefaultAllDefaulted() + 1);
								tb08TableData.setDefaultAllEligible(tb08TableData.getDefaultAllEligible() + 1);
								tb08TableData.setDefaultPulmonaryBCDefaulted(tb08TableData.getDefaultPulmonaryBCDefaulted() + 1);
								tb08TableData.setDefaultPulmonaryBCEligible(tb08TableData.getDefaultPulmonaryBCEligible() + 1);
							}
							else if (transferOut != null && transferOut) {
								tb08TableData.setDefaultAllTransferOut(tb08TableData.getDefaultAllTransferOut() + 1);
								tb08TableData.setDefaultPulmonaryBCTransferOut(tb08TableData.getDefaultPulmonaryBCTransferOut() + 1);
							}
							else if (canceled != null && canceled) {
								tb08TableData.setDefaultAllCanceled(tb08TableData.getDefaultAllCanceled() + 1);
								tb08TableData.setDefaultPulmonaryBCCanceled(tb08TableData.getDefaultPulmonaryBCCanceled() + 1);
							}
							else if (sld != null && sld) {
								tb08TableData.setDefaultAllSLD(tb08TableData.getDefaultAllSLD() + 1);
								tb08TableData.setDefaultPulmonaryBCSLD(tb08TableData.getDefaultPulmonaryBCSLD() + 1);
							}
						}
						//CD
						else {
							tb08TableData.setDefaultPulmonaryCDDetected(tb08TableData.getDefaultPulmonaryCDDetected() + 1);
							if (cured != null && cured) {
								tb08TableData.setDefaultAllCured(tb08TableData.getDefaultAllCured() + 1);
								tb08TableData.setDefaultAllEligible(tb08TableData.getDefaultAllEligible() + 1);
								tb08TableData.setDefaultPulmonaryCDCured(tb08TableData.getDefaultPulmonaryCDCured() + 1);
								tb08TableData.setDefaultPulmonaryCDEligible(tb08TableData.getDefaultPulmonaryCDEligible() + 1);
							}
							else if (txCompleted != null && txCompleted) {
								tb08TableData.setDefaultAllCompleted(tb08TableData.getDefaultAllCompleted() + 1);
								tb08TableData.setDefaultAllEligible(tb08TableData.getDefaultAllEligible() + 1);
								tb08TableData.setDefaultPulmonaryCDCompleted(tb08TableData.getDefaultPulmonaryCDCompleted() + 1);
								tb08TableData.setDefaultPulmonaryCDEligible(tb08TableData.getDefaultPulmonaryCDEligible() + 1);
							}
							else if (diedTB != null && diedTB) {
								tb08TableData.setDefaultAllDiedTB(tb08TableData.getDefaultAllDiedTB() + 1);
								tb08TableData.setDefaultAllEligible(tb08TableData.getDefaultAllEligible() + 1);
								tb08TableData.setDefaultPulmonaryCDDiedTB(tb08TableData.getDefaultPulmonaryCDDiedTB() + 1);
								tb08TableData.setDefaultPulmonaryCDEligible(tb08TableData.getDefaultPulmonaryCDEligible() + 1);
							}
							else if (diedNotTB != null && diedNotTB) {
								tb08TableData.setDefaultAllDiedNotTB(tb08TableData.getDefaultAllDiedNotTB() + 1);
								tb08TableData.setDefaultAllEligible(tb08TableData.getDefaultAllEligible() + 1);
								tb08TableData.setDefaultPulmonaryCDDiedNotTB(tb08TableData.getDefaultPulmonaryCDDiedNotTB() + 1);
								tb08TableData.setDefaultPulmonaryCDEligible(tb08TableData.getDefaultPulmonaryCDEligible() + 1);
							}
							else if (failed != null && failed) {
								tb08TableData.setDefaultAllFailed(tb08TableData.getDefaultAllFailed() + 1);
								tb08TableData.setDefaultAllEligible(tb08TableData.getDefaultAllEligible() + 1);
								tb08TableData.setDefaultPulmonaryCDFailed(tb08TableData.getDefaultPulmonaryCDFailed() + 1);
								tb08TableData.setDefaultPulmonaryCDEligible(tb08TableData.getDefaultPulmonaryCDEligible() + 1);
							}
							else if (defaulted != null && defaulted) {
								tb08TableData.setDefaultAllDefaulted(tb08TableData.getDefaultAllDefaulted() + 1);
								tb08TableData.setDefaultAllEligible(tb08TableData.getDefaultAllEligible() + 1);
								tb08TableData.setDefaultPulmonaryCDDefaulted(tb08TableData.getDefaultPulmonaryCDDefaulted() + 1);
								tb08TableData.setDefaultPulmonaryCDEligible(tb08TableData.getDefaultPulmonaryCDEligible() + 1);
							}
							else if (transferOut != null && transferOut) {
								tb08TableData.setDefaultAllTransferOut(tb08TableData.getDefaultAllTransferOut() + 1);
								tb08TableData.setDefaultPulmonaryCDTransferOut(tb08TableData.getDefaultPulmonaryCDTransferOut() + 1);
							}
							else if (canceled != null && canceled) {
								tb08TableData.setDefaultAllCanceled(tb08TableData.getDefaultAllCanceled() + 1);
								tb08TableData.setDefaultPulmonaryCDCanceled(tb08TableData.getDefaultPulmonaryCDCanceled() + 1);
							}
							else if (sld != null && sld) {
								tb08TableData.setDefaultAllSLD(tb08TableData.getDefaultAllSLD() + 1);
								tb08TableData.setDefaultPulmonaryCDSLD(tb08TableData.getDefaultPulmonaryCDSLD() + 1);
							}
						}
					}
					
					//EP
					else if (pulmonary != null && !pulmonary) {
						tb08TableData.setDefaultExtrapulmonaryDetected(tb08TableData.getDefaultExtrapulmonaryDetected() + 1);
						if (cured != null && cured) {
							tb08TableData.setDefaultAllCured(tb08TableData.getDefaultAllCured() + 1);
							tb08TableData.setDefaultAllEligible(tb08TableData.getDefaultAllEligible() + 1);
							tb08TableData.setDefaultExtrapulmonaryCured(tb08TableData.getDefaultExtrapulmonaryCured() + 1);
							tb08TableData.setDefaultExtrapulmonaryEligible(tb08TableData.getDefaultExtrapulmonaryEligible() + 1);
						}
						else if (txCompleted != null && txCompleted) {
							tb08TableData.setDefaultAllCompleted(tb08TableData.getDefaultAllCompleted() + 1);
							tb08TableData.setDefaultAllEligible(tb08TableData.getDefaultAllEligible() + 1);
							tb08TableData.setDefaultExtrapulmonaryCompleted(tb08TableData.getDefaultExtrapulmonaryCompleted() + 1);
							tb08TableData.setDefaultExtrapulmonaryEligible(tb08TableData.getDefaultExtrapulmonaryEligible() + 1);
						}
						else if (diedTB != null && diedTB) {
							tb08TableData.setDefaultAllDiedTB(tb08TableData.getDefaultAllDiedTB() + 1);
							tb08TableData.setDefaultAllEligible(tb08TableData.getDefaultAllEligible() + 1);
							tb08TableData.setDefaultExtrapulmonaryDiedTB(tb08TableData.getDefaultExtrapulmonaryDiedTB() + 1);
							tb08TableData.setDefaultExtrapulmonaryEligible(tb08TableData.getDefaultExtrapulmonaryEligible() + 1);
						}
						else if (diedNotTB != null && diedNotTB) {
							tb08TableData.setDefaultAllDiedNotTB(tb08TableData.getDefaultAllDiedNotTB() + 1);
							tb08TableData.setDefaultAllEligible(tb08TableData.getDefaultAllEligible() + 1);
							tb08TableData.setDefaultExtrapulmonaryDiedNotTB(tb08TableData.getDefaultExtrapulmonaryDiedNotTB() + 1);
							tb08TableData.setDefaultExtrapulmonaryEligible(tb08TableData.getDefaultExtrapulmonaryEligible() + 1);
						}
						else if (failed != null && failed) {
							tb08TableData.setDefaultAllFailed(tb08TableData.getDefaultAllFailed() + 1);
							tb08TableData.setDefaultAllEligible(tb08TableData.getDefaultAllEligible() + 1);
							tb08TableData.setDefaultExtrapulmonaryFailed(tb08TableData.getDefaultExtrapulmonaryFailed() + 1);
							tb08TableData.setDefaultExtrapulmonaryEligible(tb08TableData.getDefaultExtrapulmonaryEligible() + 1);
						}
						else if (defaulted != null && defaulted) {
							tb08TableData.setDefaultAllDefaulted(tb08TableData.getDefaultAllDefaulted() + 1);
							tb08TableData.setDefaultAllEligible(tb08TableData.getDefaultAllEligible() + 1);
							tb08TableData.setDefaultExtrapulmonaryDefaulted(tb08TableData.getDefaultExtrapulmonaryDefaulted() + 1);
							tb08TableData.setDefaultExtrapulmonaryEligible(tb08TableData.getDefaultExtrapulmonaryEligible() + 1);
						}
						else if (transferOut != null && transferOut) {
							tb08TableData.setDefaultAllTransferOut(tb08TableData.getDefaultAllTransferOut() + 1);
							tb08TableData.setDefaultExtrapulmonaryTransferOut(tb08TableData.getDefaultExtrapulmonaryTransferOut() + 1);
						}
						else if (canceled != null && canceled) {
							tb08TableData.setDefaultAllCanceled(tb08TableData.getDefaultAllCanceled() + 1);
							tb08TableData.setDefaultExtrapulmonaryCanceled(tb08TableData.getDefaultExtrapulmonaryCanceled() + 1);
						}
						else if (sld != null && sld) {
							tb08TableData.setDefaultAllSLD(tb08TableData.getDefaultAllSLD() + 1);
							tb08TableData.setDefaultExtrapulmonarySLD(tb08TableData.getDefaultExtrapulmonarySLD() + 1);
						}
					}
				}
				//OTHER
				else if (q.getId().equals(
				    Integer.parseInt(Context.getAdministrationService()
				            .getGlobalProperty(MdrtbConstants.GP_OTHER_CONCEPT_ID)))) {
					tb08TableData.setOtherAllDetected(tb08TableData.getOtherAllDetected() + 1);
					if (pulmonary != null && pulmonary) {
						if (bacPositive) {
							tb08TableData.setOtherPulmonaryBCDetected(tb08TableData.getOtherPulmonaryBCDetected() + 1);
							if (cured != null && cured) {
								tb08TableData.setOtherAllCured(tb08TableData.getOtherAllCured() + 1);
								tb08TableData.setOtherAllEligible(tb08TableData.getOtherAllEligible() + 1);
								tb08TableData.setOtherPulmonaryBCCured(tb08TableData.getOtherPulmonaryBCCured() + 1);
								tb08TableData.setOtherPulmonaryBCEligible(tb08TableData.getOtherPulmonaryBCEligible() + 1);
							}
							else if (txCompleted != null && txCompleted) {
								tb08TableData.setOtherAllCompleted(tb08TableData.getOtherAllCompleted() + 1);
								tb08TableData.setOtherAllEligible(tb08TableData.getOtherAllEligible() + 1);
								tb08TableData.setOtherPulmonaryBCCompleted(tb08TableData.getOtherPulmonaryBCCompleted() + 1);
								tb08TableData.setOtherPulmonaryBCEligible(tb08TableData.getOtherPulmonaryBCEligible() + 1);
							}
							else if (diedTB != null && diedTB) {
								tb08TableData.setOtherAllDiedTB(tb08TableData.getOtherAllDiedTB() + 1);
								tb08TableData.setOtherAllEligible(tb08TableData.getOtherAllEligible() + 1);
								tb08TableData.setOtherPulmonaryBCDiedTB(tb08TableData.getOtherPulmonaryBCDiedTB() + 1);
								tb08TableData.setOtherPulmonaryBCEligible(tb08TableData.getOtherPulmonaryBCEligible() + 1);
							}
							else if (diedNotTB != null && diedNotTB) {
								tb08TableData.setOtherAllDiedNotTB(tb08TableData.getOtherAllDiedNotTB() + 1);
								tb08TableData.setOtherAllEligible(tb08TableData.getOtherAllEligible() + 1);
								tb08TableData.setOtherPulmonaryBCDiedNotTB(tb08TableData.getOtherPulmonaryBCDiedNotTB() + 1);
								tb08TableData.setOtherPulmonaryBCEligible(tb08TableData.getOtherPulmonaryBCEligible() + 1);
							}
							else if (failed != null && failed) {
								tb08TableData.setOtherAllFailed(tb08TableData.getOtherAllFailed() + 1);
								tb08TableData.setOtherAllEligible(tb08TableData.getOtherAllEligible() + 1);
								tb08TableData.setOtherPulmonaryBCFailed(tb08TableData.getOtherPulmonaryBCFailed() + 1);
								tb08TableData.setOtherPulmonaryBCEligible(tb08TableData.getOtherPulmonaryBCEligible() + 1);
							}
							else if (defaulted != null && defaulted) {
								tb08TableData.setOtherAllDefaulted(tb08TableData.getOtherAllDefaulted() + 1);
								tb08TableData.setOtherAllEligible(tb08TableData.getOtherAllEligible() + 1);
								tb08TableData.setOtherPulmonaryBCDefaulted(tb08TableData.getOtherPulmonaryBCDefaulted() + 1);
								tb08TableData.setOtherPulmonaryBCEligible(tb08TableData.getOtherPulmonaryBCEligible() + 1);
							}
							else if (transferOut != null && transferOut) {
								tb08TableData.setOtherAllTransferOut(tb08TableData.getOtherAllTransferOut() + 1);
								tb08TableData.setOtherPulmonaryBCTransferOut(tb08TableData.getOtherPulmonaryBCTransferOut() + 1);
							}
							else if (canceled != null && canceled) {
								tb08TableData.setOtherAllCanceled(tb08TableData.getOtherAllCanceled() + 1);
								tb08TableData.setOtherPulmonaryBCCanceled(tb08TableData.getOtherPulmonaryBCCanceled() + 1);
							}
							else if (sld != null && sld) {
								tb08TableData.setOtherAllSLD(tb08TableData.getOtherAllSLD() + 1);
								tb08TableData.setOtherPulmonaryBCSLD(tb08TableData.getOtherPulmonaryBCSLD() + 1);
							}
						}
						//CD
						else {
							tb08TableData.setOtherPulmonaryCDDetected(tb08TableData.getOtherPulmonaryCDDetected() + 1);
							if (cured != null && cured) {
								tb08TableData.setOtherAllCured(tb08TableData.getOtherAllCured() + 1);
								tb08TableData.setOtherAllEligible(tb08TableData.getOtherAllEligible() + 1);
								tb08TableData.setOtherPulmonaryCDCured(tb08TableData.getOtherPulmonaryCDCured() + 1);
								tb08TableData.setOtherPulmonaryCDEligible(tb08TableData.getOtherPulmonaryCDEligible() + 1);
							}
							else if (txCompleted != null && txCompleted) {
								tb08TableData.setOtherAllCompleted(tb08TableData.getOtherAllCompleted() + 1);
								tb08TableData.setOtherAllEligible(tb08TableData.getOtherAllEligible() + 1);
								tb08TableData.setOtherPulmonaryCDCompleted(tb08TableData.getOtherPulmonaryCDCompleted() + 1);
								tb08TableData.setOtherPulmonaryCDEligible(tb08TableData.getOtherPulmonaryCDEligible() + 1);
							}
							else if (diedTB != null && diedTB) {
								tb08TableData.setOtherAllDiedTB(tb08TableData.getOtherAllDiedTB() + 1);
								tb08TableData.setOtherAllEligible(tb08TableData.getOtherAllEligible() + 1);
								tb08TableData.setOtherPulmonaryCDDiedTB(tb08TableData.getOtherPulmonaryCDDiedTB() + 1);
								tb08TableData.setOtherPulmonaryCDEligible(tb08TableData.getOtherPulmonaryCDEligible() + 1);
							}
							else if (diedNotTB != null && diedNotTB) {
								tb08TableData.setOtherAllDiedNotTB(tb08TableData.getOtherAllDiedNotTB() + 1);
								tb08TableData.setOtherAllEligible(tb08TableData.getOtherAllEligible() + 1);
								tb08TableData.setOtherPulmonaryCDDiedNotTB(tb08TableData.getOtherPulmonaryCDDiedNotTB() + 1);
								tb08TableData.setOtherPulmonaryCDEligible(tb08TableData.getOtherPulmonaryCDEligible() + 1);
							}
							else if (failed != null && failed) {
								tb08TableData.setOtherAllFailed(tb08TableData.getOtherAllFailed() + 1);
								tb08TableData.setOtherAllEligible(tb08TableData.getOtherAllEligible() + 1);
								tb08TableData.setOtherPulmonaryCDFailed(tb08TableData.getOtherPulmonaryCDFailed() + 1);
								tb08TableData.setOtherPulmonaryCDEligible(tb08TableData.getOtherPulmonaryCDEligible() + 1);
							}
							else if (defaulted != null && defaulted) {
								tb08TableData.setOtherAllDefaulted(tb08TableData.getOtherAllDefaulted() + 1);
								tb08TableData.setOtherAllEligible(tb08TableData.getOtherAllEligible() + 1);
								tb08TableData.setOtherPulmonaryCDDefaulted(tb08TableData.getOtherPulmonaryCDDefaulted() + 1);
								tb08TableData.setOtherPulmonaryCDEligible(tb08TableData.getOtherPulmonaryCDEligible() + 1);
							}
							else if (transferOut != null && transferOut) {
								tb08TableData.setOtherAllTransferOut(tb08TableData.getOtherAllTransferOut() + 1);
								tb08TableData.setOtherPulmonaryCDTransferOut(tb08TableData.getOtherPulmonaryCDTransferOut() + 1);
							}
							else if (canceled != null && canceled) {
								tb08TableData.setOtherAllCanceled(tb08TableData.getOtherAllCanceled() + 1);
								tb08TableData.setOtherPulmonaryCDCanceled(tb08TableData.getOtherPulmonaryCDCanceled() + 1);
							}
							else if (sld != null && sld) {
								tb08TableData.setOtherAllSLD(tb08TableData.getOtherAllSLD() + 1);
								tb08TableData.setOtherPulmonaryCDSLD(tb08TableData.getOtherPulmonaryCDSLD() + 1);
							}
						}
					}
					//EP
					else if (pulmonary != null && !pulmonary) {
						tb08TableData.setOtherExtrapulmonaryDetected(tb08TableData.getOtherExtrapulmonaryDetected() + 1);
						if (cured != null && cured) {
							tb08TableData.setOtherAllCured(tb08TableData.getOtherAllCured() + 1);
							tb08TableData.setOtherAllEligible(tb08TableData.getOtherAllEligible() + 1);
							tb08TableData.setOtherExtrapulmonaryCured(tb08TableData.getOtherExtrapulmonaryCured() + 1);
							tb08TableData.setOtherExtrapulmonaryEligible(tb08TableData.getOtherExtrapulmonaryEligible() + 1);
						}
						else if (txCompleted != null && txCompleted) {
							tb08TableData.setOtherAllCompleted(tb08TableData.getOtherAllCompleted() + 1);
							tb08TableData.setOtherAllEligible(tb08TableData.getOtherAllEligible() + 1);
							tb08TableData.setOtherExtrapulmonaryCompleted(tb08TableData.getOtherExtrapulmonaryCompleted() + 1);
							tb08TableData.setOtherExtrapulmonaryEligible(tb08TableData.getOtherExtrapulmonaryEligible() + 1);
						}
						else if (diedTB != null && diedTB) {
							tb08TableData.setOtherAllDiedTB(tb08TableData.getOtherAllDiedTB() + 1);
							tb08TableData.setOtherAllEligible(tb08TableData.getOtherAllEligible() + 1);
							tb08TableData.setOtherExtrapulmonaryDiedTB(tb08TableData.getOtherExtrapulmonaryDiedTB() + 1);
							tb08TableData.setOtherExtrapulmonaryEligible(tb08TableData.getOtherExtrapulmonaryEligible() + 1);
						}
						else if (diedNotTB != null && diedNotTB) {
							tb08TableData.setOtherAllDiedNotTB(tb08TableData.getOtherAllDiedNotTB() + 1);
							tb08TableData.setOtherAllEligible(tb08TableData.getOtherAllEligible() + 1);
							tb08TableData.setOtherExtrapulmonaryDiedNotTB(tb08TableData.getOtherExtrapulmonaryDiedNotTB() + 1);
							tb08TableData.setOtherExtrapulmonaryEligible(tb08TableData.getOtherExtrapulmonaryEligible() + 1);
						}
						else if (failed != null && failed) {
							tb08TableData.setOtherAllFailed(tb08TableData.getOtherAllFailed() + 1);
							tb08TableData.setOtherAllEligible(tb08TableData.getOtherAllEligible() + 1);
							tb08TableData.setOtherExtrapulmonaryFailed(tb08TableData.getOtherExtrapulmonaryFailed() + 1);
							tb08TableData.setOtherExtrapulmonaryEligible(tb08TableData.getOtherExtrapulmonaryEligible() + 1);
						}
						else if (defaulted != null && defaulted) {
							tb08TableData.setOtherAllDefaulted(tb08TableData.getOtherAllDefaulted() + 1);
							tb08TableData.setOtherAllEligible(tb08TableData.getOtherAllEligible() + 1);
							tb08TableData.setOtherExtrapulmonaryDefaulted(tb08TableData.getOtherExtrapulmonaryDefaulted() + 1);
							tb08TableData.setOtherExtrapulmonaryEligible(tb08TableData.getOtherExtrapulmonaryEligible() + 1);
						}
						else if (transferOut != null && transferOut) {
							tb08TableData.setOtherAllTransferOut(tb08TableData.getOtherAllTransferOut() + 1);
							tb08TableData.setOtherExtrapulmonaryTransferOut(tb08TableData.getOtherExtrapulmonaryTransferOut() + 1);
						}
						else if (canceled != null && canceled) {
							tb08TableData.setOtherAllCanceled(tb08TableData.getOtherAllCanceled() + 1);
							tb08TableData.setOtherExtrapulmonaryCanceled(tb08TableData.getOtherExtrapulmonaryCanceled() + 1);
						}
						else if (sld != null && sld) {
							tb08TableData.setOtherAllSLD(tb08TableData.getOtherAllSLD() + 1);
							tb08TableData.setOtherExtrapulmonarySLD(tb08TableData.getOtherExtrapulmonarySLD() + 1);
						}
					}
				}
			}
		}
		
		Map<String, Object> tables = new HashMap<>();
		tables.put("table1", table1);
		tables.put("table2", table2);
		tables.put("table3", table3);
		tables.put("table4", table4);
		tables.put("table5a", table5a);
		tables.put("table6", tb08TableData);
		return tables;
	}
}
