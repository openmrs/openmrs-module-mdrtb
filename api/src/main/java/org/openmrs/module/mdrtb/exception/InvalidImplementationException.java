package org.openmrs.module.mdrtb.exception;

/**
 * Thrown when a service receives a domain object backed by an unsupported implementation.
 */
public class InvalidImplementationException extends MdrtbAPIException {
	
	private static final long serialVersionUID = 1L;
	
	public InvalidImplementationException(Class<?> implementationType) {
		super("Not a valid implementation for the given service. " + implementationType.getSimpleName());
	}
}
