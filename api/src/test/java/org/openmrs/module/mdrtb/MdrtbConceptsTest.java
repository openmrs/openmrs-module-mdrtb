/**
 * 
 */
package org.openmrs.module.mdrtb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.api.MdrtbService;

/**
 * Verifies that the constants declared in {@link MdrtbConcepts} resolve to real concepts through
 * {@link MdrtbService#getConcept(String)}, which is the single entry point for concept lookup.
 * 
 * @author owais
 */
public class MdrtbConceptsTest extends MdrtbTestBase {
	
	private MdrtbService service;
	
	@Before
	public void setUp() throws Exception {
		super.initTestData();
		service = Context.getService(MdrtbService.class);
		service.resetConceptMapCache();
	}
	
	@Test
	public final void shouldResolveConstantToConcept() {
		Concept concept = service.getConcept(MdrtbConcepts.YES);
		
		assertNotNull(concept);
		assertEquals(yesConcept.getConceptId(), concept.getConceptId());
	}
	
	@Test
	public final void shouldInitializeLazyCollectionsOfResolvedConcept() {
		Concept concept = service.getConcept(MdrtbConcepts.YES);
		
		assertNotNull(concept.getDatatype());
		assertNotNull(concept.getNames());
		assertNotNull(concept.getAnswers());
	}
	
	@Test
	public final void shouldReturnNullForUnknownConcept() {
		assertNull(service.getConcept("NONEXISTENT MDRTB CONCEPT"));
	}
	
	@Test
	public final void shouldReturnNullForNullOrEmptyLookup() {
		assertNull(service.getConcept(null));
		assertNull(service.getConcept(""));
	}
	
	@Test
	public final void shouldReturnSameConceptOnSecondLookup() {
		Concept first = service.getConcept(MdrtbConcepts.YES);
		Concept second = service.getConcept(MdrtbConcepts.YES);
		
		// Second call is served from the ID cache, but must still be a live object of this session
		assertSame(first, second);
	}
	
	@Test
	public final void shouldStillResolveAfterCacheReset() {
		Concept before = service.getConcept(MdrtbConcepts.YES);
		
		service.resetConceptMapCache();
		Concept after = service.getConcept(MdrtbConcepts.YES);
		
		assertNotNull(after);
		assertEquals(before.getConceptId(), after.getConceptId());
	}
	
	@Test
	public final void shouldResolveConceptByUuid() {
		Concept byName = service.getConcept(MdrtbConcepts.YES);
		
		Concept byUuid = service.getConcept(byName.getUuid());
		
		assertEquals(byName.getConceptId(), byUuid.getConceptId());
	}
}
