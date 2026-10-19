/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.mdrtb.api.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.*;
import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.mdrtb.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Repository("mdrtb.MdrtbDao")
public class MdrtbDao {
	
	protected static final Log log = LogFactory.getLog(MdrtbDao.class);
	
	@Autowired
	private SessionFactory sessionFactory;
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public ReportData getReportData(Integer id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<ReportData> query = cb.createQuery(ReportData.class);
		Root<ReportData> root = query.from(ReportData.class);
		query.select(root).where(cb.equal(root.get("id"), id));
		return sessionFactory.getCurrentSession().createQuery(query).uniqueResult();
	}
	
	public ReportData getReportDataByUuid(String uuid) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<ReportData> query = cb.createQuery(ReportData.class);
		Root<ReportData> root = query.from(ReportData.class);
		query.select(root).where(cb.equal(root.get("uuid"), uuid));
		return sessionFactory.getCurrentSession().createQuery(query).uniqueResult();
	}
	
	public ReportData saveReportData(ReportData reportData) {
		sessionFactory.getCurrentSession().saveOrUpdate(reportData);
		return reportData;
	}
	
	/**
	 */
	@SuppressWarnings("unchecked")
	public List<Location> getLocationsWithAnyProgramEnrollments() throws DAOException {
		String query = "select distinct location from PatientProgram where voided = false";
		return sessionFactory.getCurrentSession().createQuery(query).list();
	}
	
	/**
	 */
	@Deprecated
	public List<String> getAllRayonsTJK() throws DAOException {
		List<BaseLocation> list = getLocationsByHierarchyLevel(LocationHierarchy.DISTRICT);
		List<String> names = new ArrayList<>();
		for (BaseLocation location : list) {
			names.add(location.getName());
		}
		return names;
	}
	
	public Map<Integer, List<DrugOrder>> getDrugOrders(Cohort patients, List<Concept> drugConcepts) throws DAOException {
		Map<Integer, List<DrugOrder>> ret = new HashMap<>();
		if (patients != null && patients.isEmpty())
			return ret;

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<DrugOrder> query = cb.createQuery(DrugOrder.class);
		Root<DrugOrder> root = query.from(DrugOrder.class);
		root.fetch("patient", JoinType.LEFT);
		List<Predicate> predicates = new ArrayList<>();

		// only include this where clause if patients were passed in
		if (patients != null)
			predicates.add(root.get("patient").get("personId").in(patients.getMemberIds()));

		if (drugConcepts != null)
			predicates.add(root.get("concept").in(drugConcepts));
		predicates.add(cb.equal(root.get("voided"), false));
		query.select(root).where(predicates.toArray(new Predicate[0])).orderBy(cb.asc(root.get("startDate")));
		List<DrugOrder> temp = sessionFactory.getCurrentSession().createQuery(query)
		        .setCacheMode(CacheMode.IGNORE).getResultList();
		for (DrugOrder regimen : temp) {
			Integer ptId = regimen.getPatient().getPatientId();
			List<DrugOrder> list = ret.get(ptId);
			if (list == null) {
				list = new ArrayList<>();
				ret.put(ptId, list);
			}
			list.add(regimen);
		}
		return ret;
	}
	
	public PatientIdentifier getPatientIdentifierById(Integer patientIdentifierId) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<PatientIdentifier> query = cb.createQuery(PatientIdentifier.class);
		Root<PatientIdentifier> root = query.from(PatientIdentifier.class);
		query.select(root).where(cb.equal(root.get("patientIdentifierId"), patientIdentifierId));
		return sessionFactory.getCurrentSession().createQuery(query).uniqueResult();
	}
	
	/**
	 * Fetch all reports and create a nested list for each column
	 */
	public List<List<Object>> getReports(String reportType) {
		String sql = "select report_id, region_id, district_id, facility_id, report_name, year, quarter, month, report_date, report_type, report_status from report_data where report_type = "
		        + reportType;
		return Context.getAdministrationService().executeSQL(sql, true);
	}
	
	public List<ReportData> searchReportData(Location region, Location district, Location facility, Integer year, Integer quarter,
	                                         Integer month, String reportName, ReportType reportType) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<ReportData> query = cb.createQuery(ReportData.class);
		Root<ReportData> root = query.from(ReportData.class);
		List<Predicate> predicates = new ArrayList<>();
		predicates.add(cb.equal(root.get("year"), year));
		if (reportType != null) {
			predicates.add(cb.equal(root.get("reportType"), reportType));
		}
		if (reportName != null) {
			predicates.add(cb.equal(root.get("reportName"), reportName));
		}
		if (quarter != null) {
			predicates.add(cb.equal(root.get("quarter"), quarter));
		} else if (month != null) {
			predicates.add(cb.equal(root.get("month"), month));
		}
		List<Location> locationList = new ArrayList<>();
		boolean regionProvided = region != null;
		boolean districtProvided = district != null;
		boolean facilityProvided = facility != null;
		// If Facility is also provided, then return only the list for that facility
		if (facilityProvided) {
			locationList.add(facility);
		}
		// If Region and District are provided, then the specific Region, District and all facilities will be fetched
		else if (districtProvided) {
			locationList.add(district);
			// If District is provided, then retrieve all its children
			BaseLocation parent = new BaseLocation(district, LocationHierarchy.DISTRICT);
			List<BaseLocation> facilities = getLocationsByParent(parent);
            locationList.addAll(facilities);
		}
		// If only Region is provided, then Region, and its entire tree of children and grandchildren
		else if (regionProvided) {
			locationList.add(district);
			// O boy! We're in for a lengthy stride...
			BaseLocation parent = new BaseLocation(region, LocationHierarchy.REGION);
			List<BaseLocation> districts = getLocationsByParent(parent);
			for (BaseLocation d : districts) {
				locationList.add(d);
				List<BaseLocation> facilities = getLocationsByParent(d);
                locationList.addAll(facilities);
			}
		}
		predicates.add(root.get("location").in(locationList));
		query.select(root).where(predicates.toArray(new Predicate[0]));
		return sessionFactory.getCurrentSession().createQuery(query).getResultList();
	}
	
	public List<String> getReportDataAsList(Integer regionId, Integer districtId, Integer facilityId, Integer year, Integer quarter,
	                                        Integer month, String reportName, ReportType reportType) {
		Location region = regionId != null ? Context.getLocationService().getLocation(regionId) : null;
		Location district = districtId != null ? Context.getLocationService().getLocation(districtId) : null;
		Location facility = facilityId != null ? Context.getLocationService().getLocation(facilityId) : null;
		List<ReportData> reports = searchReportData(region, district, facility, year, quarter, month, reportName, reportType);
		List<String> list = new ArrayList<>();
		for (ReportData report : reports) {
			try {
				list.add(report.getTableData());
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	public boolean getReportArchived(Integer regionId, Integer districtId, Integer facilityId, Integer year,
	        Integer quarter, Integer month, String reportName, ReportType reportType) {
		Location region = regionId != null ? Context.getLocationService().getLocation(regionId) : null;
		Location district = districtId != null ? Context.getLocationService().getLocation(districtId) : null;
		Location facility = facilityId != null ? Context.getLocationService().getLocation(facilityId) : null;
		List<ReportData> reports = searchReportData(region, district, facility, year, quarter, month, reportName, reportType);
		return !reports.isEmpty();
	}
	
	public List<Encounter> getEncountersByEncounterTypes(List<String> encounterTypeNames) {
		return getEncountersByEncounterTypes(encounterTypeNames, null, null);
	}
	
	@SuppressWarnings("unchecked")
	/* TODO: Remove unused closeDate parameter */
	public List<Encounter> getEncountersByEncounterTypes(List<String> encounterTypeNames, Date startDate, Date endDate) {
		SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<Integer> encounterIds = new ArrayList<>();
		List<Integer> tempList;
		String sql = "";
		Session session = sessionFactory.getCurrentSession();
		for (String encounterTypeName : encounterTypeNames) {
			sql = "select e.encounter_id from encounter e inner join encounter_type et where e.encounter_type=et.encounter_type_id and et.name='"
					+ encounterTypeName + "' and e.voided=0";

			if (startDate != null && endDate != null) {
				sql += " and e.encounter_datetime between '" + dbDateFormat.format(startDate) + "' and '"
						+ dbDateFormat.format(endDate) + "'";
			}
			sql += ";";
			tempList = (List<Integer>) session.createSQLQuery(sql).list();

			for (Integer encounterId : tempList) {
				if (!(encounterIds.contains(encounterId))) {
					encounterIds.add(encounterId);
				}
			}
		}

		List<Encounter> encounters = new ArrayList<>();
		Encounter encounter = new Encounter();
		for (Integer encounterId : encounterIds) {
			encounter = Context.getEncounterService().getEncounter(encounterId);
			encounters.add(encounter);
		}
		return encounters;
	}
	
	public void evict(Object obj) {
		sessionFactory.getCurrentSession().evict(obj);
	}
	
	/**
	 * Fetches Locations by their respective level of hierarchy
	 */
	public List<BaseLocation> getLocationsByHierarchyLevel(LocationHierarchy level) {
		Map<LocationAttributeType, Object> attributeValues = new HashMap<>();
		LocationAttributeType key = Context.getLocationService().getLocationAttributeTypeByName(
				MdrtbConstants.LOCATION_ATTRIBUTE_TYPE_LEVEL);
		attributeValues.put(key, level.toString());
		List<Location> list = Context.getLocationService().getLocations(null, null, attributeValues, false, null, null);
		List<BaseLocation> locations = new ArrayList<>();
		for (Location location : list) {
			locations.add(new BaseLocation(location, level));
		}
		return locations;
	}
	
	public List<BaseLocation> getLocationsByParent(BaseLocation parent) {
		List<BaseLocation> list = new ArrayList<>();
		LocationHierarchy childLevel;
		switch (parent.getLevel()) {
			case REGION:
				childLevel = LocationHierarchy.SUBREGION;
				break;
			case SUBREGION:
				childLevel = LocationHierarchy.DISTRICT;
				break;
            default:
				childLevel = LocationHierarchy.FACILITY;
		}
		for (Location location : parent.getChildLocations()) {
			list.add(new BaseLocation(location, childLevel));
		}
		return list;
	}
}
