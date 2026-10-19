package org.openmrs.module.mdrtb.web.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptName;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.web.dto.SimpleReportData;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.Searchable;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.util.OpenmrsClassLoader;

@Resource(name = RestConstants.VERSION_1 + "/mdrtb/metadata", supportedClass = SimpleReportData.class, supportedOpenmrsVersions = { "2.2.*,2.3.*,2.4.*,2.8.*" })
public class MetadataResourceController extends DataDelegatingCrudResource<SimpleReportData> implements Searchable {
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addSelfLink();
		description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
		description.addProperty("uuid");
		return description;
	}
	
	@Override
	public SimpleReportData newDelegate() {
		return new SimpleReportData();
	}
	
	@PropertyGetter("display")
	public String getDisplayString(SimpleReportData reportObject) {
		return "";
	}
	
	@Override
	public SimpleReportData getByUniqueId(String uuid) {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public SimpleReportData save(SimpleReportData delegate) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	protected void delete(SimpleReportData delegate, String reason, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public void purge(SimpleReportData delegate, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	protected PageableResult doSearch(RequestContext context) {
		String resource = context.getRequest().getParameter("resource");
		List<SimpleObject> objects = new ArrayList<>();
		if (resource.equalsIgnoreCase("concept")) {
			List<Concept> list = Context.getConceptService().getAllConcepts(null, true, false);
			for (Concept c : list) {
				SimpleObject so = new SimpleObject();
				so.add("uuid", c.getUuid());
				so.add("display", c.getDisplayString());
				// Attach names
				Collection<ConceptName> names = c.getNames();
				Collection<SimpleObject> nameSet = new ArrayList<>();
				for (ConceptName n : names) {
					SimpleObject nameObj = new SimpleObject();
					nameObj.add("uuid", n.getUuid());
					nameObj.add("locale", n.getLocale().getLanguage());
					nameObj.add("conceptNameType", n.getConceptNameType().name());
					nameSet.add(nameObj);
				}
				so.add("names", nameSet);
				// Attach answers
				Collection<ConceptAnswer> answers = c.getAnswers(false);
				List<String> answerSet = new ArrayList<>();
				for (ConceptAnswer a : answers) {
					answerSet.add(a.getUuid());
				}
				so.add("answers", answerSet);
				objects.add(so);
			}
		} 
		else if (resource.equalsIgnoreCase("message")) {
			String[] resources = {"messages.properties", "messages_ru.properties", "messages_tj.properties"};
            for (String s : resources) {
                InputStream stream = OpenmrsClassLoader.getInstance().getResourceAsStream(s);
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                    ArrayList<String> lines = reader.lines().collect(Collectors.toCollection(ArrayList::new));
                    Map<String, String> map = new HashMap<>();
                    for (String line : lines) {
                        String[] parts = line.split("\\=", 2);
                        if (parts.length == 2 && (parts[0].startsWith("mdrtb.") || parts[0].startsWith("commonlabtest."))) {
                            map.put(parts[0], parts[1]);

                        }
                    }
                    SimpleObject object = new SimpleObject();
                    object.add(s, map);
                    objects.add(object);
                } catch (IOException ignored) {
                }
            }
		}
		return new NeedsPaging<>(objects, context);
	}
}
