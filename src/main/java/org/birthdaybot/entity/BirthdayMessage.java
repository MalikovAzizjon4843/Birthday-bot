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
public class BirthdayMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;
}
