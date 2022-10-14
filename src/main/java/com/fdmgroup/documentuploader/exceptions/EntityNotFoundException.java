package com.fdmgroup.documentuploader.exceptions;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * Exception thrown when an entity was not found for any reason.
 * 
 * @author Noah Anderson
 *
 */
public class EntityNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2782023738124726794L;
	
	/**
	 * Instantiates a new {@code EntityNotFoundException} for the type of
	 * {@code clazz} with the reasons of {@code searchParams}
	 * 
	 * @param clazz        the class type of an entity which was going to be saved
	 * @param searchParams the name and value of parameters that were not found when
	 *                     searching for an entity of type {@code clazz}
	 */
	public EntityNotFoundException(Class<?> clazz, String... searchParams) {
		super("No " + clazz.getSimpleName() + " was found for parameters " + toMap(searchParams));
	}
	
	private static Map<String, String> toMap(String... searchParams) {
		if (searchParams.length % 2 == 1) {
            throw new IllegalArgumentException("Invalid entries");
		}
		return IntStream.range(0, searchParams.length / 2)
				.map(i -> i * 2)
				.collect(HashMap::new, 
						(k, i) -> k.put(searchParams[i], searchParams[i + 1]),
						Map::putAll);
	}
}
