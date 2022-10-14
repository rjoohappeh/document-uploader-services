package com.fdmgroup.documentuploader.exceptions.handler;

import java.util.HashMap;
import java.util.Map;

import com.fdmgroup.documentuploader.exceptions.EntityCouldNotBeSavedException;
import com.fdmgroup.documentuploader.exceptions.InvalidTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fdmgroup.documentuploader.exceptions.EntityNotFoundException;

import javax.validation.ConstraintViolationException;

/**
 * Contains methods which are custom implementations to handle different types
 * of exceptions.
 * 
 * @author Noah Anderson
 * 
 */
@ControllerAdvice
public class GlobalControllerExceptionHandler {

	/**
	 * Custom exception handler for thrown {@link MethodArgumentNotValidException}
	 * instances.
	 * 
	 * @param e the thrown exception
	 * @return {@link ResponseEntity} encapsulating a {@link Map} containing
	 *         key-value pairs representing the names of the fields that were not
	 *         valid and the messages associated with the invalid fields
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException e) {
		Map<String, String> errors = new HashMap<>();
		e.getBindingResult().getAllErrors().forEach(error -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(errors);
	}

	/**
	 * Custom exception handler for all thrown custom
	 * {@link EntityNotFoundException}, {@link EntityCouldNotBeSavedException} and
	 * {@link InvalidTokenException} objects.
	 * 
	 * @param e the thrown exception
	 * @return {@link ResponseEntity} encapsulating the response object
	 */
	@ExceptionHandler(value = { EntityNotFoundException.class, EntityCouldNotBeSavedException.class,
			InvalidTokenException.class })
	public ResponseEntity<Object> handleCustomExceptions(Exception e) {
		String message = e.getMessage();
		if (e instanceof EntityCouldNotBeSavedException || e instanceof InvalidTokenException) {
			return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(message);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).body(message);
	}

	/**
	 * Custom exception handler for all thrown custom
	 * {@link ConstraintViolationException} objects.
	 * 
	 * @param e the thrown exception
	 * @return {@link ResponseEntity} encapsulating the response object.\
	 */
	@ExceptionHandler(value = { ConstraintViolationException.class })
	public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException e) {
		return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(e.getMessage());
	}
}
