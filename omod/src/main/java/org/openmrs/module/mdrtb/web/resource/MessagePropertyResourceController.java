package org.openmrs.module.mdrtb.web.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.openmrs.api.MissingRequiredPropertyException;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MessageProperty;
import org.openmrs.module.mdrtb.api.MessagePropertyService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * CRUD over the {@code message_properties} table. GET /ws/rest/v1/messageproperty list GET
 * /ws/rest/v1/messageproperty?lang=ru filter by language GET /ws/rest/v1/messageproperty?q=gender
 * filter by code substring GET /ws/rest/v1/messageproperty/ru/mdrtb.yes one message POST
 * /ws/rest/v1/messageproperty upsert {lang, code, message} DELETE
 * /ws/rest/v1/messageproperty/ru/mdrtb.yes remove This is a plain Spring controller rather than a
 * webservices.rest because that one uses one path segment like {@code / resource}/{uuid}}. So
 * {@code / lang}/{code}} cannot be expressed through it. It also doesn't allow hard deletion which
 * is also required here.
 */
@Controller
@RequestMapping("/rest/" + RestConstants.VERSION_1 + "/mdrtb/messageproperty")
public class MessagePropertyResourceController {
	
	/**
	 * Lists messages, optionally filtered.
	 * 
	 * @param lang exact language match, optional
	 * @param q case-insensitive substring of the code, optional
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<SimpleObject> getMessageProperties(@RequestParam(value = "lang", required = false) String lang,
	        @RequestParam(value = "q", required = false) String q) {
		List<SimpleObject> results = new ArrayList<SimpleObject>();
		for (MessageProperty candidate : Context.getService(MessagePropertyService.class).getAllMessageProperties()) {
			if (lang != null && !lang.equalsIgnoreCase(candidate.getLang())) {
				continue;
			}
			if (q != null && !q.isEmpty()) {
				String code = candidate.getCode() == null ? "" : candidate.getCode();
				if (!code.toLowerCase(Locale.ROOT).contains(q.toLowerCase(Locale.ROOT))) {
					continue;
				}
			}
			results.add(asSimpleObject(candidate));
		}
		return results;
	}
	
	/**
	 * @return the one message for this language and code
	 * @throws ObjectNotFoundException (404) when it is not stored
	 */
	@RequestMapping(value = "/{lang}/{code:.+}", method = RequestMethod.GET)
	@ResponseBody
	public SimpleObject getMessageProperty(@PathVariable("lang") String lang, @PathVariable("code") String code) {
		MessageProperty stored = Context.getService(MessagePropertyService.class).getMessageProperty(lang, code);
		if (stored == null) {
			throw new ObjectNotFoundException();
		}
		return asSimpleObject(stored);
	}
	
	/**
	 * Creates or updates a message. The row is identified by the {@code lang} and {@code code} in
	 * the body, not by the URL: posting a body whose key already exists replaces that message,
	 * posting a new key inserts it.
	 * 
	 * @param body {@code "lang": "ru", "code": "mdrtb.yes", "message": "Да"}
	 * @return 201 with the stored message when it was created, 200 when an existing one was updated
	 * @throws MissingRequiredPropertyException when lang or code is absent from the body
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<SimpleObject> saveMessageProperty(@RequestBody SimpleObject body) {
		if (!body.containsKey("lang") || !body.containsKey("code")) {
			throw new MissingRequiredPropertyException("lang/code");
		}
		String lang = body.get("lang");
		String code = body.get("code");
		String message = body.get("message");
		boolean existed = Context.getService(MessagePropertyService.class).getMessageProperty(lang, code) != null;
		MessageProperty saved = Context.getService(MessagePropertyService.class).saveMessageProperty(new MessageProperty(lang, code, message));
		return new ResponseEntity<>(asSimpleObject(saved), existed ? HttpStatus.OK : HttpStatus.CREATED);
	}
	
	/**
	 * Permanently removes a message. There is no soft delete for this entity
	 */
	@RequestMapping(value = "/{lang}/{code:.+}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteMessageProperty(@PathVariable("lang") String lang, @PathVariable("code") String code) {
		if (!Context.getService(MessagePropertyService.class).deleteMessageProperty(new MessageProperty(lang, code, null))) {
			throw new ObjectNotFoundException();
		}
	}
	
	SimpleObject asSimpleObject(MessageProperty messageProperty) {
		return new SimpleObject().add("lang", messageProperty.getLang()).add("code", messageProperty.getCode())
		        .add("message", messageProperty.getMessage())
		        .add("display", messageProperty.getCode() + " (" + messageProperty.getLang() + ")");
	}
}
