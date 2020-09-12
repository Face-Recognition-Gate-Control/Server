package no.fractal.socket.payload;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Class loader for classes of type <T> or <? extends T> which must me
 * instatiated in runtime. A class is stored with a name, and a new instance is
 * retrieved by the same name. The constructor types for the given <T> must be
 * provided to constructor, so only one class type is possible to store/load. So
 * child classes must have the same constructor as its parent.
 * 
 * @param <T>
 */
public class PayloadLoader<T> {

	private static Logger LOGGER = Logger.getLogger(PayloadLoader.class.getName());

	/**
	 * Holds all registered classes by name
	 */
	private Map<String, Class<?>> classes = new HashMap<String, Class<?>>();

	/**
	 * Argument types for the generic class type
	 */
	private Class<?>[] constructorTypes;

	public PayloadLoader() {
	}

	public PayloadLoader(Class<?>[] construcorTypes) {
		this.constructorTypes = construcorTypes;
	}

	/**
	 * Adds a class to the instatiator by name, this name is referenced when pulling
	 * a new instance of it. The name can not be null, and class must be of ?
	 * extends T.
	 * 
	 * @param name      storage name
	 * @param classtype class type to add
	 */
	public void addSubClass(String name, Class<? extends T> classtype) {
		if (name == null)
			throw new IllegalArgumentException("Name can not be null");
		this.classes.put(name, classtype);
	}

	/**
	 * Adds a class to the instatiator by name, this name is referenced when pulling
	 * a new instance of it. The name can not be null, and class must be of type T.
	 * 
	 * @param name      storage name
	 * @param classtype class type to add
	 */
	public void addClass(String name, Class<T> classtywpe) {
		if (name == null)
			throw new IllegalArgumentException("Name can not be null");
		this.classes.put(name, classtywpe);
	}

	/**
	 * Returns a new instance of the class stored with the given name, with
	 * parameters injected. If the store does not have class of type, return null.
	 * If an exception occurs, returns null.
	 * 
	 * @param name   key name for the class
	 * @param params constructor parameters for the class
	 * @return returns class T or null
	 */
	@SuppressWarnings({ "unchecked" })
	public T getnewInstance(String name, Object[] params) {
		try {
			return (T) this.classes.get(name).getConstructor(constructorTypes).newInstance(params);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			LOGGER.warning("Could not create class stored with name: " + name + "\n" + e.getMessage());
		}
		return null;
	}

}
