package com.company.timesheets.view.mytimeentrieslist;


import com.company.timesheets.app.TimeEntrySupport;
import com.company.timesheets.entity.TimeEntry;
import com.company.timesheets.view.main.MainView;
import com.company.timesheets.view.timeentry.TimeEntryDetailView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.DialogWindows;
import io.jmix.flowui.Dialogs;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.app.inputdialog.DialogActions;
import io.jmix.flowui.app.inputdialog.DialogOutcome;
import io.jmix.flowui.app.inputdialog.InputDialog;
import io.jmix.flowui.app.inputdialog.InputParameter;
import io.jmix.flowui.asynctask.UiAsyncTasks;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "my-time-entries-list-view", layout = MainView.class)
@ViewController(id = "ts_TimeEntry.my")
@ViewDescriptor(path = "my-time-entries-list-view.xml")
public class MyTimeEntriesListView extends StandardView {

    @ViewComponent
    private DataGrid<TimeEntry> timeEntriesDataGrid;
    @Autowired
    private TimeEntrySupport timeEntrySupport;
    @Autowired
    private Dialogs dialogs;
    @Autowired
    private UiAsyncTasks uiAsyncTasks;
    @Autowired
    private Notifications notifications;
    @Autowired
    private DialogWindows dialogWindows;

    @Subscribe("timeEntriesDataGrid.copy")
    public void onTimeEntriesDataGridCopy(final ActionPerformedEvent event) {
        TimeEntry selectedTE = timeEntriesDataGrid.getSingleSelectedItem();
        if (selectedTE == null) {
            return;
        }

        TimeEntry copiedTE = timeEntrySupport.copy(selectedTE);

        DialogWindow<TimeEntryDetailView> dialogWindow = dialogWindows.detail(timeEntriesDataGrid)
                .withViewClass(TimeEntryDetailView.class)
                .newEntity(copiedTE)
                .build();
        dialogWindow.getView().setOwnTimeEntry(true);
        dialogWindow.open();

    }

    @Install(to = "timeEntriesDataGrid.create", subject = "queryParametersProvider")
    private QueryParameters timeEntriesDataGridCreateQueryParametersProvider() {
        return QueryParameters.of(TimeEntryDetailView.PARAMETER_OWN_TIME_ENTRY, "");
    }

    @Install(to = "timeEntriesDataGrid.edit", subject = "queryParametersProvider")
    private QueryParameters timeEntriesDataGridEditQueryParametersProvider() {
        return QueryParameters.of(TimeEntryDetailView.PARAMETER_OWN_TIME_ENTRY, "");
    }

    @Subscribe(id = "asyncBtn", subject = "clickListener")
    public void onAsyncBtnClick(final ClickEvent<JmixButton> event) throws InterruptedException {
        InputDialog inputDialog = dialogs.createInputDialog(this)
                .withParameter(InputParameter.stringParameter("input"))
                .withHeader("Type something")
                .withActions(DialogActions.OK_CANCEL)
                .withCloseListener(closeEvent -> {
                    if (closeEvent.closedWith(DialogOutcome.OK)) {

                        String toPass = closeEvent.getValue("input");
                        uiAsyncTasks.supplierConfigurer(() -> {
                                            try {
                                                return performStuff(toPass);
                                            } catch (InterruptedException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                )
                                .withResultHandler(result ->
                                        notifications.create(result)
                                                .withCloseable(true)
                                                .withDuration(0)
                                                .show()
                                                )

                                .supplyAsync();
                    }
                })
                .open();
    }

    private String performStuff(String toPerform) throws InterruptedException {
        Thread.sleep(5000);

//        notifications.show("Stuff is done!");
        return ("Performed string result is: " + toPerform.toUpperCase());
    }
}