
package ru.up01.app.model;

import javafx.beans.property.*;
import java.time.LocalDateTime;

public class ServiceRequest {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty importId = new SimpleStringProperty();
    private final IntegerProperty addressId = new SimpleIntegerProperty();
    private final IntegerProperty employeeId = new SimpleIntegerProperty(); // 0 means null
    private final IntegerProperty statusId = new SimpleIntegerProperty();
    private final StringProperty applicantFullName = new SimpleStringProperty();
    private final StringProperty applicantPhone = new SimpleStringProperty();
    private final StringProperty problemDescription = new SimpleStringProperty();
    private final ObjectProperty<LocalDateTime> createdAt = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> updatedAt = new SimpleObjectProperty<>();

    // View fields (joined)
    private final StringProperty addressLine = new SimpleStringProperty();
    private final StringProperty employeeName = new SimpleStringProperty();
    private final StringProperty statusName = new SimpleStringProperty();

    public ServiceRequest(int id, String importId, int addressId, Integer employeeId, int statusId,
                          String applicantFullName, String applicantPhone, String problemDescription,
                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id.set(id);
        this.importId.set(importId);
        this.addressId.set(addressId);
        this.employeeId.set(employeeId == null ? 0 : employeeId);
        this.statusId.set(statusId);
        this.applicantFullName.set(applicantFullName);
        this.applicantPhone.set(applicantPhone);
        this.problemDescription.set(problemDescription);
        this.createdAt.set(createdAt);
        this.updatedAt.set(updatedAt);
    }

    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }

    public String getImportId() { return importId.get(); }
    public StringProperty importIdProperty() { return importId; }

    public int getAddressId() { return addressId.get(); }
    public IntegerProperty addressIdProperty() { return addressId; }

    public Integer getEmployeeIdNullable() { return employeeId.get() == 0 ? null : employeeId.get(); }
    public int getEmployeeId() { return employeeId.get(); }
    public IntegerProperty employeeIdProperty() { return employeeId; }

    public int getStatusId() { return statusId.get(); }
    public IntegerProperty statusIdProperty() { return statusId; }

    public String getApplicantFullName() { return applicantFullName.get(); }
    public StringProperty applicantFullNameProperty() { return applicantFullName; }

    public String getApplicantPhone() { return applicantPhone.get(); }
    public StringProperty applicantPhoneProperty() { return applicantPhone; }

    public String getProblemDescription() { return problemDescription.get(); }
    public StringProperty problemDescriptionProperty() { return problemDescription; }

    public LocalDateTime getCreatedAt() { return createdAt.get(); }
    public ObjectProperty<LocalDateTime> createdAtProperty() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt.get(); }
    public ObjectProperty<LocalDateTime> updatedAtProperty() { return updatedAt; }

    public String getAddressLine() { return addressLine.get(); }
    public StringProperty addressLineProperty() { return addressLine; }
    public void setAddressLine(String v) { addressLine.set(v); }

    public String getEmployeeName() { return employeeName.get(); }
    public StringProperty employeeNameProperty() { return employeeName; }
    public void setEmployeeName(String v) { employeeName.set(v); }

    public String getStatusName() { return statusName.get(); }
    public StringProperty statusNameProperty() { return statusName; }
    public void setStatusName(String v) { statusName.set(v); }
}
