package com.dm4nk.track_library_vaadin.components.utility;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.Lumo;
import lombok.Getter;

@UIScope
public class ToolBar extends HorizontalLayout {
    private final Button backButton;
    @Getter
    private final TextField filter = new TextField("", "Type to filter");
    private final Button addNewButton = new Button("Add", VaadinIcon.ADD_DOCK.create());
    private final Button toggleButton = new Button(VaadinIcon.MOON.create());
    private final Button refreshButton = new Button(VaadinIcon.REFRESH.create());

    public ToolBar(String backButtonText,
                   VaadinIcon backButtonIcon,
                   HasValue.ValueChangeListener onFilterChangeListener,
                   ComponentEventListener onAddClick,
                   ComponentEventListener onBackClick,
                   ComponentEventListener onRefreshClick) {
        backButton = new Button(backButtonText, backButtonIcon.create());

        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(onFilterChangeListener);
        addNewButton.addClickListener(onAddClick);
        backButton.addClickListener(onBackClick);
        refreshButton.addClickListener(onRefreshClick);

        toggleButton.addClickListener(click -> {
            ThemeList themeList = UI.getCurrent().getElement().getThemeList();

            if (themeList.contains(Lumo.DARK)) {
                themeList.remove(Lumo.DARK);
            } else {
                themeList.add(Lumo.DARK);
            }
        });

        add(backButton, addNewButton);
        addAndExpand(filter);
        add(refreshButton, toggleButton);
        setAlignSelf(FlexComponent.Alignment.STRETCH, filter);
        setAlignSelf(FlexComponent.Alignment.START, backButton);
        setAlignSelf(FlexComponent.Alignment.START, addNewButton);
        setAlignSelf(FlexComponent.Alignment.END, toggleButton);
    }
}
