package kz.arta.synergy.components.client.table;

/**
 * User: vsl
 * Date: 27.08.14
 * Time: 11:33
 *
 * Объект для таблицы
 */
public class User {
    private static int idCount = 0;

    private int id;
    private String firstName;
    private String lastName;
    private String address;
    private boolean alive;
    private double lifeLived;

    public User(String firstName, String lastName, String address, boolean alive, double lifeLived) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.alive = alive;
        this.lifeLived = lifeLived;
        id = idCount++;
    }

    public double getLifeLived() {
        return lifeLived;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public boolean isAlive() {
        return alive;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getKey() {
        return id;
    }

    public static void resetIdCount() {
        idCount = 0;
    }

    @Override
    public String toString() {
        return id + " " + firstName + " " + lastName;
    }
}
