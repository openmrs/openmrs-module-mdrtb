package org.openmrs.module.mdrtb.web.dwr;

import java.util.List;

public class MdrtbPatientSummaryObject {
    
        private String patientName;
        private String patientIdentifier;
        private String age;
        private String gender;
        private String weight;
        private String hivStatus;
        private String hivDate;
        private String mdrtbPatientStatus;
        
        private String currentRegimen;
        private String initialSmearResult;
        private String initialSmearDate;
        private String dateOfFirstTreatment;
        private String dateOfHospitalization; 
        
        private String provenance; //city
        private String cultureResult;
        private String cultureDate;
        private String dateOfSecondTreatment;
        private String dateOfEndOfHospitalization;
        
        private String antecedents;
        private String originalSmearResult;
        private String originalSmearDate;
        private String finalRegimenDate;
        private String mostRecentXrayResult;
        private String mostRecentXrayDate;
        
        private String patientPhoneAndAddress;
        private String dstResistanceProfile;
        private String mostRecentSmearDate;
        private String mostRecentSmearResult;
        
        private String personResponsible;
        private String empiricalTreatment;
        private String mostRecentCultureDate;
        private String mostRecentCultureResult;
        
        private String personResponsiblePhoneAndAddress;
        private String dateOfPersonalizedTreatment;
        private String mostRecentThyroidDate;
        private String mostRecentThyroidResult;
        
        private List<MdrtbSideEffect> sideEffectsList;
        private String pulmonaryOrExtrapulmonary;
        private String resistanceType;  //primary or secondary
        
        
        public void setPatientName(String patientName) {
            this.patientName = patientName;
        }
        public void setPatientIdentifier(String patientIdentifier) {
            this.patientIdentifier = patientIdentifier;
        }
        public void setAge(String age) {
            this.age = age;
        }
        public void setWeight(String weight) {
            this.weight = weight;
        }
        public void setHivStatus(String hivStatus) {
            this.hivStatus = hivStatus;
        }
        public void setHivDate(String hivDate) {
            this.hivDate = hivDate;
        }
        public void setMdrtbPatientStatus(String mdrtbPatientStatus) {
            this.mdrtbPatientStatus = mdrtbPatientStatus;
        }
        public void setCurrentRegimen(String currentRegimen) {
            this.currentRegimen = currentRegimen;
        }
        public void setInitialSmearResult(String initialSmearResult) {
            this.initialSmearResult = initialSmearResult;
        }
        public void setInitialSmearDate(String initialSmearDate) {
            this.initialSmearDate = initialSmearDate;
        }
        public void setDateOfFirstTreatment(String dateOfFirstTreatment) {
            this.dateOfFirstTreatment = dateOfFirstTreatment;
        }
        public void setDateOfHospitalization(String dateOfHospitalization) {
            this.dateOfHospitalization = dateOfHospitalization;
        }
        public void setProvenance(String provenance) {
            this.provenance = provenance;
        }
        public void setCultureResult(String cultureResult) {
            this.cultureResult = cultureResult;
        }
        public void setCultureDate(String cultureDate) {
            this.cultureDate = cultureDate;
        }
        public void setDateOfSecondTreatment(String dateOfSecondTreatment) {
            this.dateOfSecondTreatment = dateOfSecondTreatment;
        }
        public void setDateOfEndOfHospitalization(String dateOfEndOfHospitalization) {
            this.dateOfEndOfHospitalization = dateOfEndOfHospitalization;
        }
        public void setAntecedents(String antecedents) {
            this.antecedents = antecedents;
        }
        public void setOriginalSmearResult(String originalSmearResult) {
            this.originalSmearResult = originalSmearResult;
        }
        public void setOriginalSmearDate(String originalSmearDate) {
            this.originalSmearDate = originalSmearDate;
        }
        public void setFinalRegimenDate(String finalRegimenDate) {
            this.finalRegimenDate = finalRegimenDate;
        }
        public void setMostRecentXrayResult(String mostRecentXrayResult) {
            this.mostRecentXrayResult = mostRecentXrayResult;
        }
        public void setMostRecentXrayDate(String mostRecentXrayDate) {
            this.mostRecentXrayDate = mostRecentXrayDate;
        }
        public void setPatientPhoneAndAddress(String patientPhoneAndAddress) {
            this.patientPhoneAndAddress = patientPhoneAndAddress;
        }
        public void setDstResistanceProfile(String dstResistanceProfile) {
            this.dstResistanceProfile = dstResistanceProfile;
        }
        public void setMostRecentSmearDate(String mostRecentSmearDate) {
            this.mostRecentSmearDate = mostRecentSmearDate;
        }
        public void setMostRecentSmearResult(String mostRecentSmearResult) {
            this.mostRecentSmearResult = mostRecentSmearResult;
        }
        public void setPersonResponsible(String personResponsible) {
            this.personResponsible = personResponsible;
        }
        public void setEmpiricalTreatment(String empiricalTreatment) {
            this.empiricalTreatment = empiricalTreatment;
        }
        public void setMostRecentCultureDate(String mostRecentCultureDate) {
            this.mostRecentCultureDate = mostRecentCultureDate;
        }
        public void setMostRecentCultureResult(String mostRecentCultureResult) {
            this.mostRecentCultureResult = mostRecentCultureResult;
        }
        public void setPersonResponsiblePhoneAndAddress(
                String personResponsiblePhoneAndAddress) {
            this.personResponsiblePhoneAndAddress = personResponsiblePhoneAndAddress;
        }
        public void setDateOfPersonalizedTreatment(String dateOfPersonalizedTreatment) {
            this.dateOfPersonalizedTreatment = dateOfPersonalizedTreatment;
        }
        public void setMostRecentThyroidDate(String mostRecentThyroidDate) {
            this.mostRecentThyroidDate = mostRecentThyroidDate;
        }
        public void setMostRecentThyroidResult(String mostRecentThyroidResult) {
            this.mostRecentThyroidResult = mostRecentThyroidResult;
        }
        public void setSideEffectsList(List<MdrtbSideEffect> sideEffectsList) {
            this.sideEffectsList = sideEffectsList;
        }
        public void setPulmonaryOrExtrapulmonary(String pulmonaryOrExtrapulmonary) {
            this.pulmonaryOrExtrapulmonary = pulmonaryOrExtrapulmonary;
        }
        public void setResistanceType(String resistanceType) {
            this.resistanceType = resistanceType;
        }
        public void setGender(String gender) {
            this.gender = gender;
        }
        
        
        
}
