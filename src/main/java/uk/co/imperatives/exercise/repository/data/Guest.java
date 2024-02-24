package uk.co.imperatives.exercise.repository.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("guests")
public class Guest {
    @Id
    private Long id;
    private String name;
    private Integer tableNumber;
    private Integer accompanyingGuests;
    private LocalDateTime timeArrived;
}
