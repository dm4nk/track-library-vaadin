package com.dm4nk.track_library_vaadin.components.genre;

import com.dm4nk.track_library_vaadin.components.utility.WidthHeightSetter;
import com.dm4nk.track_library_vaadin.domain.Genre;
import com.dm4nk.track_library_vaadin.service.GenreService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Getter;
import lombok.Setter;

@SpringComponent
@UIScope
public class ShowGenresComponent extends VerticalLayout {
    private final Dialog dialog = new Dialog();
    private final GenreService genreService;
    @Getter
    private final EditGenreComponent editGenreComponent;
    private final Button addNewButton = new Button("Add", VaadinIcon.ADD_DOCK.create());
    private final TextField filter = new TextField("", "Search in Genres");
    private final HorizontalLayout toolbar = new HorizontalLayout();
    private Grid<Genre> grid;
    @Setter
    private ClickHandler clickHandler;

    public ShowGenresComponent(GenreService genreService, EditGenreComponent editGenreComponent) {
        this.genreService = genreService;
        this.editGenreComponent = editGenreComponent;

        WidthHeightSetter.setWidthHeight(this);

        toolbar.add(addNewButton);
        toolbar.addAndExpand(filter);

        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(event -> showGenres(event.getValue()));
        addNewButton.addClickListener(event -> {
            dialog.close();
            editGenreComponent.editGenre(Genre.builder().build());
        });
    }

    private void initGrid() {
        grid.addColumn(
                        new ComponentRenderer<>(Button::new, (button, genre) -> {
                            button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                            button.addClickListener(e -> {
                                dialog.close();
                                this.clickHandler.onClick(genre);
                            });
                            button.setText(genre.getName());
                        }))
                .setHeader("Name");

        grid.addItemClickListener(event -> {
            dialog.close();
            editGenreComponent.editGenre(event.getItem());
        });
    }

    private void showGenres(String template) {
        grid.setItems(genreService.findAllByNameLike(template));
    }

    //todo: needed?
    public void initComponent(Genre... genre) {
        grid = new Grid<>();
        removeAll();
        add(toolbar, grid);
        initGrid();

        if (genre == null)
            showGenres(filter.getValue());
        else
            grid.setItems(genre);

        dialog.open();
        dialog.add(this);
    }

    public void initComponent() {
        initComponent(null);
    }

    public interface ClickHandler {
        void onClick(Genre genre);
    }
}
