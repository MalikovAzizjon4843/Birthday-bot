package org.birthdaybot.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.Set;

/**
 * @author Malikov Azizjon    birthday-bot       25.02.2025       12:01
 */

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private Long telegramId;
    private LocalDate birthDate;

    @ManyToMany(mappedBy = "users")
    private Set<Group> groups;
}
