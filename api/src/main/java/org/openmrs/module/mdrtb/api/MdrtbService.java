/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.mdrtb.api;

import org.openmrs.*;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.module.mdrtb.*;
import org.openmrs.module.mdrtb.form.custom.*;
import org.openmrs.module.mdrtb.program.*;
import org.openmrs.module.mdrtb.specimen.*;
import org.openmrs.module.mdrtb.specimen.custom.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * The main service of this module, which is exposed for other modules. See
 * moduleApplicationContext.xml on how it is wired up.
 */
public interface MdrtbService extends OpenmrsService {
	
	/**
	 * Returns an item by uuid. It can be called by any authenticated user. It is fetched in read
	 * only transaction.
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	ReportData getReportDataByUuid(String uuid) throws APIException;
	
	/**
	 * Saves an item. Sets the owner to superuser, if it is not set. It can be called by users with
	 * this module's privilege. It is executed in a transaction.
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional
	ReportData saveReportData(ReportData reportData) throws APIException;
	
	/**
	 * Saves an item. Sets the owner to superuser, if it is not set. It can be called by users with
	 * this module's privilege. It is executed in a transaction.
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional
	void voidReportData(ReportData reportData, String reason) throws APIException;
	
	/**
	 * Saves an item. Sets the owner to superuser, if it is not set. It can be called by users with
	 * this module's privilege. It is executed in a transaction.
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional
	void unvoidReportData(ReportData reportData) throws APIException;
	
	/**
	 * Returns the Concept specified by the passed lookup string. Checks MdrtbConcepts mapping, id,
	 * name, and uuid before returning null
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	public Concept getConcept(String lookup);
	
	/**
	 * Returns collection of {@link ConceptAnswer} objects against a question concept passed as
	 * string
	 */
	//TODO: Remove this. Simply use getConcept().getConceptAnswers()
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	@Deprecated
	Collection<ConceptAnswer> getPossibleConceptAnswers(String conceptQuestion);
	
	/**
	 * Check to see what color to associate with a given result concept
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	String getColorForConcept(Concept concept);
	
	/**
	 * Resets the concept map cache
	 */
	void resetConceptMapCache();
	
	/**
	 * Resets the color map cache to null to force cache reload
	 */
	void resetColorMapCache();
	
	/**
	 * Returns list of {@link Patient} objects by Ids
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<Patient> getPatients(Collection<Integer> patientIds);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<Patient> getAllPatientsWithRegimenForms();
	
	/**
	 * Returns list of all users with Provider role
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	Collection<Person> getProviders();
	
	/**
	 * Returns {@link PatientState} by Id. This is because since Openmrs 2.x the getById method was
	 * removed
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	PatientState getPatientState(Integer stateId);
	
	/**
	 * Returns all DrugOrders of given {@link Patient}
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<DrugOrder> getDrugOrders(Patient patient);
	
	/**
	 * Returns map of Patients with their respective {@link DrugOrder} lists
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	Map<Integer, List<DrugOrder>> getDrugOrders(Cohort cohort, Concept drugSet);
	
	/**
	 * Save {@link DrugOrder}
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	DrugOrder saveDrugOrder(DrugOrder drugOrder);
	
	/**
	 * Returns all of the Drug Concepts within the ConceptSet which match the parameter
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<Concept> getDrugsInSet(Concept concept);
	
	/**
	 * Returns all the possible Drug Concepts to display in a DST result, in the order we want to
	 * display
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<Concept> getMdrtbDrugs();
	
	/**
	 * Returns all the possible Antiretroviral Concepts
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<Concept> getAntiretrovirals();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<Concept> getAllDrugResistanceConcepts();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<TbPatientProgram> getAllTbPatientProgramsEnrolledInDateRangeAndLocations(List<Location> locations, Date startDate,
	        Date endDate);
	
	/**
	 * Gets the DOTS program
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	Program getTbProgram();
	
	/**
	 * Returns a specific MdrtbPatientProgram by id
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	TbPatientProgram getTbPatientProgram(Integer patientProgramId);
	
	/**
	 * Returns all the DOTS programs for a given patient
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<TbPatientProgram> getTbPatientPrograms(Patient patient);
	
	/**
	 * Returns all the patient programs for a given patient that fall within a specific date range
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<TbPatientProgram> getTbPatientProgramsInDateRange(Patient patient, Date startDate, Date endDate);
	
	/**
	 * Returns the most recent mdrtb program for a given patient
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	TbPatientProgram getMostRecentTbPatientProgram(Patient patient);
	
	/**
	 * Returns all the MDR-TB programs in the system
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<MdrtbPatientProgram> getAllMdrtbPatientPrograms();
	
	/**
	 * Returns all the MDR-TB programs in the system that were active during a specific date range
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<MdrtbPatientProgram> getAllMdrtbPatientProgramsInDateRange(Date startDate, Date endDate);
	
	/**
	 * Returns all MDRTB Patient Programs enrolled within given range
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<MdrtbPatientProgram> getAllMdrtbPatientProgramsEnrolledInDateRange(Date startDate, Date endDate);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<MdrtbPatientProgram> getAllMdrtbPatientProgramsEnrolledInDateRangeAndLocations(List<Location> locations,
	        Date startDate, Date endDate);
	
	/**
	 * Returns all the MDR-TB programs for a given patient
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<MdrtbPatientProgram> getMdrtbPatientPrograms(Patient patient);
	
	/**
	 * Returns all the patient programs for a given patient that fall within a specific date range
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<MdrtbPatientProgram> getMdrtbPatientProgramsInDateRange(Patient patient, Date startDate, Date endDate);
	
	/**
	 * Return the specific MdrtbPatientProgram the patient was enrolled in on the specified date
	 * (This assumes that a patient is only enrolled in one MDR-TB patient program at a time) If the
	 * date is before any program enrollments, it returns the first program enrollment If the date
	 * is after all program enrollments, it returns the most recent program enrollment If the date
	 * is between two program enrollments, it returns the later of the two
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	MdrtbPatientProgram getMdrtbPatientProgramOnDate(Patient patient, Date date);
	
	/**
	 * Returns a specific MdrtbPatientProgram by id
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	MdrtbPatientProgram getMdrtbPatientProgram(Integer patientProgramId);
	
	/**
	 * Returns the most recent mdrtb program for a given patient
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	MdrtbPatientProgram getMostRecentMdrtbPatientProgram(Patient patient);
	
	/**
	 * Returns {@link PatientIdentifier} object from {@link PatientProgram}
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	PatientIdentifier getPatientProgramIdentifier(PatientProgram patientProgram);
	
	/**
	 * Returns {@link PatientIdentifier} object from {@link MdrtbPatientProgram}
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	PatientIdentifier getPatientMdrtbProgramIdentifier(MdrtbPatientProgram mdrPatientProgram);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	Program getMdrtbProgram();
	
	/**
	 * Replacing the deprecated method in {@link ProgramWorkflowService} to return
	 * {@link ProgramWorkflow} by {@link Concept}
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	ProgramWorkflow getProgramWorkflow(Program program, Integer conceptId);
	
	/**
	 * Replacing the deprecated method in {@link ProgramWorkflowService} to return
	 * {@link ProgramWorkflowState} by {@link ProgramWorkflow} and {@link Concept}
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	ProgramWorkflowState getProgramWorkflowState(ProgramWorkflow programWorkflow, Integer conceptId) throws APIException;
	
	/**
	 * Returns a specific ProgramWorkflowState, given the concept associated with the state
	 * 
	 * @return {@link ProgramWorkflowState}
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	ProgramWorkflowState getProgramWorkflowState(Concept programWorkflowStateConcept);
	
	/**
	 * Returns set of {@link ProgramWorkflowState} objects according to any of the Patient group
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	Set<ProgramWorkflowState> getPossibleClassificationsAccordingToPatientGroups();
	
	/**
	 * Returns set of {@link ProgramWorkflowState} objects according to Past Drug
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	Set<ProgramWorkflowState> getPossibleDOTSClassificationsAccordingToPreviousDrugUse();
	
	/**
	 * Returns all possible MDR-TB previous drug use classifications
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	Set<ProgramWorkflowState> getPossibleClassificationsAccordingToPreviousDrugUse();
	
	/**
	 * Returns all possible MDR-TB previous treatment classifications
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	Set<ProgramWorkflowState> getPossibleClassificationsAccordingToPreviousTreatment();
	
	/**
	 * Returns all possible outcomes for the DOTS program
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	Set<ProgramWorkflowState> getPossibleTbProgramOutcomes();
	
	/**
	 * Returns all possible outcomes for the MDR-TB program
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	Set<ProgramWorkflowState> getPossibleMdrtbProgramOutcomes();
	
	/**
	 * Creates a new specimen, associated with the given patient
	 */
	Specimen createSpecimen(Patient patient);
	
	/**
	 * Fetches a specimen sample obj given a specimen id
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	Specimen getSpecimen(Integer specimedId);
	
	/**
	 * Fetches a specimen sample obj given an encounter of the Specimen Collection type
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	Specimen getSpecimen(Encounter encounter);
	
	/**
	 * Fetches all specimens for a patient (i.e., all Specimen Collection encounters)
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<Specimen> getSpecimens(Patient patient);
	
	/**
	 * Fetches all specimens for a patient (i.e., all Specimen Collection encounters) in a given
	 * program
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<Specimen> getSpecimens(Patient patient, Integer programId);
	
	/**
	 * Fetches all specimens within a certain data range
	 * 
	 * @param patient: only include specimens associated with this patient
	 * @param startDateCollected: only include specimens with a date collected after (or equal to)
	 *            this start date
	 * @param endDateCollected: only include specimens with a date collected before (or equal to)
	 *            this end date All parameters can be set to null
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<Specimen> getSpecimens(Patient patient, Date startDateCollected, Date endDateCollected);
	
	/**
	 * Fetches all specimens within a certain data range and from a certain lab
	 * 
	 * @param patient: only include specimens associated with this patient
	 * @param startDateCollected: only include specimens with a date collected after (or equal to)
	 *            this start date
	 * @param endDateCollected: only include specimens with a date collected before (or equal to)
	 *            this end date
	 * @param locationCollected: only include specimens collected from the specified location All
	 *            parameters can be set to null
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<Specimen> getSpecimens(Patient patient, Date startDateCollected, Date endDateCollected, Location locationCollected);
	
	/**
	 * Saves or updates a specimen object
	 */
	void saveSpecimen(Specimen specimen);
	
	/**
	 * Deletes a smear, culture, or DST test
	 */
	void deleteTest(Integer testId);
	
	/**
	 * Deletes a specimen, referenced by specimen Id
	 */
	void deleteSpecimen(Integer patientId);
	
	/**
	 * Search for encounters by various parameters
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<Encounter> getEncounters(Patient patient, Location location, Date start, Date end, Collection<EncounterType> types);
	
	/**
	 * Search for encounters by Patient and Encounter Types
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<Encounter> getEncountersByPatientAndTypes(Patient patient, Collection<EncounterType> types);
	
	@Deprecated
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<Encounter> getEncountersByEncounterTypes(List<String> encounterTypeNames, Date startDate, Date endDate,
	        Date closeDate);
	
	/**
	 * Search for encounters of given {@link EncounterType} and {@link Patient} having no program
	 * attached
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<Encounter> getEncountersWithNoProgram(EncounterType encounterType, Patient patient);
	
	/**
	 * Attach given program Id to an encounter Id
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	void addProgramIdToEncounter(Integer encounterId, Integer programId);
	
	/**
	 * Attach given Patient identifier to Program
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	void addIdentifierToProgram(Integer patientIdenifierId, Integer patientProgramId);
	
	/**
	 * Gets all TB specific encounters for the given patient
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<Encounter> getTbEncounters(Patient patient);
	
	/**
	 * Gets all MDR-TB specific encounters for the given patient
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<Encounter> getMdrtbEncounters(Patient patient);
	
	/**
	 * Fetches an GeneXpert test against the given obsId
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	Xpert getXpert(Integer obsId);
	
	/**
	 * Saves a GeneXpert test in the approriate obs construct
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	void saveXpert(Xpert xpert);
	
	/**
	 * Fetches a HAIN test against given {@link Obs}
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	HAIN getHAIN(Obs obs);
	
	/**
	 * Saves a HAIN in the approriate obs construct
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	void saveHAIN(HAIN hain);
	
	/**
	 * Fetches a HAIN2 test against given obsId
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	HAIN2 getHAIN2(Obs obs);
	
	/**
	 * Saves a HAIN2 in the approriate obs construct
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	void saveHAIN2(HAIN2 hain);
	
	/**
	 * Creates a new culture, associated with the given specimen, by copying the member properties
	 * of the given culture
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	Culture createCulture(Specimen specimen, Culture culture);
	
	/**
	 * Saves a culture in the appropriate obs construct
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	void saveCulture(Culture culture);
	
	/**
	 * Creates a new dst, associated with the given specimen, by copying the member properties of
	 * the given dst
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	Dst createDst(Specimen specimen, Dst dst);
	
	/**
	 * Fetches a smear given the obs of a Tuberculosis Smear Test Construct
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	Smear getSmear(Obs obs);
	
	/**
	 * Saves a DST in the appropriate obs construct
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	void saveDst(Dst dst);
	
	/**
	 * Creates a new Smear, associated with the given specimen
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	Smear createSmear(Specimen specimen);
	
	/**
	 * Creates a new Smear, associated with the given specimen, by copying the member properties of
	 * the given smear
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	Smear createSmear(Specimen specimen, Smear smear);
	
	/**
	 * Saves a smear in the appropriate obs construct
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	void saveSmear(Smear smear);
	
	/***************/
	/** LOCATIONS **/
	/***************/
	/**
	 * Find the {@link Location} object mapped with given parameters
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	Location getLocation(Integer regionId, Integer districtId, Integer facilityId);
	
	/**
	 * Get list of ISO countries
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<String> getCountries();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<Region> getRegions();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	Region getRegion(Integer regionId);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<Location> getLocationsFromRegion(Region region);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<Facility> getFacilities();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<Facility> getRegFacilities();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<Facility> getFacilitiesByParent(Integer parentId);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<Facility> getRegFacilities(Integer parentId);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	Facility getFacility(Integer facilityId);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<District> getDistrictsByParent(Integer parentId);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<District> getRegDistricts(Integer parentId);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	District getDistrict(Integer districtId);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	District getDistrict(String name);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<District> getDistricts();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<District> getRegDistricts();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<Location> getLocationsFromDistrict(District district);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<Location> getEnrollmentLocations();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<Location> getCultureLocations();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<Location> getLocationsInHierarchy(Location parent, boolean includeRetired);
	
	/**
	 * Should return the list of child locations based on the parameters. If only the region is
	 * supplied, then the list of all its children as well as grand children should be returned.
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<Location> getLocations(Region region, District district, Facility facility);
	
	List<Location> getLocationListForDushanbe(Integer oblastId, Integer districtId, Integer facilityId);
	
	/*****************/
	/** ANSWER SETS **/
	/*****************/
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	Collection<ConceptAnswer> getPossibleMtbResults();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	Collection<ConceptAnswer> getPossibleRifResistanceResults();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	Collection<ConceptAnswer> getPossibleInhResistanceResults();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	Collection<ConceptAnswer> getPossibleFqResistanceResults();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	Collection<ConceptAnswer> getPossibleInjResistanceResults();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	Collection<ConceptAnswer> getPossibleXpertMtbBurdens();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	Collection<ConceptAnswer> getPossibleAnatomicalSites();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	Collection<ConceptAnswer> getPossibleCultureResults();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	Collection<ConceptAnswer> getPossibleCultureMethods();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	Collection<ConceptAnswer> getPossibleDstMethods();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	Collection<ConceptAnswer> getPossibleOrganismTypes();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	Collection<ConceptAnswer> getPossibleSmearResults();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	Collection<ConceptAnswer> getPossibleSmearMethods();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	Collection<ConceptAnswer> getPossibleSpecimenTypes();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	Collection<ConceptAnswer> getPossibleSpecimenAppearances();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	Collection<ConceptAnswer> getPossibleIPTreatmentSites();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	Collection<ConceptAnswer> getPossibleCPTreatmentSites();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	Collection<ConceptAnswer> getPossibleRegimens();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	Collection<ConceptAnswer> getPossibleHIVStatuses();
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	Collection<Concept> getPossibleDstResults();
	
	/***********/
	/** FORMS **/
	/***********/
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<SmearForm> getSmearForms(Integer patientProgramId);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<CultureForm> getCultureForms(Integer patientProgramId);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<XpertForm> getXpertForms(Integer patientProgramId);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<HAINForm> getHAINForms(Integer patientProgramId);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<HAIN2Form> getHAIN2Forms(Integer patientProgramId);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<DSTForm> getDstForms(Integer patientProgramId);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<DrugResistanceDuringTreatmentForm> getDrdtForms(Integer patientProgramId);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	TB03Form getClosestTB03Form(Location location, Date encounterDate, Patient patient);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	TB03uForm getTB03uFormForProgram(Patient patient, Integer patientProgramId);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<RegimenForm> getRegimenFormsForProgram(Patient patient, Integer patientProgramId);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<TB03Form> getTB03FormsForProgram(Patient patient, Integer patientProgramId);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<Form89> getForm89FormsForProgram(Patient patient, Integer patientProgramId);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<Form89> getForm89FormsFilledForPatientProgram(Patient patient, Location location, Integer patientProgramId,
	        Integer year, Integer quarter, Integer month, Integer month2);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	RegimenForm getPreviousRegimenForm(Patient patient, List<Location> locactions, Date beforeDate);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	RegimenForm getCurrentRegimenForm(Patient patient, Date beforeDate);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<TB03uForm> getTB03uFormsFilled(List<Location> locations, Integer year, Integer quarter, Integer month,
	        Integer month2);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<Form89> getForm89FormsFilled(List<Location> locations, Integer year, Integer quarter, Integer month, Integer month2);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<TB03Form> getTB03FormsFilled(List<Location> locations, Integer year, Integer quarter, Integer month, Integer month2);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<TransferInForm> getTransferInFormsFilled(List<Location> locations, Integer year, Integer quarter, Integer month,
	        Integer month2);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<TransferInForm> getTransferInFormsFilledForPatient(Patient patient);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<TransferOutForm> getTransferOutFormsFilled(List<Location> locations, Integer year, Integer quarter, Integer month,
	        Integer month2);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<TransferOutForm> getTransferOutFormsFilledForPatient(Patient patient);
	
	/**
	 * End of Treatment forms attached to the given patient program (via the PATIENT_PROGRAM_ID
	 * obs). Normally there is at most one per program; a list is returned so callers can detect
	 * duplicates.
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<EndOfTreatmentForm> getEndOfTreatmentFormsForProgram(Patient patient, Integer patientProgramId);
	
	/**
	 * End of Treatment forms whose encounter date falls in the given period (year + quarter/month)
	 * and, if given, in one of the locations. Used by the End of Treatment report.
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<EndOfTreatmentForm> getEndOfTreatmentFormsFilled(List<Location> locations, Integer year, Integer quarter,
	        Integer month, Integer month2);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<TB03uForm> getTB03uFormsWithTreatmentStartedDuring(List<Location> locations, Integer year, Integer quarter,
	        Integer month, Integer month2);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<TB03uForm> getTB03uFormsForProgram(Patient patient, Integer mdrtbProgramId);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<AdverseEventsForm> getAEFormsForProgram(Patient patient, Integer patientProgId);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<AdverseEventsForm> getAEFormsFilled(List<Location> locations, Integer year, Integer quarter, Integer month,
	        Integer month2);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	List<RegimenForm> getRegimenFormsFilled(List<Location> locations, Integer year, Integer quarter, Integer month);
	
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	void evict(Object obj);
	
	/**
	 * Returns Cohort of patients by {@link Program} and {@link ProgramWorkflowState} between given
	 * date range
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	@Transactional(readOnly = true)
	Cohort getPatientsByProgramAndState(Program program, List<ProgramWorkflowState> stateList, Date fromDate, Date toDate);
	
	/**
	 * Handles exiting a patient from care
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	void processDeath(Patient patient, Date deathDate, Concept causeOfDeath);
	
	/**
	 * Searches for saved Report Data using various parameters
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	List<ReportData> searchReportData(Location region, Location district, Location facility, Integer year, Integer quarter,
	        Integer month, String reportName, ReportType reportType);
	
	void unlockReport(ReportData reportData);
	
	boolean getReportArchived(Integer oblastId, Integer districtId, Integer facilityId, Integer year, Integer quarter,
	        Integer month, String name, ReportType reportType);
	
	List<String> readTableData(Integer oblastId, Integer districtId, Integer facilityId, Integer year, Integer quarter,
	        Integer month, String name, ReportType reportType);
	
	/**
	 * Fetch all reports and create a nested list for each column
	 * 
	 * @return List of Reports
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	List<List<Object>> getReportsWithoutData(ReportType reportType);
	
	void lockReport(ReportData reportData);
	
	/**
	 * Saves a scanned lab report in the appropriate obs constructs
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	void saveScannedLabReport(ScannedLabReport report);
	
	/**
	 * Returns patient summary of given patient
	 * 
	 * @param patient
	 * @return
	 */
	@Authorized(MdrtbConfig.MODULE_PRIVILEGE)
	PatientSummary getPatientSummary(Patient patient);
}
