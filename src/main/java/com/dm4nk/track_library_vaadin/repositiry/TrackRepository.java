package com.dm4nk.track_library_vaadin.repositiry;

import com.dm4nk.track_library_vaadin.domain.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository class for {@link Track} domain objects
 */
@Repository
public interface TrackRepository extends JpaRepository<Track, Integer> {
    @Query("select t from Track t left join t.genre where t.name like %?1% or " +
            "t.album like %?1% or " +
            "t.author like %?1% or " +
            "t.genre.name like %?1%")
    List<Track> findAllByNameAlbumAuthorLike(String template);

    @Query("select t from Track t left join t.genre where t.genre.name = ?1")
    List<Track> findByGenre(String genre);
}
