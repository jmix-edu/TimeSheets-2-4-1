package com.company.timesheets.view.client;

import com.company.timesheets.component.ColorComponent;
import com.company.timesheets.component.ColorPicker;
import com.company.timesheets.entity.Client;
import com.company.timesheets.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.component.image.JmixImage;
import io.jmix.flowui.component.upload.FileUploadField;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.model.InstanceContainer;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "clients/:id", layout = MainView.class)
@ViewController("ts_Client.detail")
@ViewDescriptor("client-detail-view.xml")
@EditedEntityContainer("clientDc")
public class ClientDetailView extends StandardDetailView<Client> {
    @ViewComponent
    private HorizontalLayout uploadWrapper;
    @ViewComponent
    private FileUploadField imageUpload;
    @ViewComponent
    private JmixImage<byte[]> image;
    @Autowired
    private Notifications notifications;

    @Subscribe
    public void onReady(final ReadyEvent event) {
        updateImage(getEditedEntity().getImage());
        applySecurityPermissions();
    }

    private void applySecurityPermissions() {
        // Since imageUpload is bound to the entity attribute,
        // it has already applied security restriction, so we
        // can just check if this field is in read-only state
        uploadWrapper.setVisible(!imageUpload.isReadOnly());
    }

    private void updateImage(byte[] value) {
        if (value == null) {
            image.setSrc("images/add-image-placeholder.png");
        }
    }

    @Subscribe(id = "uploadClearBtn", subject = "clickListener")
    public void onUploadClearBtnClick(final ClickEvent<JmixButton> event) {
        getEditedEntity().setImage(null);
    }

    @Subscribe(id = "clientDc", target = Target.DATA_CONTAINER)
    public void onClientDcItemPropertyChange(final InstanceContainer.ItemPropertyChangeEvent<Client> event) {
        if ("image".equals(event.getProperty())) {
            updateImage(getEditedEntity().getImage());
        }
    }

    @Subscribe
    public void onInit(final InitEvent event) {
//        ColorPicker colorPicker = new ColorPicker();
//        getContent().add(colorPicker);
//
//        colorPicker.addValueChangeListener(e ->
//                notifications.show("Color: " + e.getValue()));

        ColorComponent component = new ColorComponent();
        getContent().add(component);
    }
    
    
    
    


}