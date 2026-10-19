package org.openmrs.module.mdrtb.reporting.custom;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientProgram;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbConstants;
import org.openmrs.module.mdrtb.MdrtbUtil;
import org.openmrs.module.mdrtb.api.LabTestService;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.form.custom.CultureForm;
import org.openmrs.module.mdrtb.form.custom.DSTForm;
import org.openmrs.module.mdrtb.form.custom.HAIN2Form;
import org.openmrs.module.mdrtb.form.custom.HAINForm;
import org.openmrs.module.mdrtb.form.custom.SmearForm;
import org.openmrs.module.mdrtb.form.custom.TB03Form;
import org.openmrs.module.mdrtb.form.custom.TB03uForm;
import org.openmrs.module.mdrtb.form.custom.XpertForm;
import org.openmrs.module.mdrtb.lab.LabTest;
import org.openmrs.module.mdrtb.lab.LabTestAttribute;
import org.openmrs.module.mdrtb.lab.LabTestType;
import org.openmrs.module.mdrtb.specimen.Culture;
import org.openmrs.module.mdrtb.specimen.CultureImpl;
import org.openmrs.module.mdrtb.specimen.Dst;
import org.openmrs.module.mdrtb.specimen.DstImpl;
import org.openmrs.module.mdrtb.specimen.Smear;
import org.openmrs.module.mdrtb.specimen.SmearImpl;
import org.openmrs.module.mdrtb.specimen.Specimen;
import org.openmrs.module.mdrtb.specimen.custom.HAIN;
import org.openmrs.module.mdrtb.specimen.custom.HAIN2;
import org.openmrs.module.mdrtb.specimen.custom.HAIN2Impl;
import org.openmrs.module.mdrtb.specimen.custom.HAINImpl;
import org.openmrs.module.mdrtb.specimen.custom.Xpert;
import org.openmrs.module.mdrtb.specimen.custom.XpertImpl;
import org.openmrs.module.mdrtb.api.MessagePropertyService;

public class TB03Util {
	
	private TB03Util() {
	}
	
	public static Culture getDiagnosticCulture(TB03Form tf) {
		Culture c = null;
		for (CultureForm cf : tf.getCultures()) {
			if (cf.getMonthOfTreatment() != null && cf.getMonthOfTreatment() == 0) {
				LabTestType labTestType = Context.getService(LabTestService.class).getCommonTestType();
				LabTest culture = Context.getService(LabTestService.class).getMdrtbLabTestOrder(cf.getEncounter(),
				    labTestType);
				c = new CultureImpl(culture);
				break;
			}
		}
		return c;
	}
	
	public static CultureForm getDiagnosticCultureForm(TB03Form tf) {
		CultureForm c = null;
		for (CultureForm cf : tf.getCultures()) {
			if (cf.getMonthOfTreatment() != null && cf.getMonthOfTreatment() == 0) {
				c = cf;
				break;
			}
		}
		return c;
	}
	
	public static Xpert getFirstXpert(TB03Form tf) {
		Xpert c = null;
		List<XpertForm> xperts = tf.getXperts();
		if (xperts != null && !xperts.isEmpty()) {
			LabTestType labTestType = Context.getService(LabTestService.class).getCommonTestType();
			LabTest xpert = Context.getService(LabTestService.class).getMdrtbLabTestOrder(xperts.get(0).getEncounter(),
			    labTestType);
			c = new XpertImpl(xpert);
		}
		return c;
	}
	
	public static XpertForm getFirstXpertForm(TB03Form tf) {
		XpertForm c = null;
		List<XpertForm> xperts = tf.getXperts();
		if (xperts != null && !xperts.isEmpty()) {
			c = xperts.get(0);
		}
		return c;
	}
	
	public static HAIN getFirstHAIN(TB03Form tf) {
		HAIN c = null;
		List<HAINForm> hains = tf.getHains();
		if (hains != null && !hains.isEmpty()) {
			LabTestType labTestType = Context.getService(LabTestService.class).getCommonTestType();
			LabTest hain = Context.getService(LabTestService.class).getMdrtbLabTestOrder(hains.get(0).getEncounter(),
			    labTestType);
			c = new HAINImpl(hain);
		}
		return c;
	}
	
	public static HAINForm getFirstHAINForm(TB03Form tf) {
		HAINForm c = null;
		List<HAINForm> hains = tf.getHains();
		if (hains != null && !hains.isEmpty()) {
			c = hains.get(0);
		}
		return c;
	}
	
	public static HAIN2 getFirstHAIN2(TB03Form tf) {
		HAIN2 c = null;
		List<HAIN2Form> hains = tf.getHain2s();
		if (hains != null && !hains.isEmpty()) {
			LabTestType labTestType = Context.getService(LabTestService.class).getCommonTestType();
			LabTest hain = Context.getService(LabTestService.class).getMdrtbLabTestOrder(hains.get(0).getEncounter(),
			    labTestType);
			c = new HAIN2Impl(hain);
		}
		return c;
	}
	
	public static HAIN2Form getFirstHAIN2Form(TB03Form tf) {
		HAIN2Form c = null;
		List<HAIN2Form> hains = tf.getHain2s();
		
		if (hains != null && !hains.isEmpty()) {
			c = hains.get(0);
		}
		return c;
	}
	
	public static Smear getDiagnosticSmear(TB03Form form) {
		Smear c = null;
		for (SmearForm sf : form.getSmears()) {
			if (sf.getMonthOfTreatment() != null && sf.getMonthOfTreatment() == 0) {
				LabTestType labTestType = Context.getService(LabTestService.class).getCommonTestType();
				LabTest smear = Context.getService(LabTestService.class)
				        .getMdrtbLabTestOrder(sf.getEncounter(), labTestType);
				c = new SmearImpl(smear);
				c.setResult(sf.getSmearResult());
				c.setResultDate(sf.getEncounterDatetime());
				break;
			}
		}
		return c;
	}
	
	public static SmearForm getDiagnosticSmearForm(TB03Form form) {
		SmearForm c = null;
		for (SmearForm sf : form.getSmears()) {
			if (sf.getMonthOfTreatment() != null && sf.getMonthOfTreatment() == 0) {
				c = sf;
				break;
			}
		}
		return c;
	}
	
	public static Smear getFollowupSmear(TB03Form form, Integer month) {
		Smear c = null;
		for (SmearForm sf : form.getSmears()) {
			if (sf.getMonthOfTreatment() != null && sf.getMonthOfTreatment() == month.intValue()) {
				LabTestType labTestType = Context.getService(LabTestService.class).getCommonTestType();
				LabTest smear = Context.getService(LabTestService.class)
				        .getMdrtbLabTestOrder(sf.getEncounter(), labTestType);
				c = new SmearImpl(smear);
				break;
			}
		}
		return c;
	}
	
	public static SmearForm getFollowupSmearForm(TB03Form form, Integer month) {
		SmearForm c = null;
		for (SmearForm sf : form.getSmears()) {
			if (sf.getMonthOfTreatment() != null && sf.getMonthOfTreatment() == month.intValue()) {
				c = sf;
				break;
			}
		}
		return c;
	}
	
	public static Dst getDiagnosticDST(TB03Form tf) {
		Dst d = null;
		List<DSTForm> dsts = tf.getDsts();
		List<LabTest> monthZeroDsts = new ArrayList<>();
		Concept monthConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MONTH_OF_TREATMENT);
		if (dsts != null && !dsts.isEmpty()) {
			for (DSTForm dstForm : dsts) {
				// Which one is diagnostic? The one with Month = 0
				Obs monthOfTreatment = MdrtbUtil.getObsFromEncounter(monthConcept, dstForm.getEncounter());
				if (monthOfTreatment != null && monthOfTreatment.getValueNumeric().equals(0D)) {
					LabTest dst = Context.getService(LabTestService.class).getDstLabTestOrder(dstForm.getEncounter());
					if (dst != null)
						monthZeroDsts.add(dst);
				}
			}
		}
		Date latest = new Date(0);
		for (LabTest labTest : monthZeroDsts) {
			Set<LabTestAttribute> attributes = labTest.getAttributes();
			// Any DST-related stuff?
			for (LabTestAttribute attribute : attributes) {
				if(MdrtbConstants.DST_TEST_GROUP.equalsIgnoreCase(attribute.getAttributeType().getGroupName())) {
					return new DstImpl(labTest);
				}
			}
			if (labTest.getDateCreated().after(latest)) {
				latest = labTest.getDateCreated();
				d = new DstImpl(labTest);
			}
		}
		return d;
	}
	
	//find out which date and before or after
	//also to use sample collection date of test or other date?
	public static Specimen getClosestTestTo(List<Specimen> tests, Date date) {
		
		Specimen min = tests.get(0);
		long minDiff = 0;
		long diffInTime = 0;
		GregorianCalendar dateCal = new GregorianCalendar();
		dateCal.setTime(date);
		dateCal.set(Calendar.HOUR, 0);
		dateCal.set(Calendar.MINUTE, 0);
		dateCal.set(Calendar.SECOND, 0);
		dateCal.set(Calendar.MILLISECOND, 1);
		
		GregorianCalendar testCal = new GregorianCalendar();
		testCal.setTime(min.getDateCollected());
		testCal.set(Calendar.HOUR, 0);
		testCal.set(Calendar.MINUTE, 0);
		testCal.set(Calendar.SECOND, 0);
		testCal.set(Calendar.MILLISECOND, 1);
		
		diffInTime = dateCal.getTimeInMillis() - testCal.getTimeInMillis();
		
		minDiff = Math.abs(diffInTime);
		
		for (int i = 1; i < tests.size(); i++) {
			testCal.setTime(tests.get(i).getDateCollected());
			testCal.set(Calendar.HOUR, 0);
			testCal.set(Calendar.MINUTE, 0);
			testCal.set(Calendar.SECOND, 0);
			testCal.set(Calendar.MILLISECOND, 1);
			
			diffInTime = dateCal.getTimeInMillis() - testCal.getTimeInMillis();
			
			diffInTime = Math.abs(diffInTime);
			
			if (diffInTime < minDiff) {
				minDiff = diffInTime;
				min = tests.get(i);
			}
		}
		return min;
		
	}
	
	public static String getRegistrationNumber(TB03Form form) {
		return getRegistrationNumber(form == null ? null : form.getEncounter());
	}
	
	public static String getRegistrationNumber(TB03uForm form) {
		return getRegistrationNumber(form == null ? null : form.getEncounter());
	}
	
	private static String getRegistrationNumber(Encounter encounter) {
		String registrationNumber = null;
		Concept ppidConcept = Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PATIENT_PROGRAM_ID);
		if (encounter != null) {
			Obs idObs = MdrtbUtil.getObsFromEncounter(ppidConcept, encounter);
			if (idObs != null && idObs.getValueNumeric() != null) {
				PatientProgram patientProgram = Context.getProgramWorkflowService().getPatientProgram(
				    idObs.getValueNumeric().intValue());
				if (patientProgram != null) {
					PatientIdentifier patientIdentifier = Context.getService(MdrtbService.class)
					        .getPatientProgramIdentifier(patientProgram);
					if (patientIdentifier != null) {
						registrationNumber = patientIdentifier.getIdentifier();
					}
				}
			}
		}
		if (registrationNumber == null || registrationNumber.isEmpty()) {
			return Context.getService(MessagePropertyService.class).getMessage("mdrtb.unassigned");
		}
		return registrationNumber;
	}
}
