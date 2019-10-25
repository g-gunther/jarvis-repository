package com.gguproject.jarvis.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.codestory.http.Context;
import net.codestory.http.filters.Filter;
import net.codestory.http.filters.PayloadSupplier;
import net.codestory.http.payload.Payload;

public class LogRequestFilter implements Filter {
	private static final long serialVersionUID = -2152765172980722589L;
	private static Logger LOGGER = LoggerFactory.getLogger(LogRequestFilter.class);

	@Override
	public Payload apply(String uri, Context context, PayloadSupplier nextFilter) throws Exception {
		LOGGER.debug("[{}] - {}", context.method(), uri);
		return nextFilter.get();
	}
}
