package org.openmrs.module.mdrtb.web.pihhaiti;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HospitalizationReportController {

		@RequestMapping("/module/mdrtb/pihhaiti/hospitalizationReport.form")
		public ModelAndView createHospitalizationReport() {

			DateFormat df = Context.getDateFormat();
			
			Concept [] typeOfPatient = {Context.getConceptService().getConcept(3289), Context.getConceptService().getConcept(1715)};
			
			// loop through all patients
			for (Person patient : Context.getPatientService().getAllPatients(false)) {
				
				Person [] patientArray = {patient};				
				
				List<Obs> status = Context.getObsService().getObservations(Arrays.asList(patientArray), null, Arrays.asList(typeOfPatient), null, null, null, null, null, null, null, null, false);
		
				Collections.reverse(status);
				
				if (status.size() > 0) {
				
					StringBuffer output = new StringBuffer();
					output.append(patient.getId() + "|" + patient.getPersonName().toString());
					
					for (Obs obs : status) {
						
						if (obs.getConcept().getId() == 3289) {
							output.append(("|" + obs.getValueCoded().getDisplayString()));
						}
						
						if (obs.getConcept().getId() == 1715) {
							output.append(("|" + (obs.getValueAsBoolean() ? "HOSP. SINCE LAST VISIT" : "NOT HOSP. SINCE LAST VISIT")));
						}
						
						output.append("|" + df.format(obs.getObsDatetime()));					}
					
					System.out.println(output.toString());
					
				}
			}
			
			
			return new ModelAndView();
		}
}
