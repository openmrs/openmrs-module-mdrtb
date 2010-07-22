package org.openmrs.module.mdrtb.specimen;

import org.openmrs.Location;
import org.springframework.web.multipart.MultipartFile;


public interface ScannedLabReport {

		/**
		 * Data this interface provides access to
		 */
		
		public Object getScannedLabReport();
		public String getId();
		
		public String getFilename();
		
		public void setFile(MultipartFile file);
		
		public Location getLab();
		public void setLab(Location location);
}
