package com.company.timesheets.view.user;

import com.company.timesheets.entity.User;
import com.company.timesheets.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;


@Route(value = "users-simple", layout = MainView.class)
@ViewController(id = "ts_User.simple-list")
@ViewDescriptor(path = "user-list-view-simple.xml")
@LookupComponent("usersDataGrid")
@DialogMode(width = "64em")
public class UserListViewSimple extends StandardListView<User> {
}