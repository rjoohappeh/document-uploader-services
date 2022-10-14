package com.fdmgroup.documentuploader.exceptions;

/**
 * Exception thrown when a service class receives an expired token as as
 * argument.
 *
 * @author Noah Anderson
 */
public class InvalidTokenException extends RuntimeException {

	/**
	 * Instantiates a new {@code InvalidTokenException} for the type of
	 * {@code clazz}.
	 * 
	 * @param clazz the type of Token which caused the exception to be thrown
	 * @param token the String value representing the token
	 */
	public InvalidTokenException(Class<?> clazz, String token) {
		super("The " + clazz.getSimpleName() + " is expired or invalid: token : " + token);
	}
}
