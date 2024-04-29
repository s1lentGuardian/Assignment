package org.kharkiv.javapracticaltestassignment.service;

import org.kharkiv.javapracticaltestassignment.exception.UserBadRequestException;
import org.kharkiv.javapracticaltestassignment.exception.UserIllegalArgumentException;
import org.kharkiv.javapracticaltestassignment.exception.UserNotFoundException;
import org.kharkiv.javapracticaltestassignment.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
public class UserService {

    private List<User> users = new ArrayList<>();

    @Value("${min.age}")
    private int minAge;

    public List<User> getAllUsers() {
        return users;
    }

    public void create(User user) {
        if (calculateAge(user.getBirthDate()) < minAge) {
            throw new UserBadRequestException("User must be at least " + minAge + " years old.");
        }

        if (findUserByEmail(user.getEmail()).isPresent()) {
            throw new UserBadRequestException("User with email " + user.getEmail() + " already exists.");
        }

        users.add(user);
    }

    public int calculateAge(LocalDate birthDate) {
        LocalDate today = LocalDate.now();
        return Period.between(birthDate, today).getYears();
    }

    public void updateUser(String email, Map<String, String> updates) {
        Optional<User> optionalUser = findUserByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            for (Map.Entry<String, String> entry : updates.entrySet()) {
                String fieldName = entry.getKey();
                String fieldValue = entry.getValue();

                switch (fieldName) {
                    case "firstName":
                        validateAndUpdateFirstName(user, fieldValue);
                        break;
                    case "lastName":
                        validateAndUpdateLastName(user, fieldValue);
                        break;
                    case "birthDate":
                        validateAndUpdateBirthDate(user, fieldValue);
                        break;
                    case "address":
                        user.setAddress(fieldValue);
                        break;
                    case "phoneNumber":
                        validateAndUpdatePhoneNumber(user, fieldValue);
                        break;
                    default:
                        throw new UserIllegalArgumentException("Invalid field name: " + fieldName);
                }
            }

            users.set(users.indexOf(user), user);
        } else {
            throw new UserNotFoundException("User with email " + email + " not found");
        }
    }

    private void validateAndUpdateFirstName(User user, String firstName) {
        if (firstName.isEmpty()) {
            throw new UserBadRequestException("First name cannot be empty");
        }
        user.setFirstName(firstName);
    }

    private void validateAndUpdateLastName(User user, String lastName) {
        if (lastName.isEmpty()) {
            throw new UserBadRequestException("Last name cannot be empty");
        }
        user.setLastName(lastName);
    }

    private void validateAndUpdateBirthDate(User user, String birthDateStr) {
        LocalDate newBirthDate = LocalDate.parse(birthDateStr);
        LocalDate today = LocalDate.now();
        if (newBirthDate.isAfter(today)) {
            throw new UserBadRequestException("Date of birth must be in the past");
        }
        if (calculateAge(newBirthDate) < minAge) {
            throw new UserBadRequestException("User must be at least " + minAge + " years old.");
        }
        user.setBirthDate(newBirthDate);
    }


    private void validateAndUpdatePhoneNumber(User user, String phoneNumber) {
        if (!phoneNumber.matches("\\+?[0-9]+")) {
            throw new UserBadRequestException("Phone number must contain only digits and may start with '+'");
        }
        user.setPhoneNumber(phoneNumber);
    }


    public Optional<User> findUserByEmail(String email) {
        Optional<User> optionalUser = users.stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
        return optionalUser;
    }

    public void updateUserByEmail(String email, User updatedUser) {
        Optional<User> optionalUser = findUserByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (!user.getEmail().equals(updatedUser.getEmail())) {
                if (findUserByEmail(updatedUser.getEmail()).isPresent()) {
                    throw new UserBadRequestException("User with email " + updatedUser.getEmail() + " already exists.");
                }
            }

            if (calculateAge(updatedUser.getBirthDate()) < minAge) {
                throw new UserBadRequestException("User must be at least " + minAge + " years old.");
            }

            BeanUtils.copyProperties(updatedUser, user);
            users.set(users.indexOf(user), user);
        } else {
            throw new UserNotFoundException("User with email " + email + " not found");
        }
    }

    public void delete(String email) {
        Optional<User> optionalUser = findUserByEmail(email);
        if (optionalUser.isPresent()) {
            users.remove(optionalUser.get());
        } else {
            throw new UserNotFoundException("User with email " + email + " not found");
        }
    }

    public List<User> getUsersByBirthDateRange(LocalDate fromDate, LocalDate toDate) {
        LocalDate currentDate = LocalDate.now();
        if (fromDate.isAfter(currentDate) || toDate.isAfter(currentDate ) || fromDate.equals(currentDate) || toDate.equals(currentDate)) {
            throw new UserIllegalArgumentException("Values must be earlier than current date");
        }

        if (fromDate.isAfter(toDate)) {
            throw new UserIllegalArgumentException("'From' date must be less than 'To' date");
        }

        return users.stream()
                .filter(user -> user.getBirthDate().isAfter(fromDate.minusDays(1)) &&
                        user.getBirthDate().isBefore(toDate.plusDays(1)))
                .collect(toList());
    }

}
