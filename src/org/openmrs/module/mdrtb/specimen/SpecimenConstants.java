package org.openmrs.module.mdrtb.specimen;


public final class SpecimenConstants {

	public static enum TestStatus {
		UNKNOWN, ORDERED, RECEIVED, STARTED, COMPLETED
	}

	 public static enum BacteriologyResult{
		POSITIVE("mdrtb.positiveCode","red"),
	    NEGATIVE("mdrtb.negativeCode","green"),
	    SCANTY("mdrtb.scantyCode","yellow"),
	    PENDING("mdrtb.pendingCode","grey"),
	    CONTAMINATED("mdrtb.contaminatedCode","yellow");
	    	
	    String displayCode; // spring:message code for this result  
	    String color;  // the color code for this result
	    	
	    BacteriologyResult (String displayCode, String color){
	    	this.displayCode = displayCode;
	    	this.color = color;
	    }
	    public String getDisplayCode(){
	    	return this.displayCode;
	    }
	    public String getColor(){
	    	return this.color;
	    }
	}
}
