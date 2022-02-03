package com.dm4nk.track_library_vaadin.components.utility;

import com.dm4nk.track_library_vaadin.domain.NamedEntity;
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

import java.util.Collection;

@SpringComponent
@UIScope
public class ShowTracksOfNamedEntityComponent<T extends NamedEntity> extends VerticalLayout {
    private final Dialog dialog = new Dialog();
    private final Label label = new Label();
    private Grid<Track> grid;
    @Setter
    private ClickHandler clickHandler;

    public ShowTracksOfNamedEntityComponent() {
        WidthHeightSetter.setWidthHeight(this);
    }

    private void initGrid() {
        grid.addColumn(Track::getName).setSortable(true).setHeader("Name");
        grid.addColumn(
                        new ComponentRenderer<>(Button::new, (button, track) -> {
                            button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                            button.addClickListener(e -> {
                                dialog.close();
                                this.initComponent((T) track.getGenre(), track.getGenre().getTracks());
                            });
                            button.setText(track.getGenre().getName());
                        }))
                .setHeader("Genre");
        grid.addColumn(
                        new ComponentRenderer<>(Button::new, (button, track) -> {
                            button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                            button.addClickListener(e -> {
                                dialog.close();
                                this.initComponent((T) track.getAuthor(), track.getAuthor().getTracks());
                            });
                            button.setText(track.getAuthor().getName());
                        }))
                .setHeader("Author");
        grid.addColumn(
                        new ComponentRenderer<>(Button::new, (button, track) -> {
                            button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                            button.addClickListener(e -> {
                                dialog.close();
                                this.initComponent((T) track.getAlbum(), track.getAlbum().getTracks());
                            });
                            button.setText(track.getAlbum().getName());
                        }))
                .setHeader("Album");

        grid.addItemClickListener(event -> {
            Track track = event.getItem();

            if (track != null) {
                clickHandler.onClick(event.getItem().getId());
                dialog.close();
            }
        });
    }

    public void initComponent(T genre, Collection<Track> tracks) {
        grid = new Grid<>();
        removeAll();
        label.setText(genre.getName());
        add(label, grid);
        initGrid();

        grid.setItems(tracks);

        dialog.open();
        dialog.add(this);
    }

    public interface ClickHandler {
        void onClick(Integer trackId);
    }
}
