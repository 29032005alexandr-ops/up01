
package ru.up01.app.model;

import javafx.beans.property.*;

public class Employee {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty fullName = new SimpleStringProperty();
    private final StringProperty phone = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty position = new SimpleStringProperty();

    public Employee(int id, String fullName, String phone, String email, String position) {
        this.id.set(id);
        this.fullName.set(fullName);
        this.phone.set(phone);
        this.email.set(email);
        this.position.set(position);
    }

    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }

    public String getFullName() { return fullName.get(); }
    public StringProperty fullNameProperty() { return fullName; }

    public String getPhone() { return phone.get(); }
    public StringProperty phoneProperty() { return phone; }

    public String getEmail() { return email.get(); }
    public StringProperty emailProperty() { return email; }

    public String getPosition() { return position.get(); }
    public StringProperty positionProperty() { return position; }

    @Override public String toString() {
        return getFullName() + (getPosition() == null || getPosition().isBlank() ? "" : " (" + getPosition() + ")");
    }
}
