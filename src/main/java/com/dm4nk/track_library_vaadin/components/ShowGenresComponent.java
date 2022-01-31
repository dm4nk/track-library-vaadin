package com.dm4nk.track_library_vaadin.components;

import com.dm4nk.track_library_vaadin.domain.Genre;
import com.dm4nk.track_library_vaadin.service.GenreService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Getter;

@SpringComponent
@UIScope
public class ShowGenresComponent extends VerticalLayout {
    private final Dialog dialog = new Dialog();
    private final GenreService genreService;
    private Grid<Genre> grid;
    @Getter
    private final GenreEditor genreEditor;

    private final Button addNewButton = new Button("Add", VaadinIcon.ADD_DOCK.create());
    private final TextField filter = new TextField("", "Type to filter");
    private final HorizontalLayout toolbar = new HorizontalLayout();

    public ShowGenresComponent(GenreService genreService, GenreEditor genreEditor) {
        this.genreService = genreService;
        this.genreEditor = genreEditor;
        setWidth("600px");
        setHeight("400px");

        toolbar.add(addNewButton);
        toolbar.addAndExpand(filter);

        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(event -> showGenres(event.getValue()));
        addNewButton.addClickListener(event -> {
            dialog.close();
            genreEditor.editGenre(Genre.builder().build());
        });
    }

    private void initGrid() {
        grid.addColumn(Genre::getName).setSortable(true).setHeader("Name");

        grid.addItemClickListener(event -> {
            dialog.close();
            genreEditor.editGenre(event.getItem());
        });
    }

    private void showGenres(String template){
        grid.setItems(genreService.findAllByNameLike(template));
    }

    public void initComponent() {
        grid = new Grid<>();
        removeAll();
        add(toolbar, grid);
        initGrid();

        showGenres(filter.getValue());

        genreEditor.setChangeHandler(this::initComponent);

        dialog.open();
        dialog.add(this);
    }
}
