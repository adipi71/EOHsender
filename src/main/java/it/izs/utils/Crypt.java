package it.izs.utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Crypt { // StrongAES

	public static String encrypt(String strClearText, String strKey) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
		String strData = "";

		SecretKeySpec skeyspec = new SecretKeySpec(strKey.getBytes(), "Blowfish");
		Cipher cipher = Cipher.getInstance("Blowfish");
		cipher.init(Cipher.ENCRYPT_MODE, skeyspec);
		byte[] encrypted = cipher.doFinal(strClearText.getBytes());
		// strData=new String(encrypted);
		strData = Base64.getEncoder().encodeToString(encrypted);

		return strData;
	}

	public static String decrypt(String strEncrypted, String strKey) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
		String strData = "";

		// strEncrypted=Base64.getDecoder().decode(strEncrypted);
		SecretKeySpec skeyspec = new SecretKeySpec(strKey.getBytes(), "Blowfish");
		Cipher cipher = Cipher.getInstance("Blowfish");
		cipher.init(Cipher.DECRYPT_MODE, skeyspec);
		// byte[] decrypted=cipher.doFinal(strEncrypted.getBytes());
		byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(strEncrypted));
		strData = new String(decrypted);

		return strData;
	}

	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String msg="usage:\n"
				+ "1. java -jar crypt.jar -e key word2encrypt\n"
				+ "   The output base64_word_encrypted = base64(crypt(word2encrypt using key)\n" 
				+ "1. java -jar crypt.jar -d key base64_word_encrypted  \n"
				+ "   The output is word2encrypt\n";

		
		if( 	args.length < 3 ) {
			System.out.println(msg);
			System.exit(1);
		}
		String			what=args[0],
						key=args[1],
						input=args[2],
						output="";
		
		if( 		what.equals("-e")) {
			output=Crypt.encrypt(input, key);
		}else if( 	what.equals("-d")) {
			output=Crypt.decrypt(input, key);
		}else {
			System.out.println(msg);
			System.exit(1);
		}
		
		System.out.println(output);
	}

}