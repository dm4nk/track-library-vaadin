package com.dm4nk.track_library_vaadin.components;

import com.dm4nk.track_library_vaadin.domain.Genre;
import com.dm4nk.track_library_vaadin.repositiry.GenreRepository;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Setter;

@SpringComponent
@UIScope
public class GenreEditor extends FormLayout implements KeyNotifier {
    private final GenreRepository genreRepository;

    private final TextField name = new TextField("name");

    private final Button save = new Button("Save", VaadinIcon.CHECK.create());
    private final Button cancel = new Button("Cancel");
    private final Button delete = new Button("Delete", VaadinIcon.TRASH.create());
    private final HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);

    private final BeanValidationBinder<Genre> binder = new BeanValidationBinder<>(Genre.class);
    private final Dialog dialog = new Dialog();
    private final Notification notification = new Notification();
    @Setter
    private ChangeHandler changeHandler;
    private Genre genre;

    public GenreEditor(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;

        add(name, actions);

        binder.forField(name)
                .asRequired("Name is required")
                .withValidator(
                        prop -> genreRepository.findByName(prop).isEmpty(),
                        "Must be unique"
                )
                .bind(Genre::getName, Genre::setName);

        save.getElement().getThemeList().add("primary");
        delete.getElement().getThemeList().add("error");

        addKeyPressListener(Key.ENTER, e -> save());

        save.addClickListener(e -> save());
        delete.addClickListener(e -> delete());
        cancel.addClickListener(e -> dialog.close());

        initNotification();
    }

    private void initNotification() {
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setPosition(Notification.Position.BOTTOM_CENTER);
        notification.setDuration(1200);
        notification.setText("Failed to delete genre in use");
    }

    public void editGenre(Genre newGenre) {
        if (newGenre == null) {
            return;
        }

        if (newGenre.getId() != null) {
            this.genre = genreRepository.findById(newGenre.getId()).orElse(newGenre);
        } else {
            this.genre = newGenre;
        }

        binder.setBean(genre);

        dialog.open();
        dialog.add(this);

        name.focus();
    }

    private void delete() {
        try {
            genreRepository.delete(genre);
            changeHandler.onChange();
            dialog.close();
        } catch (Exception e) {
            notification.open();
        }
    }

    private void save() {
        if (binder.validate().isOk()) {
            genreRepository.save(genre);
            changeHandler.onChange();
            dialog.close();
        }
    }

    public interface ChangeHandler {
        void onChange();
    }
}
