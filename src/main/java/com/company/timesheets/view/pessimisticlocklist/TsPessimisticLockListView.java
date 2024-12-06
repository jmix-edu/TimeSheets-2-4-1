package com.company.timesheets.view.pessimisticlocklist;

import com.company.timesheets.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import io.jmix.pessimisticlockflowui.view.pessimisticlock.PessimisticLockListView;

@Route(value = "pslock/pessimistic-locks", layout = MainView.class)
@ViewController(id = "pslock_LockInfo.list")
@ViewDescriptor(path = "ts-pessimistic-lock-list-view.xml")
public class TsPessimisticLockListView extends PessimisticLockListView {

    @Override
    public void onInit(InitEvent event) {
        messageBundle.setMessageGroup("io.jmix.pessimisticlockflowui.view.pessimisticlock");
        super.onInit(event);
    }
}