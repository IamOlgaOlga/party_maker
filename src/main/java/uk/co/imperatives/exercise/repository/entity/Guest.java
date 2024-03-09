package uk.co.imperatives.exercise.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

@Getter
@AllArgsConstructor
@Table("guests")
public class Guest {
    @Id
    private String name;
    private Integer tableNumber;
    private Integer totalGuests;
    private Date timeArrived;

    public Guest(String name) {
        this.name = name;
    }

    public Guest(String name, Integer tableNumber, Integer totalGuests) {
        this.name = name;
        this.tableNumber = tableNumber;
        this.totalGuests = totalGuests;
    }

    public Guest(String name, Integer totalGuests, Date timeArrived) {
        this.name = name;
        this.totalGuests = totalGuests;
        this.timeArrived = timeArrived;
    }
}
