package com.dm4nk.track_library_vaadin.view;

import com.dm4nk.track_library_vaadin.components.ShowTracksOfGenreComponent;
import com.dm4nk.track_library_vaadin.components.TrackEditor;
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

import java.io.ByteArrayInputStream;

@Slf4j

@Route("/tracks")
@RouteAlias("")
@SpringComponent
@UIScope
public class TrackActivity extends VerticalLayout {
    private final TrackRepository trackRepository;
    private final Button backButton = new Button("Genres", VaadinIcon.HEART.create());
    private final TextField filter = new TextField("", "Type to filter");
    private final Button addNewButton = new Button("Add", VaadinIcon.ADD_DOCK.create());
    private final Button toggleButton = new Button(VaadinIcon.MOON.create());

    private final TrackEditor trackEditor;
    private final ShowTracksOfGenreComponent showTracksOfGenreComponent;

    private final Grid<Track> grid = new Grid<>();
    private final HorizontalLayout toolBar = new HorizontalLayout();
    private final Notification notification = new Notification();

    public TrackActivity(TrackRepository trackRepository, TrackEditor trackEditor, ShowTracksOfGenreComponent showTracksOfGenreComponent) {
        this.trackRepository = trackRepository;
        this.trackEditor = trackEditor;
        this.showTracksOfGenreComponent = showTracksOfGenreComponent;

        initToolbar();

        initGrid(trackRepository);

        initNotification();

        add(toolBar, grid);

        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(event -> showTracks(event.getValue()));

        addNewButton.addClickListener(event -> trackEditor.editTrack(Track.builder().build()));

        backButton.addClickListener(e -> UI.getCurrent().navigate("/genres"));

        trackEditor.setChangeHandler(() -> showTracks(filter.getValue()));
        showTracksOfGenreComponent.setClickHandler(this::showTracks);

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
    }

    private void initNotification() {
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
