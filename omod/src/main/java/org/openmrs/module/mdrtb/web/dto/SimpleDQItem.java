package org.openmrs.module.mdrtb.web.dto;

import java.util.List;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.PersonAddress;
import org.openmrs.module.mdrtb.reporting.custom.DQItem;

public class SimpleDQItem extends BaseOpenmrsData {
	
	private static final long serialVersionUID = 1L;
	
	private String patientUuid;
	
	private String patientName;
	
	private String gender;
	
	private String dateOfBirth;
	
	private String residentialAddress;
	
	private String locName;
	
	private List<String> tb03Links;
	
	private List<String> form89Links;
	
	public SimpleDQItem(DQItem dqItem) {
		setPatientUuid(dqItem.getPatient().getUuid());
		setPatientName(dqItem.getPatient().getPersonName().getFullName());
		setGender(dqItem.getGender());
		setDateOfBirth(dqItem.getDateOfBirth());
		PersonAddress pa = dqItem.getPatient().getPersonAddress();
		if (pa != null) {
			String address = pa.getCountry() + "," + pa.getStateProvince() + "," + pa.getCountyDistrict();
			if (pa.getAddress1() != null && !pa.getAddress1().isEmpty()) {
				address += "," + pa.getAddress1();
				if (pa.getAddress2() != null && !pa.getAddress2().isEmpty())
					address += "," + pa.getAddress2();
			}
			setResidentialAddress(address);
		}
		setLocName(dqItem.getLocName());
		setTb03Links(dqItem.getTb03Links());
		setForm89Links(dqItem.getForm89Links());
	}
	
	@Override
	public Integer getId() {
		return -1;
	}
	
	@Override
	public void setId(Integer id) {
	}
	
	public String getPatientUuid() {
		return patientUuid;
	}
	
	public void setPatientUuid(String patientUuid) {
		this.patientUuid = patientUuid;
	}
	
	public String getPatientName() {
		return patientName;
	}
	
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	
	public String getGender() {
		return gender;
	}
	
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public String getDateOfBirth() {
		return dateOfBirth;
	}
	
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	
	public String getLocName() {
		return locName;
	}
	
	public void setLocName(String locName) {
		this.locName = locName;
	}
	
	public String getResidentialAddress() {
		return residentialAddress;
	}
	
	public void setResidentialAddress(String residentialAddress) {
		this.residentialAddress = residentialAddress;
	}
	
	public List<String> getTb03Links() {
		return tb03Links;
	}
	
	public void setTb03Links(List<String> tb03Links) {
		this.tb03Links = tb03Links;
	}
	
	public void addTb03Link(String tb03Link) {
		if (tb03Link != null) {
			this.tb03Links.add(tb03Link);
		}
	}
	
	public List<String> getForm89Links() {
		return form89Links;
	}
	
	public void setForm89Links(List<String> form89Links) {
		this.form89Links = form89Links;
	}
	
	public void addForm89Link(String tb03form89Link) {
		if (tb03form89Link != null) {
			this.form89Links.add(tb03form89Link);
		}
	}
}
