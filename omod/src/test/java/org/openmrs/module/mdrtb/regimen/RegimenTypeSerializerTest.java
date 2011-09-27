package org.openmrs.module.mdrtb.regimen;

import java.util.List;

import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class RegimenTypeSerializerTest extends BaseModuleContextSensitiveTest {
	
	/**
	 */
	@Test
	public void evaluate_shouldSerialize() throws Exception {
		
		List<RegimenType> allTypes = RegimenUtils.getRegimenTypes();
		System.out.println("allTypes: " + allTypes);
		
	}
}