package kr.co.shop.makao.util;

import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

class StringEncoderTest {
    String rawPassword = "securePassword";

    @Test
    void encode_성공() {

        String hashedPassword = StringEncoder.encode(rawPassword);

        assertNotNull(hashedPassword);
    }

    @Test
    void match_성공() {
        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());

        boolean isMatch = StringEncoder.match(rawPassword, hashedPassword);

        assertTrue(isMatch);
    }

    @Test
    void match_실패() {
        String differentPassword = "wrongPassword";
        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());

        boolean isMatch = StringEncoder.match(differentPassword, hashedPassword);

        assertFalse(isMatch);
    }
}
