package kr.co.shop.makao.util;

import org.mindrot.jbcrypt.BCrypt;

public class StringEncoder {
    public static String encode(String str) {
        return BCrypt.hashpw(str, BCrypt.gensalt());
    }

    public static boolean match(String plain, String hashed) {
        return BCrypt.checkpw(plain, hashed);
    }
}