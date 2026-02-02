
package ru.up01.app.model;

import javafx.beans.property.*;
import java.time.LocalDateTime;

public class HistoryRow {
    private final IntegerProperty historyId = new SimpleIntegerProperty();
    private final IntegerProperty requestId = new SimpleIntegerProperty();
    private final StringProperty addressLine = new SimpleStringProperty();
    private final StringProperty employeeName = new SimpleStringProperty();
    private final StringProperty statusName = new SimpleStringProperty();
    private final ObjectProperty<LocalDateTime> changedAt = new SimpleObjectProperty<>();
    private final StringProperty comment = new SimpleStringProperty();

    public HistoryRow(int historyId, int requestId, String addressLine, String employeeName, String statusName,
                      LocalDateTime changedAt, String comment) {
        this.historyId.set(historyId);
        this.requestId.set(requestId);
        this.addressLine.set(addressLine);
        this.employeeName.set(employeeName);
        this.statusName.set(statusName);
        this.changedAt.set(changedAt);
        this.comment.set(comment);
    }

    public int getHistoryId() { return historyId.get(); }
    public IntegerProperty historyIdProperty() { return historyId; }

    public int getRequestId() { return requestId.get(); }
    public IntegerProperty requestIdProperty() { return requestId; }

    public String getAddressLine() { return addressLine.get(); }
    public StringProperty addressLineProperty() { return addressLine; }

    public String getEmployeeName() { return employeeName.get(); }
    public StringProperty employeeNameProperty() { return employeeName; }

    public String getStatusName() { return statusName.get(); }
    public StringProperty statusNameProperty() { return statusName; }

    public LocalDateTime getChangedAt() { return changedAt.get(); }
    public ObjectProperty<LocalDateTime> changedAtProperty() { return changedAt; }

    public String getComment() { return comment.get(); }
    public StringProperty commentProperty() { return comment; }
}
