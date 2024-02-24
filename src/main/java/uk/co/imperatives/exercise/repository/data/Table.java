package uk.co.imperatives.exercise.repository.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@org.springframework.data.relational.core.mapping.Table("tables")
public class Table {
    @Id
    private Integer id;
    private Integer capacity;
}
