package com.market_view.spring.mv.security.oauth2;

import com.market_view.spring.mv.model.Role;
import com.market_view.spring.mv.model.User;
import com.market_view.spring.mv.repository.RoleRepository;
import com.market_view.spring.mv.repository.UserRepository;
import com.market_view.spring.mv.security.UserPrincipal;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public CustomOAuth2UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // Extract user info from OAuth2User
        String email = oAuth2User.getAttribute("email");

        // Check if a user exists in DB
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            // You could update user info here if you want
        } else {
            // Register new user with role USER
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setName("ROLE_USER");
                        return roleRepository.save(newRole);
                    });

            user = new User();
            user.setUsername(email); // or generate username from name/email
            user.setEmail(email);
            user.setRoles(Collections.singleton(userRole));
            user.setProvider("google");
            user = userRepository.save(user);
        }

        // Return a UserPrincipal for Spring Security context
        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }
}
