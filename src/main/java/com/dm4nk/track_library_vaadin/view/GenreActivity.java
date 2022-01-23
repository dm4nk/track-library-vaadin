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
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.Lumo;

@Route("/genres")
public class GenreActivity extends VerticalLayout {
    private final GenreRepository genreRepository;
    private final Button backButton = new Button("Tracks", VaadinIcon.MUSIC.create());
    private final TextField filter = new TextField("", "Type to filter");
    private final Button addNewButton = new Button("Add", VaadinIcon.ADD_DOCK.create());
    private final Button toggleButton = new Button(VaadinIcon.MOON.create());
    private final HorizontalLayout toolBar = new HorizontalLayout();
    private final GenreEditor genreEditor;
    private final Grid<Genre> grid = new Grid<>();

    public GenreActivity(GenreRepository genreRepository, GenreEditor genreEditor) {
        this.genreRepository = genreRepository;
        this.genreEditor = genreEditor;

        initToolbar();

        add(toolBar, grid);

        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(event -> showGenres(event.getValue()));

        initGrid(genreRepository, genreEditor);

        addNewButton.addClickListener(event -> genreEditor.editGenre(Genre.builder().build()));

        backButton.addClickListener(e -> UI.getCurrent().navigate("/tracks"));

        genreEditor.setChangeHandler(() -> showGenres(filter.getValue()));

        toggleButton.addClickListener(click -> {
            ThemeList themeList = UI.getCurrent().getElement().getThemeList();

            if (themeList.contains(Lumo.DARK)) {
                themeList.remove(Lumo.DARK);
            } else {
                themeList.add(Lumo.DARK);
            }
        });

        showGenres("");
    }

    private void initToolbar() {
        toolBar.add(backButton, addNewButton);
        toolBar.addAndExpand(filter);
        toolBar.add(toggleButton);
        toolBar.setAlignSelf(Alignment.STRETCH, filter);
        toolBar.setAlignSelf(Alignment.START, backButton);
        toolBar.setAlignSelf(Alignment.START, addNewButton);
        toolBar.setAlignSelf(Alignment.END, toggleButton);
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

    private void showGenres(String template) {
        if (template.isEmpty()) {
            grid.setItems(genreRepository.findAll());
        } else {
            grid.setItems(genreRepository.findAllByNameLike(template));
        }
    }
}
