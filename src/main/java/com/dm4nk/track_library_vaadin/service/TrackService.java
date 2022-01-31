package com.dm4nk.track_library_vaadin.service;

import com.dm4nk.track_library_vaadin.domain.Track;

import java.util.List;
import java.util.Optional;

public interface TrackService {
    List<Track> findAll();

    List<Track> findAllByNameAlbumAuthorLike(String template);

    Optional<Track> findById(Integer id);

    Track save(Track track);

    void delete(Track track);
}
