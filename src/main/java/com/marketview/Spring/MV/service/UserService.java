package com.marketview.Spring.MV.service;

import com.marketview.Spring.MV.dto.UserDTO;
import com.marketview.Spring.MV.model.User;
import com.marketview.Spring.MV.repository.UserRepository;
import com.marketview.Spring.MV.util.CustomException;
import lombok.Data;
import org.springframework.stereotype.Service;
import java.util.List;

@Data
@Service
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<String> getAllUsernames() {
        return userRepository.findAll().stream()
                .map(User::getUsername)
                .toList();
    }

    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User createUser(UserDTO user) {
        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setEmail(user.getEmail());
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        return userRepository.save(newUser);
    }

    public User updateUser(String id, UserDTO userDetails) {
        User user = getUserById(id);
        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        // update other fields like roles, password, etc.
        return userRepository.save(user);
    }

    public void deleteUser(String id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException("User not found with username: " + username,
                        org.springframework.http.HttpStatus.NOT_FOUND));
    }
}
