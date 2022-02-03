package com.dm4nk.track_library_vaadin.view;

import com.dm4nk.track_library_vaadin.components.album.ShowAlbumsComponent;
import com.dm4nk.track_library_vaadin.components.author.ShowAuthorsComponent;
import com.dm4nk.track_library_vaadin.components.genre.ShowGenresComponent;
import com.dm4nk.track_library_vaadin.components.track.EditTrackComponent;
import com.dm4nk.track_library_vaadin.components.utility.ShowTracksOfNamedEntityComponent;
import com.dm4nk.track_library_vaadin.converters.WrappedByteArrayToByteArray;
import com.dm4nk.track_library_vaadin.domain.Album;
import com.dm4nk.track_library_vaadin.domain.Author;
import com.dm4nk.track_library_vaadin.domain.Genre;
import com.dm4nk.track_library_vaadin.domain.Track;
import com.dm4nk.track_library_vaadin.service.TrackService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.Lumo;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.util.List;

@Slf4j

@Route("/tracks")
@RouteAlias("")
public class TrackActivity extends VerticalLayout {
    //service
    private final TrackService trackService;

    //components
    private final EditTrackComponent editTrackComponent;
    private final ShowTracksOfNamedEntityComponent<Genre> showTracksOfGenreComponent;
    private final ShowGenresComponent showGenresComponent;
    private final ShowTracksOfNamedEntityComponent<Author> showTracksOfAuthorComponent;
    private final ShowAuthorsComponent showAuthorsComponent;
    private final ShowTracksOfNamedEntityComponent<Album> showTracksOfAlbumComponent;
    private final ShowAlbumsComponent showAlbumsComponent;

    //grid
    private final Grid<Track> grid = new Grid<>();

    //notification
    private final Notification downloadNotification = new Notification();
    private final Notification numberFormatNotification = new Notification();
    private final Notification notFoundNotification = new Notification();

    //toolbar
    private final HorizontalLayout toolBar = new HorizontalLayout();
    private final Button genresButton = new Button("Genres", VaadinIcon.HEART.create());
    private final Button authorsButton = new Button("Authors", VaadinIcon.GROUP.create());
    private final Button albumsButton = new Button("Albums", VaadinIcon.FOLDER_O.create());
    private final TextField filter = new TextField("", "Type to filter");
    private final Button addNewButton = new Button("Add", VaadinIcon.ADD_DOCK.create());
    private final Button toggleButton = new Button(VaadinIcon.MOON.create());
    private final Button refreshButton = new Button(VaadinIcon.REFRESH.create());

    public TrackActivity(TrackService trackService, EditTrackComponent editTrackComponent, ShowTracksOfNamedEntityComponent<Genre> showTracksOfGenreComponent, ShowGenresComponent showGenresComponent, ShowTracksOfNamedEntityComponent<Author> showTracksOfAuthorComponent, ShowAuthorsComponent showAuthorsComponent, ShowTracksOfNamedEntityComponent<Album> showTracksOfAlbumComponent, ShowAlbumsComponent showAlbumsComponent) {
        this.trackService = trackService;
        this.editTrackComponent = editTrackComponent;
        this.showTracksOfGenreComponent = showTracksOfGenreComponent;
        this.showGenresComponent = showGenresComponent;
        this.showTracksOfAuthorComponent = showTracksOfAuthorComponent;
        this.showAuthorsComponent = showAuthorsComponent;
        this.showTracksOfAlbumComponent = showTracksOfAlbumComponent;
        this.showAlbumsComponent = showAlbumsComponent;

        configureNotification();
        configureComponents();
        add(createToolBar(), createGrid());
        showTracks("");
        setSizeFull();
    }

    private void configureComponents() {
        this.editTrackComponent.setChangeHandler(() -> showTracks(filter.getValue()));

        this.showTracksOfGenreComponent.setClickHandler(trackId -> filter.setValue("id:" + trackId));
        this.showGenresComponent.getEditGenreComponent().setChangeHandler(() -> {
            showTracks(filter.getValue());
            showGenresComponent.initComponent();
        });
        this.showGenresComponent.setClickHandler(genre -> showTracksOfGenreComponent.initComponent(genre, genre.getTracks()));

        this.showTracksOfAuthorComponent.setClickHandler(trackId -> filter.setValue("id:" + trackId));
        this.showAuthorsComponent.getEditAuthorComponent().setChangeHandler(() -> {
            showTracks(filter.getValue());
            showAuthorsComponent.initComponent();
        });
        this.showAuthorsComponent.setClickOnNameHandler(author -> showTracksOfAuthorComponent.initComponent(author, author.getTracks()));
        this.showAuthorsComponent.setClickOnAlbumsHandler(author -> showAlbumsComponent.initComponent(author.getAlbums().toArray(new Album[0])));

        this.showTracksOfAlbumComponent.setClickHandler(trackId -> filter.setValue("id:" + trackId));
        this.showAlbumsComponent.getEditAlbumComponent().setChangeHandler(() -> {
            showTracks(filter.getValue());
            showAlbumsComponent.initComponent();
        });
        this.showAlbumsComponent.setClickOnNameHandler(album -> showTracksOfAlbumComponent.initComponent(album, album.getTracks()));
        this.showAlbumsComponent.setClickOnAuthorHandler(album -> showAuthorsComponent.initComponent(album.getAuthor()));
    }

    private Grid<Track> createGrid() {
        grid.setItems(trackService.findAll());
        grid.addColumn(Track::getName).setSortable(true).setHeader("Name");
        grid.addColumn(
                        new ComponentRenderer<>(Button::new, (button, track) -> {
                            button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                            button.addClickListener(e -> showTracksOfAuthorComponent.initComponent(track.getAuthor(), track.getAuthor().getTracks()));
                            button.setText(track.getAuthor().getName());
                        }))
                .setHeader("Author");
        grid.addColumn(
                        new ComponentRenderer<>(Button::new, (button, track) -> {
                            button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                            button.addClickListener(e -> showTracksOfAlbumComponent.initComponent(track.getAlbum(), track.getAlbum().getTracks()));
                            button.setText(track.getAlbum().getName());
                        }))
                .setHeader("Album");
        grid.addColumn(Track::getDuration)
                .setSortable(true)
                .setHeader("Duration");
        grid.addColumn(
                        new ComponentRenderer<>(Button::new, (button, track) -> {
                            button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                            button.addClickListener(e -> showTracksOfGenreComponent.initComponent(track.getGenre(), track.getGenre().getTracks()));
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

        grid.asSingleSelect().addValueChangeListener(event -> editTrackComponent.editTrack(event.getValue()));
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

    private HorizontalLayout createToolBar() {
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(event -> showTracks(event.getValue()));
        addNewButton.addClickListener(event -> editTrackComponent.editTrack(Track.builder().build()));
        genresButton.addClickListener(event -> showGenresComponent.initComponent());
        authorsButton.addClickListener(event -> showAuthorsComponent.initComponent());
        albumsButton.addClickListener(event -> showAlbumsComponent.initComponent());
        refreshButton.addClickListener(click -> showTracks(filter.getValue()));

        toggleButton.addClickListener(click -> {
            ThemeList themeList = UI.getCurrent().getElement().getThemeList();

            if (themeList.contains(Lumo.DARK)) {
                themeList.remove(Lumo.DARK);
            } else {
                themeList.add(Lumo.DARK);
            }
        });

        toolBar.add(genresButton, authorsButton, albumsButton, addNewButton);
        toolBar.addAndExpand(filter);
        toolBar.add(refreshButton, toggleButton);
        toolBar.setAlignSelf(FlexComponent.Alignment.STRETCH, filter);
        toolBar.setAlignSelf(FlexComponent.Alignment.START, genresButton);
        toolBar.setAlignSelf(FlexComponent.Alignment.START, addNewButton);
        toolBar.setAlignSelf(FlexComponent.Alignment.END, toggleButton);

        return toolBar;
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
            return trackService.findAll();
        } else {
            if (template.startsWith("id:")) {
                String idstr = template.substring(3);

                if (idstr.isEmpty())
                    return trackService.findAll();

                Integer id = Integer.parseInt(idstr);

                Track track = trackService.findById(id).orElse(null);

                if (track == null) throw new NotFoundException("No track with such id");

                return List.of(track);
            } else {
                return trackService.findAllByNameAlbumAuthorLike(template);
            }
        }
    }
}
