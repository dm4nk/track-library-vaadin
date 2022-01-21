package com.dm4nk.track_library_vaadin.view;

import com.dm4nk.track_library_vaadin.components.TrackEditor;
import com.dm4nk.track_library_vaadin.domain.Track;
import com.dm4nk.track_library_vaadin.repositiry.TrackRepository;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@Route("/tracks")
@RouteAlias("")
public class TrackActivity extends VerticalLayout {
    private final TrackRepository trackRepository;

    private Grid<Track> grid = new Grid<>();

    private final Button backButton = new Button("Genres", VaadinIcon.HEART.create());
    private final TextField filter = new TextField("", "Type to filter");
    private final Button addNewButton = new Button("Add new");
    private HorizontalLayout toolBar = new HorizontalLayout(backButton, filter, addNewButton);

    private final TrackEditor trackEditor;


    public TrackActivity(TrackRepository trackRepository, TrackEditor trackEditor) {
        this.trackRepository = trackRepository;
        this.trackEditor = trackEditor;

        initGrid(trackRepository);

        add(toolBar, grid, trackEditor);

        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(event -> showTracks(event.getValue()));

        addNewButton.addClickListener(event -> trackEditor.editTrack(Track.builder().build()));

        backButton.addClickListener(e -> UI.getCurrent().navigate("/genres"));

        trackEditor.setChangeHandler(() ->{
            trackEditor.setVisible(false);
            showTracks(filter.getValue());
        });

        showTracks("");
    }

    private void initGrid(TrackRepository trackRepository) {
        grid.setItems(trackRepository.findAll());
        grid.addColumn(Track::getName).setSortable(true).setHeader("Name");
        grid.addColumn(Track::getAuthor).setHeader("Author");
        grid.addColumn(Track::getAlbum).setHeader("Album");
        grid.addColumn(Track::getDuration).setSortable(true).setHeader("Duration");
        grid.addColumn(Track::getGenre).setSortable(true).setHeader("Genre");

        grid.asSingleSelect().addValueChangeListener(event -> {
            trackEditor.editTrack(event.getValue());
        });
    }

    private void showTracks(String template){
        if(template.isEmpty()){
            grid.setItems(trackRepository.findAll());
        }
        else {
            grid.setItems(trackRepository.findAllByNameAlbumAuthorLike(template));
        }
    }
}
