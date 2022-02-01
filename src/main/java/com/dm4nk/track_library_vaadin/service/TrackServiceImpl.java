package com.dm4nk.track_library_vaadin.service;

import com.dm4nk.track_library_vaadin.domain.Author;
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
    private final AuthorService authorService;

    public TrackServiceImpl(TrackRepository trackRepository, GenreService genreService, AuthorService authorService) {
        this.trackRepository = trackRepository;
        this.genreService = genreService;
        this.authorService = authorService;
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
        if (genre.getId() == null) {
            throw new RuntimeException("Genre without id");
        }
        genre.getTracks().add(track);

        Author author = track.getAuthor();
        if (author.getId() == null) {
            throw new RuntimeException("Author without id");
        }
        author.getTracks().add(track);

        Track savedTrack = trackRepository.save(track);

        return savedTrack;
    }

    @Override
    public void delete(Track track) {
        Genre genre = track.getGenre();
        if (genre.getId() == null) {
            throw new RuntimeException("Genre without id");
        }
        genre.getTracks().remove(track);

        Author author = track.getAuthor();
        if (author.getId() == null) {
            throw new RuntimeException("Author without id");
        }
        author.getTracks().remove(track);

        trackRepository.delete(track);
    }
}
