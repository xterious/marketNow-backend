package com.market_view.spring.mv.repository;

import com.market_view.spring.mv.model.Role;
import com.market_view.spring.mv.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();

        Role userRole = new Role("ROLE_USER");

        user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("securepassword")
                .firstName("Test")
                .lastName("User")
                .provider("local")
                .providerId(null)
                .roles(Set.of(userRole))
                .build();

        userRepository.save(user);
    }

    @Test
    public void testFindByUsername() {
        Optional<User> found = userRepository.findByUsername("testuser");
        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
    }

    @Test
    public void testFindByEmail() {
        Optional<User> found = userRepository.findByEmail("test@example.com");
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
    }

    @Test
    public void testExistsByUsername() {
        assertTrue(userRepository.existsByUsername("testuser"));
        assertFalse(userRepository.existsByUsername("nonexistent"));
    }

    @Test
    public void testExistsByEmail() {
        assertTrue(userRepository.existsByEmail("test@example.com"));
        assertFalse(userRepository.existsByEmail("fake@example.com"));
    }

    @Test
    public void testFindByProviderAndProviderId() {
        // Should be empty for this test user
        Optional<User> found = userRepository.findByProviderAndProviderId("google", "123456");
        assertFalse(found.isPresent());

        // Save a new user with OAuth2 info
        User oauthUser = User.builder()
                .username("googleuser")
                .email("google@example.com")
                .password(null)
                .firstName("OAuth")
                .lastName("User")
                .provider("google")
                .providerId("123456")
                .roles(Set.of(new Role("ROLE_USER")))
                .build();

        userRepository.save(oauthUser);

        Optional<User> oauthFound = userRepository.findByProviderAndProviderId("google", "123456");
        assertTrue(oauthFound.isPresent());
        assertEquals("googleuser", oauthFound.get().getUsername());
    }
}
