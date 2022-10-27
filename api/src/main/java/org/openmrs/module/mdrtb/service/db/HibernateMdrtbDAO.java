package org.openmrs.module.mdrtb.service.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.mdrtb.BaseLocation;
import org.openmrs.module.mdrtb.District;
import org.openmrs.module.mdrtb.reporting.custom.PDFHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//TODO: This entire class will be heavily refactored
@Component
public class HibernateMdrtbDAO implements MdrtbDAO {

	protected static final Log log = LogFactory.getLog(HibernateMdrtbDAO.class);

	@Autowired
	private SessionFactory sessionFactory;
	
	public SessionFactory getSessionFactory() {
		return this.sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * @see MdrtbDAO#getLocationsWithAnyProgramEnrollments()
	 */
	@SuppressWarnings("unchecked")
	public List<Location> getLocationsWithAnyProgramEnrollments() throws DAOException {
		String query = "select distinct location from PatientProgram where voided = false";
		return sessionFactory.getCurrentSession().createQuery(query).list();
	}

	/**
	 * @see MdrtbDAO#getAllRayonsTJK()
	 */
	public List<String> getAllRayonsTJK() throws DAOException {
		List<BaseLocation> list = getAddressHierarchyLocationsByHierarchyLevel(District.HIERARCHY_LEVEL);
		List<String> names = new ArrayList<String>();
		for (BaseLocation location : list) {
			names.add(location.getName());
		}
		return names;
	}

	public PatientIdentifier getPatientIdentifierById(Integer patientIdentifierId) {
		return (PatientIdentifier) sessionFactory.getCurrentSession()
				.createQuery("from PatientIdentifier p where patientIdentifierId = :pid")
				.setInteger("pid", patientIdentifierId.intValue()).uniqueResult();
	}

	public void doPDF(Integer oblast, Integer district, Integer facility, Integer year, String quarter, String month,
			String reportDate, String tableData, boolean reportStatus, String reportName, String reportType) {
		try {
			log.debug("Save PDF-PARAMS:" + oblast + ":" + district + ":" + facility + ":" + year + ":"
					+ reportDate + ":" + tableData + ":" + reportName + ":" + reportType);

			Integer status = 0;
			if (reportStatus == true) {
				status = 1;
			}
			String sql = "INSERT INTO report_data (oblast_id, district_id, facility_id,year, quarter, month, report_date, table_data, report_status, report_name, report_type) VALUES ("
					+ oblast + ", " + district + ", " + facility + ", " + year + ", '" + quarter + "', '" + month
					+ "', '" + reportDate + "', '" + tableData + "', " + status + ", '" + reportName + "', '"
					+ reportType + "')";
			Session session = sessionFactory.getCurrentSession();
			session.beginTransaction();
			session.createSQLQuery(sql).executeUpdate();
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public int countPDFRows() {
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		//TODO: How on earth is this even working for over 10 years? Makes zero sense as it will only return single record
		List<String> list = (List<String>) session.createSQLQuery("select count(*) from report_data where report_type = 'MDRTB'").list();
		return list.size();
	}

	public ArrayList<String> getPDFColumns() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("report_id");
		list.add("oblast_id");
		list.add("district_id");
		list.add("facility_id");
		list.add("year");
		list.add("quarter");
		list.add("month");
		list.add("report_date");
		// list.add("table_data");
		list.add("report_status");
		list.add("report_name");
		return list;
	}

	@SuppressWarnings({ "unchecked" })
	public List<List<Integer>> getPDFData(String reportType) {
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		List<List<Integer>> list = new ArrayList<List<Integer>>();
		list.add((List<Integer>) session
				.createSQLQuery("select report_id from report_data where report_type = '" + reportType + "'").list());
		list.add((List<Integer>) session
				.createSQLQuery("select oblast_id from report_data where report_type = '" + reportType + "'").list());
		list.add((List<Integer>) session
				.createSQLQuery("select district_id from report_data where report_type = '" + reportType + "'").list());
		list.add((List<Integer>) session
				.createSQLQuery("select facility_id from report_data where report_type = '" + reportType + "'").list());
		list.add((List<Integer>) session
				.createSQLQuery("select year from report_data where report_type = '" + reportType + "'").list());
		list.add((List<Integer>) session
				.createSQLQuery("select quarter from report_data where report_type = '" + reportType + "'").list());
		list.add((List<Integer>) session
				.createSQLQuery("select month from report_data where report_type = '" + reportType + "'").list());
		list.add((List<Integer>) session
				.createSQLQuery("select report_date from report_data where report_type = '" + reportType + "'").list());
		list.add((List<Integer>) session
				.createSQLQuery("select report_status from report_data where report_type = '" + reportType + "'")
				.list());
		list.add((List<Integer>) session
				.createSQLQuery("select report_name from report_data where report_type = '" + reportType + "'").list());
		list.add((List<Integer>) session
				.createSQLQuery("select report_type from report_data where report_type = '" + reportType + "'").list());
		return list;
	}

	@SuppressWarnings({ "unchecked" })
	public List<String> readTableData(Integer oblast, Integer district, Integer facility, Integer year, String quarter,
			String month, String name, String date, String reportType) {
		String sql = "select table_data from report_data ";
		if (name != null && !name.equals(""))
			sql += " where report_name='" + name + "'";
		if (date != null && !date.equals(""))
			sql += " and report_date='" + date + "'";
		if (oblast != null)
			sql += " and oblast_id=" + oblast;
		else
			sql += " and oblast_id IS NULL";

		if (district != null)
			sql += " and district_id=" + district;
		else
			sql += " and district_id IS NULL";

		if (facility != null)
			sql += " and facility_id=" + facility;
		else
			sql += " and facility_id IS NULL";

		if (year != null)
			sql += " and year=" + year;
		else
			sql += "and year IS NULL";

		if (quarter != null && !quarter.equals(Context.getMessageSourceService().getMessage("mdrtb.annual")))
			sql += " and quarter='" + quarter + "'";
		else if (quarter.equals(Context.getMessageSourceService().getMessage("mdrtb.annual")))
			sql += " and quarter='" + "" + "'";
		else
			sql += " and quarter IS NULL";

		if (month != null)
			sql += " and month='" + month + "'";
		else
			sql += " and month IS NULL";

		sql += " and report_type= '" + reportType + "'";

		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		List<String> list = (List<String>) session.createSQLQuery(sql).list();
		return list;
	}

	public void unlockReport(Integer oblast, Integer district, Integer facility, Integer year, String quarter,
			String month, String name, String date, String reportType) {
		String sql = "delete from report_data " + processReportFilters(oblast, district, facility, year, quarter, month, name, reportType);

		sql += " and report_type='" + reportType + "'";
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		session.createSQLQuery(sql).executeUpdate();
		session.getTransaction().commit();
	}

	public String processReportFilters(Integer oblast, Integer district, Integer facility, Integer year, String quarter, String month,
	        String name, String reportType) {
		StringBuffer sb = new StringBuffer();
		sb.append("where report_type='" + reportType + "' ");
		sb.append("".equals(name) ? "" : "and report_name='" + name + "' ");
		sb.append(oblast == null ? "and oblast_id IS NULL " : " and oblast_id='" + oblast + "' ");
		sb.append(district == null ? "and district_id IS NULL " : " and district_id='" + district + "' ");
		sb.append(facility == null ? "and facility_id IS NULL " : " and facility_id='" + facility + "' ");
		sb.append(year == null ? "and year IS NULL " : " and year='" + year + "' ");
		if (quarter == null) {
			sb.append("and quarter IS NULL ");
		} else if (quarter.equals(Context.getMessageSourceService().getMessage("mdrtb.annual"))) {
			sb.append("and quarter = '' ");
		} else {
			sb.append("and quarter = '" + quarter + "' ");
		}
		sb.append(month == null ? "and month IS NULL " : " and month='" + month + "' ");
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	public boolean readReportStatus(Integer oblast, Integer district, Integer facility, Integer year, String quarter,
			String month, String name, String reportType) {
		String sql = "select report_status from report_data " + processReportFilters(oblast, district, facility, year, quarter, month, name, reportType);
		
		Session session = sessionFactory.getCurrentSession();
//		session.beginTransaction();
		List<String> statusList = (List<String>) session.createSQLQuery(sql).list();
		List<String> list = new PDFHelper().byteToStrArray(statusList.toString());
		boolean reportStatus = false;
		if (list.size() > 0) {
			try {
				reportStatus = Integer.parseInt(list.get(0)) == 1;
			}
			catch (Exception e) {}
		}
//		session.getTransaction().commit();
		return reportStatus;
	}

	public List<Encounter> getEncountersByEncounterTypes(List<String> encounterTypeNames) {
		return getEncountersByEncounterTypes(encounterTypeNames, null, null, null);
	}

	@SuppressWarnings("unchecked")
	/* TODO: Remove unused closeDate parameter */
	public List<Encounter> getEncountersByEncounterTypes(List<String> encounterTypeNames, Date startDate, Date endDate,
			Date closeDate) {
		SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<Integer> encounterIds = new ArrayList<Integer>();
		List<Integer> tempList = new ArrayList<Integer>();
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

		List<Encounter> encounters = new ArrayList<Encounter>();
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

	public BaseLocation getAddressHierarchyLocation(Integer locationId) {
		String query = "select address_hierarchy_entry_id, name, level_id from address_hierarchy_entry where address_hierarchy_entry_id=" + locationId;
		List<List<Object>> results = Context.getAdministrationService().executeSQL(query, true);
		for (List<Object> list : results) {
			Integer id = (Integer)(list.get(0));
			String name = String.valueOf(list.get(1));
			Integer levelId = (Integer)(list.get(2));
			BaseLocation baseLocation = new BaseLocation(id, name, levelId);
			baseLocation.setLevelId(levelId);
			return baseLocation;
		}
		return null;
	}

	public BaseLocation getAddressHierarchyLocationParent(BaseLocation child) {
		String query = "select parent_id from address_hierarchy_entry where address_hierarchy_entry_id=" + child.getId();
		List<List<Object>> results = Context.getAdministrationService().executeSQL(query, true);
		for (List<Object> list : results) {
			Integer id = (Integer)(list.get(0));
			return getAddressHierarchyLocation(id);
		}
		return null;
	}

	public List<BaseLocation> getAddressHierarchyLocationsByHierarchyLevel(Integer level) {
		if (level == null) {
			level = 0;
		}
		String query = "select distinct address_hierarchy_entry_id, name from address_hierarchy_entry where level_id=" + level;
		List<List<Object>> results = Context.getAdministrationService().executeSQL(query, true);
		List<BaseLocation> locations = new ArrayList<BaseLocation>();
		for (List<Object> list : results) {
			Integer id = (Integer)(list.get(0));
			String name = String.valueOf(list.get(1));
			BaseLocation baseLocation = new BaseLocation(id, name, level);
			baseLocation.setLevelId(level);
			locations.add(baseLocation);
		}
		return locations;
	}

	public List<BaseLocation> getAddressHierarchyLocationsByParent(BaseLocation parent) {
		String query = "select distinct address_hierarchy_entry_id, name, level_id from address_hierarchy_entry where parent_id=" + parent.getId();
		List<List<Object>> results = Context.getAdministrationService().executeSQL(query, true);
		List<BaseLocation> locations = new ArrayList<BaseLocation>();
		for (List<Object> list : results) {
			Integer id = (Integer)(list.get(0));
			String name = String.valueOf(list.get(1));
			Integer levelId = (Integer)(list.get(2));
			BaseLocation baseLocation = new BaseLocation(id, name, levelId);
			baseLocation.setLevelId(levelId);
			locations.add(baseLocation);
		}
		return locations;
	}
}
