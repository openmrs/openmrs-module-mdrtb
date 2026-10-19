package org.openmrs.module.mdrtb.reporting.custom;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Patient;
import org.openmrs.module.mdrtb.api.MdrtbService;

public class DQItem {
	
	private Patient patient;
	
	private String dateOfBirth;
	
	private String locName;
	
	private List<String> tb03Links;
	
	private List<String> form89Links;
	
	public DQItem() {
		patient = null;
		dateOfBirth = null;
		tb03Links = new ArrayList<>();
		form89Links = new ArrayList<>();
	}
	
	public String getLocName() {
		return locName;
	}
	
	public void setLocName(String locName) {
		this.locName = locName;
	}
	
	public Patient getPatient() {
		return patient;
	}
	
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	
	public String getDateOfBirth() {
		return dateOfBirth;
	}
	
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	
	public List<String> getTb03Links() {
		return tb03Links;
	}
	
	public void setTb03Links(List<String> tb03Links) {
		this.tb03Links = tb03Links;
	}
	
	public String getTb03Link(int i) {
		return tb03Links.get(i);
	}
	
	public void setTb03Link(int i, String lnk) {
		tb03Links.set(i, lnk);
	}
	
	public void addTb03Link(String lnk) {
		tb03Links.add(lnk);
	}
	
	/**
	 * Returns the gender exactly as stored on the Patient, "M" or "F", and never a translated
	 * label. Translation of this field belongs to the consuming client alone: this getter used to
	 * resolve mdrtb.tb03.gender.male/female here, which put display text in the API payload and
	 * left the web app unable to render it in the user's own locale (a Russian client received the
	 * string the server had already chosen). The web templates now build the message key from this
	 * value.
	 * 
	 * @return "M", "F", or "" when no gender is recorded
	 */
	public String getGender() {
		if (patient == null || patient.getGender() == null) {
			return "";
		}
		return patient.getGender();
	}
	
	public List<String> getForm89Links() {
		return form89Links;
	}
	
	public void setForm89Links(List<String> form89Links) {
		this.form89Links = form89Links;
	}
	
	public String getForm89Link(int i) {
		return form89Links.get(i);
	}
	
	public void setForm89Link(int i, String lnk) {
		form89Links.set(i, lnk);
	}
	
	public void addForm89Link(String lnk) {
		form89Links.add(lnk);
	}
	
}
