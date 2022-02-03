package com.dm4nk.track_library_vaadin.domain;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "albums")


@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Album extends NamedEntity {

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "album")
    List<Track> tracks = new ArrayList<>();

    @ManyToOne
    //@NotNull
    Author author;

    @Builder
    public Album(Integer id, String name, List<Track> tracks, Author author) {
        super(id, name);
        this.tracks = tracks == null ? new ArrayList<>() : tracks;
        this.author = author;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Author author)) return false;

        if (!Objects.equals(getId(), author.getId())) return false;
        return Objects.equals(getName(), author.getName());
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        return result;
    }
}
