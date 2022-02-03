package com.dm4nk.track_library_vaadin.service;

import com.dm4nk.track_library_vaadin.domain.Album;

import java.util.List;
import java.util.Optional;

public interface AlbumService {
    List<Album> findAll();

    Optional<Album> findByName(String name);

    Optional<Album> findById(Integer id);

    List<Album> findAllByNameLike(String template);

    Album save(Album author);

    void delete(Album author);
}
