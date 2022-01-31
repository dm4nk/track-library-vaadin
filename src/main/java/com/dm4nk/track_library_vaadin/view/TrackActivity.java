package com.dm4nk.track_library_vaadin.view;

import com.dm4nk.track_library_vaadin.components.ShowTracksOfGenreComponent;
import com.dm4nk.track_library_vaadin.components.TrackEditor;
import com.dm4nk.track_library_vaadin.components.utility.ToolBar;
import com.dm4nk.track_library_vaadin.converters.WrappedByteArrayToByteArray;
import com.dm4nk.track_library_vaadin.domain.Genre;
import com.dm4nk.track_library_vaadin.domain.Track;
import com.dm4nk.track_library_vaadin.repositiry.TrackRepository;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.Lumo;
import lombok.extern.slf4j.Slf4j;

import javax.tools.Tool;
import java.io.ByteArrayInputStream;

@Slf4j

@Route("/tracks")
@RouteAlias("")

public class TrackActivity extends VerticalLayout {
    private final TrackRepository trackRepository;

    private final TrackEditor trackEditor;
    private final ShowTracksOfGenreComponent showTracksOfGenreComponent;

    private final Grid<Track> grid = new Grid<>();
    private final Notification notification = new Notification();
    private ToolBar toolBar = null;

    public TrackActivity(TrackRepository trackRepository, TrackEditor trackEditor, ShowTracksOfGenreComponent showTracksOfGenreComponent) {
        this.trackRepository = trackRepository;
        this.trackEditor = trackEditor;
        this.showTracksOfGenreComponent = showTracksOfGenreComponent;

        configureNotification();

        toolBar = new ToolBar(
                "Genres",
                VaadinIcon.HEART,
                event -> showTracks((String) event.getValue()),
                event -> trackEditor.editTrack(Track.builder().build()),
                e -> UI.getCurrent().navigate("/genres"),
                click -> showTracks(toolBar.getFilter().getValue()));

        add(toolBar, createGrid());

        trackEditor.setChangeHandler(() -> showTracks(toolBar.getFilter().getValue()));
        showTracksOfGenreComponent.setClickHandler(toolBar.getFilter()::setValue);

        showTracks("");
    }

    private Grid<Track> createGrid() {
        grid.setItems(trackRepository.findAll());
        grid.addColumn(Track::getName).setSortable(true).setHeader("Name");
        grid.addColumn(Track::getAuthor).setHeader("Author");
        grid.addColumn(Track::getAlbum).setHeader("Album");
        grid.addColumn(Track::getDuration)
                .setSortable(true)
                .setHeader("Duration");
        grid.addColumn(
                        new ComponentRenderer<>(Button::new, (button, track) -> {
                            button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                            button.addClickListener(e -> this.showTracksOfGenre(track.getGenre()));
                            button.setText(track.getGenre().getName());
                        }))
                .setHeader("Genre");
        grid.addColumn(
                        new ComponentRenderer<>(Button::new, (button, track) -> {
                            button.addThemeVariants(ButtonVariant.LUMO_ICON,
                                    ButtonVariant.LUMO_PRIMARY);
                            button.addClickListener(e -> this.download(track));
                            button.setIcon(new Icon(VaadinIcon.DOWNLOAD));
                        }))
                .setHeader("Download");

        grid.asSingleSelect().addValueChangeListener(event -> trackEditor.editTrack(event.getValue()));

        return grid;
    }

    private void configureNotification() {
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.BOTTOM_CENTER);
        notification.setDuration(1200);
    }

    private void download(Track track) {
        notification.setText("Downloading " + track.getName());
        notification.open();

        final StreamResource resource = new StreamResource(track.getName() + ".wav",
                () -> new ByteArrayInputStream(WrappedByteArrayToByteArray.convert(track.getTrack())));
        final StreamRegistration registration = VaadinSession.getCurrent().getResourceRegistry().registerResource(resource);
        UI.getCurrent().getPage().open(String.valueOf(registration.getResourceUri()), "_blank");
    }

    private void showTracks(String template) {
        if (template.isEmpty()) {
            grid.setItems(trackRepository.findAll());
        } else {
            grid.setItems(trackRepository.findAllByNameAlbumAuthorLike(template));
        }
    }

    private void showTracksOfGenre(Genre genre) {
        showTracksOfGenreComponent.initComponent(
                trackRepository.findByGenre(genre.getName())
        );
    }
}
