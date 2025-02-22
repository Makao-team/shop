package kr.co.shop.makao.util;

public class RandomString {
    private static final String ENG_DIGIT_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String generateEngDigit(int length) {
        StringBuilder builder = new StringBuilder();
        while (length-- != 0) {
            int character = (int) (Math.random() * ENG_DIGIT_STRING.length());
            builder.append(ENG_DIGIT_STRING.charAt(character));
        }
        return builder.toString();
    }
}
