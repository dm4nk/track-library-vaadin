package com.dm4nk.track_library_vaadin.components;

import com.dm4nk.track_library_vaadin.domain.Track;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Setter;

import java.util.Set;

@SpringComponent
@UIScope
public class ShowTracksOfGenreComponent extends FormLayout {
    private final Dialog dialog = new Dialog();
    private Grid<Track> grid;
    @Setter
    private ClickHandler clickHandler;

    public ShowTracksOfGenreComponent() {
        setWidth("600px");
        setHeight("400px");
    }

    private void initGrid() {
        grid.addColumn(Track::getName).setSortable(true).setHeader("Name");
        grid.addColumn(Track::getAuthor).setHeader("Author");

        grid.addItemClickListener(event -> {
            Track track = event.getItem();

            if (track != null) {
                clickHandler.onClick(event.getItem().getId());
                dialog.close();
            }
        });
    }

    public void initComponent(Set<Track> trackList) {
        grid = new Grid<>();
        removeAll();
        add(grid);
        initGrid();

        grid.setItems(trackList);

        dialog.open();
        dialog.add(this);
    }

    public interface ClickHandler {
        void onClick(Integer trackId);
    }
}
