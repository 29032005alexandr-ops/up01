
package ru.up01.app.model;

import javafx.beans.property.*;

public class RequestStatus {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();

    public RequestStatus(int id, String name) {
        this.id.set(id);
        this.name.set(name);
    }

    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }

    public String getName() { return name.get(); }
    public StringProperty nameProperty() { return name; }

    @Override public String toString() { return getName(); }
}
