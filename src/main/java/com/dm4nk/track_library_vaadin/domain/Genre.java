package com.dm4nk.track_library_vaadin.domain;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Simple business object representing a Genre
 */
@Entity
@Table(name = "genres")


@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Genre extends NamedEntity implements Serializable {
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "genre")
    List<Track> tracks = new ArrayList<>();

    @Builder
    public Genre(Integer id, String name, List<Track> tracks) {
        super(id, name);
        this.tracks = tracks == null ? new ArrayList<>() : tracks;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Genre genre)) return false;

        if (!Objects.equals(getId(), genre.getId())) return false;
        return Objects.equals(getName(), genre.getName());
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        return result;
    }
}
