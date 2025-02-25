package org.birthdaybot.repository;
import org.birthdaybot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * @author Malikov Azizjon    birthday-bot       25.02.2025       12:05
 */

@Repository
public interface UserRepository  extends JpaRepository<User, Long> {
    Optional<User> findByTelegramId(Long telegramId);
}
