package com.company.timesheets.component.themetoggle;

import io.jmix.flowui.xml.layout.loader.AbstractComponentLoader;

public class ThemeToggleLoader extends AbstractComponentLoader<ThemeToggle> {

    @Override
    protected ThemeToggle createComponent() {
        return factory.create(ThemeToggle.class);
    }

    @Override
    public void loadComponent() {
        loadBoolean(element, "autofocus", resultComponent::setAutofocus);
        loadString(element, "storageKey", resultComponent::setStorageKey);

        componentLoader().loadText(resultComponent, element);
        componentLoader().loadTooltip(resultComponent, element);
        componentLoader().loadEnabled(resultComponent, element);
        componentLoader().loadClassNames(resultComponent, element);
        componentLoader().loadThemeNames(resultComponent, element);
        componentLoader().loadSizeAttributes(resultComponent, element);
        componentLoader().loadFocusableAttributes(resultComponent, element);
    }
}
