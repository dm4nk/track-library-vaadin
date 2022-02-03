package com.dm4nk.track_library_vaadin.repositiry;

import com.dm4nk.track_library_vaadin.domain.Album;
import com.dm4nk.track_library_vaadin.domain.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository class for {@link Album} domain objects
 */
@Repository
public interface AlbumRepository extends JpaRepository<Album, Integer> {
    List<Album> findAll();

    /**
     * Retrieve {@link Author} from data store with given name
     *
     * @param name Value to search for
     * @return Single optional of genre with matching name (as name is unique column in {@link Album})
     */
    @Query("select g from Album g where g.name = ?1")
    Optional<Album> findByName(String name);

    @Query("select g from Album g where g.name like %?1%")
    List<Album> findAllByNameLike(String template);
}
