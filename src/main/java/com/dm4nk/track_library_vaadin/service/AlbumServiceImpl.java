package com.dm4nk.track_library_vaadin.service;

import com.dm4nk.track_library_vaadin.domain.Album;
import com.dm4nk.track_library_vaadin.domain.Author;
import com.dm4nk.track_library_vaadin.repositiry.AlbumRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class AlbumServiceImpl implements AlbumService {
    private final AlbumRepository albumRepository;
    private final AuthorService authorService;

    public AlbumServiceImpl(AlbumRepository albumRepository, AuthorService authorService) {
        this.albumRepository = albumRepository;
        this.authorService = authorService;
    }

    @Override
    @Transactional
    @Cacheable("albums")
    public List<Album> findAll() {
        return albumRepository.findAll();
    }

    @Override
    public Optional<Album> findByName(String name) {
        return albumRepository.findByName(name);
    }

    @Override
    public Optional<Album> findById(Integer id) {
        return albumRepository.findById(id);
    }

    @Override
    public List<Album> findAllByNameLike(String template) {
        return albumRepository.findAllByNameLike(template);
    }

    @Override
    @Transactional
    @Caching(put = @CachePut("albums"), evict = @CacheEvict("tracks"))
    public Album save(Album album) {
        return albumRepository.save(album);
    }

    @Override
    @Transactional
    @CacheEvict("albums")
    public void delete(Album album) {
        Author author = album.getAuthor();
        if (author.getId() == null) {
            throw new RuntimeException("Author without id");
        }
        author.getAlbums().remove(album);

        authorService.save(author);
        albumRepository.delete(album);
    }
}
