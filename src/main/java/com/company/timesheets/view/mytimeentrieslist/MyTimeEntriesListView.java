package com.company.timesheets.view.mytimeentrieslist;


import com.company.timesheets.app.TimeEntrySupport;
import com.company.timesheets.entity.TimeEntry;
import com.company.timesheets.view.main.MainView;
import com.company.timesheets.view.timeentry.TimeEntryDetailView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import io.jmix.core.MetadataTools;
import io.jmix.flowui.DialogWindows;
import io.jmix.flowui.Dialogs;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.app.inputdialog.DialogActions;
import io.jmix.flowui.app.inputdialog.DialogOutcome;
import io.jmix.flowui.app.inputdialog.InputDialog;
import io.jmix.flowui.app.inputdialog.InputParameter;
import io.jmix.flowui.asynctask.UiAsyncTasks;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.facet.Timer;
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

    @Autowired
    private MetadataTools metadataTools;

    @Supply(to = "timeEntriesDataGrid.status", subject = "renderer")
    private Renderer<TimeEntry> timeEntriesDataGridStatusRenderer() {
        return new ComponentRenderer<>(Span::new, ((span, timeEntry) -> {
            String theme = switch (timeEntry.getStatus()) {
                case NEW -> "";
                case APPROVED -> "success";
                case REJECTED -> "error";
                case CLOSED -> "contrast";
            };
            span.getElement().setAttribute("theme", "badge " + theme);
            span.setText(metadataTools.format(timeEntry.getStatus()));
        }));

    }

//    int seconds = 0;

//    @Subscribe("timer")
//    public void onTimerTimerAction(final Timer.TimerActionEvent event) {
//
//        seconds += event.getSource().getDelay() / 1000;
//        notifications.show("Timer tick", seconds + " seconds passed");
//    }
//
//    @Subscribe("timer")
//    public void onTimerTimerStop(final Timer.TimerStopEvent event) {
//        notifications.show("Timer stopped");
//    }
}