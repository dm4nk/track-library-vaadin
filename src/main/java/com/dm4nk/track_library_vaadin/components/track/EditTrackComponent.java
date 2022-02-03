package com.dm4nk.track_library_vaadin.components.track;

import com.dm4nk.track_library_vaadin.converters.ByteArrayToWrappedByteArray;
import com.dm4nk.track_library_vaadin.domain.Album;
import com.dm4nk.track_library_vaadin.domain.Author;
import com.dm4nk.track_library_vaadin.domain.Genre;
import com.dm4nk.track_library_vaadin.domain.Track;
import com.dm4nk.track_library_vaadin.service.AlbumService;
import com.dm4nk.track_library_vaadin.service.AuthorService;
import com.dm4nk.track_library_vaadin.service.GenreService;
import com.dm4nk.track_library_vaadin.service.TrackService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Setter;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalTime;

@SpringComponent
@UIScope
public class EditTrackComponent extends FormLayout implements KeyNotifier {
    private final TrackService trackService;
    private final GenreService genreService;
    private final AuthorService authorService;
    private final AlbumService albumService;
    private final ComboBox<Genre> genre = new ComboBox<>("genre");
    private final TextField name = new TextField("name");
    private final ComboBox<Author> author = new ComboBox<>("author");
    private final ComboBox<Album> album = new ComboBox<>("album");
    private final TimePicker duration = new TimePicker();
    private final Button save = new Button("Save", VaadinIcon.CHECK.create());
    private final Button cancel = new Button("Cancel");
    private final Button delete = new Button("Delete", VaadinIcon.TRASH.create());
    private final MemoryBuffer memoryBuffer = new MemoryBuffer();
    private final Upload upload = new Upload(memoryBuffer);
    private final HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);

    private final BeanValidationBinder<Track> binder = new BeanValidationBinder<>(Track.class);
    private final Dialog dialog = new Dialog();
    @Setter
    private ChangeHandler changeHandler;
    private Track track;

    public EditTrackComponent(TrackService trackService, GenreService genreService, AuthorService authorService, AlbumService albumService) {
        this.trackService = trackService;
        this.genreService = genreService;
        this.authorService = authorService;
        this.albumService = albumService;

        add(name, author, album, duration, genre, upload, actions);

        initDuration();

        initBinders();

        save.getElement().getThemeList().add("primary");
        delete.getElement().getThemeList().add("error");

        addKeyPressListener(Key.ENTER, e -> save());

        save.addClickListener(e -> save());
        delete.addClickListener(e -> delete());
        cancel.addClickListener(e -> dialog.close());

        upload.setAcceptedFileTypes(".wav");
        upload.addSucceededListener(event -> {
            InputStream fileData = memoryBuffer.getInputStream();

            try {
                track.setTrack(ByteArrayToWrappedByteArray.convert(fileData.readAllBytes()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void initDuration() {
        duration.setAutoOpen(false);
        duration.setStep(Duration.ofSeconds(1));
        duration.setMinTime(LocalTime.of(0, 0, 5));
        duration.setMaxTime(LocalTime.of(10, 0, 0));
    }

    private void initBinders() {
        binder.forField(name)
                .asRequired("Name is required")
                .withValidator(new StringLengthValidator("Must me less 255 symbols", 1, 255))
                .bind(Track::getName, Track::setName);

        binder.forField(author)
                .asRequired("Author is required")
                .bind(Track::getAuthor, Track::setAuthor);

        binder.forField(album)
                .asRequired("Album is required")
                .bind(Track::getAlbum, Track::setAlbum);

        binder.forField(duration)
                .asRequired("Duration is required")
                .withValidator(
                        dur -> dur.isAfter(LocalTime.of(0, 0, 1)) &&
                                dur.isBefore(LocalTime.of(10, 0, 0)),
                        "Must be between 00:00:1 and 10:00:00"
                )
                .bind(Track::getDuration, Track::setDuration);

        binder.forField(genre)
                .asRequired("Genre is required")
                .bind(Track::getGenre, Track::setGenre);
    }

    public void editTrack(Track newTrack) {
        if (newTrack == null) {
            return;
        }

        if (newTrack.getId() != null) {
            this.track = trackService.findById(newTrack.getId()).orElse(newTrack);
        } else {
            binder.removeBean();
            this.track = newTrack;
        }

        genre.setItems(genreService.findAll());
        author.setItems(authorService.findAll());
        album.setItems(albumService.findAll());
        binder.setBean(track);
        dialog.open();
        dialog.add(this);

        name.focus();
    }

    private void delete() {
        trackService.delete(track);
        changeHandler.onChange();
        dialog.close();
    }

    private void save() {
        if (binder.validate().isOk()) {
            trackService.save(track);
            changeHandler.onChange();
            dialog.close();
        }
    }

    public interface ChangeHandler {
        void onChange();
    }
}
