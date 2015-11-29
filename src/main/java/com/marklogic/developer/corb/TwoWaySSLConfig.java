package com.marklogic.developer.corb;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class TwoWaySSLConfig extends AbstractSSLConfig {
    
	private static final Logger LOG = Logger.getLogger(TwoWaySSLConfig.class.getSimpleName());
    public static final String SSL_CIPHER_SUITES = "SSL-CIPHER-SUITES";
    public static final String SSL_ENABLED_PROTOCOLS = "SSL-ENABLED-PROTOCOLS";
    public static final String SSL_KEYSTORE = "SSL-KEYSTORE";
    public static final String SSL_KEY_PASSWORD = "SSL-KEY-PASSWORD";
    public static final String SSL_KEYSTORE_PASSWORD = "SSL-KEYSTORE-PASSWORD";
    public static final String SSL_KEYSTORE_TYPE = "SSL-KEYSTORE-TYPE";
    public static final String SSL_PROPERTIES_FILE = "SSL-PROPERTIES-FILE"; 

	/**
	 * @return acceptable list of cipher suites
	 */
	@Override
	public String[] getEnabledCipherSuites() {
        if (properties != null) {
            String cipherSuites = properties.getProperty(SSL_CIPHER_SUITES);
            if (cipherSuites != null && !cipherSuites.isEmpty()) {
                String[] cipherSuitesList = cipherSuites.split(",");
                LOG.log(Level.INFO, "Using cipher suites: {0}", cipherSuitesList);
                return cipherSuitesList;
            }
        }
		return null;
	}

	/**
	 * @return list of acceptable protocols
	 */
	@Override
	public String[] getEnabledProtocols() {
        if (properties != null) {
            String enabledProtocols = properties.getProperty(SSL_ENABLED_PROTOCOLS);
            if (enabledProtocols != null && !enabledProtocols.isEmpty()) {
                String[] enabledProtocolsList = enabledProtocols.split(",");
                LOG.log(Level.INFO, "Using enabled protocols: {0}", enabledProtocolsList);
                return enabledProtocolsList;
            }
        }
		return null;
	}

	private String getRequiredProperty(String propertyName) {
		String property = getProperty(propertyName);
		if (property != null && property.length() != 0) {
			return property;
		} else {
			throw new IllegalStateException("Property " + propertyName + " is not provided and is required");
		}
	}

	/**
	 * loads properties file and adds it to properties 
	 * @throws IOException
	 */
	protected void loadPropertiesFile() throws IOException {
		String securityFileName = getProperty(SSL_PROPERTIES_FILE);
		if (securityFileName != null && securityFileName.trim().length() != 0) {
			File f = new File(securityFileName);
			if (f.exists() && !f.isDirectory()) {
				LOG.log(Level.INFO, "Loading SSL configuration file {0} from filesystem", securityFileName);
				InputStream is = null;
				try {
					is = new FileInputStream(f);
                    if (properties == null){
                        properties = new Properties();
                    }
					properties.load(is);
				} catch (IOException e) {
					LOG.severe("Error loading ssl properties file");
					throw new RuntimeException(e);
				} finally {
					if (is != null) {
						is.close();
					}
				}
			} else {
				throw new IllegalStateException("Unable to load " + securityFileName);
			}
		} else {
			LOG.info(MessageFormat.format("Property {0} not present", SSL_PROPERTIES_FILE));
		}
	}
		
	@Override
	public SSLContext getSSLContext() throws NoSuchAlgorithmException, KeyManagementException {
		try {
			loadPropertiesFile();
		} catch (IOException e1) {
			LOG.severe(MessageFormat.format("Error loading {0}", SSL_PROPERTIES_FILE));
			throw new RuntimeException(e1);
		}

		String sslkeyStore = getRequiredProperty(SSL_KEYSTORE);
		String sslkeyStorePassword = getRequiredProperty(SSL_KEYSTORE_PASSWORD);
		String sslkeyPassword = getRequiredProperty(SSL_KEY_PASSWORD);
		String sslkeyStoreType = getRequiredProperty(SSL_KEYSTORE_TYPE);
		// decrypting password values
		if (decrypter != null) {
			if (sslkeyStorePassword != null) {
				sslkeyStorePassword = decrypter.decrypt(SSL_KEYSTORE_PASSWORD, sslkeyStorePassword);
			}
			if (sslkeyPassword != null) {
				sslkeyPassword = decrypter.decrypt(SSL_KEY_PASSWORD, sslkeyPassword);
			}
		} else {
			LOG.info("Decrypter is not initialized");
		}
		try {
			// adding default trust store
			TrustManager[] trust = null;

			// adding custom key store
			KeyStore clientKeyStore = KeyStore.getInstance(sslkeyStoreType);
			clientKeyStore.load(new FileInputStream(sslkeyStore), sslkeyStorePassword.toCharArray());
			// using SunX509 format
			KeyManagerFactory keyManagerFactory = KeyManagerFactory
					.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(clientKeyStore, sslkeyPassword.toCharArray());
			KeyManager[] key = keyManagerFactory.getKeyManagers();
			SSLContext sslContext = SSLContext.getInstance("TLSv1");
			sslContext.init(key, trust, null);
			return sslContext;
		} catch (Exception e) {
			throw new IllegalStateException("Unable to create SSLContext in TwoWaySSLOptions", e);
		}
	}
}