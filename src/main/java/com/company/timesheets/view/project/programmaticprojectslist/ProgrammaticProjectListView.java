package com.company.timesheets.view.project.programmaticprojectslist;


import com.company.timesheets.entity.Project;
import com.company.timesheets.view.main.MainView;
import com.vaadin.flow.component.grid.Grid;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import io.jmix.core.FetchPlan;
import io.jmix.core.FetchPlans;
import io.jmix.core.MessageTools;
import io.jmix.core.MetadataTools;
import io.jmix.core.metamodel.model.MetaClass;
import io.jmix.core.metamodel.model.MetaPropertyPath;
import io.jmix.flowui.Actions;
import io.jmix.flowui.Facets;
import io.jmix.flowui.UiComponents;
import io.jmix.flowui.action.list.CreateAction;
import io.jmix.flowui.action.list.EditAction;
import io.jmix.flowui.action.list.RemoveAction;
import io.jmix.flowui.action.view.LookupDiscardAction;
import io.jmix.flowui.action.view.LookupSelectAction;
import io.jmix.flowui.component.genericfilter.GenericFilter;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.component.grid.DataGridColumn;
import io.jmix.flowui.component.pagination.SimplePagination;
import io.jmix.flowui.data.grid.ContainerDataGridItems;
import io.jmix.flowui.data.pagination.PaginationDataLoader;
import io.jmix.flowui.data.pagination.PaginationDataLoaderImpl;
import io.jmix.flowui.facet.DataLoadCoordinator;
import io.jmix.flowui.facet.UrlQueryParametersFacet;
import io.jmix.flowui.facet.urlqueryparameters.GenericFilterUrlQueryParametersBinder;
import io.jmix.flowui.facet.urlqueryparameters.PaginationUrlQueryParametersBinder;
import io.jmix.flowui.kit.action.Action;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.model.DataComponents;
import io.jmix.flowui.model.DataContext;
import io.jmix.flowui.model.impl.NoopDataContext;
import io.jmix.flowui.view.*;
import io.jmix.flowui.view.navigation.UrlParamSerializer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Optional;

@Route(value = "programmatic-projects", layout = MainView.class)
@PrimaryLookupView(Project.class)
@ViewController("ProgrammaticProjectListView")
@DialogMode(width = "64em")
public class ProgrammaticProjectListView extends StandardListView<Project> {

    @Autowired
    private Facets facets;
    @Autowired
    private Actions actions;
    @Autowired
    private FetchPlans fetchPlans;
    @Autowired
    private UiComponents uiComponents;
    @Autowired
    private MessageTools messageTools;
    @Autowired
    private MetadataTools metadataTools;
    @Autowired
    private MessageBundle messageBundle;
    @Autowired
    private DataComponents dataComponents;
    @Autowired
    private UrlParamSerializer urlParamSerializer;

    private DataGrid<Project> projectsDataGrid;

    private CollectionContainer<Project> projectsDc;
    private CollectionLoader<Project> projectsDl;

    private UrlQueryParametersFacet urlQueryParametersFacet;

    @Subscribe
    public void onInit(final InitEvent event) {
        initData();
        initFacets();
        initLayout();

        projectsDataGrid.getElement().setProperty("autofocus", true);
    }

    @Override
    public String getPageTitle() {
        return messageBundle.getMessage("programmaticProjectListView.title");
    }

    private void initData() {
//        DataContext dataContext = dataComponents.createDataContext();
        // Dummy implementation of DataContext used for read-only views like entity list views.
        DataContext dataContext = new NoopDataContext(getApplicationContext());
        getViewData().setDataContext(dataContext);

        projectsDc = dataComponents.createCollectionContainer(Project.class);
        getViewData().registerContainer("projectsDc", projectsDc);

        projectsDl = dataComponents.createCollectionLoader();
        projectsDl.setContainer(projectsDc);
        // If the data context is set, all loaded instances will be merged into it.
        // For the list view we don't need to set it.
//        projectsDl.setDataContext(dataContext);
        projectsDl.setQuery("select e from ts_Project e");
        projectsDl.setFetchPlan(createProjectsFetchPlan());
        getViewData().registerLoader("projectsDl", projectsDl);
    }

    private FetchPlan createProjectsFetchPlan() {
        return fetchPlans.builder(Project.class)
                .addFetchPlan(FetchPlan.BASE)
                .add("client", fetchPlanBuilder ->
                        fetchPlanBuilder.addFetchPlan(FetchPlan.BASE))
                .build();
    }

    private void initFacets() {
        DataLoadCoordinator dataLoadCoordinator = facets.create(DataLoadCoordinator.class);
        getViewFacets().addFacet(dataLoadCoordinator);
        dataLoadCoordinator.configureAutomatically();

        urlQueryParametersFacet = facets.create(UrlQueryParametersFacet.class);
        getViewFacets().addFacet(urlQueryParametersFacet);
    }

    private void initLayout() {
        initGenericFilter();
        initDataGrid();
        initViewButtonsPanel();
    }

    private void initGenericFilter() {
        GenericFilter genericFilter = uiComponents.create(GenericFilter.class);
        genericFilter.setId("genericFilter");
        genericFilter.setDataLoader(projectsDl);
        genericFilter.loadConfigurationsAndApplyDefault();

        getContent().add(genericFilter);

        //noinspection JmixInternalElementUsage
        GenericFilterUrlQueryParametersBinder genericFilterBinder =
                new GenericFilterUrlQueryParametersBinder(genericFilter, urlParamSerializer, getApplicationContext());
        urlQueryParametersFacet.registerBinder(genericFilterBinder);
    }

    private void initDataGrid() {
        //noinspection unchecked
        projectsDataGrid = uiComponents.create(DataGrid.class);
        projectsDataGrid.setId("projectsDataGrid");
        projectsDataGrid.setWidthFull();
        projectsDataGrid.setMinHeight("20em");

        initDataGridColumns();
        projectsDataGrid.setItems(new ContainerDataGridItems<>(projectsDc));
        initDataGridActions();
        initDataGridButtonsPanel();

        getContent().add(projectsDataGrid);
    }

    private void initDataGridActions() {
        CreateAction<Project> createAction = actions.create(CreateAction.ID, "create");
        projectsDataGrid.addAction(createAction);

        EditAction<Project> editAction = actions.create(EditAction.ID, "edit");
        projectsDataGrid.addAction(editAction);

        RemoveAction<Project> removeAction = actions.create(RemoveAction.ID, "remove");
        projectsDataGrid.addAction(removeAction);
    }

    private void initDataGridColumns() {
        addColumn("name");
        addColumn("client");

        Grid.Column<Project> statusColumn = addColumn("status")
                .setFlexGrow(0);
        statusColumn.setRenderer(new ComponentRenderer<>(Span::new, (span, item) -> {
            String theme = switch (item.getStatus()) {
                case OPEN -> "success";
                case CLOSED -> "error";
            };

            span.getElement().setAttribute("theme", "badge " + theme);
            span.setText(metadataTools.format(item.getStatus()));
        }));

        addColumn("description")
                .setFlexGrow(2);
    }

    private DataGridColumn<Project> addColumn(String property) {
        MetaClass metaClass = projectsDc.getEntityMetaClass();
        MetaPropertyPath metaPropertyPath = metadataTools.resolveMetaPropertyPath(metaClass, property);

        DataGridColumn<Project> column = projectsDataGrid.addColumn(metaPropertyPath);
        column.setHeader(messageTools.getPropertyCaption(metaClass, property));

        return column;
    }

    private void initDataGridButtonsPanel() {
        HorizontalLayout buttonsPanel = uiComponents.create(HorizontalLayout.class);
        buttonsPanel.setId("buttonsPanel");
        buttonsPanel.addClassName("buttons-panel");
        getContent().add(buttonsPanel);

        Collection<Action> gridActions = projectsDataGrid.getActions();
        for (Action action : gridActions) {
            JmixButton button = uiComponents.create(JmixButton.class);
            button.setId(action.getId() + "Btn");
            button.setAction(action);

            buttonsPanel.add(button);
        }

        SimplePagination simplePagination = createSimplePagination();
        buttonsPanel.add(simplePagination);
    }

    private SimplePagination createSimplePagination() {
        SimplePagination simplePagination = uiComponents.create(SimplePagination.class);
        simplePagination.setId("pagination");

        PaginationDataLoader paginationLoader =
                getApplicationContext().getBean(PaginationDataLoaderImpl.class, projectsDl);
        simplePagination.setPaginationLoader(paginationLoader);

        //noinspection JmixInternalElementUsage
        PaginationUrlQueryParametersBinder paginationBinder =
                new PaginationUrlQueryParametersBinder(simplePagination, urlParamSerializer);
        urlQueryParametersFacet.registerBinder(paginationBinder);
        return simplePagination;
    }

    private void initViewButtonsPanel() {
        HorizontalLayout buttonsPanel = uiComponents.create(HorizontalLayout.class);
        buttonsPanel.setId("lookupActions");
        buttonsPanel.setVisible(false);
        getContent().add(buttonsPanel);

        LookupSelectAction<Project> selectAction = actions.create(LookupSelectAction.ID, "selectAction");
        getViewActions().addAction(selectAction);

        JmixButton selectButton = uiComponents.create(JmixButton.class);
        selectButton.setId("selectBtn");
        selectButton.setAction(selectAction);
        buttonsPanel.add(selectButton);

        LookupDiscardAction<Project> discardAction = actions.create(LookupDiscardAction.ID, "discardAction");
        getViewActions().addAction(discardAction);

        JmixButton discardButton = uiComponents.create(JmixButton.class);
        discardButton.setId("discardBtn");
        discardButton.setAction(discardAction);
        buttonsPanel.add(discardButton);
    }

    @Override
    public Optional<io.jmix.flowui.component.LookupComponent<Project>> findLookupComponent() {
        return Optional.ofNullable(projectsDataGrid);
    }
}