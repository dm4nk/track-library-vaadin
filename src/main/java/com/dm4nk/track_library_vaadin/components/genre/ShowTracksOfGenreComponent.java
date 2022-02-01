package com.dm4nk.track_library_vaadin.components.genre;

import com.dm4nk.track_library_vaadin.components.author.ShowTracksOfAuthorComponent;
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
public class ShowTracksOfGenreComponent extends VerticalLayout {
    private final ShowTracksOfAuthorComponent showTracksOfAuthorComponent;
    private final Dialog dialog = new Dialog();
    private final Label label = new Label();
    private Grid<Track> grid;
    @Setter
    private ClickHandler clickHandler;

    public ShowTracksOfGenreComponent(@Lazy ShowTracksOfAuthorComponent showTracksOfAuthorComponent) {
        this.showTracksOfAuthorComponent = showTracksOfAuthorComponent;
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
                                this.showTracksOfAuthor(track.getAuthor());
                            });
                            button.setText(track.getAuthor().getName());
                        }))
                .setHeader("Author");

        grid.addItemClickListener(event -> {
            Track track = event.getItem();

            if (track != null) {
                clickHandler.onClick(event.getItem().getId());
                dialog.close();
            }
        });
    }

    public void initComponent(Genre genre) {
        grid = new Grid<>();
        removeAll();
        label.setText(genre.getName());
        add(label, grid);
        initGrid();

        grid.setItems(genre.getTracks());

        dialog.open();
        dialog.add(this);
    }

    private void showTracksOfAuthor(Author author) {
        showTracksOfAuthorComponent.initComponent(author);
    }

    public interface ClickHandler {
        void onClick(Integer trackId);
    }
}
