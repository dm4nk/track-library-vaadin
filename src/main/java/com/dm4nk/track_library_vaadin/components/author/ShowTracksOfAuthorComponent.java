package com.dm4nk.track_library_vaadin.components.author;

import com.dm4nk.track_library_vaadin.components.genre.ShowTracksOfGenreComponent;
import com.dm4nk.track_library_vaadin.domain.Author;
import com.dm4nk.track_library_vaadin.domain.Genre;
import com.dm4nk.track_library_vaadin.domain.Track;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Setter;
import org.springframework.context.annotation.Lazy;

@SpringComponent
@UIScope
public class ShowTracksOfAuthorComponent extends VerticalLayout {
    private final ShowTracksOfGenreComponent showTracksOfGenreComponent;
    private final Dialog dialog = new Dialog();
    private final Label label = new Label();
    private Grid<Track> grid;
    @Setter
    private ShowTracksOfGenreComponent.ClickHandler clickHandler;

    public ShowTracksOfAuthorComponent(@Lazy ShowTracksOfGenreComponent showTracksOfGenreComponent) {
        this.showTracksOfGenreComponent = showTracksOfGenreComponent;
        setWidth("600px");
        setHeight("400px");
    }

    private void initGrid() {
        grid.addColumn(Track::getName).setSortable(true).setHeader("Name");
        grid.addColumn(
                        new ComponentRenderer<>(Button::new, (button, track) -> {
                            button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                            button.addClickListener(e -> {
                                dialog.close();
                                this.showTracksOfGenre(track.getGenre());
                            });
                            button.setText(track.getGenre().getName());
                        }))
                .setHeader("Genre");

        grid.addItemClickListener(event -> {
            Track track = event.getItem();

            if (track != null) {
                clickHandler.onClick(event.getItem().getId());
                dialog.close();
            }
        });
    }

    private void showTracksOfGenre(Genre genre) {
        showTracksOfGenreComponent.initComponent(genre);
    }

    public void initComponent(Author author) {
        grid = new Grid<>();
        removeAll();
        label.setText(author.getName());
        add(label, grid);
        initGrid();

        grid.setItems(author.getTracks());

        dialog.open();
        dialog.add(this);
    }

    public interface ClickHandler {
        void onClick(Integer trackId);
    }
}
