package com.dm4nk.track_library_vaadin.service;

import com.dm4nk.track_library_vaadin.domain.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorService {
    List<Author> findAll();

    Optional<Author> findByName(String name);

    Optional<Author> findById(Integer id);

    List<Author> findAllByNameLike(String template);

    Author save(Author author);

    void delete(Author author);
}
