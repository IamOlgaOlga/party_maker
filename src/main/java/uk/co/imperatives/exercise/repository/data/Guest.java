package uk.co.imperatives.exercise.repository.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@AllArgsConstructor
@Table("guests")
public class Guest {
    @Id
    private String name;
    private Integer tableNumber;
    private Integer totalGuests;
}
