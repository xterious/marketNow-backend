package com.marketview.Spring.MV.security.oauth2;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.marketview.Spring.MV.model.Role;
import com.marketview.Spring.MV.model.User;
import com.marketview.Spring.MV.repository.RoleRepository;
import com.marketview.Spring.MV.repository.UserRepository;
import com.marketview.Spring.MV.security.UserPrincipal;
import com.marketview.Spring.MV.security.oauth2.user.OAuth2UserInfo;
import com.marketview.Spring.MV.security.oauth2.user.OAuth2UserInfoFactory;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oAuth2User.getAttributes());

        if(!StringUtils.hasText(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        Optional<User> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());
        User user;

        if(userOptional.isPresent()) {
            user = userOptional.get();

            // If user is registered with a different provider, throw exception
            if(!user.getProvider().equals(registrationId)) {
                throw new OAuth2AuthenticationException("You're signed up with " + user.getProvider() + ". Please use that to login.");
            }

            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        }

        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();

        // Get or create the default user role
        Role userRole = roleRepository.findByName(Role.ROLE_USER)
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .name(Role.ROLE_USER)
                        .description("Default user role")
                        .build()));

        User user = User.builder()
                .provider(registrationId)
                .providerId(oAuth2UserInfo.getId())
                .username(oAuth2UserInfo.getEmail().split("@")[0] + "_" + registrationId)
                .email(oAuth2UserInfo.getEmail())
                .firstName(oAuth2UserInfo.getName())
                .pictureUrl(oAuth2UserInfo.getImageUrl())
                .build();

        user.addRole(userRole);

        return userRepository.save(user);
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setFirstName(oAuth2UserInfo.getName());
        existingUser.setPictureUrl(oAuth2UserInfo.getImageUrl());
        return userRepository.save(existingUser);
    }
}
