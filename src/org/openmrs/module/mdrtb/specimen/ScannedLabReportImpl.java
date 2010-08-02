package org.openmrs.module.mdrtb.specimen;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbFactory;
import org.openmrs.module.mdrtb.MdrtbService;
import org.openmrs.obs.ComplexData;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.web.multipart.MultipartFile;


public class ScannedLabReportImpl implements ScannedLabReport {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	MdrtbFactory mdrtbFactory;
	
	Obs report;
	
	public ScannedLabReportImpl() {
		this.mdrtbFactory = Context.getService(MdrtbService.class).getMdrtbFactory();
	}
	
	// create a new scanned lab report object, given an existing encounter
	public ScannedLabReportImpl(Encounter encounter) {
		this.mdrtbFactory = Context.getService(MdrtbService.class).getMdrtbFactory();
		
		if(encounter == null) {
			throw new RuntimeException ("Cannot create scanned lab report: encounter can not be null.");
		}
		
		report = new Obs (encounter.getPatient(), mdrtbFactory.getConceptScannedLabReport(), encounter.getEncounterDatetime(), null);
	}
	
	// create a scanned lab report given an existing obs
	public ScannedLabReportImpl(Obs report) {
		this.mdrtbFactory = Context.getService(MdrtbService.class).getMdrtbFactory();
		
		try {
			this.report = Context.getObsService().getComplexObs(report.getId(), OpenmrsConstants.RAW_VIEW);
		}
		catch(Exception e) {
			throw new RuntimeException ("Unable to retrieve scanned lab report. File may be missing.", e);
		}
	}

	public Object getScannedLabReport() {
		return this.report;
	}
	
    public String getFilename() {
    	return report.getComplexData().getTitle();
    }

    public String getId() {
	    return report.getId().toString();
    }
    
    public Location getLab() {
    	return report.getLocation();
    }
    
    public void setFile(MultipartFile file) {
    	ComplexData data = null;
    	
        try {
	        data = new ComplexData(file.getOriginalFilename(), file.getInputStream());
        }
        catch (IOException e) {
	        log.error("Unable to load scanned lab report", e);
        }
    	report.setComplexData(data);
    }
    
    public void setLab(Location lab) {
    	report.setLocation(lab);
    }
    
    /**
	  * Protected methods used for interacting with the matching MdrSpecimenImpl
	  */

	 protected Obs getObs() {
		 return report;
	 }
}
