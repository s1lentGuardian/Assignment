package org.kharkiv.javapracticaltestassignment.controller;

import jakarta.validation.Valid;
import org.kharkiv.javapracticaltestassignment.exception.UserIllegalArgumentException;
import org.kharkiv.javapracticaltestassignment.model.User;
import org.kharkiv.javapracticaltestassignment.service.UserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void createUser(@Valid @RequestBody User user, Errors errors) {
        if (!errors.hasErrors()) {
            userService.create(user);
        } else
            throw new UserIllegalArgumentException(errors);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/{email}")
    public void updateUser(@PathVariable String email, @RequestBody Map<String, String> updates) {
        userService.updateUser(email, updates);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{email}")
    public void updateUserByEmail(@Valid @RequestBody User user, Errors errors, @PathVariable String email) {
        if (!errors.hasErrors()) {
            userService.updateUserByEmail(email, user);
        } else
            throw new UserIllegalArgumentException(errors);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{email}")
    public void delete(@PathVariable String email) {
        userService.delete(email);
    }

    @GetMapping("/")
    public List<User> getUsersByBirthDateRange(
            @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        return userService.getUsersByBirthDateRange(fromDate, toDate);
    }
}
