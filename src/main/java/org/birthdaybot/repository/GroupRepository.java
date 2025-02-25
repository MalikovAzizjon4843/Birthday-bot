package org.birthdaybot.repository;
import org.birthdaybot.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * @author Malikov Azizjon    birthday-bot       25.02.2025       12:06
 */

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
//    Optional<Group> findById(Long id);
    Optional<Group> findByTelegramGroupId(Long telegramGroupId);
}
