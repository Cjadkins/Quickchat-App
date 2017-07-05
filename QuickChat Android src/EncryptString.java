package quickchat;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptString {
	private static String prependString = "furious";
	private static String appendString = "bananas!";
	
	public static String encryptSHA512(String stringToEncrypt) {
		String result = "";
		
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-512");
			result = (new BigInteger(1, digest.digest(prependString.concat(stringToEncrypt).concat(appendString).getBytes()))).toString(16);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
//