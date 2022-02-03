package com.dm4nk.track_library_vaadin.domain;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Simple business object representing a Track
 */
@Entity
@Table(name = "tracks")

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Track extends NamedEntity implements Serializable {
    @ManyToOne
    @NotNull
    Author author;

    @ManyToOne
    @NotNull
    Album album;

    @NotNull
    LocalTime duration;

    @ManyToOne
    @NotNull
    Genre genre;

    @Lob
    Byte[] track;

    @Builder
    public Track(Integer id, String name, Author author, Album album, LocalTime duration, Genre genre, Byte[] track) {
        super(id, name);
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

        if (!Objects.equals(getId(), track.getId())) return false;
        if (!Objects.equals(getName(), track.getName())) return false;
        if (!Objects.equals(author, track.author)) return false;
        if (!Objects.equals(album, track.album)) return false;
        if (!Objects.equals(duration, track.duration)) return false;
        return Objects.equals(genre, track.genre);
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + (album != null ? album.hashCode() : 0);
        result = 31 * result + (duration != null ? duration.hashCode() : 0);
        result = 31 * result + (genre != null ? genre.hashCode() : 0);
        return result;
    }
}
