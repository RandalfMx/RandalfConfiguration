/**
 * 
 */
package mx.randalf.configuration;


import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mx.randalf.configuration.exception.ConfigurationException;

/**
 * Questa classe viene utilizzata per la gestione dell'apertura deil file comuni
 * e la connessione con il database
 * 
 * @author Massimiliano Randazzo
 * 
 */
public class Configuration {

	/**
	 * Questa variabile viene utilizzata per gestire il log dell'applicazione
	 */
	private static Logger log = LogManager.getLogger(Configuration.class);

	/**
	 * Questa variabile viene utilizzata per gestire la lista dei parametri
	 * dell'applicazione
	 */
	private static Hashtable<String, Object> listaParametri = null;

	/**
	 * Questo metodo viene utilizzato per leggere le informazioni presenti nei
	 * files di Configurazione
	 * 
	 * @param pathProperties
	 *            Path dove si trovano i file di Configurazione
	 * @param fileConf
	 *            Lista dei file di configurazione da caricare
	 * @throws ConfigurationException
	 */
	public static void init(String pathProperties)
			throws ConfigurationException {
		File f = null;
		File[] fl = null;

		f = new File(pathProperties);
		if (f.exists()) {
			if (f.isDirectory()){
				fl = f.listFiles(new FileFilter() {
	
					public boolean accept(File f) {
						if (f.isFile()) {
							if (!f.getName().trim().toLowerCase().startsWith("._")
									&& (f.getName().trim().toLowerCase()
											.endsWith(".properties") || f.getName()
											.trim().toLowerCase()
											.endsWith(".properties.xml"))) {
								return true;
							} else {
								return false;
							}
						} else {
							return false;
						}
					}
				});
				for (int x = 0; x < fl.length; x++) {
					initParameter(fl[x]);
				}
			} else {
				initParameter(f);
			}
		} else {
			throw new ConfigurationException("La cartella ["
					+ f.getAbsolutePath() + "] non esiste");
		}
	}

	/**
	 * Questo metodo viene utilizzato per leggere le informazioni presenti nei
	 * files di Configurazione
	 * 
	 * @param pathProperties
	 *            Path dove si trovano i file di Configurazione
	 * @param fileConf
	 *            Lista dei file di configurazione da caricare
	 * @throws ConfigurationException Eccezione di configurazione
	 */
	public static void init(String pathProperties, String fileConf) throws ConfigurationException {
		init(pathProperties, fileConf.split(","));
	}

	/**
	 * Questo metodo viene utilizzato per leggere le informazioni presenti nei
	 * files di Configurazione
	 * 
	 * @param pathProperties
	 *            Path dove si trovano i file di Configurazione
	 * @param fileConf
	 *            Lista dei file di configurazione da caricare
	 * @throws ConfigurationException Eccezione di configurazione
	 */
	public static void init(String pathProperties, String[] fileConf) throws ConfigurationException {
		try {
			for (int x = 0; x < fileConf.length; x++) {
				initParameter(pathProperties, fileConf[x]);
			}
		} catch (ConfigurationException e) {
			throw e;
		}
	}

	/**
	 * Questo metodo viene utilizzato per inizializzare i paramepri del file
	 * Gestionale
	 * 
	 * @param pathProperties
	 *            Path relativa alla posizione dei file di Properties
	 * @param fileConf
	 *            Nome deil file di configurazione
	 * @throws ConfigurationException Eccezione di configurazione
	 */
	private static void initParameter(String pathProperties, String fileConf) throws ConfigurationException {
		File f = null;

		try {
			if (!pathProperties.endsWith(File.separator)) {
				pathProperties += File.separator;
			}
			f = new File(pathProperties + fileConf);
			if (f.exists()) {
				initParameter(f);
			} else
				throw new ConfigurationException("Il File " + f.getAbsolutePath() + " non esiste");
		} catch (ConfigurationException e) {
			throw e;
		}
	}

	/**
	 * Questo metodo viene utilizzato per inizializzare i paramepri del file
	 * Gestionale
	 * 
	 * @param filesConf
	 *            Nome deil file di configurazione
	 * @throws ConfigurationException Eccezione di configurazione
	 */
	@SuppressWarnings("unchecked")
	private static void initParameter(File fileConf) throws ConfigurationException {
		Properties prop = null;
		FileInputStream fis = null;
		int pos = 0;
//		String key = "";
		String key2 = "";
		String value = "";
		Hashtable<String, String> values = null;
		Vector<String> values2 = null;

		try {
			prop = new Properties();
			fis = new FileInputStream(fileConf);
			if (fileConf.getName().toLowerCase().endsWith(".xml")) {
				prop.loadFromXML(fis);
			} else {
				prop.load(fis);
			}

			if (listaParametri == null)
				listaParametri = new Hashtable<String, Object>();
			List<String> e = new ArrayList<String>();
			for (Object key:prop.keySet()){
				e.add((String) key);
			}
			Collections.sort(e);
			for (String key:e){
//			for (Enumeration<Object> e = prop.keys(); e.hasMoreElements();) {
//				key = (String) e.nextElement();
				value = prop.getProperty(key, "");
				if (!value.equals("")){
					if (key.contains("[")){
						pos = key.indexOf("[");
						key2 = key.substring(pos+1, key.length()-1).trim();
						key = key.substring(0, pos).trim();
						if (listaParametri.get(key)==null){
							values = new Hashtable<String, String>();
						} else {
							values = (Hashtable<String, String>) listaParametri.get(key);
						}
						values.put(key2, value);
						listaParametri.put(key, values);
					} else if (key.contains("(")){
						pos = key.indexOf("(");
						key = key.substring(0, pos).trim();
						if (listaParametri.get(key)==null){
							values2 = new Vector<String>();
						} else {
							values2 = (Vector<String>) listaParametri.get(key);
						}
						values2.add(value);
						listaParametri.put(key, values2);
					} else {
						listaParametri.put(key, value);
					}
				}
			}
		} catch (NumberFormatException e) {
			log.error(e.getMessage(),e);
			throw new ConfigurationException(e.getMessage(), e);
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(),e);
			throw new ConfigurationException(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(),e);
			throw new ConfigurationException(e.getMessage(), e);
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				log.error(e.getMessage(),e);
				throw new ConfigurationException(e.getMessage(), e);
			}
		}
	}

	/**
	 * Metodo viene utilizzato per leggere le informazioni relative ad una determinata chiave
	 * 
	 * @param key Chiave di ricerca
	 * @return Valore trovato
	 * @throws ConfigurationException Eccezione di configurazione
	 */
	public static String getValue(String key) throws ConfigurationException{
		if (listaParametri != null && listaParametri.get(key)!= null){
			if (listaParametri.get(key).getClass().getName().equals(String.class.getName())){
				return (String) listaParametri.get(key);
			} else {
				throw new ConfigurationException("Variabile con valori multipli");
			}
		} else {
			return null;
		}
	}

	/**
	 * Metodo viene utilizzato per leggere le informazioni relative ad una determinata chiave e nel caso sia vuota viene utilizzato il
	 * valore di default indicato
	 * 
	 * @param key Chiave di ricerca
	 * @param defaults Valore di default da asegnare
	 * @return Valore trovato
	 * @throws ConfigurationException Eccezione di configurazione
	 */
	public static String getValueDefault(String key, String defaults) throws ConfigurationException{
		String result = null;
		
		result = getValue(key);
		if (result== null){
			result = defaults;
		}
		return result;
	}

	/**
	 * Metodo viene utilizzato per leggere le informazioni relative ad una determinata chiave
	 * 
	 * @param key Chiave di ricerca
	 * @param key2 Chiave di ricerca nei valori multipli
	 * @return Valore trovato
	 * @throws ConfigurationException Eccezione di configurazione
	 */
	@SuppressWarnings("unchecked")
	public static String getValue(String key, String key2) throws ConfigurationException{
		Hashtable<String, String> values = null;
		
		values = (Hashtable<String, String>) getValues(key);
		if (values != null){
			return values.get(key2);
		} else {
			return null;
		}
	}

	/**
	 * Metodo utulizzato per leggere le informazioni relative ad una determinata chiave
	 * 
	 * @param key Chiave di ricerca
	 * @return Valori multipli
	 * @throws ConfigurationException Eccezione di configurazione
	 */
	public static Object getValues(String key) throws ConfigurationException{
		if (listaParametri.get(key)!= null){
			if (listaParametri.get(key).getClass().getName().equals(String.class.getName())){
				throw new ConfigurationException("Variabile non contiene valori multipli");
			} else {
				return listaParametri.get(key);
			}
		} else {
			return null;
		}
	}

	/**
	 * Questo metodo viene utilizzato per verificare se è già stata eseguita l'inizializzazione del progetto
	 * 
	 * @return Verifica lo stato di inizializzazione
	 */
	public static boolean isInizialize(){
		return (listaParametri != null);
	}
}
