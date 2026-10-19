/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.mdrtb;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

/**
 * A single localised UI string, keyed by language and message code. This is the database-backed
 * replacement for the messages*.properties bundles that OpenMRS reads into its
 * {@code DefaultMessageSourceService} at startup; see
 * {@link org.openmrs.module.mdrtb.api.MdrtbService#getMessage(String, Object[], String)} for the
 * lookup path and {@link MdrtbActivator} for how the table is seeded from those bundles.
 * <p>
 * {@code lang} holds the bundle suffix rather than a full {@link java.util.Locale} — {@code en},
 * {@code ru}, {@code tj} — matching the {@code <lang>} values declared in the module's config.xml.
 * The column accepts any language tag, so rows can be added for a locale the module ships no bundle
 * for.
 * <p>
 * Please note that a corresponding table schema must be created in liquibase.xml.
 */
@Entity(name = "mdrtb.MessageProperty")
@Table(name = "message_properties")
@IdClass(MessageProperty.MessagePropertyId.class)
public class MessageProperty implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/** Maximum persisted length of {@link #code}; see the liquibase changeset for why it is 190. */
	public static final int MAX_CODE_LENGTH = 190;
	
	/** Maximum persisted length of {@link #lang}. */
	public static final int MAX_LANG_LENGTH = 20;
	
	@Id
	@Column(name = "lang", length = MAX_LANG_LENGTH, nullable = false)
	private String lang;
	
	@Id
	@Column(name = "code", length = MAX_CODE_LENGTH, nullable = false)
	private String code;
	
	@Column(name = "message", length = 65535)
	private String message;
	
	public MessageProperty() {
	}
	
	public MessageProperty(String lang, String code, String message) {
		this.lang = lang;
		this.code = code;
		this.message = message;
	}
	
	public String getLang() {
		return lang;
	}
	
	public void setLang(String lang) {
		this.lang = lang;
	}
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof MessageProperty)) {
			return false;
		}
		MessageProperty that = (MessageProperty) other;
		return equal(lang, that.lang) && equal(code, that.code);
	}
	
	@Override
	public int hashCode() {
		return (lang == null ? 0 : lang.hashCode()) * 31 + (code == null ? 0 : code.hashCode());
	}
	
	@Override
	public String toString() {
		return "MessageProperty[" + lang + ", " + code + "]";
	}
	
	private static boolean equal(String one, String two) {
		return one == null ? two == null : one.equals(two);
	}
	
	/**
	 * Composite primary key (lang + code) for {@link MessageProperty}. Hibernate requires this to
	 * be a public class with a no-arg constructor, equals and hashCode.
	 */
	public static class MessagePropertyId implements Serializable {
		
		private static final long serialVersionUID = 1L;
		
		private String lang;
		
		private String code;
		
		public MessagePropertyId() {
		}
		
		public MessagePropertyId(String lang, String code) {
			this.lang = lang;
			this.code = code;
		}
		
		public String getLang() {
			return lang;
		}
		
		public void setLang(String lang) {
			this.lang = lang;
		}
		
		public String getCode() {
			return code;
		}
		
		public void setCode(String code) {
			this.code = code;
		}
		
		@Override
		public boolean equals(Object other) {
			if (this == other) {
				return true;
			}
			if (!(other instanceof MessagePropertyId)) {
				return false;
			}
			MessagePropertyId that = (MessagePropertyId) other;
			return MessageProperty.equal(lang, that.lang) && MessageProperty.equal(code, that.code);
		}
		
		@Override
		public int hashCode() {
			return (lang == null ? 0 : lang.hashCode()) * 31 + (code == null ? 0 : code.hashCode());
		}
	}
}
