package org.openmrs.module.mdrtb.regimen.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base Implementation of the RegimenService API
 */
@Transactional
@Service
public class RegimenServiceImpl extends BaseOpenmrsService implements RegimenService {

	protected static Log log = LogFactory.getLog(RegimenServiceImpl.class);
	

}