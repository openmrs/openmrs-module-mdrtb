/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.labmodule.reporting.definition;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.ConceptSet;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.labmodule.TbConcepts;
import org.openmrs.module.labmodule.TbUtil;
import org.openmrs.module.labmodule.MdrtbConstants.TbClassification;
import org.openmrs.module.labmodule.reporting.MdrtbQueryService;
import org.openmrs.module.labmodule.service.TbService;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Evaluates an DotsDstResultCohortDefinition and produces a Cohort
 */
@Handler(supports={LabDstResultCohortDefinition.class})
public class LabDstResultCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

		/**
	 * Default Constructor
	 */
	public LabDstResultCohortDefinitionEvaluator() {}
	
	/**
     * @see CohortDefinitionEvaluator#evaluateCohort(CohortDefinition, EvaluationContext)
     * @should return confirmed poly-resistance patients for the period
     * @should return confirmed mdrtb patients for the period
     * @should return confirmed xdrtb patients for the period
     */
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
    	
    	LabDstResultCohortDefinition cd = (LabDstResultCohortDefinition) cohortDefinition;
    	Cohort c = null;
    	
		Date sd = cd.getMinResultDate();
		Date ed = cd.getMaxResultDate();
    	
		
		if (cd.getTbClassification() == TbClassification.POLY_RESISTANT_TB) {			
	    	for(Concept currentDrug: cd.getSpecificDrugs())
			{
				//Add 
				c = c.intersect(c, MdrtbQueryService.getPatientsResistantToAnyDrugs(context, sd, ed, currentDrug));
			}
	    	//Exclude the drugs which were not requested and available in the DST1 drugs ie FLD
	    	for(Concept susceptibleDrug : TbUtil.getDst1Drugs())
	    	{
	    		if(cd.getSpecificDrugs().size()>0)
	    		{
	    			if(!cd.getSpecificDrugs().contains(susceptibleDrug)){
			    		c = c.intersect(c, MdrtbQueryService.getPatientsSusceptibleToAnyDrugs(context, sd, ed, susceptibleDrug));
		    		}
	    		}
	    		c = c.intersect(c, MdrtbQueryService.getPatientsSusceptibleToAnyDrugs(context, sd, ed, susceptibleDrug));
	    	}
		}
		
		if (cd.getTbClassification() == TbClassification.ANY_RESISTANCE) {
	    	
			for(Concept currentDrug: cd.getSpecificDrugs())
			{
				//Add 
				c = c.intersect(c, MdrtbQueryService.getPatientsResistantToAnyDrugs(context, sd, ed, currentDrug));
			}
		}
		
		if (cd.getTbClassification() == TbClassification.FULLY_SENSITIVE) {
			
	    	Cohort c0 = Cohort.intersect(c, MdrtbQueryService.getPatientsSusceptibleToAnyDrugs(context, sd, ed, TbUtil.getRif()));
	    	Cohort c1 = Cohort.intersect(c, MdrtbQueryService.getPatientsSusceptibleToAnyDrugs(context, sd, ed, TbUtil.getInh()));
	    	Cohort c2 = Cohort.intersect(c, MdrtbQueryService.getPatientsSusceptibleToAnyDrugs(context, sd, ed, TbUtil.getEth()));
	    	Cohort c3 = Cohort.intersect(c, MdrtbQueryService.getPatientsSusceptibleToAnyDrugs(context, sd, ed, TbUtil.getStrept()));
	    	Cohort c4 = Cohort.intersect(c, MdrtbQueryService.getPatientsSusceptibleToAnyDrugs(context, sd, ed, TbUtil.getPyr()));
	    	
	    	c = Cohort.intersect(c0,c1);
	    	c = Cohort.intersect(c,c2);
	    	c = Cohort.intersect(c,c3);
	    	c = Cohort.intersect(c,c4);
	    	
		}
		
		if(cd.getTbClassification()==TbClassification.RR_TB) {
	    	c = Cohort.intersect(c, MdrtbQueryService.getPatientsResistantToAnyDrugs(context, sd, ed, TbUtil.getRif()));
		}
		
		if(cd.getTbClassification()==TbClassification.MONO_RESISTANT_TB) {
			if(cd.getSpecificDrugs().isEmpty())
		    	return new EvaluatedCohort(c, cohortDefinition, context);
			
			Concept monoDrug  = (Concept)cd.getSpecificDrugs().get(0);
			//Check for resistance for only the Specific drugs
	    	c = Cohort.intersect(c, MdrtbQueryService.getPatientsResistantToAnyDrugs(context, sd, ed, monoDrug));
	    	
	    	//Check for Susceptibility for the rest of the drugs
	    	if(monoDrug.getConceptId() !=  TbUtil.getRif().getConceptId())
	    	{
	    		c = Cohort.intersect(c, MdrtbQueryService.getPatientsSusceptibleToAnyDrugs(context, sd, ed, TbUtil.getRif()));		    	
	    	}
	    	if(monoDrug.getConceptId() !=  TbUtil.getInh().getConceptId())
	    	{
	    		c = Cohort.intersect(c, MdrtbQueryService.getPatientsSusceptibleToAnyDrugs(context, sd, ed, TbUtil.getInh()));		    	
	    	}
	    	if(monoDrug.getConceptId() !=  TbUtil.getEth().getConceptId())
	    	{
	    		c = Cohort.intersect(c, MdrtbQueryService.getPatientsSusceptibleToAnyDrugs(context, sd, ed, TbUtil.getEth()));		    	
	    	}
	    	if(monoDrug.getConceptId() !=  TbUtil.getStrept().getConceptId())
	    	{
	    		c = Cohort.intersect(c, MdrtbQueryService.getPatientsSusceptibleToAnyDrugs(context, sd, ed, TbUtil.getStrept()));		    	
	    	}
	    	if(monoDrug.getConceptId() !=  TbUtil.getEth().getConceptId())
	    	{
	    		c = Cohort.intersect(c, MdrtbQueryService.getPatientsSusceptibleToAnyDrugs(context, sd, ed, TbUtil.getPyr()));		    	
	    	}
		}
		
    	if (cd.getTbClassification() == TbClassification.XDR_TB) {

    		// Must be resistant to INH
    		c = MdrtbQueryService.getPatientsResistantToAnyDrugs(context, sd, ed, TbUtil.getInh());
	    	
	    	// Must be resistant to RIF
	    	c = Cohort.intersect(c, MdrtbQueryService.getPatientsResistantToAnyDrugs(context, sd, ed, TbUtil.getRif()));
	    		
    		// Must be resistant to at least one fluoroquinolone
    		Concept quinoloneSet = Context.getService(TbService.class).getConcept(TbConcepts.QUINOLONES_RESISTANCE);
    		List<Concept> quinolones = new ArrayList<Concept>();
    		for (ConceptSet set : quinoloneSet.getConceptSets()) {
    			quinolones.add(set.getConcept());
    		}
    		Concept[] quins = quinolones.toArray(new Concept[quinolones.size()]);
    		c = Cohort.intersect(c, MdrtbQueryService.getPatientsResistantToAnyDrugs(context, sd, ed, quins));
    		
    		// Must be resistant to at least one of capreomycin, kanamycin, and amikacin
    		Concept cap = Context.getService(TbService.class).getConcept(TbConcepts.CM_R);
    		Concept kan = Context.getService(TbService.class).getConcept(TbConcepts.KM_R);
    		Concept amk = Context.getService(TbService.class).getConcept(TbConcepts.AM_R);
    		c = Cohort.intersect(c, MdrtbQueryService.getPatientsResistantToAnyDrugs(context, sd, ed, cap, kan, amk));
	    		
    	}

    	if (cd.getTbClassification() == TbClassification.MDR_TB ) {

    		// Must be resistant to INH
    		c = MdrtbQueryService.getPatientsResistantToAnyDrugs(context, sd, ed, TbUtil.getInh());
	    	
	    	// Must be resistant to RIF
	    	c = Cohort.intersect(c, MdrtbQueryService.getPatientsResistantToAnyDrugs(context, sd, ed, TbUtil.getRif()));
	    	
	    	//Plus other drugs needed for the report	    	
	    	for(Concept currentDrug: cd.getSpecificDrugs())
	    	{
	    		//Will ignore any request to add these again as they are checked anyways
	    		if(currentDrug.equals(TbUtil.getInh()) || currentDrug.equals(TbUtil.getRif()))
	    			continue;
	    		c = c.intersect(c, MdrtbQueryService.getPatientsResistantToAnyDrugs(context, sd, ed, currentDrug));	    		
	    	}
	    	//rest should be susceptible
	    	//find out the rest Dst1Drugs - specific drugs
	    	for(Concept susceptibleDrug : TbUtil.getDst1Drugs())
	    	{
	    		//We have already determinned resistance for Rif and Inh so skip these
	    		if(susceptibleDrug.equals(TbUtil.getRif()) || susceptibleDrug.equals(TbUtil.getInh()))
	    			continue;
	    		
	    		if(!cd.getSpecificDrugs().isEmpty())
	    		{
	    			if(!cd.getSpecificDrugs().contains(susceptibleDrug)){
			    		c = c.intersect(c, MdrtbQueryService.getPatientsSusceptibleToAnyDrugs(context, sd, ed, susceptibleDrug));
		    		}
	    		}
	    		c = c.intersect(c, MdrtbQueryService.getPatientsSusceptibleToAnyDrugs(context, sd, ed, susceptibleDrug));
	    	}
    	}
    	return new EvaluatedCohort(c, cohortDefinition, context);

    }
}