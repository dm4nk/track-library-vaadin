package com.dm4nk.track_library_vaadin.service;

import com.dm4nk.track_library_vaadin.domain.Genre;
import com.dm4nk.track_library_vaadin.domain.Track;
import com.dm4nk.track_library_vaadin.repositiry.TrackRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrackServiceImpl implements TrackService {
    private final TrackRepository trackRepository;
    private final GenreService genreService;

    public TrackServiceImpl(TrackRepository trackRepository, GenreService genreService) {
        this.trackRepository = trackRepository;
        this.genreService = genreService;
    }

    @Override
    public List<Track> findAll() {
        return trackRepository.findAll();
    }

    @Override
    public List<Track> findAllByNameAlbumAuthorLike(String template) {
        return trackRepository.findAllByNameAlbumAuthorLike(template);
    }

    @Override
    public Optional<Track> findById(Integer id) {
        return trackRepository.findById(id);
    }

    @Override
    public Track save(Track track) {
        Genre genre = track.getGenre();
        if(genre.getId() == null){
            throw new RuntimeException("Genre without id");
        }
        genre.getTracks().add(track);

        Track savedTrack = trackRepository.save(track);
        genreService.save(genre);

        return savedTrack;
    }

    @Override
    public void delete(Track track) {
        Genre genre = track.getGenre();
        if(genre.getId() == null){
            throw new RuntimeException("Genre without id");
        }
        genre.getTracks().remove(track);
        genreService.save(genre);
        trackRepository.delete(track);
    }
}
