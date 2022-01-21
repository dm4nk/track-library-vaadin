package com.dm4nk.track_library_vaadin.view;

import com.dm4nk.track_library_vaadin.components.TrackEditor;
import com.dm4nk.track_library_vaadin.domain.Track;
import com.dm4nk.track_library_vaadin.repositiry.TrackRepository;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@Route("/tracks")
@RouteAlias("")
public class TrackActivity extends VerticalLayout {
    private final TrackRepository trackRepository;
    private final Button backButton = new Button("Genres", VaadinIcon.HEART.create());
    private final TextField filter = new TextField("", "Type to filter");
    private final Button addNewButton = new Button("Add", VaadinIcon.ADD_DOCK.create());
    private final Button toggleButton = new Button(VaadinIcon.MOON.create());
    private final TrackEditor trackEditor;
    private final Grid<Track> grid = new Grid<>();
    private final HorizontalLayout toolBar = new HorizontalLayout();


    public TrackActivity(TrackRepository trackRepository, TrackEditor trackEditor) {
        this.trackRepository = trackRepository;
        this.trackEditor = trackEditor;

        initToolbar();

        initGrid(trackRepository);

        add(toolBar, grid);

        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(event -> showTracks(event.getValue()));

        addNewButton.addClickListener(event -> trackEditor.editTrack(Track.builder().build()));

        backButton.addClickListener(e -> UI.getCurrent().navigate("/genres"));

        trackEditor.setChangeHandler(() -> showTracks(filter.getValue()));

        toggleButton.addClickListener(click -> {
            ThemeList themeList = UI.getCurrent().getElement().getThemeList();

            if (themeList.contains(Lumo.DARK)) {
                themeList.remove(Lumo.DARK);
            } else {
                themeList.add(Lumo.DARK);
            }
        });

        showTracks("");
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

    private void showTracks(String template) {
        if (template.isEmpty()) {
            grid.setItems(trackRepository.findAll());
        } else {
            grid.setItems(trackRepository.findAllByNameAlbumAuthorLike(template));
        }
    }
}
