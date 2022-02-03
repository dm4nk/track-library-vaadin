package com.dm4nk.track_library_vaadin.components.album;

import com.dm4nk.track_library_vaadin.domain.Album;
import com.dm4nk.track_library_vaadin.domain.Author;
import com.dm4nk.track_library_vaadin.service.AlbumService;
import com.dm4nk.track_library_vaadin.service.AuthorService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Setter;

@SpringComponent
@UIScope
public class EditAlbumComponent extends FormLayout implements KeyNotifier {
    private final AlbumService albumService;
    private final AuthorService authorService;
    private final TextField name = new TextField("name");
    private final ComboBox<Author> author = new ComboBox<>();
    private final Button save = new Button("Save", VaadinIcon.CHECK.create());
    private final Button cancel = new Button("Cancel");
    private final Button delete = new Button("Delete", VaadinIcon.TRASH.create());
    private final HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);
    private final BeanValidationBinder<Album> binder = new BeanValidationBinder<>(Album.class);
    private final Dialog dialog = new Dialog();
    private final Notification notification = new Notification();
    @Setter
    private ChangeHandler changeHandler;
    private Album album;

    public EditAlbumComponent(AlbumService authorService, AuthorService authorService1) {
        this.albumService = authorService;
        this.authorService = authorService1;
        add(name, author, actions);

        binder.forField(name)
                .withValidator(
                        prop -> authorService.findByName(prop).isEmpty(),
                        "Must be unique"
                )
                .withValidator(new StringLengthValidator("Must me less 255 symbols", 1, 255))
                .asRequired("Name is required")
                .bind(Album::getName, Album::setName);

        binder.forField(author)
                .asRequired("Author is required")
                .bind(Album::getAuthor, Album::setAuthor);

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
        notification.setText("Failed to delete author in use");
    }

    public void editAlbum(Album newAlbum) {
        if (newAlbum == null) {
            return;
        }

        if (newAlbum.getId() != null) {
            this.album = albumService.findById(newAlbum.getId()).orElse(newAlbum);
        } else {
            this.album = newAlbum;
        }

        author.setItems(authorService.findAll());
        binder.setBean(album);

        dialog.open();
        dialog.add(this);

        name.focus();
    }

    private void delete() {
        try {
            albumService.delete(album);
            changeHandler.onChange();
            dialog.close();
        } catch (Exception e) {
            notification.open();
        }
    }

    private void save() {
        if (binder.validate().isOk()) {
            albumService.save(album);
            changeHandler.onChange();
            dialog.close();
        }
    }

    public interface ChangeHandler {
        void onChange();
    }
}
