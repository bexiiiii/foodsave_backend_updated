package foodsave.com.foodsave.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;        // Чьё имя или от кого
    private String business;    // Название магазина или организации
    private String type;        // Тип операции (например: "purchase", "refund")
    private String account;     // Номер аккаунта
    private Double amount;      // Сумма
    private LocalDateTime date;
}
