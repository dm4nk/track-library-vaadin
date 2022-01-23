package com.dm4nk.track_library_vaadin.components;

import com.dm4nk.track_library_vaadin.domain.Genre;
import com.dm4nk.track_library_vaadin.domain.Track;
import com.dm4nk.track_library_vaadin.repositiry.GenreRepository;
import com.dm4nk.track_library_vaadin.repositiry.TrackRepository;
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
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.validator.DateTimeRangeValidator;
import com.vaadin.flow.data.validator.IntegerRangeValidator;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@SpringComponent
@UIScope
public class TrackEditor extends FormLayout implements KeyNotifier {
    private final TrackRepository trackRepository;
    private final GenreRepository genreRepository;
    private final ComboBox<Genre> genre = new ComboBox<>("genre");
    private final TextField name = new TextField("name");
    private final TextField author = new TextField("author");
    private final TextField album = new TextField("album");
    private final TimePicker duration = new TimePicker();
    private final Button save = new Button("Save", VaadinIcon.CHECK.create());
    private final Button cancel = new Button("Cancel");
    private final Button delete = new Button("Delete", VaadinIcon.TRASH.create());
    private final HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);

    private final BeanValidationBinder<Track> binder = new BeanValidationBinder<>(Track.class);
    private final Dialog dialog = new Dialog();
    @Setter
    private ChangeHandler changeHandler;
    private Track track;

    public TrackEditor(TrackRepository trackRepository, GenreRepository genreRepository) {
        this.trackRepository = trackRepository;
        this.genreRepository = genreRepository;

        add(name, author, album, duration, genre, actions);

        initDuration();

        initBinders(genreRepository);

        save.getElement().getThemeList().add("primary");
        delete.getElement().getThemeList().add("error");

        addKeyPressListener(Key.ENTER, e -> save());

        save.addClickListener(e -> save());
        delete.addClickListener(e -> delete());
        cancel.addClickListener(e -> dialog.close());
    }

    private void initDuration() {
        duration.setAutoOpen(false);
        duration.setStep(Duration.ofSeconds(1));
        duration.setMinTime(LocalTime.of(0, 0, 5));
        duration.setMaxTime(LocalTime.of(10, 0, 0));
    }

    private void initBinders(GenreRepository genreRepository) {
        binder.forField(name)
                .asRequired("Name is required")
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

        genre.setItems(genreRepository.findAll());
        binder.forField(genre)
                .asRequired("Genre is required")
                .bind(Track::getGenre, Track::setGenre);
    }

    public void editTrack(Track newTrack) {
        if (newTrack == null) {
            return;
        }

        if (newTrack.getId() != null) {
            this.track = trackRepository.findById(newTrack.getId()).orElse(newTrack);
            binder.setBean(track);
        } else {
            binder.removeBean();
            this.track = newTrack;
        }

        dialog.open();
        dialog.add(this);

        name.focus();
    }

    private void delete() {
        binder.setBean(track);
        trackRepository.delete(track);
        changeHandler.onChange();
        dialog.close();
    }

    private void save() {
        if (binder.validate().isOk()) {
            binder.setBean(track);
            trackRepository.save(track);
            changeHandler.onChange();
            dialog.close();
        }
    }

    public interface ChangeHandler {
        void onChange();
    }
}
