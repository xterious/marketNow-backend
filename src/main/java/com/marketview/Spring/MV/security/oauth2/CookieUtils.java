package com.marketview.Spring.MV.security.oauth2;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.SerializationUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Base64;
import java.util.Optional;

class CookieUtils {
    private CookieUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return Optional.of(cookie);
                }
            }
        }

        return Optional.empty();
    }

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
    }

    public static String serialize(Object object) {
        return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(object));
    }

    @SuppressWarnings("unchecked")
    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(Base64.getUrlDecoder().decode(cookie.getValue()));
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return cls.cast(ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalArgumentException("Failed to deserialize cookie value", e);
        }
    }

}
