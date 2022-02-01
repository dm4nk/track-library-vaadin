package com.dm4nk.track_library_vaadin.components.author;

import com.dm4nk.track_library_vaadin.domain.Author;
import com.dm4nk.track_library_vaadin.service.AuthorService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Getter;
import lombok.Setter;

@SpringComponent
@UIScope
public class ShowAuthorsComponent extends VerticalLayout {
    private final Dialog dialog = new Dialog();
    private final AuthorService authorService;
    @Getter
    private final EditAuthorComponent editAuthorComponent;
    private final Button addNewButton = new Button("Add", VaadinIcon.ADD_DOCK.create());
    private final TextField filter = new TextField("", "Type to filter");
    private final HorizontalLayout toolbar = new HorizontalLayout();
    private Grid<Author> grid;
    @Setter
    private ClickHandler clickHandler;

    public ShowAuthorsComponent(AuthorService authorService, EditAuthorComponent editAuthorComponent) {
        this.authorService = authorService;
        this.editAuthorComponent = editAuthorComponent;

        setWidth("600px");
        setHeight("400px");

        toolbar.add(addNewButton);
        toolbar.addAndExpand(filter);

        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(event -> showAuthors(event.getValue()));
        addNewButton.addClickListener(event -> {
            dialog.close();
            editAuthorComponent.editAuthor(Author.builder().build());
        });
    }

    private void initGrid() {
        grid.addColumn(
                        new ComponentRenderer<>(Button::new, (button, author) -> {
                            button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                            button.addClickListener(e -> {
                                dialog.close();
                                this.clickHandler.onClick(author);
                            });
                            button.setText(author.getName());
                        }))
                .setHeader("Name");

        grid.addItemClickListener(event -> {
            dialog.close();
            editAuthorComponent.editAuthor(event.getItem());
        });
    }

    private void showAuthors(String template) {
        grid.setItems(authorService.findAllByNameLike(template));
    }

    public void initComponent() {
        grid = new Grid<>();
        removeAll();
        add(toolbar, grid);
        initGrid();

        showAuthors(filter.getValue());

        dialog.open();
        dialog.add(this);
    }


    public interface ClickHandler {
        void onClick(Author author);
    }
}
