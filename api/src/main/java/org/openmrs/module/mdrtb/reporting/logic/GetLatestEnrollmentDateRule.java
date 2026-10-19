package org.openmrs.module.mdrtb.reporting.logic;

import java.util.Map;
import java.util.Set;

import org.openmrs.Patient;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;

public class GetLatestEnrollmentDateRule implements Rule {
	
	/**
	 * @see Rule#eval(LogicContext, Patient, Map)
	 */
	public Result eval(LogicContext context, Patient patient, Map<String, Object> parameters) throws LogicException {
		
		return context.read(patient.getId(), context.getLogicDataSource("pihprogram"), "MDR-TB PROGRAM");
		
	}
	
	@Deprecated
	public Result eval(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
		return context.read(patientId, context.getLogicDataSource("pihprogram"), "MDR-TB PROGRAM");
	}
	
	/**
	 * @see Rule#getChildRules()
	 */
	public String[] getDependencies() {
		return null;
	}
	
	/**
	 * @see Rule#getDefaultDatatype()
	 */
	public Datatype getDefaultDatatype() {
		return null;
	}
	
	/**
	 * @see Rule#getParameterList()
	 */
	public Set<RuleParameterInfo> getParameterList() {
		return null;
	}
	
	/**
	 * @see Rule#getTTL()
	 */
	public int getTTL() {
		return 0;
	}
	
}
