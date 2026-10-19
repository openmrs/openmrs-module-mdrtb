package org.openmrs.module.mdrtb.reporting.custom;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.api.LabTestService;
import org.openmrs.module.mdrtb.lab.LabTest;
import org.openmrs.module.mdrtb.lab.LabTestAttribute;
import org.openmrs.module.mdrtb.lab.LabTestType;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.form.custom.CultureForm;
import org.openmrs.module.mdrtb.form.custom.DSTForm;
import org.openmrs.module.mdrtb.form.custom.HAIN2Form;
import org.openmrs.module.mdrtb.form.custom.HAINForm;
import org.openmrs.module.mdrtb.form.custom.SmearForm;
import org.openmrs.module.mdrtb.form.custom.TB03uForm;
import org.openmrs.module.mdrtb.form.custom.XpertForm;
import org.openmrs.module.mdrtb.specimen.Dst;
import org.openmrs.module.mdrtb.specimen.DstImpl;
import org.openmrs.module.mdrtb.specimen.DstResult;
import org.openmrs.module.mdrtb.specimen.Specimen;
import org.openmrs.module.mdrtb.specimen.custom.HAIN;
import org.openmrs.module.mdrtb.specimen.custom.HAIN2;
import org.openmrs.module.mdrtb.specimen.custom.HAIN2Impl;
import org.openmrs.module.mdrtb.specimen.custom.HAINImpl;
import org.openmrs.module.mdrtb.specimen.custom.Xpert;
import org.openmrs.module.mdrtb.specimen.custom.XpertImpl;

public class TB03uUtil {
	
	public static SmearForm getFollowupSmearForm(TB03uForm form, Integer month) {
		SmearForm c = null;
		for (SmearForm sf : form.getSmears()) {
			if (sf.getMonthOfTreatment() != null && sf.getMonthOfTreatment() == month.intValue()) {
				c = sf;
				break;
			}
		}
		return c;
	}
	
	public static CultureForm getFollowupCultureForm(TB03uForm form, Integer month) {
		CultureForm c = null;
		for (CultureForm cf : form.getCultures()) {
			if (cf.getMonthOfTreatment() != null && cf.getMonthOfTreatment() == month.intValue()) {
				c = cf;
				break;
			}
		}
		return c;
	}
	
	public static Dst getDiagnosticDST(TB03uForm tf) {
		List<DSTForm> dsts = tf.getDsts();
		if (dsts != null && !dsts.isEmpty()) {
			// Search for diagnostic DST, i.e. where month of treatment = 0
			for (DSTForm dstForm : dsts) {
				if (dstForm.getMonthOfTreatment() == null)
					continue;
				if (dstForm.getMonthOfTreatment() != 0)
					continue;
				LabTest dstTest = Context.getService(LabTestService.class).getDstLabTestOrder(dstForm.getEncounter());
				if (dstTest != null) {
					return new DstImpl(dstTest);
				}
			}
		}
		return null;
	}
	
	public static List<DstResult> getDiagnosticDSTResults(TB03uForm tf) {
		List<DSTForm> dsts = tf.getDsts();
		if (dsts != null && !dsts.isEmpty()) {
			for (DSTForm dstForm : dsts) {
				if (dstForm.getMonthOfTreatment() == null)
					continue;
				if (dstForm.getMonthOfTreatment() != 0)
					continue;
				LabTest dstTest = Context.getService(LabTestService.class).getDstLabTestOrder(dstForm.getEncounter());
				if (dstTest != null) {
					LabTestAttribute rif = Context.getService(LabTestService.class).getCommonAttributeByTestAndName(dstTest,
					    MdrtbConcepts.RIFAMPICIN_RESISTANCE);
					LabTestAttribute iso = Context.getService(LabTestService.class).getCommonAttributeByTestAndName(dstTest,
					    MdrtbConcepts.ISONIAZID_RESISTANCE);
					if (rif != null || iso != null) {
						return new DstImpl(dstTest).getResults();
					}
				}
			}
		}
		return new ArrayList<>();
	}
	
	//find out which date and before or after
	//also to use sample collection date of test or other date?
	public static Specimen getClosestTestTo(List<Specimen> tests, Date date) {
		
		Specimen min = tests.get(0);
		long minDiff;
		long diffInTime;
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
	
	public static Xpert getFirstXpert(TB03uForm tf) {
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
	
	public static XpertForm getFirstXpertForm(TB03uForm tf) {
		XpertForm c = null;
		List<XpertForm> xperts = tf.getXperts();
		if (xperts != null && !xperts.isEmpty()) {
			c = xperts.get(0);
			
		}
		
		return c;
	}
	
	public static HAIN getFirstHAIN(TB03uForm tf) {
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
	
	public static HAINForm getFirstHAINForm(TB03uForm tf) {
		HAINForm c = null;
		
		List<HAINForm> hains = tf.getHains();
		if (hains != null && !hains.isEmpty()) {
			c = hains.get(0);
		}
		
		return c;
	}
	
	public static HAIN2 getFirstHAIN2(TB03uForm tf) {
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
	
	public static HAIN2Form getFirstHAIN2Form(TB03uForm tf) {
		HAIN2Form c = null;
		
		List<HAIN2Form> hains = tf.getHain2s();
		if (hains != null && !hains.isEmpty()) {
			c = hains.get(0);
		}
		
		return c;
	}
	
}
