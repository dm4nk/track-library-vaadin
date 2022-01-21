package com.dm4nk.track_library_vaadin.domain;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Simple business object representing a Track
 */
@Entity

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Track implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @NotBlank
    String name;
    @NotBlank
    String author;
    @NotBlank
    String album;
    @Digits(fraction = 0, integer = 6)
    @NotNull
    Integer duration;
    @ManyToOne
    @NotNull
    Genre genre;

    @Builder
    public Track(Integer id, String name, String author, String album, Integer duration, Genre genre) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.album = album;
        this.duration = duration;
        this.genre = genre;
    }
}