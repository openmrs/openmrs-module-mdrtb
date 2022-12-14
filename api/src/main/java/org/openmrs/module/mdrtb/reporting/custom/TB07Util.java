package org.openmrs.module.mdrtb.reporting.custom;

public class TB07Util {

	/*public static Boolean isBacPositive(Patient patient) {
		Smear diagSmear = TB03Util.getDiagnosticSmear(patient);
		Xpert diagXpert = TB03Util.getFirstXpert(patient);
		HAIN diagHAIN = TB03Util.getFirstHAIN(patient);
		Culture diagCulture = TB03Util.getDiagnosticCulture(patient);
		
		Concept smearResult = null;
		Concept xpertResult = null;
		Concept hainResult = null;
		Concept	cultureResult = null;
		
		if(diagSmear != null)
			smearResult = diagSmear.getResult();
		
		if(diagXpert != null)
			xpertResult = diagXpert.getResult();
		
		if(diagHAIN != null)
			hainResult = diagHAIN.getResult();
		
		if(diagCulture != null)
			cultureResult = diagCulture.getResult();
		
		Integer [] positiveResultConceptIds = TbUtil.getPositiveResultConceptIds();
		
		for(int i=0; i<positiveResultConceptIds.length; i++) {
			if((smearResult!=null && smearResult.getConceptId().intValue()==positiveResultConceptIds[i].intValue()) || (cultureResult!=null && cultureResult.getConceptId().intValue()==positiveResultConceptIds[i].intValue()) || (xpertResult!=null && xpertResult.getConceptId().intValue()==positiveResultConceptIds[i].intValue()) || (hainResult!=null && hainResult.getConceptId().intValue()==positiveResultConceptIds[i].intValue())) {
				return true;
			}
		}
		
		
		return false;
	}*/
	
	/*public static Boolean isBacPositive(Patient patient), Date startDate, Date endDate) {
		Smear diagSmear = TB03Util.getDiagnosticSmear(patient);
		Xpert diagXpert = TB03Util.getFirstXpert(patient);
		HAIN diagHAIN = TB03Util.getFirstHAIN(patient);
		Culture diagCulture = TB03Util.getDiagnosticCulture(patient);
		
		Concept smearResult = null;
		Concept xpertResult = null;
		Concept hainResult = null;
		Concept	cultureResult = null;
		
		if(diagSmear != null)
			smearResult = diagSmear.getResult();
		
		if(diagXpert != null)
			xpertResult = diagXpert.getResult();
		
		if(diagHAIN != null)
			hainResult = diagHAIN.getResult();
		
		if(diagCulture != null)
			cultureResult = diagCulture.getResult();
		
		Integer [] positiveResultConceptIds = TbUtil.getPositiveResultConceptIds();
		
		for(int i=0; i<positiveResultConceptIds.length; i++) {
			if((smearResult!=null && smearResult.getConceptId().intValue()==positiveResultConceptIds[i].intValue()) || (cultureResult!=null && cultureResult.getConceptId().intValue()==positiveResultConceptIds[i].intValue()) || (xpertResult!=null && xpertResult.getConceptId().intValue()==positiveResultConceptIds[i].intValue()) || (hainResult!=null && hainResult.getConceptId().intValue()==positiveResultConceptIds[i].intValue())) {
				return true;
			}
		}
		
		
		return false;
	}*/
	
	
	
	
}
