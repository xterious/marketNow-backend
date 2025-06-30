package com.market_view.spring.mv.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.market_view.spring.mv.dto.UserDTO;
import com.market_view.spring.mv.model.User;
import com.market_view.spring.mv.service.StockService;
import com.market_view.spring.mv.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private StockService stockService;

    @MockBean
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "testuser")
    void getCurrentUser_ShouldReturnUserDTO() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");

        Mockito.when(userService.findByUsername("testuser")).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("testuser")))
                .andExpect(jsonPath("$.email", is("test@example.com")))
                .andExpect(jsonPath("$.firstName", is("Test")))
                .andExpect(jsonPath("$.lastName", is("User")))
                .andDo(print());
    }

    @Test
    void getAllUsers_ShouldReturnUserList() throws Exception {
        User user1 = new User(); user1.setId("1"); user1.setUsername("user1");
        User user2 = new User(); user2.setId("2"); user2.setUsername("user2");
        List<User> users = List.of(user1, user2);

        Mockito.when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].username", is("user1")))
                .andExpect(jsonPath("$[1].username", is("user2")))
                .andDo(print());
    }

    @Test
    void createUser_ShouldReturnCreatedUser() throws Exception {
        UserDTO userDTO = new UserDTO("newuser", "new@example.com", "New", "User");
        User createdUser = new User();
        createdUser.setId("123");
        createdUser.setUsername("newuser");
        createdUser.setEmail("new@example.com");

        Mockito.when(userService.createUser(any(UserDTO.class))).thenReturn(createdUser);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("newuser")))
                .andExpect(jsonPath("$.email", is("new@example.com")))
                .andDo(print());
    }
}
