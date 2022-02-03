package com.dm4nk.track_library_vaadin.service;

import com.dm4nk.track_library_vaadin.domain.Album;
import com.dm4nk.track_library_vaadin.domain.Author;
import com.dm4nk.track_library_vaadin.domain.Genre;
import com.dm4nk.track_library_vaadin.domain.Track;
import com.dm4nk.track_library_vaadin.repositiry.TrackRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class TrackServiceImpl implements TrackService {
    private final TrackRepository trackRepository;
    private final GenreService genreService;
    private final AuthorService authorService;
    private final AlbumService albumService;

    public TrackServiceImpl(TrackRepository trackRepository, GenreService genreService, AuthorService authorService, AlbumService albumService) {
        this.trackRepository = trackRepository;
        this.genreService = genreService;
        this.authorService = authorService;
        this.albumService = albumService;
    }

    @Override
    @Transactional
    @Caching(cacheable = @Cacheable("tracks"),
            evict = {@CacheEvict("tracks"), @CacheEvict("genres"), @CacheEvict("albums"), @CacheEvict("authors")})
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
    @Transactional
    @Caching(put = @CachePut("tracks"),
            evict = {@CacheEvict("genres"), @CacheEvict("albums"), @CacheEvict("authors"),})
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

        Album album = track.getAlbum();
        if (album.getId() == null) {
            throw new RuntimeException("Album without id");
        }
        album.getTracks().add(track);
        album.setAuthor(author);

        return trackRepository.save(track);
    }

    @Override
    @Transactional
    @CacheEvict({"tracks", "genres", "albums", "authors"})
    public void delete(Track track) {
        Genre genre = track.getGenre();
        if (genre.getId() == null) {
            throw new RuntimeException("Genre without id");
        }
        genre.getTracks().remove(track);

        Album album = track.getAlbum();
        if (album.getId() == null) {
            throw new RuntimeException("Album without id");
        }
        album.getTracks().remove(track);

        Author author = track.getAuthor();
        if (author.getId() == null) {
            throw new RuntimeException("Author without id");
        }
        author.getTracks().remove(track);

        genreService.save(genre);
        authorService.save(author);
        albumService.save(album);

        trackRepository.delete(track);
    }
}
