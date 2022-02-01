package com.dm4nk.track_library_vaadin.repositiry;

import com.dm4nk.track_library_vaadin.domain.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository class for {@link Author} domain objects
 */
@Repository
public interface AuthorRepository extends JpaRepository<Author, Integer> {
    List<Author> findAll();

    /**
     * Retrieve {@link Author} from data store with given name
     *
     * @param name Value to search for
     * @return Single optional of genre with matching name (as name is unique column in {@link Author})
     */
    @Query("select g from Author g where g.name = ?1")
    Optional<Author> findByName(String name);

    @Query("select g from Author g where g.name like %?1%")
    List<Author> findAllByNameLike(String template);
}
