package com.dm4nk.track_library_vaadin.domain;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalTime;
import java.util.Objects;

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
    @NotNull
    LocalTime duration;
    @ManyToOne
    @NotNull
    Genre genre;
    @Lob
    Byte[] track;

    @Builder
    public Track(Integer id, String name, String author, String album, LocalTime duration, Genre genre, Byte[] track) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.album = album;
        this.duration = duration;
        this.genre = genre;
        this.track = track;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Track track = (Track) o;
        return id != null && Objects.equals(id, track.id);
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
