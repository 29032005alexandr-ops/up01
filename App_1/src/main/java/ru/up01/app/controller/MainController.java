
package ru.up01.app.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import ru.up01.app.dao.*;
import ru.up01.app.model.*;
import ru.up01.app.ui.Alerts;
import ru.up01.app.ui.Validators;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class MainController {

    // Tables: Addresses
    @FXML private TableView<Address> addressesTable;
    @FXML private TableColumn<Address, Integer> colAddrId;
    @FXML private TableColumn<Address, String> colAddrLine;
    @FXML private TableColumn<Address, String> colAddrCity;
    @FXML private TableColumn<Address, String> colAddrStreet;
    @FXML private TableColumn<Address, String> colAddrHouse;

    // Tables: Employees
    @FXML private TableView<Employee> employeesTable;
    @FXML private TableColumn<Employee, Integer> colEmpId;
    @FXML private TableColumn<Employee, String> colEmpName;
    @FXML private TableColumn<Employee, String> colEmpPos;
    @FXML private TableColumn<Employee, String> colEmpPhone;
    @FXML private TableColumn<Employee, String> colEmpEmail;

    // Tables: Requests
    @FXML private TableView<ServiceRequest> requestsTable;
    @FXML private TableColumn<ServiceRequest, Integer> colReqId;
    @FXML private TableColumn<ServiceRequest, String> colReqAddress;
    @FXML private TableColumn<ServiceRequest, String> colReqEmployee;
    @FXML private TableColumn<ServiceRequest, String> colReqStatus;
    @FXML private TableColumn<ServiceRequest, String> colReqApplicant;
    @FXML private TableColumn<ServiceRequest, String> colReqPhone;
    @FXML private TableColumn<ServiceRequest, String> colReqProblem;
    @FXML private TableColumn<ServiceRequest, Object> colReqCreated;
    @FXML private TableColumn<ServiceRequest, Object> colReqUpdated;

    // Tables: History
    @FXML private TableView<HistoryRow> historyTable;
    @FXML private TableColumn<HistoryRow, Integer> colHistId;
    @FXML private TableColumn<HistoryRow, Integer> colHistReqId;
    @FXML private TableColumn<HistoryRow, String> colHistAddress;
    @FXML private TableColumn<HistoryRow, String> colHistEmployee;
    @FXML private TableColumn<HistoryRow, String> colHistStatus;
    @FXML private TableColumn<HistoryRow, Object> colHistChanged;
    @FXML private TableColumn<HistoryRow, String> colHistComment;
    @FXML private ComboBox<Address> historyAddressFilter;
    @FXML private ComboBox<Employee> historyEmployeeFilter;

    private final AddressDao addressDao = new AddressDao();
    private final EmployeeDao employeeDao = new EmployeeDao();
    private final StatusDao statusDao = new StatusDao();
    private final ServiceRequestDao requestDao = new ServiceRequestDao();
    private final HistoryDao historyDao = new HistoryDao();

    private final ObservableList<Address> addresses = FXCollections.observableArrayList();
    private final ObservableList<Employee> employees = FXCollections.observableArrayList();
    private final ObservableList<RequestStatus> statuses = FXCollections.observableArrayList();
    private final ObservableList<ServiceRequest> requests = FXCollections.observableArrayList();
    private final ObservableList<HistoryRow> history = FXCollections.observableArrayList();

    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    public void initialize() {
        initColumns();
        bindTables();
        loadAll();
    }

    private void initColumns() {
        // Address
        colAddrId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colAddrLine.setCellValueFactory(new PropertyValueFactory<>("addressLine"));
        colAddrCity.setCellValueFactory(new PropertyValueFactory<>("city"));
        colAddrStreet.setCellValueFactory(new PropertyValueFactory<>("street"));
        colAddrHouse.setCellValueFactory(new PropertyValueFactory<>("house"));

        // Employee
        colEmpId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colEmpName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colEmpPos.setCellValueFactory(new PropertyValueFactory<>("position"));
        colEmpPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmpEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Requests
        colReqId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colReqAddress.setCellValueFactory(new PropertyValueFactory<>("addressLine"));
        colReqEmployee.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        colReqStatus.setCellValueFactory(new PropertyValueFactory<>("statusName"));
        colReqApplicant.setCellValueFactory(new PropertyValueFactory<>("applicantFullName"));
        colReqPhone.setCellValueFactory(new PropertyValueFactory<>("applicantPhone"));
        colReqProblem.setCellValueFactory(new PropertyValueFactory<>("problemDescription"));

        colReqCreated.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        colReqCreated.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : DT.format((java.time.LocalDateTime) item));
            }
        });

        colReqUpdated.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));
        colReqUpdated.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : DT.format((java.time.LocalDateTime) item));
            }
        });

        // History
        colHistId.setCellValueFactory(new PropertyValueFactory<>("historyId"));
        colHistReqId.setCellValueFactory(new PropertyValueFactory<>("requestId"));
        colHistAddress.setCellValueFactory(new PropertyValueFactory<>("addressLine"));
        colHistEmployee.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        colHistStatus.setCellValueFactory(new PropertyValueFactory<>("statusName"));
        colHistChanged.setCellValueFactory(new PropertyValueFactory<>("changedAt"));
        colHistChanged.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : DT.format((java.time.LocalDateTime) item));
            }
        });
        colHistComment.setCellValueFactory(new PropertyValueFactory<>("comment"));
    }

    private void bindTables() {
        addressesTable.setItems(addresses);
        employeesTable.setItems(employees);
        requestsTable.setItems(requests);
        historyTable.setItems(history);

        historyAddressFilter.setConverter(new StringConverter<>() {
            @Override public String toString(Address a) { return a == null ? "Все" : a.toString(); }
            @Override public Address fromString(String s) { return null; }
        });
        historyEmployeeFilter.setConverter(new StringConverter<>() {
            @Override public String toString(Employee e) { return e == null ? "Все" : e.toString(); }
            @Override public Employee fromString(String s) { return null; }
        });
    }

    private void loadAll() {
        try {
            List<Address> a = addressDao.findAll();
            List<Employee> e = employeeDao.findAll();
            List<RequestStatus> s = statusDao.findAll();
            List<ServiceRequest> r = requestDao.findAllJoined();
            List<HistoryRow> h = historyDao.findHistory(null, null);

            addresses.setAll(a);
            employees.setAll(e);
            statuses.setAll(s);
            requests.setAll(r);
            history.setAll(h);

            historyAddressFilter.getItems().setAll(addresses);
            historyEmployeeFilter.getItems().setAll(employees);

        } catch (SQLException ex) {
            Alerts.error("Ошибка БД", ex.getMessage());
        }
    }

    // Toolbar
    @FXML private void onRefreshAll() { loadAll(); }

    // Requests actions
    @FXML private void onRefreshRequests() {
        try {
            requests.setAll(requestDao.findAllJoined());
        } catch (SQLException ex) {
            Alerts.error("Ошибка БД", ex.getMessage());
        }
    }

    @FXML private void onAddRequest() {
        RequestEditDialog dlg = new RequestEditDialog(null, addresses, employees, statuses);
        ServiceRequest created = dlg.showAndWait();
        if (created == null) return;

        try {
            // ensure import_id is present (unique)
            if (created.getImportId() == null || created.getImportId().isBlank()) {
                created.importIdProperty().set("MANUAL-" + UUID.randomUUID());
            }
            requestDao.insert(created, "Создана заявка");
            onRefreshRequests();
            refreshHistoryKeepingFilters();
        } catch (SQLException ex) {
            Alerts.error("Ошибка БД", ex.getMessage());
        }
    }

    @FXML private void onEditRequest() {
        ServiceRequest selected = requestsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alerts.info("Редактирование", "Выберите заявку в таблице.");
            return;
        }
        RequestEditDialog dlg = new RequestEditDialog(selected, addresses, employees, statuses);
        ServiceRequest edited = dlg.showAndWait();
        if (edited == null) return;

        try {
            requestDao.update(edited, "Изменена заявка");
            onRefreshRequests();
            refreshHistoryKeepingFilters();
        } catch (SQLException ex) {
            Alerts.error("Ошибка БД", ex.getMessage());
        }
    }

    @FXML private void onDeleteRequest() {
        ServiceRequest selected = requestsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alerts.info("Удаление", "Выберите заявку в таблице.");
            return;
        }
        boolean ok = Alerts.confirmDanger("Удаление заявки", "Удалить заявку ID=" + selected.getId() + "?\nИстория также будет удалена.");
        if (!ok) return;

        try {
            requestDao.deleteById(selected.getId());
            onRefreshRequests();
            refreshHistoryKeepingFilters();
        } catch (SQLException ex) {
            Alerts.error("Ошибка БД", ex.getMessage());
        }
    }

    // History filters
    @FXML private void onApplyHistoryFilters() { refreshHistoryKeepingFilters(); }

    @FXML private void onResetHistoryFilters() {
        historyAddressFilter.getSelectionModel().clearSelection();
        historyEmployeeFilter.getSelectionModel().clearSelection();
        refreshHistoryKeepingFilters();
    }

    private void refreshHistoryKeepingFilters() {
        Integer addressId = null;
        Integer employeeId = null;
        Address a = historyAddressFilter.getSelectionModel().getSelectedItem();
        Employee e = historyEmployeeFilter.getSelectionModel().getSelectedItem();
        if (a != null) addressId = a.getId();
        if (e != null) employeeId = e.getId();

        try {
            history.setAll(historyDao.findHistory(addressId, employeeId));
        } catch (SQLException ex) {
            Alerts.error("Ошибка БД", ex.getMessage());
        }
    }

    /** Dialog for creating/editing a request. */
    private static class RequestEditDialog {
        private final Dialog<ButtonType> dialog = new Dialog<>();
        private final ComboBox<Address> cbAddress = new ComboBox<>();
        private final ComboBox<Employee> cbEmployee = new ComboBox<>();
        private final ComboBox<RequestStatus> cbStatus = new ComboBox<>();
        private final TextField tfApplicant = new TextField();
        private final TextField tfPhone = new TextField();
        private final TextArea taProblem = new TextArea();

        private final ServiceRequest existing;

        RequestEditDialog(ServiceRequest existing,
                          ObservableList<Address> addresses,
                          ObservableList<Employee> employees,
                          ObservableList<RequestStatus> statuses) {
            this.existing = existing;

            dialog.setTitle(existing == null ? "Добавление заявки" : "Редактирование заявки");
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            cbAddress.setItems(addresses);
            cbEmployee.setItems(employees);
            cbStatus.setItems(statuses);

            cbAddress.setPrefWidth(520);
            cbEmployee.setPrefWidth(520);
            cbStatus.setPrefWidth(520);

            taProblem.setPrefRowCount(4);
            taProblem.setWrapText(true);

            var grid = new javafx.scene.layout.GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new javafx.geometry.Insets(12));

            int r = 0;
            grid.add(new Label("Адрес*:"), 0, r); grid.add(cbAddress, 1, r++);
            grid.add(new Label("Исполнитель:"), 0, r); grid.add(cbEmployee, 1, r++);
            grid.add(new Label("Статус*:"), 0, r); grid.add(cbStatus, 1, r++);
            grid.add(new Label("ФИО заявителя*:"), 0, r); grid.add(tfApplicant, 1, r++);
            grid.add(new Label("Телефон*:"), 0, r); grid.add(tfPhone, 1, r++);
            grid.add(new Label("Описание проблемы*:"), 0, r); grid.add(taProblem, 1, r++);

            dialog.getDialogPane().setContent(grid);

            // preload for edit
            if (existing != null) {
                // address
                addresses.stream().filter(a -> a.getId() == existing.getAddressId()).findFirst()
                        .ifPresent(a -> cbAddress.getSelectionModel().select(a));
                // employee
                if (existing.getEmployeeIdNullable() != null) {
                    employees.stream().filter(e -> e.getId() == existing.getEmployeeId()).findFirst()
                            .ifPresent(e -> cbEmployee.getSelectionModel().select(e));
                }
                // status
                statuses.stream().filter(s -> s.getId() == existing.getStatusId()).findFirst()
                        .ifPresent(s -> cbStatus.getSelectionModel().select(s));

                tfApplicant.setText(existing.getApplicantFullName());
                tfPhone.setText(existing.getApplicantPhone());
                taProblem.setText(existing.getProblemDescription());
            } else {
                // defaults
                if (!addresses.isEmpty()) cbAddress.getSelectionModel().select(0);
                if (!statuses.isEmpty()) cbStatus.getSelectionModel().select(0);
            }

            // validation on OK
            Button okBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
            okBtn.addEventFilter(javafx.event.ActionEvent.ACTION, ev -> {
                String err = validate();
                if (err != null) {
                    Alerts.error("Ошибка ввода", err);
                    ev.consume();
                }
            });
        }

        ServiceRequest showAndWait() {
            var res = dialog.showAndWait();
            if (res.isEmpty() || res.get() != ButtonType.OK) return null;

            Address a = cbAddress.getSelectionModel().getSelectedItem();
            Employee e = cbEmployee.getSelectionModel().getSelectedItem();
            RequestStatus s = cbStatus.getSelectionModel().getSelectedItem();

            if (existing == null) {
                // id=0 placeholder; import_id will be set by caller if empty
                return new ServiceRequest(
                        0,
                        "",
                        a.getId(),
                        e == null ? null : e.getId(),
                        s.getId(),
                        tfApplicant.getText().trim(),
                        tfPhone.getText().trim(),
                        taProblem.getText().trim(),
                        null,
                        null
                );
            } else {
                ServiceRequest edited = new ServiceRequest(
                        existing.getId(),
                        existing.getImportId(),
                        a.getId(),
                        e == null ? null : e.getId(),
                        s.getId(),
                        tfApplicant.getText().trim(),
                        tfPhone.getText().trim(),
                        taProblem.getText().trim(),
                        existing.getCreatedAt(),
                        existing.getUpdatedAt()
                );
                return edited;
            }
        }

        private String validate() {
            if (cbAddress.getSelectionModel().getSelectedItem() == null) return "Выберите адрес.";
            if (cbStatus.getSelectionModel().getSelectedItem() == null) return "Выберите статус.";

            String err;
            err = Validators.requireNotBlank(tfApplicant.getText(), "ФИО заявителя", 200);
            if (err != null) return err;

            err = Validators.requireNotBlank(tfPhone.getText(), "Телефон", 20);
            if (err != null) return err;

            err = Validators.validatePhone(tfPhone.getText(), "Телефон");
            if (err != null) return err;

            err = Validators.requireNotBlank(taProblem.getText(), "Описание проблемы", 2000);
            if (err != null) return err;

            return null;
        }
    }
}
