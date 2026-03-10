package seedu.duke.equipment;

public class Equipment {
    private String name;
    private int quantity;
    private int available;
    private int loaned;

    public Equipment(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
        this.available = quantity;
        this.loaned = 0;
    }

    public Equipment(String name, int quantity, int available, int loaned) {
        this.name = name;
        this.quantity = quantity;
        this.available = available;
        this.loaned = loaned;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getAvailable() {
        return available;
    }

    public int getLoaned() {
        return loaned;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public void setLoaned(int loaned) {
        this.loaned = loaned;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return name + "| Total: " + quantity + " | Available: " + available + " | loaned: " + loaned;
    }
}
