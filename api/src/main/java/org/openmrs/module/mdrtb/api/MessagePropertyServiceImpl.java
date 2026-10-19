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
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.MdrtbMessages;
import org.openmrs.module.mdrtb.MessageProperty;
import org.openmrs.module.mdrtb.api.dao.MessagePropertyDao;
import org.springframework.beans.factory.annotation.Autowired;

public class MessagePropertyServiceImpl extends BaseOpenmrsService implements MessagePropertyService {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	@Autowired
	MessagePropertyDao messagePropertyDao;
	
	/**
	 * Owns the message cache and the locale fallback chain, in the same way {@link MdrtbConcepts}
	 * owns the concept cache.
	 */
	MdrtbMessages mdrtbMessages = new MdrtbMessages();
	
	private boolean messageStoreWired = false;
	
	/**
	 * Injected in moduleApplicationContext.xml
	 */
	public void setMessagePropertyDao(MessagePropertyDao messagePropertyDao) {
		this.messagePropertyDao = messagePropertyDao;
	}
	
	@Override
	public void onStartup() {
		log.info("MDRTB message property service starting up");
	}
	
	@Override
	public String getMessage(String code) {
		return getMessage(code, null, null);
	}
	
	@Override
	public String getMessage(String code, Object[] arguments) {
		return getMessage(code, arguments, null);
	}
	
	@Override
	public String getMessage(String code, Object[] arguments, String defaultMessage) {
		if (StringUtils.isBlank(code)) {
			return defaultMessage == null ? "" : defaultMessage;
		}
		Locale locale = resolveLocale();
		
		// Module's own messages. MdrtbMessages owns the locale, the cache and the MessageFormat handling;
		// it calls back into the DAO.
		String message = messages().resolve(code, locale, arguments);
		if (message != null) {
			return message;
		}
		
		// Core and other-module codes (header.logout, Person.gender.male, ...) are not in message_properties.
		// OpenMRS applies MessageFormat itself, so the result is returned as-is.
		message = lookupInOpenmrs(code, arguments, locale);
		if (message != null) {
			return message;
		}
		return MdrtbMessages.format(defaultMessage != null ? defaultMessage : code, arguments, locale);
	}
	
	/**
	 * Gives {@link MdrtbMessages} its way back to the database. Done lazily rather than in a field
	 * initialiser because {@code dao} is injected after construction.
	 */
	private MdrtbMessages messages() {
		if (!messageStoreWired) {
			mdrtbMessages.setStore((lang, code) -> {
                MessageProperty stored = messagePropertyDao.getMessageProperty(lang, code);
                return stored == null ? null : stored.getMessage();
            });
			messageStoreWired = true;
		}
		return mdrtbMessages;
	}
	
	@Override
	public int saveMessageProperties(Set<MessageProperty> messageProperties) {
		int inserted = messagePropertyDao.saveMessageProperties(messageProperties);
		if (inserted > 0) {
			resetMessageCache();
		}
		return inserted;
	}
	
	@Override
	public void resetMessageCache() {
		mdrtbMessages.resetCache();
	}
	
	@Override
	public List<MessageProperty> getAllMessageProperties() {
		return messagePropertyDao.getAllMessageProperties();
	}
	
	@Override
	public MessageProperty getMessageProperty(String lang, String code) {
		return messagePropertyDao.getMessageProperty(lang, code);
	}
	
	@Override
	public MessageProperty saveMessageProperty(MessageProperty messageProperty) {
		MessageProperty saved = messagePropertyDao.saveMessageProperty(messageProperty);
		resetMessageCache();
		return saved;
	}
	
	@Override
	public boolean deleteMessageProperty(MessageProperty messageProperty) {
		boolean deleted = messagePropertyDao.deleteMessageProperty(messageProperty);
		if (deleted) {
			resetMessageCache();
		}
		return deleted;
	}
	
	/**
	 * Delegates to OpenMRS's message source. Returns null (rather than the code) when nothing is
	 * registered.
	 */
	private String lookupInOpenmrs(String code, Object[] arguments, Locale locale) {
		try {
			String message = Context.getMessageSourceService().getMessage(code, arguments, null, locale);
			return StringUtils.isBlank(message) || code.equals(message) ? null : message;
		}
		catch (Exception e) {
			log.debug("OpenMRS message source could not resolve " + code, e);
			return null;
		}
	}
	
	/**
	 * {@code Context.getLocale()} throws Exception when there is no open session; Fallback to JVM
	 * default in that case.
	 */
	private static Locale resolveLocale() {
		try {
			Locale locale = Context.getLocale();
			return locale == null ? Locale.ENGLISH : locale;
		}
		catch (Exception e) {
			return Locale.ENGLISH;
		}
	}
}
