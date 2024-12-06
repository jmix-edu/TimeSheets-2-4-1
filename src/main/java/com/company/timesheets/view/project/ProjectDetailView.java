package com.company.timesheets.view.project;

import com.company.timesheets.entity.Project;
import com.company.timesheets.entity.ProjectParticipant;
import com.company.timesheets.entity.Task;
import com.company.timesheets.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.flowui.DialogWindows;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.component.tabsheet.JmixTabSheet;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.kit.action.BaseAction;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "projects/:id", layout = MainView.class)
@ViewController("ts_Project.detail")
@ViewDescriptor("project-detail-view.xml")
@EditedEntityContainer("projectDc")
@DialogMode(width = "64em")
public class ProjectDetailView extends StandardDetailView<Project> {
    @Autowired
    private DataManager dataManager;
    @Autowired
    private DialogWindows dialogWindows;
//    @ViewComponent
    private DataGrid<Task> tasksDataGrid;
//    @ViewComponent
    private DataGrid<ProjectParticipant> participantsDataGrid;
    @Autowired
    private Notifications notifications;
    @ViewComponent
    private CollectionContainer<Task> tasksDc;
    @ViewComponent
    private CollectionContainer<ProjectParticipant> projectParticipantsDc;
    @ViewComponent
    private CollectionLoader<Task> tasksDl;
    @ViewComponent
    private CollectionLoader<ProjectParticipant> projectParticipantsDl;

    @Subscribe("tabSheet")
    public void onTabSheetSelectedChange(final JmixTabSheet.SelectedChangeEvent event) {
        if ("tasksTab".equals(event.getSelectedTab().getId().orElse(""))) {
            initTasks();
        }
        if ("participantsTab".equals(event.getSelectedTab().getId().orElse(""))) {
            initParticipants();
        }
    }

    private void initTasks() {
        if (tasksDataGrid != null) {
            // It means that we've already opened this tab and initialized table and loader
            return;
        }

        tasksDl.setParameter("project", getEditedEntity());
        tasksDl.load();

        tasksDataGrid =(DataGrid<Task>) getContent().findComponent("tasksDataGrid").get();
        BaseAction createAction = (BaseAction) tasksDataGrid.getAction("create");
        createAction.addActionPerformedListener(this::onTasksDataGridCreate);
        BaseAction editAction = (BaseAction) tasksDataGrid.getAction("edit");
        editAction.addActionPerformedListener(this::onTasksDataGridEdit);
    }

    private void initParticipants() {
        if (participantsDataGrid != null) {
            // It means that we've already opened this tab and initialized table and loader
            return;
        }

        projectParticipantsDl.setParameter("project", getEditedEntity());
        projectParticipantsDl.load();

        participantsDataGrid =(DataGrid<ProjectParticipant>) getContent().findComponent("participantsDataGrid").get();
        BaseAction createAction = (BaseAction) participantsDataGrid.getAction("create");
        createAction.addActionPerformedListener(this::onParticipantsDataGridCreate);
        BaseAction editAction = (BaseAction) participantsDataGrid.getAction("remove");
        editAction.addActionPerformedListener(this::onParticipantsDataGridEdit);
    }


    //    @Subscribe("tasksDataGrid.create")
    public void onTasksDataGridCreate(final ActionPerformedEvent event) {
        Task newTask = dataManager.create(Task.class);
        newTask.setProject(getEditedEntity());

        dialogWindows.detail(tasksDataGrid)
                .newEntity(newTask)
                .withParentDataContext(getViewData().getDataContext())
                .open();
    }

//    @Subscribe("tasksDataGrid.edit")
    public void onTasksDataGridEdit(final ActionPerformedEvent event) {
        Task selectedTask = tasksDataGrid.getSingleSelectedItem();
        if (selectedTask == null) {
            return;
        }

        dialogWindows.detail(tasksDataGrid)
                .editEntity(selectedTask)
                .withParentDataContext(getViewData().getDataContext())
                .open();
    }

//    @Subscribe("participantsDataGrid.create")
    public void onParticipantsDataGridCreate(final ActionPerformedEvent event) {
        ProjectParticipant newParticipant = dataManager.create(ProjectParticipant.class);
        newParticipant.setProject(getEditedEntity());

        dialogWindows.detail(participantsDataGrid)
                .newEntity(newParticipant)
                .withParentDataContext(getViewData().getDataContext())
                .open();
    }

//    @Subscribe("participantsDataGrid.edit")
    public void onParticipantsDataGridEdit(final ActionPerformedEvent event) {
        ProjectParticipant selectedItem = participantsDataGrid.getSingleSelectedItem();
        if (selectedItem == null) {
            return;
        }

        dialogWindows.detail(participantsDataGrid)
                .editEntity(selectedItem)
                .withParentDataContext(getViewData().getDataContext())
                .open();
    }

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        notifications.show("tasksDc items:" + tasksDc.getItems().size());
        notifications.show("participantsDc items:" + projectParticipantsDc.getItems().size());
    }

    @Subscribe(id = "tasksDc", target = Target.DATA_CONTAINER)
    public void onTasksDcCollectionChange(final CollectionContainer.CollectionChangeEvent<Task> event) {
        notifications.show("[tasksDc] CollectionChangeEvent", event.getChangeType() + "");
    }

    @Subscribe(id = "projectParticipantsDc", target = Target.DATA_CONTAINER)
    public void onParticipantsDcCollectionChange(final CollectionContainer.CollectionChangeEvent<ProjectParticipant> event) {
        notifications.show("[participantsDc] CollectionChangeEvent", event.getChangeType() + "");
    }

//    @ViewComponent
//    private VerticalLayout tasksWrapper;

//    @Subscribe
//    public void onInitEntity(final InitEntityEvent<Project> event) {
//        tasksWrapper.setEnabled(false);
//    }
//
//    @Subscribe
//    public void onAfterSave(final AfterSaveEvent event) {
//        tasksWrapper.setEnabled(true);
//    }
//
//    @Install(to = "tasksDataGrid.create", subject = "initializer")
//    private void tasksDataGridCreateInitializer(final Task task) {
//        task.setProject(getEditedEntity());
//    }
}