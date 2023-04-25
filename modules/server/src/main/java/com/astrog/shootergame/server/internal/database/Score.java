package com.astrog.shootergame.server.internal.database;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.IncrementGenerator;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "score")
public class Score {

    @Id
    @Getter
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", type = IncrementGenerator.class)
    private Long id;

    @Getter
    private String name;

    @Getter
    @Setter
    @Column(name = "wins")
    private Long winsCount;
}
