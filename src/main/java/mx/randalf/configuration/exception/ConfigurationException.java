/**
 * 
 */
package mx.randalf.configuration.exception;

/**
 * Classe utilizzata per indicare le eccezioni relative alle configurazioni
 * 
 * @author massi
 *
 */
public class ConfigurationException extends Exception {

	/**
	 * Questa variabile viene utilizzata per indicare il Serial Version UID
	 */
	private static final long serialVersionUID = 852042854020539453L;

	/**
	 * Costruttore
	 * 
	 * @param message Messaggio 
	 */
	public ConfigurationException(String message) {
		super(message);
	}

	/**
	 * Costruttore
	 * 
	 * @param message Messaggio
	 * @param cause Causale del messaggio
	 */
	public ConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

}
