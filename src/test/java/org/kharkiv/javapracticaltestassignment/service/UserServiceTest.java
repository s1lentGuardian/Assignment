package org.kharkiv.javapracticaltestassignment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kharkiv.javapracticaltestassignment.exception.UserBadRequestException;
import org.kharkiv.javapracticaltestassignment.exception.UserIllegalArgumentException;
import org.kharkiv.javapracticaltestassignment.exception.UserNotFoundException;
import org.kharkiv.javapracticaltestassignment.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {"min.age=18"})
class UserServiceTest {

    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService.getAllUsers().clear();
    }

    @Test
    void createUserValidUserSuccess() {
        User user = User.builder()
                .email("dummy@gmail.com")
                .firstName("dummy")
                .lastName("dummy")
                .birthDate(LocalDate.of(2000, 12, 12))
                .address("dummy")
                .phoneNumber("+30943443")
                .build();

        assertDoesNotThrow(() -> userService.create(user));
        assertTrue(userService.getAllUsers().contains(user));
    }

    @Test
    void createUserInvalidAgeExceptionThrown() {
        User user = User.builder()
                .email("dummy@gmail.com")
                .firstName("dummy")
                .lastName("dummy")
                .birthDate(LocalDate.of(2021, 12, 12))
                .address("dummy")
                .phoneNumber("+30943443")
                .build();

        assertThrows(UserBadRequestException.class, () -> userService.create(user));
    }

    @Test
    void createUserAlreadyExistsEmailExceptionThrown() {
        User user1 = User.builder()
                .email("dummy@gmail.com")
                .firstName("dummy")
                .lastName("dummy")
                .birthDate(LocalDate.of(2000, 12, 12))
                .address("dummy")
                .phoneNumber("+30943443")
                .build();
        User user2 = User.builder()
                .email("dummy@gmail.com")
                .firstName("dummy")
                .lastName("dummy")
                .birthDate(LocalDate.of(2000, 12, 12))
                .address("dummy")
                .phoneNumber("+30943443")
                .build();
        userService.create(user1);

        assertThrows(UserBadRequestException.class, () -> userService.create(user2));
    }

    @Test
    void getAllUserSuccess() {
        User user = User.builder()
                .email("dummy@gmail.com")
                .firstName("dummy")
                .lastName("dummy")
                .birthDate(LocalDate.of(2000, 12, 12))
                .address("dummy")
                .phoneNumber("+30943443")
                .build();
        userService.create(user);

        List<User> allUsers = userService.getAllUsers();

        assertEquals(1, allUsers.size());
        assertEquals(user, allUsers.get(0));
    }

    @Test
    void calculateAgeSuccess() {
        LocalDate dob = LocalDate.of(2003, 11, 11);
        int actualResult = userService.calculateAge(dob);

        assertEquals(20,actualResult);
    }

    @Test
    void findUserByEmailExistingUserReturnsOptionalWithUser() {
        User user = User.builder()
                .email("dummy@gmail.com")
                .firstName("dummy")
                .lastName("dummy")
                .birthDate(LocalDate.of(2000, 12, 12))
                .address("dummy")
                .phoneNumber("+30943443")
                .build();
        userService.create(user);

        Optional<User> optionalUser = userService.findUserByEmail("dummy@gmail.com");

        assertTrue(optionalUser.isPresent());
        assertEquals(user, optionalUser.get());
    }


    @Test
    void findUserByEmailNonExistingUserReturnsEmptyOptional() {
        Optional<User> optionalUser = userService.findUserByEmail("nonexistent@example.com");

        assertFalse(optionalUser.isPresent());
    }


    @Test
    void updateUserValidUpdatesSuccess() {
        User user = User.builder()
                .email("dummy@gmail.com")
                .firstName("dummy")
                .lastName("dummy")
                .birthDate(LocalDate.of(2000, 12, 12))
                .address("dummy")
                .phoneNumber("+30943443")
                .build();
        userService.create(user);
        Map<String, String> updates = new HashMap<>();
        updates.put("firstName", "Jane");
        updates.put("lastName", "Smith");
        updates.put("birthDate", "1995-05-05");

        User actualResult = userService.getAllUsers().get(0);

        assertDoesNotThrow(() -> userService.updateUser(user.getEmail(), updates));
        assertEquals("Jane", actualResult.getFirstName());
        assertEquals("Smith", actualResult.getLastName());
        assertEquals(LocalDate.of(1995, 5, 5), actualResult.getBirthDate());
    }

    @Test
    void updateUserNonExistingUserExceptionThrown() {
        Map<String, String> updates = new HashMap<>();
        updates.put("firstName", "Jane");

        assertThrows(UserNotFoundException.class, () -> userService.updateUser("nonexistent@example.com", updates));
    }

    @Test
    void updateUserWithEmptyLastNameExceptionThrown() {
        User user = User.builder()
                .email("dummy@gmail.com")
                .firstName("dummy")
                .lastName("dummy")
                .birthDate(LocalDate.of(2000, 12, 12))
                .address("dummy")
                .phoneNumber("+30943443")
                .build();
        userService.create(user);
        Map<String, String> updates = new HashMap<>();
        updates.put("lastName", "");


        assertThrows(UserBadRequestException.class, () -> userService.updateUser("dummy@gmail.com", updates));
    }

    @Test
    void updateUserWithEmptyFirstNameExceptionThrown() {
        User user = User.builder()
                .email("dummy@gmail.com")
                .firstName("dummy")
                .lastName("dummy")
                .birthDate(LocalDate.of(2000, 12, 12))
                .address("dummy")
                .phoneNumber("+30943443")
                .build();
        userService.create(user);
        Map<String, String> updates = new HashMap<>();
        updates.put("firstName", "");


        assertThrows(UserBadRequestException.class, () -> userService.updateUser("dummy@gmail.com", updates));
    }

    @Test
    void updateUserWithNotValidBirthDateExceptionThrown() {
        User user = User.builder()
                .email("dummy@gmail.com")
                .firstName("dummy")
                .lastName("dummy")
                .birthDate(LocalDate.of(2000, 12, 12))
                .address("dummy")
                .phoneNumber("+30943443")
                .build();
        userService.create(user);
        Map<String, String> updates = new HashMap<>();
        updates.put("birthDate", "2009-12-12");


        assertThrows(UserBadRequestException.class, () -> userService.updateUser("dummy@gmail.com", updates));
    }

    @Test
    void updateUserWithFutureBirthDateExceptionThrown() {
        User user = User.builder()
                .email("dummy@gmail.com")
                .firstName("dummy")
                .lastName("dummy")
                .birthDate(LocalDate.of(2000, 12, 12))
                .address("dummy")
                .phoneNumber("+30943443")
                .build();
        userService.create(user);
        Map<String, String> updates = new HashMap<>();
        updates.put("birthDate", "2026-12-12");


        assertThrows(UserBadRequestException.class, () -> userService.updateUser("dummy@gmail.com", updates));
    }
    @Test
    void updateUserWithNotValidPhoneNumberExceptionThrown() {
        User user = User.builder()
                .email("dummy@gmail.com")
                .firstName("dummy")
                .lastName("dummy")
                .birthDate(LocalDate.of(2000, 12, 12))
                .address("dummy")
                .phoneNumber("+30943443")
                .build();
        userService.create(user);
        Map<String, String> updates = new HashMap<>();
        updates.put("phoneNumber", "dummy");


        assertThrows(UserBadRequestException.class, () -> userService.updateUser("dummy@gmail.com", updates));
    }

    @Test
    void updateUserByEmailValidUserSuccess() {
        User user = User.builder()
                .email("dummy@gmail.com")
                .firstName("dummy")
                .lastName("dummy")
                .birthDate(LocalDate.of(2000, 12, 12))
                .address("dummy")
                .phoneNumber("+30943443")
                .build();
        userService.create(user);
        User updatedUser = User.builder()
                .email("dummy@gmail.com")
                .firstName("updated")
                .lastName("updated")
                .birthDate(LocalDate.of(2001, 12, 12))
                .address("updated")
                .phoneNumber("+111111111")
                .build();
        userService.updateUserByEmail("dummy@gmail.com", updatedUser);

        User retrievedUser = userService.findUserByEmail("dummy@gmail.com").orElse(null);
        assertNotNull(retrievedUser);
        assertEquals("updated", retrievedUser.getFirstName());
        assertEquals("updated", retrievedUser.getLastName());
        assertEquals(LocalDate.of(2001, 12, 12), retrievedUser.getBirthDate());
        assertEquals("+111111111", retrievedUser.getPhoneNumber());
        assertEquals("updated", retrievedUser.getAddress());
    }

    @Test
    void updateUserByEmailNonExistingUserExceptionThrown() {
        String nonExistingEmail = "nonexistent@example.com";
        User updatedUser = User.builder()
                .email("dummy@gmail.com")
                .firstName("updated")
                .lastName("updated")
                .birthDate(LocalDate.of(2001, 12, 12))
                .address("updated")
                .phoneNumber("+111111111")
                .build();

        assertThrows(UserNotFoundException.class, () -> userService.updateUserByEmail(nonExistingEmail, updatedUser));
    }

    @Test
    void updateUserByEmailInvalidAgeExceptionThrown() {
        User user = User.builder()
                .email("dummy@gmail.com")
                .firstName("dummy")
                .lastName("dummy")
                .birthDate(LocalDate.of(2000, 12, 12))
                .address("dummy")
                .phoneNumber("+30943443")
                .build();
        userService.create(user);
        User updatedUser = User.builder()
                .email("dummy@gmail.com")
                .firstName("updated")
                .lastName("updated")
                .birthDate(LocalDate.of(2023, 12, 12))
                .address("updated")
                .phoneNumber("+111111111")
                .build();

        assertThrows(UserBadRequestException.class, () -> userService.updateUserByEmail(user.getEmail(), updatedUser));
    }

    @Test
    void updateUserByEmailDuplicateEmailExceptionThrown() {
        User user1 = User.builder()
                .email("dummy@gmail.com")
                .firstName("dummy")
                .lastName("dummy")
                .birthDate(LocalDate.of(2000, 12, 12))
                .address("dummy")
                .phoneNumber("+30943443")
                .build();
        User user2 = User.builder()
                .email("d@gmail.com")
                .firstName("dummy")
                .lastName("dummy")
                .birthDate(LocalDate.of(2000, 12, 12))
                .address("dummy")
                .phoneNumber("+30943443")
                .build();
        userService.create(user1);
        userService.create(user2);
        User updatedUser = User.builder()
                .email("d@gmail.com")
                .firstName("updated")
                .lastName("updated")
                .birthDate(LocalDate.of(2000, 12, 12))
                .address("updated")
                .phoneNumber("+111111111")
                .build();

        assertThrows(UserBadRequestException.class, () -> userService.updateUserByEmail(user1.getEmail(), updatedUser));
    }

    @Test
    void deleteUserExistingUserSuccess() {
        User user = User.builder()
                .email("dummy@gmail.com")
                .firstName("dummy")
                .lastName("dummy")
                .birthDate(LocalDate.of(2000, 12, 12))
                .address("dummy")
                .phoneNumber("+30943443")
                .build();
        userService.create(user);

        List<User> allUsers = userService.getAllUsers();

        assertDoesNotThrow(() -> userService.delete("dummy@gmail.com"));
        assertTrue(allUsers.isEmpty());
    }

    @Test
    void deleteUserNonExistingUserExceptionThrown() {
        String nonExistingEmail = "nonexistent@example.com";

        assertThrows(UserNotFoundException.class, () -> userService.delete(nonExistingEmail));
    }

    @Test
    void getUsersByBirthDateRangeValidRangeSuccess() {
        LocalDate fromDate = LocalDate.of(1990, 1, 1);
        LocalDate toDate = LocalDate.of(2000, 1, 1);
        User user1 = User.builder()
                .email("dummy@gmail.com")
                .firstName("dummy")
                .lastName("dummy")
                .birthDate(LocalDate.of(1995, 05, 05))
                .address("dummy")
                .phoneNumber("+30943443")
                .build();
        User user2 = User.builder()
                .email("d@gmail.com")
                .firstName("dummy")
                .lastName("dummy")
                .birthDate(LocalDate.of(1990, 05, 02))
                .address("dummy")
                .phoneNumber("+30943443")
                .build();
        userService.create(user1);
        userService.create(user2);

        List<User> usersByBirthDateRange = userService.getUsersByBirthDateRange(fromDate, toDate);

        assertEquals(2, usersByBirthDateRange.size());
        assertEquals(user1, usersByBirthDateRange.get(0));
        assertEquals(user2, usersByBirthDateRange.get(1));
    }

    @Test
    void getUsersByBirthDateRangeInvalidDateRangeExceptionThrown() {
        LocalDate fromDate = LocalDate.of(2024, 1, 1);
        LocalDate toDate = LocalDate.of(2023, 1, 1);

        assertThrows(UserIllegalArgumentException.class, () -> userService.getUsersByBirthDateRange(fromDate, toDate));
    }

    @Test
    void getUsersByBirthDateRangeCurrentDateRangeExceptionThrown() {
        LocalDate currentDate = LocalDate.now();

        assertThrows(UserIllegalArgumentException.class, () -> userService.getUsersByBirthDateRange(currentDate, currentDate));
    }

}