package com.fdmgroup.documentuploader.util;

import java.util.Optional;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Singleton Class which contains static methods related to the retrieval and management
 * of {@link ServletRequest} objects.
 *
 * @author Noah Anderson
 */
public class ServletRequestUtil {

	private ServletRequestUtil() {
		super();
	}

	/**
	 * Gets the current {@link HttpServletRequest}.
	 *
	 * @return {@code empty} {@link Optional} if there is currently no
	 *         {@code HttpServletRequest}. Otherwise, an {@code Optional} wrapping
	 *         the current {@code HttpServletRequest} is returned.
	 */
	public static Optional<HttpServletRequest> getCurrentHttpRequest() {
		return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
				.filter(ServletRequestAttributes.class::isInstance)
				.map(ServletRequestAttributes.class::cast)
				.map(ServletRequestAttributes::getRequest);
	}
}
