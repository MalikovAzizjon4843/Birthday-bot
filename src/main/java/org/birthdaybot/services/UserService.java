package org.birthdaybot.services;

import org.birthdaybot.entity.User;
import org.birthdaybot.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * @author Malikov Azizjon    birthday-bot       25.02.2025       12:12
 */

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public Optional<User> findUsersByTelegramId(Long telegramId) {
        return userRepository.findByTelegramId(telegramId);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void updateUser(Long telegramId, LocalDate newBirthDate) {
        Optional<User> userOptional = userRepository.findByTelegramId(telegramId);
        userOptional.ifPresent(user -> {
            user.setBirthDate(newBirthDate);
            userRepository.save(user);
        });
    }

    public void deleteUser(Long telegramId) {
        userRepository.findByTelegramId(telegramId)
                .ifPresent(userRepository::delete);
    }
}
