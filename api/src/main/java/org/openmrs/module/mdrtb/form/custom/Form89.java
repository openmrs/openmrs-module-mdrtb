package org.openmrs.module.mdrtb.form.custom;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbConstants;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.form.AbstractSimpleForm;
import org.openmrs.module.mdrtb.program.TbPatientProgram;
import org.openmrs.module.mdrtb.api.MessagePropertyService;

public class Form89 extends AbstractSimpleForm implements Comparable<Form89> {
	
	private static final String VOID_MESSAGE = "voided by MDRTB module";
	
	private TB03Form tb03;
	
	public Form89() {
		super();
		this.encounter.setEncounterType(MdrtbConstants.ET_FORM89_TB_FOLLOWUP);
	}
	
	public Form89(Patient patient) {
		super(patient);
		this.encounter.setEncounterType(MdrtbConstants.ET_FORM89_TB_FOLLOWUP);
	}
	
	public Form89(Encounter encounter) {
		super(encounter);
		
	}
	
	public void initTB03(Integer patientProgramId) {
		
		TbPatientProgram tpp = Context.getService(MdrtbService.class).getTbPatientProgram(patientProgramId);
		List<Encounter> encounters = null;
		if (tpp != null) {
			encounters = tpp.getTb03EncountersDuringProgramObs();
		}
		if (encounters != null && !encounters.isEmpty()) {
			tb03 = new TB03Form(encounters.get(0));
		}
	}
	
	public TB03Form getTB03() {
		return tb03;
	}
	
	public void setTB03(TB03Form tb03) {
		this.tb03 = tb03;
	}
	
	public Integer getYearOfTB03Registration() {
		if (tb03 == null || tb03.getEncounterDatetime() == null)
			return null;
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTimeInMillis(tb03.getEncounterDatetime().getTime());
		return gc.get(Calendar.YEAR);
		
	}
	
	public String getPatientName() {
		PersonName p = getPatient().getPersonName();
		return p.getFamilyName() + "," + p.getGivenName();
	}
	
	public Integer getAgeAtRegistration() {
		return MdrtbUtil.calculateAge(getPatient().getBirthdate(), getEncounterDatetime());
	}
	
	public String getGender() {
		if (encounter.getPatient().getGender().equals("M"))
			return Context.getService(MessagePropertyService.class).getMessage("mdrtb.tb03.gender.male");
		if (encounter.getPatient().getGender().equals("F"))
			return Context.getService(MessagePropertyService.class).getMessage("mdrtb.tb03.gender.female");
		
		return "";
	}
	
	public Date getDateOfBirth() {
		return encounter.getPatient().getBirthdate();
	}
	
	public String getAddress() {
		
		PersonAddress pa = encounter.getPatient().getPersonAddress();
		if (pa == null)
			return null;
		
		String address = pa.getCountry() + "," + pa.getStateProvince() + "," + pa.getCountyDistrict();
		if (pa.getAddress1() != null && !pa.getAddress1().isEmpty()) {
			address += "," + pa.getAddress1();
			if (pa.getAddress2() != null && !pa.getAddress2().isEmpty())
				address += "," + pa.getAddress2();
		}
		
		return address;
	}
	
	public Concept getLocationType() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.LOCATION_TYPE), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setLocationType(Concept type) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.LOCATION_TYPE), encounter);
		
		if (obs == null && type == null) {
			return;
		}
		
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(type)) {
			
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_MESSAGE);
			}
			
			if (type != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.LOCATION_TYPE), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(type);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getProfession() {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PROFESSION),
		    encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setProfession(Concept type) {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PROFESSION),
		    encounter);
		
		if (obs == null && type == null) {
			return;
		}
		
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(type)) {
			
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_MESSAGE);
			}
			
			if (type != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.PROFESSION), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(type);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getPopulationCategory() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.POPULATION_CATEGORY), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setPopulationCategory(Concept type) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.POPULATION_CATEGORY), encounter);
		
		if (obs == null && type == null) {
			return;
		}
		
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(type)) {
			
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_MESSAGE);
			}
			
			if (type != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.POPULATION_CATEGORY), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(type);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getPlaceOfDetection() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PLACE_OF_DETECTION), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setPlaceOfDetection(Concept type) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PLACE_OF_DETECTION), encounter);
		
		if (obs == null && type == null) {
			return;
		}
		
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(type)) {
			
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_MESSAGE);
			}
			
			if (type != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.PLACE_OF_DETECTION), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(type);
				encounter.addObs(obs);
			}
		}
	}
	
	public Date getDateFirstSeekingHelp() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DATE_FIRST_SEEKING_HELP), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueDatetime();
		}
	}
	
	public void setDateFirstSeekingHelp(Date date) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DATE_FIRST_SEEKING_HELP), encounter);
		
		if (obs == null && date == null) {
			return;
		}
		
		if (obs == null || obs.getValueDatetime() == null || !obs.getValueDatetime().equals(date)) {
			
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_MESSAGE);
			}
			
			if (date != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.DATE_FIRST_SEEKING_HELP), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueDatetime(date);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getCircumstancesOfDetection() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CIRCUMSTANCES_OF_DETECTION), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setCircumstancesOfDetection(Concept type) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CIRCUMSTANCES_OF_DETECTION), encounter);
		
		if (obs == null && type == null) {
			return;
		}
		
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(type)) {
			
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_MESSAGE);
			}
			
			if (type != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.CIRCUMSTANCES_OF_DETECTION), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(type);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getMethodOfDetection() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.METHOD_OF_DETECTION), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setMethodOfDetection(Concept type) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.METHOD_OF_DETECTION), encounter);
		
		if (obs == null && type == null) {
			return;
		}
		
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(type)) {
			
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_MESSAGE);
			}
			
			if (type != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.METHOD_OF_DETECTION), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(type);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getAnatomicalSite() {
		return tb03.getAnatomicalSite();
	}
	
	public void setAnatomicalSite(Concept anatomicalSite) {
		tb03.setAnatomicalSite(anatomicalSite);
	}
	
	public Concept getEptbSite() {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class)
		        .getConcept(MdrtbConcepts.SITE_OF_EPTB), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setEptbSite(Concept eptbSite) {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class)
		        .getConcept(MdrtbConcepts.SITE_OF_EPTB), encounter);
		
		if (obs == null && eptbSite == null) {
			return;
		}
		
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(eptbSite)) {
			
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_MESSAGE);
			}
			
			if (eptbSite != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.SITE_OF_EPTB), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(eptbSite);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getPtbLocation() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.LOCATION_OF_PTB), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setPtbLocation(Concept ptbLocation) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.LOCATION_OF_PTB), encounter);
		
		if (obs == null && ptbLocation == null) {
			return;
		}
		
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(ptbLocation)) {
			
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_MESSAGE);
			}
			
			if (ptbLocation != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.LOCATION_OF_PTB), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(ptbLocation);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getEptbLocation() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.LOCATION_OF_EPTB), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setEptbLocation(Concept eptbLocation) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.LOCATION_OF_EPTB), encounter);
		
		if (obs == null && eptbLocation == null) {
			return;
		}
		
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(eptbLocation)) {
			
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_MESSAGE);
			}
			
			if (eptbLocation != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.LOCATION_OF_EPTB), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(eptbLocation);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getPresenceOfDecay() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PRESENCE_OF_DECAY), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setPresenceOfDecay(Concept type) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PRESENCE_OF_DECAY), encounter);
		
		if (obs == null && type == null) {
			return;
		}
		
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(type)) {
			
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_MESSAGE);
			}
			
			if (type != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.PRESENCE_OF_DECAY), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(type);
				encounter.addObs(obs);
			}
		}
	}
	
	/////////////////
	public Date getDateOfDecaySurvey() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DATE_OF_DECAY_SURVEY), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueDatetime();
		}
	}
	
	public void setDateOfDecaySurvey(Date date) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DATE_OF_DECAY_SURVEY), encounter);
		
		if (obs == null && date == null) {
			return;
		}
		
		if (obs == null || obs.getValueDatetime() == null || !obs.getValueDatetime().equals(date)) {
			
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_MESSAGE);
			}
			
			if (date != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.DATE_OF_DECAY_SURVEY), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueDatetime(date);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getDiabetes() {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DIABETES),
		    encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setDiabetes(Concept site) {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DIABETES),
		    encounter);
		
		if (obs == null && site == null) {
			return;
		}
		
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(site)) {
			
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_MESSAGE);
			}
			
			if (site != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.DIABETES), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(site);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getCnsdl() {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CNSDL),
		    encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setCnsdl(Concept site) {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CNSDL),
		    encounter);
		
		if (obs == null && site == null) {
			return;
		}
		
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(site)) {
			
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_MESSAGE);
			}
			
			if (site != null) {
				obs = new Obs(encounter.getPatient(),
				        Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CNSDL),
				        encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(site);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getHtHeartDisease() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HYPERTENSION_OR_HEART_DISEASE), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setHtHeartDisease(Concept site) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.HYPERTENSION_OR_HEART_DISEASE), encounter);
		
		if (obs == null && site == null) {
			return;
		}
		
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(site)) {
			
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_MESSAGE);
			}
			
			if (site != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.HYPERTENSION_OR_HEART_DISEASE), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(site);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getUlcer() {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ULCER),
		    encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setUlcer(Concept site) {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ULCER),
		    encounter);
		
		if (obs == null && site == null) {
			return;
		}
		
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(site)) {
			
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_MESSAGE);
			}
			
			if (site != null) {
				obs = new Obs(encounter.getPatient(),
				        Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ULCER),
				        encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(site);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getMentalDisorder() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MENTAL_DISORDER), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setMentalDisorder(Concept site) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MENTAL_DISORDER), encounter);
		
		if (obs == null && site == null) {
			return;
		}
		
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(site)) {
			
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_MESSAGE);
			}
			
			if (site != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.MENTAL_DISORDER), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(site);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getIbc20() {
		Obs obs = null;
		
		if (tb03 != null) {
			obs = MdrtbUtil.getObsFromEncounter(
			    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.RESULT_OF_HIV_TEST), tb03.getEncounter());
		}
		
		if (obs == null) {
			return null;
		} else {
			if (obs.getValueCoded().equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.POSITIVE))) {
				return Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.YES);
			}
			
			else if (obs.getValueCoded().equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NEGATIVE))) {
				return Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NO);
			}
			
			return null;
		}
		
	}
	
	public Concept getCancer() {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CANCER),
		    encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setCancer(Concept site) {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CANCER),
		    encounter);
		
		if (obs == null && site == null) {
			return;
		}
		
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(site)) {
			
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_MESSAGE);
			}
			
			if (site != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class)
				        .getConcept(MdrtbConcepts.CANCER), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(site);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getHepatitis() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.COMORBID_HEPATITIS), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setHepatitis(Concept site) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.COMORBID_HEPATITIS), encounter);
		
		if (obs == null && site == null) {
			return;
		}
		
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(site)) {
			
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_MESSAGE);
			}
			
			if (site != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.COMORBID_HEPATITIS), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(site);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getKidneyDisease() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.KIDNEY_DISEASE), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setKidneyDisease(Concept site) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.KIDNEY_DISEASE), encounter);
		
		if (obs == null && site == null) {
			return;
		}
		
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(site)) {
			
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_MESSAGE);
			}
			
			if (site != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.KIDNEY_DISEASE), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(site);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getNoDisease() {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NO_DISEASE),
		    encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setNoDisease(Concept site) {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NO_DISEASE),
		    encounter);
		
		// if this obs have not been created, and there is no data to add, do
		// nothing
		if (obs == null && site == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has
		// changed.
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(site)) {
			
			// (we have to do this manually because openmrs doesn't void obs
			// when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_MESSAGE);
			}
			
			if (site != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.NO_DISEASE), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(site);
				encounter.addObs(obs);
			}
		}
	}
	
	public String getOtherDisease() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OTHER_DISEASE), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueText();
		}
	}
	
	public void setOtherDisease(String site) {
		
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OTHER_DISEASE), encounter);
		
		if (obs == null && site == null) {
			return;
		}
		
		if (obs == null || obs.getValueText() == null || !obs.getValueText().equals(site)) {
			
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_MESSAGE);
			}
			
			if (site != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.OTHER_DISEASE), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueText(site);
				encounter.addObs(obs);
			}
		}
	}
	
	public Date getCmacDate() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CENTRAL_COMMISSION_DATE), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueDatetime();
		}
	}
	
	public void setCmacDate(Date date) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CENTRAL_COMMISSION_DATE), encounter);
		
		if (obs == null && date == null) {
			return;
		}
		
		if (obs == null || obs.getValueDatetime() == null || !obs.getValueDatetime().equals(date)) {
			
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_MESSAGE);
			}
			
			if (date != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.CENTRAL_COMMISSION_DATE), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueDatetime(date);
				encounter.addObs(obs);
			}
		}
	}
	
	public String getCmacNumber() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CENTRAL_COMMISSION_NUMBER), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueText();
		}
	}
	
	public void setCmacNumber(String number) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CENTRAL_COMMISSION_NUMBER), encounter);
		
		if (obs == null && number == null) {
			return;
		}
		
		if (obs == null || obs.getValueText() == null || !obs.getValueText().equals(number)) {
			
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_MESSAGE);
			}
			
			if (number != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.CENTRAL_COMMISSION_NUMBER), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueText(number);
				encounter.addObs(obs);
			}
		}
	}
	
	public Date getForm89Date() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.FORM89_DATE), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueDatetime();
		}
	}
	
	public void setForm89Date(Date date) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.FORM89_DATE), encounter);
		
		if (obs == null && date == null) {
			return;
		}
		
		if (obs == null || obs.getValueDatetime() == null || !obs.getValueDatetime().equals(date)) {
			
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_MESSAGE);
			}
			
			if (date != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.FORM89_DATE), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueDatetime(date);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getPrescribedTreatment() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.GENERAL_PRESCRIBED_TREATMENT), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueCoded();
		}
	}
	
	public void setPrescribedTreatment(Concept type) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.GENERAL_PRESCRIBED_TREATMENT), encounter);
		
		if (obs == null && type == null) {
			return;
		}
		
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(type)) {
			
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_MESSAGE);
			}
			
			if (type != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.GENERAL_PRESCRIBED_TREATMENT), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(type);
				encounter.addObs(obs);
			}
		}
	}
	
	public Integer getPatientProgramId() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), encounter);
		
		if (obs == null) {
			return null;
		} else {
			return obs.getValueNumeric().intValue();
		}
	}
	
	public void setPatientProgramId(Integer id) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID), encounter);
		
		if (obs == null && id == null) {
			return;
		}
		
		if (obs == null || obs.getValueNumeric() == null || obs.getValueNumeric().intValue() != id) {
			
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_MESSAGE);
			}
			
			if (id != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.PATIENT_PROGRAM_ID), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueNumeric(id.doubleValue());
				encounter.addObs(obs);
			}
		}
	}
	
	public List<SmearForm> getSmears() {
		if (getPatientProgramId() == null) {
			return new ArrayList<>();
		}
		
		ArrayList<SmearForm> ret = new ArrayList<>();
		List<SmearForm> allSmears = Context.getService(MdrtbService.class).getSmearForms(getPatientProgramId());
		
		if (allSmears == null)
			return ret;
		else {
			for (SmearForm sf : allSmears) {
				if (sf != null && sf.getMonthOfTreatment() != null && sf.getMonthOfTreatment() == 0) {
					ret.add(sf);
				}
			}
		}
		return ret;
		
	}
	
	public List<XpertForm> getXperts() {
		
		if (getPatientProgramId() == null) {
			return new ArrayList<>();
		}
		
		ArrayList<XpertForm> ret = new ArrayList<>();
		List<XpertForm> allXperts = Context.getService(MdrtbService.class).getXpertForms(getPatientProgramId());
		
		if (allXperts == null)
			return ret;
		else {
			for (XpertForm sf : allXperts) {
				if (sf != null && sf.getMonthOfTreatment() != null && sf.getMonthOfTreatment() == 0) {
					ret.add(sf);
				}
			}
		}
		
		return ret;
		
	}
	
	public List<HAINForm> getHains() {
		if (getPatientProgramId() == null) {
			return new ArrayList<>();
		}
		
		ArrayList<HAINForm> ret = new ArrayList<>();
		List<HAINForm> allHains = Context.getService(MdrtbService.class).getHAINForms(getPatientProgramId());
		
		if (allHains == null)
			return ret;
		else {
			for (HAINForm sf : allHains) {
				if (sf != null && sf.getMonthOfTreatment() != null && sf.getMonthOfTreatment() == 0) {
					ret.add(sf);
				}
			}
		}
		return ret;
	}
	
	public List<HAIN2Form> getHain2s() {
		if (getPatientProgramId() == null) {
			return new ArrayList<>();
		}
		
		ArrayList<HAIN2Form> ret = new ArrayList<>();
		List<HAIN2Form> allHains = Context.getService(MdrtbService.class).getHAIN2Forms(getPatientProgramId());
		
		if (allHains == null)
			return ret;
		else {
			for (HAIN2Form sf : allHains) {
				if (sf != null && sf.getMonthOfTreatment() != null && sf.getMonthOfTreatment() == 0) {
					ret.add(sf);
				}
			}
		}
		return ret;
	}
	
	public Boolean getIsPulmonary() {
		Boolean result = null;
		
		Concept site = getAnatomicalSite();
		if (site != null) {
			
			if (site.equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PULMONARY_TB)))
				result = true;
			
			else if (site.equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.EXTRA_PULMONARY_TB)))
				result = false;
		}
		return result;
	}
	
	public Boolean getIsChildbearingAge() {
		int ageFrom = 15, ageTo = 49;
		return encounter.getPatient().getGender().equals("F") && getAgeAtRegistration() >= ageFrom
		        && getAgeAtRegistration() <= ageTo;
	}
	
	public String getNameOfDoctor() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NAME_OF_DOCTOR), encounter);
		return obs == null ? null : obs.getValueText();
	}
	
	public void setNameOfDoctor(String name) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.NAME_OF_DOCTOR), encounter);
		
		if (obs == null && name == null) {
			return;
		}
		
		if (obs == null || obs.getValueText() == null || !obs.getValueText().equals(name)) {
			
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_MESSAGE);
			}
			
			if (name != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.NAME_OF_DOCTOR), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueText(name);
				encounter.addObs(obs);
			}
		}
	}
	
	public String getLink() {
		return "/module/mdrtb/form/form89.form?patientProgramId=" + getPatientProgramId() + "&encounterId="
		        + getEncounter().getId();
	}
	
	public String getCountryOfOrigin() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.COUNTRY_OF_ORIGIN), encounter);
		return obs == null ? null : obs.getValueText();
	}
	
	public void setCountryOfOrigin(String name) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.COUNTRY_OF_ORIGIN), encounter);
		
		if (obs == null && name == null) {
			return;
		}
		
		if (obs == null || obs.getValueText() == null || !obs.getValueText().equals(name)) {
			
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_MESSAGE);
			}
			
			if (name != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.COUNTRY_OF_ORIGIN), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueText(name);
				encounter.addObs(obs);
			}
		}
	}
	
	public Concept getPlaceOfCommission() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PLACE_OF_CENTRAL_COMMISSION), encounter);
		return obs == null ? null : obs.getValueCoded();
	}
	
	public void setPlaceOfCommission(Concept place) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PLACE_OF_CENTRAL_COMMISSION), encounter);
		
		if (obs == null && place == null) {
			return;
		}
		
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(place)) {
			
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_MESSAGE);
			}
			
			if (place != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.PLACE_OF_CENTRAL_COMMISSION), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(place);
				encounter.addObs(obs);
			}
		}
	}
	
	public String getCityOfOrigin() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CITY_OF_ORIGIN), encounter);
		return obs == null ? null : obs.getValueText();
	}
	
	public void setCityOfOrigin(String name) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CITY_OF_ORIGIN), encounter);
		
		if (obs == null && name == null) {
			return;
		}
		
		if (obs == null || obs.getValueText() == null || !obs.getValueText().equals(name)) {
			
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_MESSAGE);
			}
			
			if (name != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.CITY_OF_ORIGIN), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueText(name);
				encounter.addObs(obs);
			}
		}
	}
	
	public String getOtherMethodOfDetection() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OTHER_METHOD_OF_DETECTION), encounter);
		return obs == null ? null : obs.getValueText();
	}
	
	public void setOtherMethodOfDetection(String name) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.OTHER_METHOD_OF_DETECTION), encounter);
		
		if (obs == null && name == null) {
			return;
		}
		
		if (obs == null || obs.getValueText() == null || !obs.getValueText().equals(name)) {
			
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_MESSAGE);
			}
			
			if (name != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.OTHER_METHOD_OF_DETECTION), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueText(name);
				encounter.addObs(obs);
			}
		}
	}
	
	public String getComplication() {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class)
		        .getConcept(MdrtbConcepts.COMPLICATION), encounter);
		return obs == null ? null : obs.getValueText();
	}
	
	public void setComplication(String name) {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class)
		        .getConcept(MdrtbConcepts.COMPLICATION), encounter);
		
		if (obs == null && name == null) {
			return;
		}
		
		if (obs == null || obs.getValueText() == null || !obs.getValueText().equals(name)) {
			
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_MESSAGE);
			}
			
			if (name != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.COMPLICATION), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueText(name);
				encounter.addObs(obs);
			}
		}
	}
	
	public Date getDateOfReturn() {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DATE_OF_RETURN), encounter);
		return obs == null ? null : obs.getValueDatetime();
	}
	
	public void setDateOfReturn(Date date) {
		Obs obs = MdrtbUtil.getObsFromEncounter(
		    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.DATE_OF_RETURN), encounter);
		
		// if this obs have not been created, and there is no data to add, do
		// nothing
		if (obs == null && date == null) {
			return;
		}
		
		// we only need to update this if this is a new obs or if the value has
		// changed.
		if (obs == null || obs.getValueDatetime() == null || !obs.getValueDatetime().equals(date)) {
			
			// (we have to do this manually because openmrs doesn't void obs
			// when saved via encounters)
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_MESSAGE);
			}
			
			if (date != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.DATE_OF_RETURN), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueDatetime(date);
				encounter.addObs(obs);
			}
		}
	}
	
	public String getRegistrationNumber() {
		log.debug("Patient ID: " + this.getPatient().getPatientId());
		if (tb03 == null) {
			if (this.getPatientProgramId() == null) {
				return null;
			}
			
			else {
				initTB03(this.getPatientProgramId());
				if (tb03 != null) {
					return tb03.getRegistrationNumber();
				}
				
				else
					return null;
			}
		}
		
		return tb03.getRegistrationNumber();
	}
	
	public int compareTo(Form89 form) {
		if (this.getRegistrationNumber() == null)
			return 1;
		if (form.getRegistrationNumber() == null)
			return -1;
		
		return this.getRegistrationNumber().compareTo(form.getRegistrationNumber());
	}
	
	public Concept getPregnant() {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PREGNANT),
		    encounter);
		return obs == null ? null : obs.getValueCoded();
	}
	
	public void setPregnant(Concept type) {
		Obs obs = MdrtbUtil.getObsFromEncounter(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PREGNANT),
		    encounter);
		
		if (obs == null && type == null) {
			return;
		}
		
		if (obs == null || obs.getValueCoded() == null || !obs.getValueCoded().equals(type)) {
			
			if (obs != null) {
				obs.setVoided(true);
				obs.setVoidReason(VOID_MESSAGE);
			}
			
			if (type != null) {
				obs = new Obs(encounter.getPatient(), Context.getService(MdrtbService.class).getConcept(
				    MdrtbConcepts.PREGNANT), encounter.getEncounterDatetime(), encounter.getLocation());
				obs.setValueCoded(type);
				encounter.addObs(obs);
			}
		}
	}
}
