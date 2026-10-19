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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.jdbc.Work;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.mdrtb.MessageProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * Persistence for {@link MessageProperty}
 */
@Repository("mdrtb.MessagePropertyDao")
public class MessagePropertyDao {
	
	protected static final Log log = LogFactory.getLog(MessagePropertyDao.class);
	
	private static final int MESSAGE_PROPERTY_BATCH_SIZE = 200;
	
	@Autowired
	private SessionFactory sessionFactory;
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	/**
	 * The table holds a few thousand lightweight rows and is read once per cache load; fetching in
	 * a single query is cheaper than paging
	 * 
	 * @return every persisted {@link MessageProperty}
	 */
	public List<MessageProperty> getAllMessageProperties() throws DAOException {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<MessageProperty> query = cb.createQuery(MessageProperty.class);
		Root<MessageProperty> root = query.from(MessageProperty.class);
		query.select(root);
		return sessionFactory.getCurrentSession().createQuery(query).getResultList();
	}
	
	/**
	 * @param lang the bundle language code, e.g. "en", "ru"
	 * @param code the message code
	 * @return the matching {@link MessageProperty}, or null when the pair is not persisted
	 */
	public MessageProperty getMessageProperty(String lang, String code) throws DAOException {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<MessageProperty> query = cb.createQuery(MessageProperty.class);
		Root<MessageProperty> root = query.from(MessageProperty.class);
		query.select(root).where(cb.equal(root.get("lang"), lang), cb.equal(root.get("code"), code));
		return sessionFactory.getCurrentSession().createQuery(query).uniqueResult();
	}
	
	/**
	 * Inserts or updates a single message. Used for administrative corrections; bulk seeding should
	 * go through {@link #saveMessageProperties(Collection)}. This uses {@code merge} rather than
	 * {@code saveOrUpdate} on purpose. The identifier here is assigned (lang + code), not
	 * generated. If the same row has also been read into the session earlier in the transaction,
	 * because loading the message cache reads the whole table — {@code saveOrUpdate} treats the new
	 * object as transient and throws {@code NonUniqueObjectException}.
	 * 
	 * @param messageProperty the message to store
	 * @return the managed instance
	 */
	public MessageProperty saveMessageProperty(MessageProperty messageProperty) throws DAOException {
		return (MessageProperty) sessionFactory.getCurrentSession().merge(messageProperty);
	}
	
	/**
	 * Permanently removes a message. There is no voiding nor any audit fields.
	 * 
	 * @return true when a row was removed
	 */
	public boolean deleteMessageProperty(MessageProperty messageProperty) throws DAOException {
		if (messageProperty == null) {
			return false;
		}
		MessageProperty stored = getMessageProperty(messageProperty.getLang(), messageProperty.getCode());
		if (stored == null) {
			return false;
		}
		sessionFactory.getCurrentSession().delete(stored);
		return true;
	}
	
	/**
	 * Bulk-inserts messages, skipping any row the database already holds. Using pain SQL
	 * deliberately. A batched Hibernate insert may hit a duplicate key, so a single bad row can
	 * cost the entire bulk insert operation to abort. The per-row check is the safety net for the
	 * first run, and for anything inserted manually.
	 * 
	 * @param messageProperties rows to insert
	 * @return the number of rows actually written
	 */
	public int saveMessageProperties(Collection<MessageProperty> messageProperties) throws DAOException {
		if (messageProperties == null || messageProperties.isEmpty()) {
			return 0;
		}
		final Collection<MessageProperty> rows = messageProperties;
		int[] written = { 0 };
		
		sessionFactory.getCurrentSession().doWork(new Work() {
			
			@Override
			public void execute(Connection connection) throws SQLException {
				PreparedStatement exists = null;
				PreparedStatement insert = null;
				try {
					exists = connection.prepareStatement("select 1 from message_properties where lang = ? and code = ?");
					insert = connection
					        .prepareStatement("insert into message_properties (lang, code, message) values (?, ?, ?)");
					
					int pending = 0;
					for (MessageProperty row : rows) {
						if (row == null || row.getLang() == null || row.getCode() == null || alreadyExists(exists, row)) {
							continue;
						}
						insert.setString(1, row.getLang());
						insert.setString(2, row.getCode());
						insert.setString(3, row.getMessage());
						insert.addBatch();
						written[0]++;
						if (++pending % MESSAGE_PROPERTY_BATCH_SIZE == 0) {
							insert.executeBatch();
						}
					}
					insert.executeBatch();
				}
				finally {
					if (insert != null) {
						try {
							insert.close();
						}
						catch (SQLException ignored) {}
					}
					if (exists != null) {
						try {
							exists.close();
						}
						catch (SQLException ignored) {}
					}
				}
			}
			
			private boolean alreadyExists(PreparedStatement exists, MessageProperty row) throws SQLException {
				exists.setString(1, row.getLang());
				exists.setString(2, row.getCode());
				ResultSet found = null;
				try {
					found = exists.executeQuery();
					return found.next();
				}
				finally {
					if (found != null) {
						found.close();
					}
				}
			}
		});
		return written[0];
	}
}
