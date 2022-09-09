package org.openmrs.module.labmodule.reporting;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.labmodule.TbConcepts;
import org.openmrs.module.labmodule.service.TbService;
import org.openmrs.module.labmodule.specimen.Culture;
import org.openmrs.module.labmodule.specimen.Dst1;
import org.openmrs.module.labmodule.specimen.Dst2;
import org.openmrs.module.labmodule.specimen.HAIN;
import org.openmrs.module.labmodule.specimen.HAIN2;
import org.openmrs.module.labmodule.specimen.LabResultImpl;
import org.openmrs.module.labmodule.specimen.Microscopy;
import org.openmrs.module.labmodule.specimen.Xpert;

public class TB04Row {
	
	private LabResultImpl lri;
		
	public TB04Row() {
		
	}
	
	public String getGender() {
		String gender = lri.getPatient().getGender();
		
		/*if(gender == "F") && Context.getLocale().equals("ru"))
			return "Ж";
		else
			return gender;*/
		
		if(lri.getPatient().getGender().equals("M"))
			return Context.getMessageSourceService().getMessage("dotsreports.tb03.gender.male");
		else if (lri.getPatient().getGender().equals("F"))
			return Context.getMessageSourceService().getMessage("dotsreports.tb03.gender.female");
		
		return "";
	}
	
	public String getYearOfBirth() {
		Date dob = lri.getPatient().getBirthdate();
		
		String ret = "";
		
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTimeInMillis(dob.getTime());
		ret = "" + gc.get(Calendar.YEAR);
		
		return ret;

	}
	
	public LabResultImpl getLri() {
		return lri;
	}

	public void setLri(LabResultImpl lri) {
		this.lri = lri;
	}

	public Culture getLatestCulture() {
		Culture ret = null;
		List<Culture> cultures = lri.getCultures();
		if(cultures==null || cultures.size()==0)
			return null;
		
		Collections.sort(cultures);
		ret = cultures.get(cultures.size()-1);
		
		return ret;
	}
	
	public Microscopy getLatestMicroscopy() {
		 Microscopy ret = null;
		 List<Microscopy> microscopies = lri.getMicroscopies();
			if(microscopies==null || microscopies.size()==0)
				return null;
			
			Collections.sort(microscopies);
			ret = microscopies.get(microscopies.size()-1);
		return ret;
	}
	
	public Xpert getLatestXpert() {
		Xpert ret = null;
		List<Xpert> xperts = lri.getXperts();
		if(xperts==null || xperts.size()==0)
			return null;
		
		Collections.sort(xperts);
		ret = xperts.get(xperts.size()-1);
		return ret;
	}
	
	public Dst1 getLatestDst1() {
		Dst1 ret = null;
		List<Dst1> dst1s = lri.getDst1s();
		if(dst1s==null || dst1s.size()==0)
			return null;
		
		Collections.sort(dst1s);
		ret = dst1s.get(dst1s.size()-1);
		
		return ret;
	}
	
	public Dst2 getLatestDst2() {
		Dst2 ret = null;
		List<Dst2> dst2s = lri.getDst2s();
		if(dst2s==null || dst2s.size()==0)
			return null;
		
		Collections.sort(dst2s);
		ret = dst2s.get(dst2s.size()-1);
		return ret;
	}
	
	public HAIN getLatestHAIN1() {
		HAIN ret = null;
		List<HAIN> hains = lri.getHAINS();
		if(hains==null || hains.size()==0)
			return null;
		
		Collections.sort(hains);
		ret = hains.get(hains.size()-1);
		return ret;
	}
	
	public HAIN2 getLatestHAIN2() {
		HAIN2 ret = null;
		List<HAIN2> hains = lri.getHAINS2();
		if(hains==null || hains.size()==0)
			return null;
		
		Collections.sort(hains);
		ret = hains.get(hains.size()-1);
		return ret;
	}
	
	public Boolean getAnyResistance(ArrayList<Concept> list) {
		Boolean resistant = null;
		
		for(Concept c : list) {
			if(c!=null) {
				
				if(c.getId().intValue()==Context.getService(TbService.class).getConcept(TbConcepts.RESISTANT).getId().intValue()) {
					resistant = Boolean.TRUE;
					break;
				}
			
				
				else if(resistant == null) {
					resistant = Boolean.FALSE;
				}
			}
		}
		
		
		return resistant;
		
	}
	
	public Date getMinGrowthDate(ArrayList<Date> dates) {
		Date minDate = dates.get(0);
		
		for(Date d : dates) {
			if(d.before(minDate)) {
				minDate = d;
			}
		}
		
		return minDate;
	}
	

}
