package com.dm4nk.track_library_vaadin.components;

import com.dm4nk.track_library_vaadin.domain.Genre;
import com.dm4nk.track_library_vaadin.repositiry.GenreRepository;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
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

    private Genre genre;

    private TextField name = new TextField("name");

    private Button save = new Button("Save", VaadinIcon.CHECK.create());
    private Button cancel = new Button("Cancel");
    private Button delete = new Button("Delete", VaadinIcon.TRASH.create());
    private HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);

    private BeanValidationBinder<Genre> binder = new BeanValidationBinder<>(Genre.class);
    @Setter
    private ChangeHandler changeHandler;

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
        cancel.addClickListener(e -> setVisible(false));
        setVisible(false);
    }

    public void editGenre(Genre newGenre) {
        if (newGenre == null) {
            setVisible(false);
            return;
        }

        if (newGenre.getId() != null) {
            this.genre = genreRepository.findById(newGenre.getId()).orElse(newGenre);
        } else {
            this.genre = newGenre;
        }

        binder.setBean(genre);

        setVisible(true);

        name.focus();
    }

    public void delete() {
        try {
            genreRepository.delete(genre);
            changeHandler.onChange();
        } catch (Exception e) {

        }

    }

    public void save() {
        if (binder.validate().isOk()) {
            genreRepository.save(genre);
            changeHandler.onChange();
        }
    }

    public interface ChangeHandler {
        void onChange();
    }
}