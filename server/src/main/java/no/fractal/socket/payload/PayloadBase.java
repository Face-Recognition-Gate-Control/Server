package no.fractal.socket.payload;

import no.fractal.socket.Client;
import no.fractal.util.Parser;
import no.fractal.socket.meta.Meta;

/**
 * Abstract class for all payload handlers. All payloads must implement a meta
 * type which describes the meta header for given payload.
 * 
 * @param <T> type of the meta header
 */
public abstract class PayloadBase<T extends Meta> {

	/**
	 * Client owning the payload
	 */
	private Client client;

	/**
	 * Parser for the header meta
	 */
	private Parser<T> metaParser;

	public PayloadBase(Client client, Parser<T> metaParser) {
		this.client = client;
		this.metaParser = metaParser;
	}

	/**
	 * Returns the client for the payload
	 */
	protected Client getClient() {
		return client;
	}

	/**
	 * Returns the parsed meta for this payload
	 * 
	 * @param type the type for the meta
	 * @return parsed meta
	 */
	protected T getMeta(Class<T> type) {
		return this.metaParser.parse(type);
	}

	/**
	 * Executes the payload instructions for this payload. The meta includes all
	 * data for reading the payload.
	 * 
	 * @param meta meta header for this payload
	 */
	public abstract void execute();

}
