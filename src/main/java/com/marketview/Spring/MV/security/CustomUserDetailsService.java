package com.marketview.Spring.MV.security.oauth2;

import com.marketview.Spring.MV.model.Role;
import com.marketview.Spring.MV.model.User;
import com.marketview.Spring.MV.repository.RoleRepository;
import com.marketview.Spring.MV.repository.UserRepository;
import com.marketview.Spring.MV.security.UserPrincipal;
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
        OAuth2User oauth2User = super.loadUser(userRequest);

        String email = oauth2User.getAttribute("email");

        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            // Register new user with default role USER
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));

            user = new User();
            user.setEmail(email);
            user.setUsername(email.split("@")[0]);
            user.setProvider("google");
            user.setRoles(Collections.singletonList(userRole));

            userRepository.save(user);
        }

        return new UserPrincipal(user, oauth2User.getAttributes());
    }
}
