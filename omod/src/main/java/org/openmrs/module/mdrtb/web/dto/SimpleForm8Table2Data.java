package org.openmrs.module.mdrtb.web.dto;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.module.mdrtb.reporting.custom.Form8Table2Data;

public class SimpleForm8Table2Data extends BaseOpenmrsData {
	
	private static final long serialVersionUID = -8088810099020264999L;
	
	private Integer activePHCTotal;
	
	private Integer respiratoryPHCTotal;
	
	private Integer pulmonaryPHCTotal;
	
	private Integer bacExPHCTotal;
	
	private Integer decayPhasePHCTotal;
	
	private Integer decayPhaseTotal;
	
	private Integer decayPhase014;
	
	private Integer decayPhase1517;
	
	private Integer decayPhase1819;
	
	private Integer nervousSystemPHCTotal;
	
	private Integer otherOrgansPHCTotal;
	
	private Integer miliaryPHCTotal;
	
	private Integer detectedByRoutineCheckups;
	
	private Integer detectedBySpecialistsTotal;
	
	private Integer detectedByTBDoctors;
	
	private Integer detectedByOtherSpecialists;
	
	private Integer routine014;
	
	private Integer routine1517;
	
	private Integer routine1819;
	
	private Integer focalTotal;
	
	private Integer infiltrativeTotal;
	
	private Integer disseminatedTotal;
	
	private Integer cavernousTotal;
	
	private Integer fibrousTotal;
	
	private Integer cirrhoticTotal;
	
	private Integer tbComplexTotal;
	
	private Integer miliaryTotal;
	
	private Integer tuberculomaTotal;
	
	private Integer bronchiTotal;
	
	private Integer pleurisyTotal;
	
	private Integer hilarLymphNodesTotal;
	
	private Integer osteoarticularTotal;
	
	private Integer urogenitalTotal;
	
	private Integer peripheralLymphNodesTotal;
	
	private Integer abdominalTotal;
	
	private Integer skinTotal;
	
	private Integer eyeTotal;
	
	private Integer nervousSystemTotal;
	
	private Integer liverTotal;
	
	private Integer childbearing;
	
	private Integer pregnant;
	
	private Integer contact;
	
	private Integer migrants;
	
	private Integer phcWorkers;
	
	private Integer tbServiceWorkers;
	
	private Integer relapseCount;
	
	private Integer failCount;
	
	private Integer ltfuCount;
	
	private Integer otherCount;
	
	public SimpleForm8Table2Data(Form8Table2Data form8Table2Data) {
		this.activePHCTotal = form8Table2Data.getActivePHCTotal();
		this.respiratoryPHCTotal = form8Table2Data.getRespiratoryPHCTotal();
		this.pulmonaryPHCTotal = form8Table2Data.getPulmonaryPHCTotal();
		this.bacExPHCTotal = form8Table2Data.getBacExPHCTotal();
		this.decayPhasePHCTotal = form8Table2Data.getDecayPhasePHCTotal();
		this.decayPhaseTotal = form8Table2Data.getDecayPhaseTotal();
		this.decayPhase014 = form8Table2Data.getDecayPhase014();
		this.decayPhase1517 = form8Table2Data.getDecayPhase1517();
		this.decayPhase1819 = form8Table2Data.getDecayPhase1819();
		this.nervousSystemPHCTotal = form8Table2Data.getNervousSystemPHCTotal();
		this.otherOrgansPHCTotal = form8Table2Data.getOtherOrgansPHCTotal();
		this.miliaryPHCTotal = form8Table2Data.getMiliaryPHCTotal();
		this.detectedByRoutineCheckups = form8Table2Data.getDetectedByRoutineCheckups();
		this.detectedBySpecialistsTotal = form8Table2Data.getDetectedBySpecialistsTotal();
		this.detectedByTBDoctors = form8Table2Data.getDetectedByTBDoctors();
		this.detectedByOtherSpecialists = form8Table2Data.getDetectedByOtherSpecialists();
		this.routine014 = form8Table2Data.getRoutine014();
		this.routine1517 = form8Table2Data.getRoutine1517();
		this.routine1819 = form8Table2Data.getRoutine1819();
		this.focalTotal = form8Table2Data.getFocalTotal();
		this.infiltrativeTotal = form8Table2Data.getInfiltrativeTotal();
		this.disseminatedTotal = form8Table2Data.getDisseminatedTotal();
		this.cavernousTotal = form8Table2Data.getCavernousTotal();
		this.fibrousTotal = form8Table2Data.getFibrousTotal();
		this.cirrhoticTotal = form8Table2Data.getCirrhoticTotal();
		this.tbComplexTotal = form8Table2Data.getTbComplexTotal();
		this.miliaryTotal = form8Table2Data.getMiliaryTotal();
		this.tuberculomaTotal = form8Table2Data.getTuberculomaTotal();
		this.bronchiTotal = form8Table2Data.getBronchiTotal();
		this.pleurisyTotal = form8Table2Data.getPleurisyTotal();
		this.hilarLymphNodesTotal = form8Table2Data.getHilarLymphNodesTotal();
		this.osteoarticularTotal = form8Table2Data.getOsteoarticularTotal();
		this.urogenitalTotal = form8Table2Data.getUrogenitalTotal();
		this.peripheralLymphNodesTotal = form8Table2Data.getPeripheralLymphNodesTotal();
		this.abdominalTotal = form8Table2Data.getAbdominalTotal();
		this.skinTotal = form8Table2Data.getSkinTotal();
		this.eyeTotal = form8Table2Data.getEyeTotal();
		this.nervousSystemTotal = form8Table2Data.getNervousSystemTotal();
		this.liverTotal = form8Table2Data.getLiverTotal();
		this.childbearing = form8Table2Data.getChildbearing();
		this.pregnant = form8Table2Data.getPregnant();
		this.contact = form8Table2Data.getContact();
		this.migrants = form8Table2Data.getMigrants();
		this.phcWorkers = form8Table2Data.getPhcWorkers();
		this.tbServiceWorkers = form8Table2Data.getTbServiceWorkers();
		this.relapseCount = form8Table2Data.getRelapseCount();
		this.failCount = form8Table2Data.getFailCount();
		this.ltfuCount = form8Table2Data.getLtfuCount();
		this.otherCount = form8Table2Data.getOtherCount();
		
	}
	
	@Override
	public Integer getId() {
		return -1;
	}
	
	@Override
	public void setId(Integer integer) {
	}
	
	public Integer getActivePHCTotal() {
		return activePHCTotal;
	}
	
	public void setActivePHCTotal(Integer activePHCTotal) {
		this.activePHCTotal = activePHCTotal;
	}
	
	public Integer getRespiratoryPHCTotal() {
		return respiratoryPHCTotal;
	}
	
	public void setRespiratoryPHCTotal(Integer respiratoryPHCTotal) {
		this.respiratoryPHCTotal = respiratoryPHCTotal;
	}
	
	public Integer getPulmonaryPHCTotal() {
		return pulmonaryPHCTotal;
	}
	
	public void setPulmonaryPHCTotal(Integer pulmonaryPHCTotal) {
		this.pulmonaryPHCTotal = pulmonaryPHCTotal;
	}
	
	public Integer getBacExPHCTotal() {
		return bacExPHCTotal;
	}
	
	public void setBacExPHCTotal(Integer bacExPHCTotal) {
		this.bacExPHCTotal = bacExPHCTotal;
	}
	
	public Integer getDecayPhasePHCTotal() {
		return decayPhasePHCTotal;
	}
	
	public void setDecayPhasePHCTotal(Integer decayPhasePHCTotal) {
		this.decayPhasePHCTotal = decayPhasePHCTotal;
	}
	
	public Integer getDecayPhaseTotal() {
		return decayPhaseTotal;
	}
	
	public void setDecayPhaseTotal(Integer decayPhaseTotal) {
		this.decayPhaseTotal = decayPhaseTotal;
	}
	
	public Integer getDecayPhase014() {
		return decayPhase014;
	}
	
	public void setDecayPhase014(Integer decayPhase014) {
		this.decayPhase014 = decayPhase014;
	}
	
	public Integer getDecayPhase1517() {
		return decayPhase1517;
	}
	
	public void setDecayPhase1517(Integer decayPhase1517) {
		this.decayPhase1517 = decayPhase1517;
	}
	
	public Integer getDecayPhase1819() {
		return decayPhase1819;
	}
	
	public void setDecayPhase1819(Integer decayPhase1819) {
		this.decayPhase1819 = decayPhase1819;
	}
	
	public Integer getNervousSystemPHCTotal() {
		return nervousSystemPHCTotal;
	}
	
	public void setNervousSystemPHCTotal(Integer nervousSystemPHCTotal) {
		this.nervousSystemPHCTotal = nervousSystemPHCTotal;
	}
	
	public Integer getOtherOrgansPHCTotal() {
		return otherOrgansPHCTotal;
	}
	
	public void setOtherOrgansPHCTotal(Integer otherOrgansPHCTotal) {
		this.otherOrgansPHCTotal = otherOrgansPHCTotal;
	}
	
	public Integer getMiliaryPHCTotal() {
		return miliaryPHCTotal;
	}
	
	public void setMiliaryPHCTotal(Integer miliaryPHCTotal) {
		this.miliaryPHCTotal = miliaryPHCTotal;
	}
	
	public Integer getDetectedByRoutineCheckups() {
		return detectedByRoutineCheckups;
	}
	
	public void setDetectedByRoutineCheckups(Integer detectedByRoutineCheckups) {
		this.detectedByRoutineCheckups = detectedByRoutineCheckups;
	}
	
	public Integer getDetectedBySpecialistsTotal() {
		return detectedBySpecialistsTotal;
	}
	
	public void setDetectedBySpecialistsTotal(Integer detectedBySpecialistsTotal) {
		this.detectedBySpecialistsTotal = detectedBySpecialistsTotal;
	}
	
	public Integer getDetectedByTBDoctors() {
		return detectedByTBDoctors;
	}
	
	public void setDetectedByTBDoctors(Integer detectedByTBDoctors) {
		this.detectedByTBDoctors = detectedByTBDoctors;
	}
	
	public Integer getDetectedByOtherSpecialists() {
		return detectedByOtherSpecialists;
	}
	
	public void setDetectedByOtherSpecialists(Integer detectedByOtherSpecialists) {
		this.detectedByOtherSpecialists = detectedByOtherSpecialists;
	}
	
	public Integer getRoutine014() {
		return routine014;
	}
	
	public void setRoutine014(Integer routine014) {
		this.routine014 = routine014;
	}
	
	public Integer getRoutine1517() {
		return routine1517;
	}
	
	public void setRoutine1517(Integer routine1517) {
		this.routine1517 = routine1517;
	}
	
	public Integer getRoutine1819() {
		return routine1819;
	}
	
	public void setRoutine1819(Integer routine1819) {
		this.routine1819 = routine1819;
	}
	
	public Integer getFocalTotal() {
		return focalTotal;
	}
	
	public void setFocalTotal(Integer focalTotal) {
		this.focalTotal = focalTotal;
	}
	
	public Integer getInfiltrativeTotal() {
		return infiltrativeTotal;
	}
	
	public void setInfiltrativeTotal(Integer infiltrativeTotal) {
		this.infiltrativeTotal = infiltrativeTotal;
	}
	
	public Integer getDisseminatedTotal() {
		return disseminatedTotal;
	}
	
	public void setDisseminatedTotal(Integer disseminatedTotal) {
		this.disseminatedTotal = disseminatedTotal;
	}
	
	public Integer getCavernousTotal() {
		return cavernousTotal;
	}
	
	public void setCavernousTotal(Integer cavernousTotal) {
		this.cavernousTotal = cavernousTotal;
	}
	
	public Integer getFibrousTotal() {
		return fibrousTotal;
	}
	
	public void setFibrousTotal(Integer fibrousTotal) {
		this.fibrousTotal = fibrousTotal;
	}
	
	public Integer getCirrhoticTotal() {
		return cirrhoticTotal;
	}
	
	public void setCirrhoticTotal(Integer cirrhoticTotal) {
		this.cirrhoticTotal = cirrhoticTotal;
	}
	
	public Integer getTbComplexTotal() {
		return tbComplexTotal;
	}
	
	public void setTbComplexTotal(Integer tbComplexTotal) {
		this.tbComplexTotal = tbComplexTotal;
	}
	
	public Integer getMiliaryTotal() {
		return miliaryTotal;
	}
	
	public void setMiliaryTotal(Integer miliaryTotal) {
		this.miliaryTotal = miliaryTotal;
	}
	
	public Integer getTuberculomaTotal() {
		return tuberculomaTotal;
	}
	
	public void setTuberculomaTotal(Integer tuberculomaTotal) {
		this.tuberculomaTotal = tuberculomaTotal;
	}
	
	public Integer getBronchiTotal() {
		return bronchiTotal;
	}
	
	public void setBronchiTotal(Integer bronchiTotal) {
		this.bronchiTotal = bronchiTotal;
	}
	
	public Integer getPleurisyTotal() {
		return pleurisyTotal;
	}
	
	public void setPleurisyTotal(Integer pleurisyTotal) {
		this.pleurisyTotal = pleurisyTotal;
	}
	
	public Integer getHilarLymphNodesTotal() {
		return hilarLymphNodesTotal;
	}
	
	public void setHilarLymphNodesTotal(Integer hilarLymphNodesTotal) {
		this.hilarLymphNodesTotal = hilarLymphNodesTotal;
	}
	
	public Integer getOsteoarticularTotal() {
		return osteoarticularTotal;
	}
	
	public void setOsteoarticularTotal(Integer osteoarticularTotal) {
		this.osteoarticularTotal = osteoarticularTotal;
	}
	
	public Integer getUrogenitalTotal() {
		return urogenitalTotal;
	}
	
	public void setUrogenitalTotal(Integer urogenitalTotal) {
		this.urogenitalTotal = urogenitalTotal;
	}
	
	public Integer getPeripheralLymphNodesTotal() {
		return peripheralLymphNodesTotal;
	}
	
	public void setPeripheralLymphNodesTotal(Integer peripheralLymphNodesTotal) {
		this.peripheralLymphNodesTotal = peripheralLymphNodesTotal;
	}
	
	public Integer getAbdominalTotal() {
		return abdominalTotal;
	}
	
	public void setAbdominalTotal(Integer abdominalTotal) {
		this.abdominalTotal = abdominalTotal;
	}
	
	public Integer getSkinTotal() {
		return skinTotal;
	}
	
	public void setSkinTotal(Integer skinTotal) {
		this.skinTotal = skinTotal;
	}
	
	public Integer getEyeTotal() {
		return eyeTotal;
	}
	
	public void setEyeTotal(Integer eyeTotal) {
		this.eyeTotal = eyeTotal;
	}
	
	public Integer getNervousSystemTotal() {
		return nervousSystemTotal;
	}
	
	public void setNervousSystemTotal(Integer nervousSystemTotal) {
		this.nervousSystemTotal = nervousSystemTotal;
	}
	
	public Integer getLiverTotal() {
		return liverTotal;
	}
	
	public void setLiverTotal(Integer liverTotal) {
		this.liverTotal = liverTotal;
	}
	
	public Integer getChildbearing() {
		return childbearing;
	}
	
	public void setChildbearing(Integer childbearing) {
		this.childbearing = childbearing;
	}
	
	public Integer getPregnant() {
		return pregnant;
	}
	
	public void setPregnant(Integer pregnant) {
		this.pregnant = pregnant;
	}
	
	public Integer getContact() {
		return contact;
	}
	
	public void setContact(Integer contact) {
		this.contact = contact;
	}
	
	public Integer getMigrants() {
		return migrants;
	}
	
	public void setMigrants(Integer migrants) {
		this.migrants = migrants;
	}
	
	public Integer getPhcWorkers() {
		return phcWorkers;
	}
	
	public void setPhcWorkers(Integer phcWorkers) {
		this.phcWorkers = phcWorkers;
	}
	
	public Integer getTbServiceWorkers() {
		return tbServiceWorkers;
	}
	
	public void setTbServiceWorkers(Integer tbServiceWorkers) {
		this.tbServiceWorkers = tbServiceWorkers;
	}
	
	public Integer getRelapseCount() {
		return relapseCount;
	}
	
	public void setRelapseCount(Integer relapseCount) {
		this.relapseCount = relapseCount;
	}
	
	public Integer getFailCount() {
		return failCount;
	}
	
	public void setFailCount(Integer failCount) {
		this.failCount = failCount;
	}
	
	public Integer getLtfuCount() {
		return ltfuCount;
	}
	
	public void setLtfuCount(Integer ltfuCount) {
		this.ltfuCount = ltfuCount;
	}
	
	public Integer getOtherCount() {
		return otherCount;
	}
	
	public void setOtherCount(Integer otherCount) {
		this.otherCount = otherCount;
	}
}
