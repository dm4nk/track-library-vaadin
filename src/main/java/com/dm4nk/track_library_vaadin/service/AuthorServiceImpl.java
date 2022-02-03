package com.dm4nk.track_library_vaadin.service;

import com.dm4nk.track_library_vaadin.domain.Author;
import com.dm4nk.track_library_vaadin.repositiry.AuthorRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    @Transactional
    @Cacheable("authors")
    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    @Override
    public Optional<Author> findByName(String name) {
        return authorRepository.findByName(name);
    }

    @Override
    public Optional<Author> findById(Integer id) {
        return authorRepository.findById(id);
    }

    @Override
    public List<Author> findAllByNameLike(String template) {
        return authorRepository.findAllByNameLike(template);
    }

    @Override
    @Transactional
    @Caching(put = @CachePut("authors"), evict = @CacheEvict("tracks"))
    public Author save(Author author) {
        return authorRepository.save(author);
    }

    @Override
    @Transactional
    @CacheEvict("authors")
    public void delete(Author author) {
        authorRepository.delete(author);
    }
}
