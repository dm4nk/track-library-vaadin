package com.dm4nk.track_library_vaadin.view;

import com.dm4nk.track_library_vaadin.components.ShowGenresComponent;
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
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.util.List;

@Slf4j

@Route("/tracks")
@RouteAlias("")
public class TrackActivity extends VerticalLayout {
    private final TrackRepository trackRepository;

    private final TrackEditor trackEditor;
    private final ShowTracksOfGenreComponent showTracksOfGenreComponent;
    private final ShowGenresComponent showGenresComponent;

    private final Grid<Track> grid = new Grid<>();
    private final Notification downloadNotification = new Notification();
    private final Notification numberFormatNotification = new Notification();
    private final Notification notFoundNotification = new Notification();
    private ToolBar toolBar = null;

    public TrackActivity(TrackRepository trackRepository, TrackEditor trackEditor, ShowTracksOfGenreComponent showTracksOfGenreComponent, ShowGenresComponent showGenresComponent) {
        this.trackRepository = trackRepository;
        this.trackEditor = trackEditor;
        this.showTracksOfGenreComponent = showTracksOfGenreComponent;
        this.showGenresComponent = showGenresComponent;

        configureNotification();

        toolBar = new ToolBar(
                "Genres",
                VaadinIcon.HEART,
                event -> showTracks((String) event.getValue()),
                event -> trackEditor.editTrack(Track.builder().build()),
                event -> showGenresComponent.initComponent(),
                click -> showTracks(toolBar.getFilter().getValue()));

        add(toolBar, createGrid());

        this.trackEditor.setChangeHandler(() -> showTracks(toolBar.getFilter().getValue()));
        this.showTracksOfGenreComponent.setClickHandler(trackId -> toolBar.getFilter().setValue("id:" + trackId));
        this.showGenresComponent.getGenreEditor().setChangeHandler(() -> showTracks(toolBar.getFilter().getValue()));

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
        downloadNotification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        downloadNotification.setPosition(Notification.Position.BOTTOM_CENTER);
        downloadNotification.setDuration(1200);

        numberFormatNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        numberFormatNotification.setPosition(Notification.Position.BOTTOM_CENTER);
        numberFormatNotification.setText("Wrong id format");
        numberFormatNotification.setDuration(1200);

        notFoundNotification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
        notFoundNotification.setPosition(Notification.Position.BOTTOM_CENTER);
        notFoundNotification.setText("No Track with such id");
        notFoundNotification.setDuration(1200);
    }

    private void download(Track track) {
        downloadNotification.setText("Downloading " + track.getName());
        downloadNotification.open();

        final StreamResource resource = new StreamResource(track.getName() + ".wav",
                () -> new ByteArrayInputStream(WrappedByteArrayToByteArray.convert(track.getTrack())));
        final StreamRegistration registration = VaadinSession.getCurrent().getResourceRegistry().registerResource(resource);
        UI.getCurrent().getPage().open(String.valueOf(registration.getResourceUri()), "_blank");
    }

    private void showTracks(String template) {
        try {
            grid.setItems(findTracksByTemplate(template));
        } catch (NumberFormatException e) {
            numberFormatNotification.open();
        } catch (NotFoundException e) {
            notFoundNotification.open();
        }
    }

    private List<Track> findTracksByTemplate(String template) {
        if (template.isEmpty()) {
            return trackRepository.findAll();
        } else {
            if (template.startsWith("id:")) {
                String idstr = template.substring(3);

                if (idstr.isEmpty())
                    return trackRepository.findAll();

                Integer id = Integer.parseInt(idstr);

                Track track = trackRepository.findById(id).orElse(null);

                if (track == null) throw new NotFoundException("No track with such id");

                return List.of(track);
            } else {
                return trackRepository.findAllByNameAlbumAuthorLike(template);
            }
        }
    }

    private void showTracksOfGenre(Genre genre) {
        showTracksOfGenreComponent.initComponent(
                genre.getTracks()
        );
    }
}
