package com.dm4nk.track_library_vaadin.view;

import com.dm4nk.track_library_vaadin.components.GenreEditor;
import com.dm4nk.track_library_vaadin.domain.Genre;
import com.dm4nk.track_library_vaadin.repositiry.GenreRepository;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;

@Route("/genres")
public class GenreActivity extends VerticalLayout {
    private final GenreRepository genreRepository;

    private Grid<Genre> grid = new Grid<>();

    private final Button backButton = new Button("Tracks", VaadinIcon.MUSIC.create());
    private final TextField filter = new TextField("", "Type to filter");
    private final Button addNewButton = new Button("Add new");
    private HorizontalLayout toolBar = new HorizontalLayout(backButton, filter, addNewButton);

    private final GenreEditor genreEditor;

    public GenreActivity(GenreRepository genreRepository, GenreEditor genreEditor) {
        this.genreRepository = genreRepository;
        this.genreEditor = genreEditor;

        add(toolBar, grid, genreEditor);

        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(event -> showGenres(event.getValue()));

        initGrid(genreRepository, genreEditor);

        addNewButton.addClickListener(event -> genreEditor.editGenre(Genre.builder().build()));

        backButton.addClickListener(e -> UI.getCurrent().navigate("/tracks"));

        genreEditor.setChangeHandler(() ->{
            genreEditor.setVisible(false);
            showGenres(filter.getValue());
        });

        showGenres("");
    }

    private void initGrid(GenreRepository genreRepository, GenreEditor genreEditor) {
        grid.setItems(genreRepository.findAll());
        grid.addColumn(Genre::getName)
                .setSortable(true)
                .setHeader("name");

        grid.asSingleSelect().addValueChangeListener(event -> {
            genreEditor.editGenre(event.getValue());
        });
    }

    private void showGenres(String template){
        if(template.isEmpty()){
            grid.setItems(genreRepository.findAll());
        }
        else {
            grid.setItems(genreRepository.findAllByNameLike(template));
        }
    }
}
