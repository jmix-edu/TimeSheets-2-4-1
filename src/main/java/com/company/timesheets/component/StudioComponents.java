package com.company.timesheets.component;

import io.jmix.flowui.kit.meta.StudioComponent;
import io.jmix.flowui.kit.meta.StudioProperty;
import io.jmix.flowui.kit.meta.StudioPropertyType;
import io.jmix.flowui.kit.meta.StudioUiKit;

@StudioUiKit
public interface StudioComponents {

    @StudioComponent(name = "ThemeToggle",
            classFqn = "com.company.timesheets.component.themetoggle.ThemeToggle",
            category = "Components",
            xmlElement = "themeToggle",
            xmlns = "http://company.com/schema/app-ui-components",
            xmlnsAlias = "app",
            properties = {
                    /* Common attributes */
                    @StudioProperty(xmlAttribute = "id", type = StudioPropertyType.COMPONENT_ID),
                    @StudioProperty(xmlAttribute = "alignSelf", type = StudioPropertyType.ENUMERATION,
                            classFqn = "com.vaadin.flow.component.orderedlayout.FlexComponent$Alignment",
                            defaultValue = "AUTO",
                            options = {"START", "END", "CENTER", "STRETCH", "BASELINE", "AUTO"}),
                    @StudioProperty(xmlAttribute = "autofocus", type = StudioPropertyType.BOOLEAN),

                    @StudioProperty(xmlAttribute = "storageKey", type = StudioPropertyType.STRING),
                    @StudioProperty(xmlAttribute = "themeNames", type = StudioPropertyType.STRING),

                    @StudioProperty(xmlAttribute = "text", type = StudioPropertyType.STRING),
                    @StudioProperty(xmlAttribute = "enabled", type = StudioPropertyType.BOOLEAN, defaultValue = "true"),
                    @StudioProperty(xmlAttribute = "classNames", type = StudioPropertyType.STRING),


                    /* Size attributes */
                    @StudioProperty(xmlAttribute = "height", type = StudioPropertyType.SIZE),
                    @StudioProperty(xmlAttribute = "width", type = StudioPropertyType.SIZE),
                    @StudioProperty(xmlAttribute = "maxHeight", type = StudioPropertyType.SIZE),
                    @StudioProperty(xmlAttribute = "maxWidth", type = StudioPropertyType.SIZE),
                    @StudioProperty(xmlAttribute = "minHeight", type = StudioPropertyType.SIZE),
                    @StudioProperty(xmlAttribute = "minWidth", type = StudioPropertyType.SIZE),
            })
    void themeToggle();
}
