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
package org.openmrs.module.mdrtb.api.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.lab.LabTest;
import org.openmrs.module.mdrtb.lab.LabTestAttribute;
import org.openmrs.module.mdrtb.lab.LabTestAttributeType;
import org.openmrs.module.mdrtb.lab.LabTestGroup;
import org.openmrs.module.mdrtb.lab.LabTestSample;
import org.openmrs.module.mdrtb.lab.LabTestSampleStatus;
import org.openmrs.module.mdrtb.lab.LabTestType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author owais.hussain@ihsinformatics.com
 */
@Repository("mdrtb.LabDao")
public class LabDao {
	
	private static final int MAX_FETCH_LIMIT = 100;
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	@Autowired
	private SessionFactory sessionFactory;
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	/**
	 * @param includeRetired include retired objects
	 * @return {@link LabTestAttributeType} objects
	 */
	public List<LabTestAttributeType> getAllLabTestAttributeTypes(boolean includeRetired) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<LabTestAttributeType> query = cb.createQuery(LabTestAttributeType.class);
		Root<LabTestAttributeType> root = query.from(LabTestAttributeType.class);
		List<Predicate> predicates = new ArrayList<>();
		if (!includeRetired) {
			predicates.add(cb.equal(root.get("retired"), false));
		}
		query.select(root).where(predicates.toArray(new Predicate[0])).orderBy(cb.asc(root.get("name")));
		return sessionFactory.getCurrentSession().createQuery(query).getResultList();
	}
	
	/**
	 * @param includeRetired include retired objects
	 * @return {@link LabTestType} objects
	 */
	public List<LabTestType> getAllLabTestTypes(boolean includeRetired) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<LabTestType> query = cb.createQuery(LabTestType.class);
		Root<LabTestType> root = query.from(LabTestType.class);
		List<Predicate> predicates = new ArrayList<>();
		if (!includeRetired) {
			predicates.add(cb.equal(root.get("retired"), false));
		}
		query.select(root).where(predicates.toArray(new Predicate[0])).orderBy(cb.asc(root.get("name")));
		return sessionFactory.getCurrentSession().createQuery(query).getResultList();
	}
	
	/**
	 * @param name the name of lab test type
	 * @param shortName the short name
	 * @param testGroup the {@link LabTestGroup} object
	 * @param referenceConcept the {@link Concept} object
	 * @param includeRetired include retired objects
	 * @return {@link LabTestType} objects
	 */
	public List<LabTestType> getLabTestTypes(String name, String shortName, LabTestGroup testGroup,
	        Concept referenceConcept, boolean includeRetired) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<LabTestType> query = cb.createQuery(LabTestType.class);
		Root<LabTestType> root = query.from(LabTestType.class);
		List<Predicate> predicates = new ArrayList<>();
		if (name != null) {
			predicates.add(cb.like(cb.lower(root.get("name")), name.toLowerCase() + "%"));
		}
		if (shortName != null) {
			predicates.add(cb.like(cb.lower(root.get("shortName")), shortName.toLowerCase() + "%"));
		}
		if (testGroup != null) {
			predicates.add(cb.equal(root.get("testGroup"), testGroup));
		}
		if (referenceConcept != null) {
			predicates.add(cb.equal(root.get("referenceConcept"), referenceConcept));
		}
		if (!includeRetired) {
			predicates.add(cb.equal(root.get("retired"), false));
		}
		query.select(root).where(predicates.toArray(new Predicate[0]))
		        .orderBy(cb.asc(root.get("name")), cb.asc(root.get("retired")));
		return sessionFactory.getCurrentSession().createQuery(query).getResultList();
	}
	
	/**
	 * @param order the {@link org.openmrs.Order} object
	 * @return {@link LabTest} object by matching given {@link org.openmrs.Order} object
	 */
	public LabTest getLabTest(org.openmrs.Order order) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<LabTest> query = cb.createQuery(LabTest.class);
		Root<LabTest> root = query.from(LabTest.class);
		query.select(root).where(cb.equal(root.get("testOrderId"), order.getId()));
		return sessionFactory.getCurrentSession().createQuery(query).uniqueResult();
	}
	
	/**
	 * @param labTestId the Id
	 * @return {@link LabTest} object
	 */
	public LabTest getLabTest(Integer labTestId) {
		return sessionFactory.getCurrentSession().get(LabTest.class, labTestId);
	}
	
	/**
	 * @param labTestAttributeId the Id
	 * @return {@link LabTestAttribute} object
	 */
	public LabTestAttribute getLabTestAttribute(Integer labTestAttributeId) {
		return sessionFactory.getCurrentSession().get(LabTestAttribute.class, labTestAttributeId);
	}
	
	/**
	 * @param uuid the unique Id
	 * @return {@link LabTestAttribute} object
	 */
	public LabTestAttribute getLabTestAttributeByUuid(String uuid) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<LabTestAttribute> query = cb.createQuery(LabTestAttribute.class);
		Root<LabTestAttribute> root = query.from(LabTestAttribute.class);
		query.select(root).where(cb.equal(root.get("uuid"), uuid.toLowerCase()));
		return sessionFactory.getCurrentSession().createQuery(query).uniqueResult();
	}
	
	/**
	 * @param testOrderId the Id
	 * @return {@link LabTestAttribute} object(s)
	 */
	public List<LabTestAttribute> getLabTestAttributes(Integer testOrderId) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<LabTestAttribute> query = cb.createQuery(LabTestAttribute.class);
		Root<LabTestAttribute> root = query.from(LabTestAttribute.class);
		query.select(root).where(cb.equal(root.get("labTest").get("testOrderId"), testOrderId));
		return sessionFactory.getCurrentSession().createQuery(query).getResultList();
	}
	
	/**
	 * @param labTestAttributeType the {@link LabTestAttributeType} object
	 * @param valueReference the reference value
	 * @param from the start {@link Date} object
	 * @param to the end {@link Date} object
	 * @param includeVoided include retired objects
	 * @return {@link LabTestAttribute} object(s)
	 */
	public List<LabTestAttribute> getLabTestAttributes(LabTestAttributeType labTestAttributeType, String valueReference,
	        Date from, Date to, boolean includeVoided) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<LabTestAttribute> query = cb.createQuery(LabTestAttribute.class);
		Root<LabTestAttribute> root = query.from(LabTestAttribute.class);
		List<Predicate> predicates = new ArrayList<>();
		if (labTestAttributeType != null) {
			predicates.add(cb.equal(root.get("attributeType").get("labTestAttributeTypeId"), labTestAttributeType.getId()));
		}
		if (valueReference != null) {
			predicates.add(cb.like(cb.lower(root.<String>get("valueReference")), valueReference.toLowerCase() + "%"));
		}
		if (from != null && to != null) {
			predicates.add(cb.between(root.<Date>get("dateCreated"), from, to));
		}
		if (!includeVoided) {
			predicates.add(cb.equal(root.get("voided"), false));
		}
		query.select(root).where(predicates.toArray(new Predicate[0]))
		        .orderBy(cb.asc(root.get("labTestAttributeId")), cb.asc(root.get("voided")));
		return sessionFactory.getCurrentSession().createQuery(query).getResultList();
	}
	
	/**
	 * @param patient the {@link Patient} object
	 * @param labTestAttributeType the {@link LabTestAttributeType} object
	 * @param includeVoided include retired objects
	 * @return {@link LabTestAttribute} object(s)
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	public List<LabTestAttribute> getLabTestAttributes(Patient patient, LabTestAttributeType labTestAttributeType,
	        boolean includeVoided) {
		StringBuilder queryString = new StringBuilder();
		queryString.append("from LabTestAttribute lta where lta.labTest.order.patient.patientId = :patientId");
		queryString.append(labTestAttributeType == null ? ""
		        : " and lta.labTestAttributeType.labTestAttributeTypeId = :labTestAttributeType");
		queryString.append(includeVoided ? "" : " and lta.voided = :voided");
		Query query = sessionFactory.getCurrentSession().createQuery(queryString.toString());
		query.setInteger("patientId", patient.getPatientId());
		if (labTestAttributeType != null) {
			query.setInteger("labTestAttributeTypeId", labTestAttributeType.getId());
		}
		if (!includeVoided) {
			query.setBoolean("voided", false);
		}
		return query.list();
	}
	
	/**
	 * @param labTestAttributeTypeId the Id
	 * @return {@link LabTestAttributeType} object
	 */
	public LabTestAttributeType getLabTestAttributeType(Integer labTestAttributeTypeId) {
		return (LabTestAttributeType) sessionFactory.getCurrentSession().get(LabTestAttributeType.class,
		    labTestAttributeTypeId);
	}
	
	/**
	 * @param uuid the unique Id
	 * @return {@link LabTestAttributeType} object
	 */
	public LabTestAttributeType getLabTestAttributeTypeByUuid(String uuid) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<LabTestAttributeType> query = cb.createQuery(LabTestAttributeType.class);
		Root<LabTestAttributeType> root = query.from(LabTestAttributeType.class);
		query.select(root).where(cb.equal(root.get("uuid"), uuid.toLowerCase()));
		return sessionFactory.getCurrentSession().createQuery(query).uniqueResult();
	}
	
	/**
	 * @param name the name
	 * @param datatypeClassname the name of fully specified data type class
	 * @param includeRetired include retired objects
	 * @return {@link LabTestAttributeType} object(s)
	 */
	public List<LabTestAttributeType> getLabTestAttributeTypes(String name, String datatypeClassname, boolean includeRetired) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<LabTestAttributeType> query = cb.createQuery(LabTestAttributeType.class);
		Root<LabTestAttributeType> root = query.from(LabTestAttributeType.class);
		List<Predicate> predicates = new ArrayList<>();
		if (name != null) {
			predicates.add(cb.like(cb.lower(root.get("name")), name.toLowerCase() + "%"));
		}
		if (datatypeClassname != null) {
			predicates.add(cb.equal(root.get("datatypeClassname"), datatypeClassname));
		}
		if (!includeRetired) {
			predicates.add(cb.equal(root.get("retired"), false));
		}
		query.select(root).where(predicates.toArray(new Predicate[0]))
		        .orderBy(cb.asc(root.get("name")), cb.asc(root.get("retired")));
		return sessionFactory.getCurrentSession().createQuery(query).getResultList();
	}
	
	/**
	 * @param labTestType {@link LabTestType} object
	 * @param includeRetired include retired objects
	 * @return {@link LabTestAttributeType} object(s)
	 */
	public List<LabTestAttributeType> getLabTestAttributeTypes(LabTestType labTestType, boolean includeRetired) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<LabTestAttributeType> query = cb.createQuery(LabTestAttributeType.class);
		Root<LabTestAttributeType> root = query.from(LabTestAttributeType.class);
		List<Predicate> predicates = new ArrayList<>();
		predicates.add(cb.equal(root.get("labTestType"), labTestType));
		if (!includeRetired) {
			predicates.add(cb.equal(root.get("retired"), false));
		}
		query.select(root).where(predicates.toArray(new Predicate[0]))
		        .orderBy(cb.asc(root.get("sortWeight")), cb.asc(root.get("retired")));
		return sessionFactory.getCurrentSession().createQuery(query).getResultList();
	}
	
	/**
	 * @param uuid the unique Id
	 * @return {@link LabTest} object
	 */
	public LabTest getLabTestByUuid(String uuid) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<LabTest> query = cb.createQuery(LabTest.class);
		Root<LabTest> root = query.from(LabTest.class);
		query.select(root).where(cb.equal(root.get("uuid"), uuid.toLowerCase()));
		return sessionFactory.getCurrentSession().createQuery(query).uniqueResult();
	}
	
	/**
	 * @param labTestType the {@link LabTestType} object
	 * @param patient the {@link Patient} object
	 * @param orderNumber the order number
	 * @param referenceNumber the reference number
	 * @param orderConcept the {@link org.openmrs.Order} concept object
	 * @param orderer the {@link Provider} object
	 * @param from the start {@link Date} object
	 * @param to the end {@link Date} object
	 * @param includeVoided include retired objects
	 * @return {@link LabTest} object(s)
	 */
	public List<LabTest> getLabTests(LabTestType labTestType, Patient patient, String orderNumber, String referenceNumber,
	        Concept orderConcept, Provider orderer, Date from, Date to, boolean includeVoided) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<LabTest> query = cb.createQuery(LabTest.class);
		Root<LabTest> root = query.from(LabTest.class);
		Join<?, ?> orderJoin = root.join("order", JoinType.INNER);
		List<Predicate> predicates = new ArrayList<>();
		if (labTestType != null) {
			predicates.add(cb.equal(root.get("labTestType"), labTestType));
		}
		if (patient != null) {
			predicates.add(cb.equal(orderJoin.get("patient").get("patientId"), patient.getPatientId()));
		}
		if (orderNumber != null) {
			predicates.add(cb.like(cb.lower(orderJoin.get("orderReference")), orderNumber.toLowerCase() + "%"));
		}
		if (orderConcept != null) {
			predicates.add(cb.equal(orderJoin.get("concept").get("conceptId"), orderConcept.getConceptId()));
		}
		if (orderer != null) {
			predicates.add(cb.equal(orderJoin.get("orderer").get("providerId"), orderer.getProviderId()));
		}
		if (referenceNumber != null) {
			predicates.add(cb.like(cb.lower(root.get("labReferenceNumber")), referenceNumber.toLowerCase() + "%"));
		}
		if (from != null && to != null) {
			predicates.add(cb.between(root.get("dateCreated"), from, to));
		}
		if (!includeVoided) {
			predicates.add(cb.equal(orderJoin.get("voided"), false));
		}
		query.select(root).where(predicates.toArray(new Predicate[0]))
		        .orderBy(cb.asc(root.get("testOrderId")), cb.asc(root.get("voided")));
		return sessionFactory.getCurrentSession().createQuery(query).getResultList();
	}
	
	/**
	 * Returns the lab tests belonging to one program episode. Unlike the broader overload above,
	 * this filters on labtest_test.patient_program_id, the real foreign key, so a patient with more
	 * than one TB episode does not get one episode's results attributed to another.
	 * <p>
	 * A null patientProgram is NOT treated as "any program": it selects the rows whose program is
	 * still unresolved, which is a deliberate choice so callers cannot silently widen the query by
	 * passing null.
	 * 
	 * @param patient the {@link Patient} whose tests to return
	 * @param labTestType optional {@link LabTestType} filter
	 * @param patientProgram the {@link PatientProgram} episode, or null for unresolved rows
	 * @return the matching non-voided {@link LabTest} objects
	 */
	public List<LabTest> getLabTests(Patient patient, LabTestType labTestType, PatientProgram patientProgram) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<LabTest> query = cb.createQuery(LabTest.class);
		Root<LabTest> root = query.from(LabTest.class);
		Join<?, ?> orderJoin = root.join("order", JoinType.INNER);
		List<Predicate> predicates = new ArrayList<>();
		if (patient != null) {
			predicates.add(cb.equal(orderJoin.get("patient").get("patientId"), patient.getPatientId()));
		}
		if (labTestType != null) {
			predicates.add(cb.equal(root.get("labTestType"), labTestType));
		}
		if (patientProgram == null) {
			predicates.add(cb.isNull(root.get("patientProgram")));
		} else {
			predicates.add(cb.equal(root.get("patientProgram"), patientProgram));
		}
		predicates.add(cb.equal(orderJoin.get("voided"), false));
		predicates.add(cb.equal(root.get("voided"), false));
		query.select(root).where(predicates.toArray(new Predicate[0])).orderBy(cb.asc(root.get("testOrderId")));
		return sessionFactory.getCurrentSession().createQuery(query).getResultList();
	}
	
	/**
	 * @param labTestSampleId the generated Id
	 * @return {@link LabTestSample} object
	 */
	public LabTestSample getLabTestSample(Integer labTestSampleId) {
		return (LabTestSample) sessionFactory.getCurrentSession().get(LabTestSample.class, labTestSampleId);
	}
	
	/**
	 * @param uuid the unique Id
	 * @return {@link LabTestSample} object
	 */
	public LabTestSample getLabTestSampleByUuid(String uuid) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<LabTestSample> query = cb.createQuery(LabTestSample.class);
		Root<LabTestSample> root = query.from(LabTestSample.class);
		query.select(root).where(cb.equal(root.get("uuid"), uuid.toLowerCase()));
		return sessionFactory.getCurrentSession().createQuery(query).uniqueResult();
	}
	
	/**
	 * @param labTest the {@link LabTest} object
	 * @param includeVoided include retired objects
	 * @return {@link LabTestSample} object(s)
	 */
	public List<LabTestSample> getLabTestSamples(LabTest labTest, boolean includeVoided) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<LabTestSample> query = cb.createQuery(LabTestSample.class);
		Root<LabTestSample> root = query.from(LabTestSample.class);
		List<Predicate> predicates = new ArrayList<>();
		predicates.add(cb.equal(root.get("labTest").get("testOrderId"), labTest.getId()));
		if (!includeVoided) {
			predicates.add(cb.equal(root.get("voided"), false));
		}
		query.select(root).where(predicates.toArray(new Predicate[0]))
		        .orderBy(cb.asc(root.get("sampleIdentifier")), cb.asc(root.get("voided")));
		return sessionFactory.getCurrentSession().createQuery(query).getResultList();
	}
	
	/**
	 * @param patient the {@link Patient} object
	 * @param includeVoided include retired objects
	 * @return {@link LabTestSample} object(s)
	 */
	public List<LabTestSample> getLabTestSamples(Patient patient, boolean includeVoided) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<LabTestSample> query = cb.createQuery(LabTestSample.class);
		Root<LabTestSample> root = query.from(LabTestSample.class);
		Join<?, ?> labTestJoin = root.join("labTest", JoinType.INNER);
		Join<?, ?> orderJoin = labTestJoin.join("order", JoinType.INNER);
		List<Predicate> predicates = new ArrayList<>();
		predicates.add(cb.equal(orderJoin.get("patient").get("personId"), patient.getPatientId()));
		if (!includeVoided) {
			predicates.add(cb.equal(root.get("voided"), false));
		}
		query.select(root).where(predicates.toArray(new Predicate[0]))
		        .orderBy(cb.asc(root.get("sampleIdentifier")), cb.asc(root.get("voided")));
		return sessionFactory.getCurrentSession().createQuery(query).getResultList();
	}
	
	/**
	 * @param collector the {@link Provider} object
	 * @param includeVoided include retired objects
	 * @return {@link LabTestSample} object(s)
	 */
	public List<LabTestSample> getLabTestSamples(Provider collector, boolean includeVoided) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<LabTestSample> query = cb.createQuery(LabTestSample.class);
		Root<LabTestSample> root = query.from(LabTestSample.class);
		List<Predicate> predicates = new ArrayList<>();
		predicates.add(cb.equal(root.get("collector").get("providerId"), collector.getProviderId()));
		if (!includeVoided) {
			predicates.add(cb.equal(root.get("voided"), false));
		}
		query.select(root).where(predicates.toArray(new Predicate[0]))
		        .orderBy(cb.asc(root.get("sampleIdentifier")), cb.asc(root.get("voided")));
		return sessionFactory.getCurrentSession().createQuery(query).getResultList();
	}
	
	/**
	 * Returns a list of {@link LabTestSample} objects by matching the given criteria. At least one
	 * of the first three parameters must be provided, the rest are optional.
	 * 
	 * @param labTest the {@link LabTest} object
	 * @param patient the {@link Patient} object
	 * @param sampleIdentifier the identifier of specimen sample
	 * @param specimenType the {@link Concept} object representing type of specimen
	 * @param status the {@link LabTestSampleStatus} enumerated type
	 * @param collector the {@link Provider} object
	 * @param from the start {@link Date} object representing start of date of creation
	 * @param to the end {@link Date} object representing end of date of creation
	 * @param includeVoided include retired objects
	 * @return {@link LabTestSample} object(s)
	 */
	public List<LabTestSample> getLabTestSamples(LabTest labTest, Patient patient, String sampleIdentifier,
	        Concept specimenType, LabTestSampleStatus status, Provider collector, Date from, Date to, boolean includeVoided) {
		if (labTest == null && patient == null && sampleIdentifier == null) {
			return null;
		}
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<LabTestSample> query = cb.createQuery(LabTestSample.class);
		Root<LabTestSample> root = query.from(LabTestSample.class);
		List<Predicate> predicates = new ArrayList<>();
		if (labTest != null) {
			predicates.add(cb.equal(root.get("labTest").get("testOrderId"), labTest.getTestOrderId()));
		}
		if (patient != null) {
			Join<?, ?> labTestJoin = root.join("labTest", JoinType.INNER);
			Join<?, ?> orderJoin = labTestJoin.join("order", JoinType.INNER);
			predicates.add(cb.equal(orderJoin.get("patient").get("personId"), patient.getPatientId()));
		}
		if (sampleIdentifier != null) {
			predicates.add(cb.like(cb.lower(root.get("sampleIdentifier")), sampleIdentifier.toLowerCase() + "%"));
		}
		if (specimenType != null) {
			predicates.add(cb.equal(root.get("specimenType").get("conceptId"), specimenType.getConceptId()));
		}
		if (status != null) {
			predicates.add(cb.equal(root.get("status"), status));
		}
		if (collector != null) {
			predicates.add(cb.equal(root.get("collector").get("providerId"), collector.getProviderId()));
		}
		if (from != null && to != null) {
			predicates.add(cb.between(root.get("dateCreated"), from, to));
		}
		if (!includeVoided) {
			predicates.add(cb.equal(root.get("voided"), false));
		}
		query.select(root).where(predicates.toArray(new Predicate[0])).orderBy(cb.asc(root.get("sampleIdentifier")));
		return sessionFactory.getCurrentSession().createQuery(query).getResultList();
	}
	
	/**
	 * @param labTestTypeId the generated Id
	 * @return {@link LabTestType} object
	 */
	public LabTestType getLabTestType(Integer labTestTypeId) {
		return (LabTestType) sessionFactory.getCurrentSession().get(LabTestType.class, labTestTypeId);
	}
	
	/**
	 * @param uuid the unique Id
	 * @return {@link LabTestType} object
	 */
	public LabTestType getLabTestTypeByUuid(String uuid) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<LabTestType> query = cb.createQuery(LabTestType.class);
		Root<LabTestType> root = query.from(LabTestType.class);
		query.select(root).where(cb.equal(root.get("uuid"), uuid.toLowerCase()));
		return sessionFactory.getCurrentSession().createQuery(query).uniqueResult();
	}
	
	/**
	 * Returns a list of 'n' number of {@link LabTest} objects. If firstNObjects is true, then
	 * earliest 'n' objects are returned; if lastNObjects is true, then latest 'n' objects are
	 * returned. If both a true, then a union of both results is returned. Maximum number of objects
	 * to return is limited by MAX_FETCH_LIMIT
	 * 
	 * @param patient the {@link Patient} object
	 * @param n the number of objects to return
	 * @param firstNObjects whether to return initial n objects
	 * @param lastNObjects whether to return last n objects
	 * @param includeVoided include retired objects
	 * @return {@link LabTest} object
	 */
	public List<LabTest> getNLabTests(Patient patient, int n, boolean firstNObjects, boolean lastNObjects,
	        boolean includeVoided) {
		List<LabTest> firstN = null;
		List<LabTest> lastN = null;
		int maxResults = Math.min(n, MAX_FETCH_LIMIT);
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		if (firstNObjects) {
			CriteriaQuery<LabTest> query = cb.createQuery(LabTest.class);
			Root<LabTest> root = query.from(LabTest.class);
			List<Predicate> queryPredicates = new ArrayList<>();
			if (patient != null) {
				queryPredicates.add(cb.equal(root.join("order", JoinType.INNER).get("patient").get("personId"), patient.getPatientId()));
			}
			if (!includeVoided) {
				queryPredicates.add(cb.equal(root.get("voided"), false));
			}
			query.select(root).where(queryPredicates.toArray(new Predicate[0])).orderBy(cb.asc(root.get("dateCreated")));
			firstN = sessionFactory.getCurrentSession().createQuery(query).setMaxResults(maxResults).getResultList();
		}
		if (lastNObjects) {
			CriteriaQuery<LabTest> query = cb.createQuery(LabTest.class);
			Root<LabTest> root = query.from(LabTest.class);
			List<Predicate> queryPredicates = new ArrayList<>();
			if (patient != null) {
				queryPredicates.add(cb.equal(root.join("order", JoinType.INNER).get("patient").get("personId"), patient.getPatientId()));
			}
			if (!includeVoided) {
				queryPredicates.add(cb.equal(root.get("voided"), false));
			}
			query.select(root).where(queryPredicates.toArray(new Predicate[0])).orderBy(cb.desc(root.get("dateCreated")));
			lastN = sessionFactory.getCurrentSession().createQuery(query).setMaxResults(maxResults).getResultList();
		}
		List<LabTest> list = new ArrayList<>();
		if (firstN != null) {
			list.addAll(firstN);
		}
		if (lastN != null) {
			list.addAll(lastN);
		}
		return list;
	}
	
	/**
	 * Returns a list of 'n' number of {@link LabTestSample} objects by matching {@link Patient} and
	 * {@link LabTestSampleStatus} (optional, pass null to ignore). If firstNObjects is true, then
	 * earliest 'n' objects are returned; if lastNObjects is true, then latest 'n' objects are
	 * returned. If both a true, then a union of both results is returned. Maximum number of objects
	 * to return is limited by MAX_FETCH_LIMIT
	 * 
	 * @param patient the {@link Patient} object
	 * @param status the {@link LabTestSampleStatus} object
	 * @param n the number of objects to return
	 * @param firstNObjects whether to return initial n objects
	 * @param lastNObjects whether to return last n objects
	 * @param includeVoided include retired objects
	 * @return {@link LabTestSample} object
	 */
	public List<LabTestSample> getNLabTestSamples(Patient patient, LabTestSampleStatus status, int n, boolean firstNObjects,
	        boolean lastNObjects, boolean includeVoided) {
		List<LabTestSample> firstN = null;
		List<LabTestSample> lastN = null;
		int maxResults = Math.min(n, MAX_FETCH_LIMIT);
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		if (firstNObjects) {
			CriteriaQuery<LabTestSample> query = cb.createQuery(LabTestSample.class);
			Root<LabTestSample> root = query.from(LabTestSample.class);
			Join<?, ?> labTestJoin = root.join("labTest", JoinType.INNER);
			Join<?, ?> orderJoin = labTestJoin.join("order", JoinType.INNER);
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(cb.equal(orderJoin.get("patient").get("personId"), patient.getPatientId()));
			if (status != null) predicates.add(cb.equal(root.get("status"), status));
			if (!includeVoided) predicates.add(cb.equal(root.get("voided"), false));
			query.select(root).where(predicates.toArray(new Predicate[0])).orderBy(cb.asc(root.get("dateCreated")));
			firstN = sessionFactory.getCurrentSession().createQuery(query).setMaxResults(maxResults).getResultList();
		}
		if (lastNObjects) {
			CriteriaQuery<LabTestSample> query = cb.createQuery(LabTestSample.class);
			Root<LabTestSample> root = query.from(LabTestSample.class);
			Join<?, ?> labTestJoin = root.join("labTest", JoinType.INNER);
			Join<?, ?> orderJoin = labTestJoin.join("order", JoinType.INNER);
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(cb.equal(orderJoin.get("patient").get("personId"), patient.getPatientId()));
			if (status != null) predicates.add(cb.equal(root.get("status"), status));
			if (!includeVoided) predicates.add(cb.equal(root.get("voided"), false));
			query.select(root).where(predicates.toArray(new Predicate[0])).orderBy(cb.desc(root.get("dateCreated")));
			lastN = sessionFactory.getCurrentSession().createQuery(query).setMaxResults(maxResults).getResultList();
		}
		List<LabTestSample> list = new ArrayList<>();
		if (firstN != null) {
			list.addAll(firstN);
		}
		if (lastN != null) {
			list.addAll(lastN);
		}
		return list;
	}
	
	/**
	 * @param labTest the {@link LabTest} object to delete
	 */
	public void purgeLabTest(LabTest labTest) {
		sessionFactory.getCurrentSession().delete(labTest);
	}
	
	/**
	 * @param labTestAttribute the {@link LabTestAttribute} object to delete
	 */
	public void purgeLabTestAttribute(LabTestAttribute labTestAttribute) {
		sessionFactory.getCurrentSession().delete(labTestAttribute);
	}
	
	/**
	 * @param labTestAttributeType the {@link LabTestAttributeType} object to delete
	 */
	public void purgeLabTestAttributeType(LabTestAttributeType labTestAttributeType) {
		sessionFactory.getCurrentSession().delete(labTestAttributeType);
	}
	
	/**
	 * @param labTestSample the {@link LabTestSample} object to delete
	 */
	public void purgeLabTestSample(LabTestSample labTestSample) {
		sessionFactory.getCurrentSession().delete(labTestSample);
	}
	
	/**
	 * @param labTestType the {@link LabTestType} object to delete
	 */
	public void purgeLabTestType(LabTestType labTestType) {
		sessionFactory.getCurrentSession().delete(labTestType);
	}
	
	/**
	 * Detects whether it's a new order or existing one. In case the order already exits, it is NOT
	 * overridden because Order objects are immutable
	 * 
	 * @param order the {@link org.openmrs.Order} object to save
	 * @return saved {@link org.openmrs.Order} object
	 */
	public org.openmrs.Order saveLabTestOrder(org.openmrs.Order order) {
		// NOTE: do not call order.getOrderType().setJavaClassName(...) here. That object is a managed
		// Hibernate entity, so writing to it rewrites the shared order_type row for the whole system.
		// The Test Order type is aligned with org.openmrs.Order by the
		// mdrtb-2026-08-13-test-order-java-class changeset in liquibase.xml instead.
		boolean createNew = order.getId() == null;
		if (!createNew) {
			// See if the given ID actually exists or not
			createNew = Context.getOrderService().getOrder(order.getId()) == null;
		}
		if (createNew) {
			order.setId(null);
			return Context.getOrderService().saveOrder(order, null);
		}
		// Do nothing
		return order;
	}
	
	/**
	 * Persists {@link LabTest} in database. This method also persists {@link org.openmrs.Order}
	 * entity, because unlike {@link org.openmrs.Order}, the {@link LabTest} is not hierarchical
	 * 
	 * @param labTest the {@link LabTest} object to save
	 * @return saved {@link LabTest} object
	 */
	public LabTest saveLabTest(LabTest labTest) {
		org.openmrs.Order savedOrder = saveLabTestOrder(labTest.getOrder());
		labTest.setOrder(savedOrder);
		labTest.setTestOrderId(savedOrder.getOrderId());
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(labTest);
		return labTest;
	}
	
	/**
	 * @param labTestAttribute the {@link LabTestAttribute} object to save
	 * @return saved {@link LabTestAttribute} object
	 */
	public LabTestAttribute saveLabTestAttribute(LabTestAttribute labTestAttribute) {
		
		sessionFactory.getCurrentSession().saveOrUpdate(labTestAttribute);
		return labTestAttribute;
	}
	
	/**
	 * @param labTestAttributeType the {@link LabTestAttributeType} object to save
	 * @return saved {@link LabTestAttributeType} object
	 */
	public LabTestAttributeType saveLabTestAttributeType(LabTestAttributeType labTestAttributeType) {
		sessionFactory.getCurrentSession().saveOrUpdate(labTestAttributeType);
		return labTestAttributeType;
	}
	
	/**
	 * @param labTestSample the {@link LabTestSample} object to save
	 * @return saved {@link LabTestSample} object
	 */
	public LabTestSample saveLabTestSample(LabTestSample labTestSample) {
		sessionFactory.getCurrentSession().saveOrUpdate(labTestSample);
		return labTestSample;
	}
	
	/**
	 * @param labTestType the {@link LabTestType} object to save
	 * @return saved {@link LabTestType} object
	 */
	public LabTestType saveLabTestType(LabTestType labTestType) {
		sessionFactory.getCurrentSession().saveOrUpdate(labTestType);
		return labTestType;
	}
}
