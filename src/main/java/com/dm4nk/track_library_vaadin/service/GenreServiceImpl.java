package com.dm4nk.track_library_vaadin.service;

import com.dm4nk.track_library_vaadin.domain.Genre;
import com.dm4nk.track_library_vaadin.repositiry.GenreRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    public GenreServiceImpl(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    public List<Genre> findAll(){
        return genreRepository.findAll();
    }

    @Override
    public Optional<Genre> findByName(String name){
        return genreRepository.findByName(name);
    }

    @Override
    public Optional<Genre> findById(Integer id) {
        return genreRepository.findById(id);
    }

    @Override
    public List<Genre> findAllByNameLike(String template){
        return genreRepository.findAllByNameLike(template);
    }

    @Override
    public Genre save(Genre genre) {
        return genreRepository.save(genre);
    }

    @Override
    public void delete(Genre genre) {
        genreRepository.delete(genre);
    }
}
