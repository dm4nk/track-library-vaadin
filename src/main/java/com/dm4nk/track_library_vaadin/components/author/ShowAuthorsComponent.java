package com.dm4nk.track_library_vaadin.components.author;

import com.dm4nk.track_library_vaadin.components.utility.WidthHeightSetter;
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
    private final TextField filter = new TextField("", "Search in Authors");
    private final HorizontalLayout toolbar = new HorizontalLayout();
    private Grid<Author> grid;
    @Setter
    private ClickOnNameHandler clickOnNameHandler;
    @Setter
    private ClickOnAlbumsHandler clickOnAlbumsHandler;

    public ShowAuthorsComponent(AuthorService authorService, EditAuthorComponent editAuthorComponent) {
        this.authorService = authorService;
        this.editAuthorComponent = editAuthorComponent;

        WidthHeightSetter.setWidthHeight(this);

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
                                this.clickOnNameHandler.onClick(author);
                            });
                            button.setText(author.getName());
                        }))
                .setHeader("Name");

        grid.addColumn(
                        new ComponentRenderer<>(Button::new, (button, author) -> {
                            button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                            button.addClickListener(e -> {
                                dialog.close();
                                this.clickOnAlbumsHandler.onClick(author);
                            });
                            button.setText(getAlbumsString(author));
                        }))
                .setHeader("Albums");

        grid.addItemClickListener(event -> {
            dialog.close();
            editAuthorComponent.editAuthor(event.getItem());
        });
    }

    private String getAlbumsString(Author author) {
        StringBuilder str = new StringBuilder();
        author.getAlbums().forEach(album -> str.append(album).append("\t"));
        return str.toString();
    }

    private void showAuthors(String template) {
        grid.setItems(authorService.findAllByNameLike(template));
    }

    public void initComponent(Author... authors) {
        grid = new Grid<>();
        removeAll();
        add(toolbar, grid);
        initGrid();

        if (authors == null)
            showAuthors(filter.getValue());
        else
            grid.setItems(authors);

        dialog.open();
        dialog.add(this);
    }

    public void initComponent() {
        initComponent(null);
    }


    public interface ClickOnNameHandler {
        void onClick(Author author);
    }

    public interface ClickOnAlbumsHandler {
        void onClick(Author author);
    }
}
