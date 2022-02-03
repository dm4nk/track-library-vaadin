package com.dm4nk.track_library_vaadin.components.album;

import com.dm4nk.track_library_vaadin.components.utility.WidthHeightSetter;
import com.dm4nk.track_library_vaadin.domain.Album;
import com.dm4nk.track_library_vaadin.service.AlbumService;
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
public class ShowAlbumsComponent extends VerticalLayout {
    private final Dialog dialog = new Dialog();
    private final AlbumService albumService;
    @Getter
    private final EditAlbumComponent editAlbumComponent;
    private final Button addNewButton = new Button("Add", VaadinIcon.ADD_DOCK.create());
    private final TextField filter = new TextField("", "Search in Albums");
    private final HorizontalLayout toolbar = new HorizontalLayout();
    private Grid<Album> grid;
    @Setter
    private ClickOnNameHandler clickOnNameHandler;
    @Setter
    private ClickOnAuthorHandler clickOnAuthorHandler;

    public ShowAlbumsComponent(AlbumService albumService, EditAlbumComponent editAlbumComponent) {
        this.albumService = albumService;
        this.editAlbumComponent = editAlbumComponent;

        WidthHeightSetter.setWidthHeight(this);

        addNewButton.getElement().getStyle();

        toolbar.add(addNewButton);
        toolbar.addAndExpand(filter);

        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(event -> showAlbums(event.getValue()));
        addNewButton.addClickListener(event -> {
            dialog.close();
            editAlbumComponent.editAlbum(Album.builder().build());
        });
    }

    private void initGrid() {
        grid.addColumn(
                        new ComponentRenderer<>(Button::new, (button, album) -> {
                            button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                            button.addClickListener(e -> {
                                dialog.close();
                                this.clickOnNameHandler.onClick(album);
                            });
                            button.setText(album.getName());
                        }))
                .setHeader("Name");

        grid.addColumn(
                        new ComponentRenderer<>(Button::new, (button, album) -> {
                            button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                            button.addClickListener(e -> {
                                dialog.close();
                                this.clickOnAuthorHandler.onClick(album);
                            });
                            button.setText(album.getAuthor().getName());
                        }))
                .setHeader("Author");

        grid.addItemClickListener(event -> {
            dialog.close();
            editAlbumComponent.editAlbum(event.getItem());
        });
    }

    private void showAlbums(String template) {
        grid.setItems(albumService.findAllByNameLike(template));
    }

    public void initComponent(Album... albums) {
        grid = new Grid<>();
        removeAll();
        add(toolbar, grid);
        initGrid();

        if (albums == null)
            showAlbums(filter.getValue());
        else
            grid.setItems(albums);

        dialog.open();
        dialog.add(this);
    }

    public void initComponent() {
        initComponent(null);
    }


    public interface ClickOnNameHandler {
        void onClick(Album album);
    }

    public interface ClickOnAuthorHandler {
        void onClick(Album album);
    }
}
