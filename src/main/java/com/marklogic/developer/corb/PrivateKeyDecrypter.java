/*
 * Copyright 2005-2015 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * The use of the Apache License does not indicate that this project is
 * affiliated with the Apache Software Foundation.
 */
package com.marklogic.developer.corb;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.xml.bind.DatatypeConverter;
import static com.marklogic.developer.corb.util.IOUtils.closeQuietly;
import static com.marklogic.developer.corb.util.StringUtils.isBlank;
import static com.marklogic.developer.corb.util.StringUtils.isNotBlank;
import static com.marklogic.developer.corb.util.StringUtils.trim;

public class PrivateKeyDecrypter extends AbstractDecrypter {

	private String algorithm = null;
	// option 1 - generate keys with java
	// java -cp marklogic-corb-2.1.*.jar
	// com.marklogic.developer.corb.PrivateKeyDecrypter gen-keys
	// /path/to/private.key /path/to/public.key
	// java -cp marklogic-corb-2.1.*.jar
	// com.marklogic.developer.corb.PrivateKeyDecrypter encrypt
	// /path/to/public.key clearText
	//
	// option 2 - generate keys with openssl
	// openssl genrsa -out private.pem 1024
	// openssl pkcs8 -topk8 -nocrypt -in private.pem -out private.pkcs8.key
	// openssl rsa -in private.pem -pubout > public.key
	// echo "password or uri" | openssl rsautl -encrypt -pubin -inkey public.key |
	// base64
	//
	// option 3 - ssh-keygen
	// ssh-keygen (ex: gen key as id_rsa)
	// openssl pkcs8 -topk8 -nocrypt -in id_rsa -out id_rsa.pkcs8.key
	// openssl rsa -in id_rsa -pubout > public.key
	// echo "password or uri" | openssl rsautl -encrypt -pubin -inkey public.key |
	// base64
	private PrivateKey privateKey = null;

	protected static final Logger LOG = Logger.getLogger(PrivateKeyDecrypter.class.getSimpleName());

	@Override
	protected void init_decrypter() throws IOException, ClassNotFoundException {
		algorithm = getProperty("PRIVATE-KEY-ALGORITHM");
		if (isBlank(algorithm)) {
			algorithm = "RSA";
		}

		String filename = trim(getProperty("PRIVATE-KEY-FILE"));
		if (isNotBlank(filename)) {
			InputStream is = null;
			try {
				is = Manager.class.getResourceAsStream("/" + filename);
				if (is != null) {
					LOG.log(Level.INFO, "Loading private key file {0} from classpath", filename);
				} else {
					File f = new File(filename);
					if (f.exists() && !f.isDirectory()) {
						LOG.log(Level.INFO, "Loading private key file {0} from filesystem", filename);
						is = new FileInputStream(f);
					} else {
						throw new IllegalStateException("Unable to load " + filename);
					}
				}
				byte[] keyAsBytes = toByteArray(is);

				KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
				try {
					privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(keyAsBytes));
				} catch (Exception exc) {
					LOG.info("Attempting to decode private key with base64. Ignore this message if keys are generated with openssl");
					String keyAsString = new String(keyAsBytes);
					// remove the begin and end key lines if present.
					keyAsString = keyAsString.replaceAll("[-]+(BEGIN|END)[A-Z ]*KEY[-]+", "");

					privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(DatatypeConverter.parseBase64Binary(keyAsString)));
				}
			} catch (Exception exc) {
				LOG.log(Level.SEVERE, "Problem initializing PrivateKeyDecrypter", exc);
			} finally {
                closeQuietly(is);
			}
		} else {
			LOG.severe("PRIVATE-KEY-FILE property must be defined");
		}
	}

	private static void copy(InputStream input, OutputStream output) throws IOException {
		byte[] buffer = new byte[1024];
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
		}
	}

	private static byte[] toByteArray(final InputStream input) throws IOException {
		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		copy(input, output);
		return output.toByteArray();
	}

	@Override
	protected String doDecrypt(String property, String value) {
		String dValue = null;
		if (privateKey != null) {
			try {
				final Cipher cipher = Cipher.getInstance(algorithm);
				cipher.init(Cipher.DECRYPT_MODE, privateKey);
				dValue = new String(cipher.doFinal(DatatypeConverter.parseBase64Binary(value)));
			} catch (Exception exc) {
				LOG.log(Level.INFO, "Cannot decrypt {0}. Ignore if clear text.", property);
			}
		}
		return dValue == null ? value : dValue.trim();
	}

	private static final String usage1 = "Generate Keys (Note: default algorithm: RSA, default key-length: 1024):\n java -cp marklogic-corb-2.1.*.jar com.marklogic.developer.corb.PrivateKeyDecrypter gen-keys /path/to/private.key /path/to/public.key RSA 1024";
	private static final String usage2 = "Encrypt (Note: default algorithm: RSA):\n java -cp marklogic-corb-2.1.*.jar com.marklogic.developer.corb.PrivateKeyDecrypter encrypt /path/to/public.key clearText RSA";

	private static void generateKeys(String... args) throws Exception {
		String algorithm = "RSA";
		int length = 1024;
		String privateKeyPathName = null;
		String publicKeyPathName = null;

		if (args.length > 1 && isNotBlank(args[1])) {
			privateKeyPathName = args[1].trim();
		}
		if (args.length > 2 && isNotBlank(args[2])) {
			publicKeyPathName = args[2].trim();
		}
		if (args.length > 3 && isNotBlank(args[3])) {
			algorithm = args[3].trim();
		}
		if (args.length > 4 && isNotBlank(args[4])) {
			length = Integer.parseInt(args[4].trim());
		}
		if (privateKeyPathName == null || publicKeyPathName == null) {
			System.err.println(usage1);
			return;
		}

		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
		keyPairGenerator.initialize(length);
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		PrivateKey privateKey = keyPair.getPrivate();
		PublicKey publicKey = keyPair.getPublic();

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(new File(privateKeyPathName));
			fos.write(privateKey.getEncoded());
			fos.close();
			System.out.println("Generated private key: " + privateKeyPathName);

			fos = new FileOutputStream(new File(publicKeyPathName));
			fos.write(publicKey.getEncoded());
			fos.close();
			System.out.println("Generated public key: " + publicKeyPathName);
		} finally {
            closeQuietly(fos);
		}
	}

	private static void encrypt(String... args) throws Exception {
		String algorithm = "RSA";
		String publicKeyPathName = null;
		String clearText = null;
		if (args.length > 1 && isNotBlank(args[1])) {
			publicKeyPathName = args[1].trim();
		}
		if (args.length > 2 && isNotBlank(args[2])) {
			clearText = args[2].trim();
		}
		if (args.length > 3 && isNotBlank(args[3])) {
			algorithm = args[3].trim();
		}
		if (publicKeyPathName == null || clearText == null) {
			System.err.println(usage2);
			return;
		}
		
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(publicKeyPathName);
			X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(toByteArray(fis));
			Cipher cipher = Cipher.getInstance(algorithm);
			cipher.init(Cipher.ENCRYPT_MODE, KeyFactory.getInstance(algorithm).generatePublic(x509EncodedKeySpec));
			String encryptedText = DatatypeConverter.printBase64Binary(cipher.doFinal(clearText.getBytes("UTF-8")));
			System.out.println("Input: " + clearText + "\nOutput: " + encryptedText);
		} finally {
            closeQuietly(fis);
		}
	}

	// key generator
	public static void main(String[] args) throws Exception {
		String method = (args != null && args.length > 0) ? args[0].trim() : "";
		if (method.equals("gen-keys")) {
			generateKeys(args);
		} else if (method.equals("encrypt")) {
			encrypt(args);
		} else {
			System.out.println(usage1 + "\n" + usage2);
		}
	}
}
