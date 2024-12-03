package com.company.timesheets.view.timeentry;

import com.company.timesheets.entity.Task;
import com.company.timesheets.entity.TimeEntry;
import com.company.timesheets.entity.TimeEntryStatus;
import com.company.timesheets.entity.User;
import com.company.timesheets.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.core.usersubstitution.CurrentUserSubstitution;
import io.jmix.flowui.component.combobox.EntityComboBox;
import io.jmix.flowui.component.textarea.JmixTextArea;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.model.InstanceContainer;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

@Route(value = "time-entries/:id", layout = MainView.class)
@ViewController("ts_TimeEntry.detail")
@ViewDescriptor("time-entry-detail-view.xml")
@EditedEntityContainer("timeEntryDc")
public class TimeEntryDetailView extends StandardDetailView<TimeEntry> {

    @Autowired
    private CurrentUserSubstitution currentUserSubstitution;
    @ViewComponent
    private JmixTextArea rejectionReasonField;
    @ViewComponent
    private CollectionLoader<Task> tasksDl;
    @ViewComponent
    private EntityComboBox<Task> taskField;

    @Subscribe("userField.assignSelf")
    public void onUserFieldAssignSelf(final ActionPerformedEvent event) {
        final User user = (User) currentUserSubstitution.getEffectiveUser();
        getEditedEntity().setUser(user);
    }

    @Subscribe(id = "timeEntryDc", target = Target.DATA_CONTAINER)
    public void onTimeEntryDcItemChange(final InstanceContainer.ItemChangeEvent<TimeEntry> event) {
        updateRejectionReasonField();
        loadTasks();
    }


    @Subscribe(id = "timeEntryDc", target = Target.DATA_CONTAINER)
    public void onTimeEntryDcItemPropertyChange(final InstanceContainer.ItemPropertyChangeEvent<TimeEntry> event) {
        if ("status".equals(event.getProperty())) {
            updateRejectionReasonField();
        }

        if ("user".equals(event.getProperty())) {
            taskField.setReadOnly(getEditedEntity().getUser() == null);
            loadTasks();
        }
    }

    private void updateRejectionReasonField() {
        rejectionReasonField.setVisible(TimeEntryStatus.REJECTED == getEditedEntity().getStatus());
    }

    @Subscribe
    public void onInitEntity(final InitEntityEvent<TimeEntry> event) {
        TimeEntry timeEntry = event.getEntity();
        if (timeEntry.getDate() == null) {
            timeEntry.setDate(LocalDate.now());
        }

        taskField.setReadOnly(timeEntry.getUser() == null);
    }

    private void loadTasks() {
    User user = getEditedEntity().getUser();
    tasksDl.setParameter("username", user != null ? user.getUsername() : null);
    tasksDl.load();
    }


}