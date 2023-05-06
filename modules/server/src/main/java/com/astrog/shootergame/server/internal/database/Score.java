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

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "score")
public class Score {

    @Id
    @Getter
    @GeneratedValue(generator = "increment")
    private Long id;

    @Getter
    private String name;

    @Getter
    @Setter
    @Column(name = "wins")
    private Long winsCount;
}
