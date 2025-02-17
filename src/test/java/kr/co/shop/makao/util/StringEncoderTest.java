package kr.co.shop.makao.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringEncoderTest {

    @Test
    void encode_성공() {
        String rawPassword = "securePassword";
        String hashedPassword = StringEncoder.encode(rawPassword);

        assertNotNull(hashedPassword);
        assertNotEquals(rawPassword, hashedPassword);
    }

    @Test
    void match_성공() {
        String rawPassword = "securePassword";
        String hashedPassword = StringEncoder.encode(rawPassword);

        boolean isMatch = StringEncoder.match(rawPassword, hashedPassword);

        assertTrue(isMatch);
    }

    @Test
    void match_실패() {
        String rawPassword = "securePassword";
        String differentPassword = "wrongPassword";
        String hashedPassword = StringEncoder.encode(rawPassword);

        boolean isMatch = StringEncoder.match(differentPassword, hashedPassword);

        assertFalse(isMatch);
    }
}
