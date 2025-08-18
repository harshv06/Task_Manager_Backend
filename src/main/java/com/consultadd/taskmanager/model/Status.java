package com.consultadd.taskmanager.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "statuses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Status {
    @Id
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private StatusName name;

    public enum StatusName {
        TODO, IN_PROGRESS, DONE
    }
}
