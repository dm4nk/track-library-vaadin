package com.dm4nk.track_library_vaadin.domain;

import lombok.*;
import lombok.experimental.FieldDefaults;

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
        if (!(o instanceof Track track)) return false;

        if (!Objects.equals(id, track.id)) return false;
        if (!Objects.equals(name, track.name)) return false;
        if (!Objects.equals(author, track.author)) return false;
        if (!Objects.equals(album, track.album)) return false;
        if (!Objects.equals(duration, track.duration)) return false;
        return Objects.equals(genre, track.genre);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + (album != null ? album.hashCode() : 0);
        result = 31 * result + (duration != null ? duration.hashCode() : 0);
        result = 31 * result + (genre != null ? genre.hashCode() : 0);
        return result;
    }
}
