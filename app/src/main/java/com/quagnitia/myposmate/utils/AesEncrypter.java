package com.quagnitia.myposmate.utils;

import android.util.Log;

import org.apache.commons.codec.binary.Base64;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class AesEncrypter {
	byte[] salt = { (byte) 0xc7, (byte) 0x73, (byte) 0x21, (byte) 0x8c, (byte) 0x7e, (byte) 0xc8, (byte) 0xee,
			(byte) 0x99 };
	private final String CIPHER_NAME = "AES/ECB/PKCS5Padding";
	private final String ALGORITHM_NAME = "AES";
	// keySizes 128, 192, 256
	// do not change this key
	/* do not change this key */
	String username = "zoTpake@2398XXs3";
	/* do not change this key */
	String password = "Payitnz@!194XX111111111111";
	private SecretKeySpec secretKeySpec;

	// private final String CIPHER_NAME = "DES/ECB/PKCS5Padding";
	// private final String ALGORITHM_NAME = "DES"; // keySize 56
	// private final String CIPHER_NAME = "DESede/ECB/PKCS5Padding";
	// private final String ALGORITHM_NAME = "DESede"; // keySize 168
	static AesEncrypter instance;

	public static AesEncrypter getInstance() {
		if (instance == null) {
			instance = new AesEncrypter();
		}

		return instance;
	}

	public static void main(String args[]) {
		try {

			System.out.println("------------" + getInstance().encrypt("Hello"));
			System.out.println("------------" + getInstance().decrypt(getInstance().encrypt("Hello")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public AesEncrypter() {
		// TODO Auto-generated constructor stub

	}
	public String decryptAndDecode(String text) {
 
		Cipher cipher;
		try {
			byte[] key;

			key = (salt + username + password).getBytes("UTF-8");

			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16); // use only first 128 bit

			secretKeySpec = new SecretKeySpec(username.getBytes(), "AES");
			cipher = Cipher.getInstance(CIPHER_NAME);

			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
			byte[] pt = cipher.doFinal(Base64.decodeBase64(URLDecoder.decode(text, "UTF-8").getBytes()));

			return new String(pt);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			 //e.printStackTrace();
			//Log.d("ERROR_ENC",""+text);
            return decrypt(text);
		}

	}






	public String encrypt(String text) {
		try {
			byte[] key;

			key = (salt + "").getBytes("UTF-8");

			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16); // use only first 128 bit

			secretKeySpec = new SecretKeySpec(username.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance(CIPHER_NAME);
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

			return new String(Base64.encodeBase64(cipher.doFinal(text.getBytes())));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			  e.printStackTrace();
		}
		return null;
	}

	public String encrypt(String text, String username, String password) {
		try {
			byte[] key;

			key = (salt + username + password).getBytes("UTF-8");

			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16); // use only first 128 bit

			secretKeySpec = new SecretKeySpec(username.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance(CIPHER_NAME);
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

			return new String(Base64.encodeBase64(cipher.doFinal(text.getBytes())));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		return null;
	}

	public String encryptAndEncode(String text) {
		try {
			byte[] key;

			key = (salt + username + password).getBytes("UTF-8");

			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16); // use only first 128 bit

			secretKeySpec = new SecretKeySpec(username.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance(CIPHER_NAME);
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

			return URLEncoder.encode(new String(Base64.encodeBase64(cipher.doFinal(text.getBytes()))), "UTF-8");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
            Log.d("ERROR_ENC",""+text);
		}
		return null;
	}

	public String decrypt(byte[] ct) {
		Cipher cipher;
		try {
			byte[] key;

			key = (salt + username + password).getBytes("UTF-8");

			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16); // use only first 128 bit

			secretKeySpec = new SecretKeySpec(username.getBytes(), "AES");
			cipher = Cipher.getInstance(CIPHER_NAME);

			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
			byte[] pt = cipher.doFinal(ct);

			return new String(pt);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		return null;
	}

	public String decrypt(String text) {

        Cipher cipher;
        try {
            byte[] key;

            key = (salt + username + password).getBytes("UTF-8");

            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); // use only first 128 bit

            secretKeySpec = new SecretKeySpec(username.getBytes(), "AES");
            cipher = Cipher.getInstance(CIPHER_NAME);

            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] pt = cipher.doFinal(Base64.decodeBase64(text.getBytes()));

            return new String(pt);
        } catch (Exception e) {
            // TODO Auto-generated catch block

        }
        return null;

    }

	public String decrypt(String text, String username, String password) {

		Cipher cipher;
		try {
			byte[] key;

			key = (salt + username + password).getBytes("UTF-8");

			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16); // use only first 128 bit

			secretKeySpec = new SecretKeySpec(username.getBytes(), "AES");
			cipher = Cipher.getInstance(CIPHER_NAME);

			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
			byte[] pt = cipher.doFinal(Base64.decodeBase64(text.getBytes()));

			return new String(pt);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		return null;
	}

	public SecretKey keyGen(String algorithm, int keySize) {
		KeyGenerator keygen;
		try {
			keygen = KeyGenerator.getInstance(algorithm);

			keygen.init(keySize);
			System.out.println(new String(keygen.generateKey().getEncoded()));
			return keygen.generateKey();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		return null;
	}

	public String autoMaskText(String textx) {
		try {

			String text = decrypt(textx);
			int length = text.length() / 2;
			text = text.substring(0, (text.length()) / 2);
			for (int i = 0; i <= length; i++) {
				text += "*";
			}
			return text;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return textx;
	}
}
