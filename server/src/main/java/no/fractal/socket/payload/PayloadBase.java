package no.fractal.socket.payload;

import no.fractal.socket.Client;
import no.fractal.socket.meta.Meta;

/**
 * Abstract class for all payload handlers. All payloads must implement a meta
 * type which describes the meta header for given payload.
 * 
 * @param <T> type of the meta header
 */
public abstract class PayloadBase<T extends Meta> {

	private Client client;

	public PayloadBase(Client client) {
		this.client = client;
	}

	protected Client getClient() {
		return client;
	}

	/**
	 * Returns the meta type for the payload
	 */
	public abstract Class<? extends Meta> getMetaType();

	/**
	 * Executes the payload instructions for this payload. The meta includes all
	 * data for reading the payload.
	 * 
	 * @param meta meta header for this payload
	 */
	public abstract void execute(Meta meta);

}
