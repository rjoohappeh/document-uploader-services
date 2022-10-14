package com.fdmgroup.documentuploader.exceptions;

/**
 * Exception thrown when an entity could not be saved for any reason
 * 
 * @author Noah Anderson
 *
 */
public class EntityCouldNotBeSavedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4301278828224869227L;

	/**
	 * Instantiates a new {@code EntityCouldNotBeSavedException} for the type of
	 * {@code clazz} with the given {@code reason}.
	 * 
	 * @param clazz  the class type of an entity which was going to be saved
	 * @param reason the reason the entity could not be saved
	 */
	public EntityCouldNotBeSavedException(Class<?> clazz, String reason) {
		super("The " + clazz.getSimpleName() + " could not be saved for the following reason: " + reason);
	}
	
}
