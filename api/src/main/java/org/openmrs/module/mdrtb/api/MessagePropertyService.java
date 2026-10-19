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

import java.util.List;
import java.util.Set;

import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.mdrtb.MessageProperty;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.transaction.annotation.Transactional;

/**
 * Localised UI strings: resolution, caching and CRUD over the {@code message_properties} table.
 * Split out of {@link MdrtbService} because translations are their own concern, with their own DAO
 * and their own cache. See moduleApplicationContext.xml on how it is wired up.
 */
public interface MessagePropertyService extends OpenmrsService {
	
	/**
	 * Resolves a message for the current user locale. This is the entry point for message
	 * resolution in this module: API classes OMOD controllers JSP tags Messages are served from an
	 * in-memory cache over the {@code message_properties} table, which seeds from the module's
	 * {@code messages*.properties} bundles at startup {@link java.text.MessageFormat} is applied
	 * only when {@code arguments} is non-empty, matching Spring's behaviour and leaving apostrophes
	 * in unparameterised messages alone.
	 * 
	 * @param code the message code to resolve
	 * @param arguments values for MessageFormat placeholders in the message
	 * @param defaultMessage text to return when the code cannot be resolved
	 * @return the localized message; never {@code null} for a non-blank code
	 */
	@Authorized(PrivilegeConstants.GET_GLOBAL_PROPERTIES)
	@Transactional(readOnly = true)
	String getMessage(String code, Object[] arguments, String defaultMessage);
	
	/**
	 * Convenience overload of {@link #getMessage(String, Object[], String)} with no
	 * arguments/default message.
	 */
	@Authorized(PrivilegeConstants.GET_GLOBAL_PROPERTIES)
	@Transactional(readOnly = true)
	String getMessage(String code);
	
	/**
	 * Convenience overload of {@link #getMessage(String, Object[], String)} with no default
	 * message.
	 */
	@Authorized(PrivilegeConstants.GET_GLOBAL_PROPERTIES)
	@Transactional(readOnly = true)
	String getMessage(String code, Object[] arguments);
	
	/**
	 * Bulk-inserts messages in a single transaction. It walks every code in the bundles, skips the
	 * ones already stored, and hands the remainder here.
	 * 
	 * @param messageProperties rows to insert
	 * @return the number of rows written
	 */
	@Authorized(PrivilegeConstants.MANAGE_CONCEPT_REFERENCE_TERMS)
	@Transactional
	int saveMessageProperties(Set<MessageProperty> messageProperties);
	
	/**
	 * Discards every cached message so that subsequent lookups go back to the database. Call this
	 * after editing {@code message_properties} directly.
	 */
	@Authorized(PrivilegeConstants.GET_GLOBAL_PROPERTIES)
	void resetMessageCache();
	
	/**
	 * Returns every persisted message, across all languages.
	 */
	@Authorized(PrivilegeConstants.GET_GLOBAL_PROPERTIES)
	@Transactional(readOnly = true)
	List<MessageProperty> getAllMessageProperties();
	
	/**
	 * Looks a message up in the database, bypassing the cache and the locale fallback chain.
	 * 
	 * @param lang bundle language code, e.g. {@code en}, {@code ru}, {@code id_ID}
	 * @param code the message code
	 * @return the persisted row, or {@code null} when the pair is not stored
	 */
	@Authorized(PrivilegeConstants.GET_GLOBAL_PROPERTIES)
	@Transactional(readOnly = true)
	MessageProperty getMessageProperty(String lang, String code);
	
	/**
	 * Inserts or updates a single message and resets the cache so the change takes effect
	 * immediately.
	 */
	@Authorized(PrivilegeConstants.MANAGE_CONCEPT_REFERENCE_TERMS)
	@Transactional
	MessageProperty saveMessageProperty(MessageProperty messageProperty);
	
	/**
	 * Permanently deletes a message and resets the cache. There is no voiding for this entity.
	 * 
	 * @param messageProperty identifies the row by its lang and code
	 * @return true when a row was removed, false when there was nothing to remove
	 */
	@Authorized(PrivilegeConstants.PURGE_CONCEPT_REFERENCE_TERMS)
	@Transactional
	boolean deleteMessageProperty(MessageProperty messageProperty);
}
