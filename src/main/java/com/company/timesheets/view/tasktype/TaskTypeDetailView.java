package com.company.timesheets.view.tasktype;

import com.company.timesheets.entity.TaskType;
import com.company.timesheets.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "task-types/:id", layout = MainView.class)
@ViewController("ts_TaskType.detail")
@ViewDescriptor("task-type-detail-view.xml")
@EditedEntityContainer("taskTypeDc")
@DialogMode(width = "40em")
public class TaskTypeDetailView extends StandardDetailView<TaskType> {
}