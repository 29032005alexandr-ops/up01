
package ru.up01.app.model;

import javafx.beans.property.*;

public class Address {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty addressLine = new SimpleStringProperty();
    private final StringProperty city = new SimpleStringProperty();
    private final StringProperty street = new SimpleStringProperty();
    private final StringProperty house = new SimpleStringProperty();

    public Address(int id, String addressLine, String city, String street, String house) {
        this.id.set(id);
        this.addressLine.set(addressLine);
        this.city.set(city);
        this.street.set(street);
        this.house.set(house);
    }

    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }

    public String getAddressLine() { return addressLine.get(); }
    public StringProperty addressLineProperty() { return addressLine; }

    public String getCity() { return city.get(); }
    public StringProperty cityProperty() { return city; }

    public String getStreet() { return street.get(); }
    public StringProperty streetProperty() { return street; }

    public String getHouse() { return house.get(); }
    public StringProperty houseProperty() { return house; }

    @Override public String toString() {
        return getAddressLine() != null && !getAddressLine().isBlank()
                ? getAddressLine()
                : (getCity() + ", " + getStreet() + ", " + getHouse());
    }
}
