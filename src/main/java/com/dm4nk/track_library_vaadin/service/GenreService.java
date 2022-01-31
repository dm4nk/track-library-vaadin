package com.dm4nk.track_library_vaadin.service;

import com.dm4nk.track_library_vaadin.domain.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreService {
    List<Genre> findAll();

    Optional<Genre> findByName(String name);

    Optional<Genre> findById(Integer id);

    List<Genre> findAllByNameLike(String template);

    Genre save(Genre genre);

    void delete(Genre genre);
}
