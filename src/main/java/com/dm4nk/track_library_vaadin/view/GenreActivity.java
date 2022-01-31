package com.dm4nk.track_library_vaadin.view;

import com.dm4nk.track_library_vaadin.components.GenreEditor;
import com.dm4nk.track_library_vaadin.components.utility.ToolBar;
import com.dm4nk.track_library_vaadin.domain.Genre;
import com.dm4nk.track_library_vaadin.repositiry.GenreRepository;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("/genres")
public class GenreActivity extends VerticalLayout {
    private final GenreRepository genreRepository;
    private final GenreEditor genreEditor;
    private final Grid<Genre> grid = new Grid<>();
    private ToolBar toolBar = null;

    public GenreActivity(GenreRepository genreRepository, GenreEditor genreEditor) {
        this.genreRepository = genreRepository;
        this.genreEditor = genreEditor;
        this.toolBar = new ToolBar(
                "Tracks",
                VaadinIcon.MUSIC,
                event -> showGenres((String) event.getValue()),
                event -> genreEditor.editGenre(Genre.builder().build()),
                event -> UI.getCurrent().navigate("/tracks"),
                event -> showGenres(toolBar.getFilter().getValue())
        );

        add(toolBar, createGrid());

        genreEditor.setChangeHandler(() -> showGenres(toolBar.getFilter().getValue()));

        showGenres("");
    }

    private Grid<Genre> createGrid() {
        grid.setItems(genreRepository.findAll());
        grid.addColumn(Genre::getName)
                .setSortable(true)
                .setHeader("name");

        grid.asSingleSelect().addValueChangeListener(event -> genreEditor.editGenre(event.getValue()));

        return grid;
    }

    private void showGenres(String template) {
        if (template.isEmpty()) {
            grid.setItems(genreRepository.findAll());
        } else {
            grid.setItems(genreRepository.findAllByNameLike(template));
        }
    }
}
