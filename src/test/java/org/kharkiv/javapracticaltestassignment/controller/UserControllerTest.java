package org.kharkiv.javapracticaltestassignment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kharkiv.javapracticaltestassignment.exception.UserIllegalArgumentException;
import org.kharkiv.javapracticaltestassignment.exception.UserNotFoundException;
import org.kharkiv.javapracticaltestassignment.model.User;
import org.kharkiv.javapracticaltestassignment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;


    static User getUser() {
        User user = User.builder()
                .email("dummy@gmail.com")
                .firstName("dummy")
                .lastName("dummy")
                .birthDate(LocalDate.of(2000, 12, 12))
                .address("dummy")
                .phoneNumber("+30943443")
                .build();
        return user;
    }

    @Test
    void createUserValidUserReturnsCreatedStatus() throws Exception {
        User user = getUser();
        doNothing().when(userService).create(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void createUserInvalidUserReturnsBadRequest() throws Exception {
        User user = User.builder()
                .email("dummymail.com")
                .firstName("dummy")
                .lastName("dummy")
                .birthDate(LocalDate.of(2026, 12, 12))
                .address("dummy")
                .phoneNumber("+30943443")
                .build();
        doThrow(UserIllegalArgumentException.class).when(userService).create(any());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void getAllUsersReturnsListOfUsers() throws Exception {
        User user = getUser();
        when(userService.getAllUsers()).thenReturn(Arrays.asList(user));

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void updateUserValidRequestReturnsNoContent() throws Exception {
        String email = "dummy@gmail.com";
        Map<String, String> updates = new HashMap<>();
        updates.put("firstName", "updated");
        updates.put("lastName", "updated");
        doNothing().when(userService).updateUser(email, updates);

        mockMvc.perform(patch("/users/dummy@gmail.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"updated\",\"lastName\":\"updated\"}"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(userService).updateUser(email, updates);
    }

    @Test
    void updateUserInvalidEmailReturnsBadRequest() throws Exception {
        Map<String, String> updates = new HashMap<>();
        updates.put("firstName", "updated");
        updates.put("lastName", "updated");
        doThrow(UserIllegalArgumentException.class).when(userService).updateUser("invalid-email", updates);

        mockMvc.perform(patch("/users/invalid-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"updated\",\"lastName\":\"updated\"}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void updateUserByEmailValidUserReturnsNoContent() throws Exception {
        User user = User.builder()
                .email("updated@gmail.com")
                .firstName("updated")
                .lastName("updated")
                .birthDate(LocalDate.of(2000, 12, 12))
                .address("updated")
                .phoneNumber("+30943443")
                .build();
        doNothing().when(userService).updateUserByEmail(user.getEmail(), user);

        mockMvc.perform(put("/users/{email}", user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateUserByEmailInvalidUserReturnsBadRequest() throws Exception {
        User user = User.builder()
                .email("updated@gmail.com")
                .firstName("updated")
                .lastName("updated")
                .birthDate(LocalDate.of(2026, 12, 12))
                .address("updated")
                .phoneNumber("dummy")
                .build();
        doThrow(UserIllegalArgumentException.class).when(userService).updateUserByEmail(user.getEmail(), user);

        mockMvc.perform(put("/users/{email}", user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUserByEmailNonExistingUserReturnsNotFound() throws Exception {
        User user = User.builder()
                .email("updated@gmail.com")
                .firstName("updated")
                .lastName("updated")
                .birthDate(LocalDate.of(2000, 12, 12))
                .address("updated")
                .phoneNumber("dummy")
                .build();
        String email = "nonexistent@example.com";

        doThrow(UserIllegalArgumentException.class).when(userService).updateUserByEmail(email, user);

        mockMvc.perform(put("/users/{email}", email)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteValidEmailReturnsNoContent() throws Exception {
        doNothing().when(userService).delete("dummy@dummy.com");

        mockMvc.perform(delete("/users/dummy@dummy.com"))
                .andExpect(status().isNoContent());

        verify(userService).delete("dummy@dummy.com");
    }

    @Test
    void deleteInvalidEmailReturnsNotFound() throws Exception {
        doThrow(UserNotFoundException.class).when(userService).delete("invalid-email");

        mockMvc.perform(delete("/users/invalid-email"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUsersByBirthDateRangeValidRangeReturnsUsers() throws Exception {
        LocalDate fromDate = LocalDate.of(1990, 1, 1);
        LocalDate toDate = LocalDate.of(1995, 12, 31);
        List<User> users = new ArrayList<>();
        User user1 = User.builder()
                .email("d@gmail.com")
                .firstName("dummy")
                .lastName("dummy")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address("dummy")
                .phoneNumber("+30943443")
                .build();
        User user2 = User.builder()
                .email("dummy@gmail.com")
                .firstName("dummy")
                .lastName("dummy")
                .birthDate(LocalDate.of(1993, 1, 1))
                .address("dummy")
                .phoneNumber("+30943443")
                .build();
        users.add(user1);
        users.add(user2);
        when(userService.getUsersByBirthDateRange(fromDate, toDate)).thenReturn(users);

        mockMvc.perform(get("/users/")
                        .param("fromDate", "1990-01-01")
                        .param("toDate", "1995-12-31")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getUsersByBirthDateRangeInvalidRangeReturnsBadRequest() throws Exception {
        LocalDate fromDate = LocalDate.of(2025, 1, 1);
        LocalDate toDate = LocalDate.of(2024, 12, 31);
        doThrow(UserIllegalArgumentException.class).when(userService).getUsersByBirthDateRange(fromDate, toDate);

        mockMvc.perform(get("/users/")
                        .param("fromDate", "2025-01-01")
                        .param("toDate", "2024-12-31")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void getUsersByBirthDateRangeCurrentDateRangeExceptionThrown() throws Exception {
        LocalDate currentDate = LocalDate.now();
        String currentDateStr = currentDate.format(DateTimeFormatter.ISO_DATE);
        doThrow(UserIllegalArgumentException.class).when(userService).getUsersByBirthDateRange(currentDate, currentDate);

        mockMvc.perform(get("/users/")
                        .param("fromDate", currentDateStr)
                        .param("toDate", currentDateStr)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

}