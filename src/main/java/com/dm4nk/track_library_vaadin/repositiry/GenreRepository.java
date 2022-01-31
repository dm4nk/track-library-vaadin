package com.dm4nk.track_library_vaadin.repositiry;

import com.dm4nk.track_library_vaadin.domain.Genre;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Repository class for {@link Genre} domain objects
 */
@Repository
public interface GenreRepository extends JpaRepository<Genre, Integer> {
    List<Genre> findAll();

    /**
     * Retrieve {@link Genre} from data store with given name
     *
     * @param name Value to search for
     * @return Single optional of genre with matching name (as name is unique column in {@link Genre})
     */
    @Query("select g from Genre g where g.name = ?1")
    Optional<Genre> findByName(String name);

    @Query("select g from Genre g where g.name like %?1%")
    List<Genre> findAllByNameLike(String template);
}
