package vn.edu.likelion.QLK.base64Pass;

import java.util.Base64;

public class PasswordUtil {
	public static String encodeBase64(String password) {
        return Base64.getEncoder().encodeToString(password.getBytes());
    }

    public static String decodeBase64(String encodedPassword) {
        return new String(Base64.getDecoder().decode(encodedPassword));
    }
}
