package seedu.equipmentmaster.equipment;

/**
 * Represents a piece of equipment in the EquipmentMaster system.
 * Each equipment has a name, total quantity, number of available items,
 * and number of loaned items.
 */
public class Equipment {
    private String name;
    private int quantity;
    private int available;
    private int loaned;

    /**
     * Creates new Equipment with the specified name and quantity.
     * All items are initially available and none are loaned.
     *
     * @param name Name of the equipment.
     * @param quantity Total quantity of the equipment.
     */
    public Equipment(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
        this.available = quantity;
        this.loaned = 0;
    }

    /**
     * Creates Equipment with full details.
     *
     * @param name Name of the equipment.
     * @param quantity Total quantity of the equipment.
     * @param available Number of available items.
     * @param loaned Number of loaned items.
     */
    public Equipment(String name, int quantity, int available, int loaned) {
        this.name = name;
        this.quantity = quantity;
        this.available = available;
        this.loaned = loaned;
    }

    /**
     * Returns the name of the equipment.
     *
     * @return Name of the equipment.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the total quantity of the equipment.
     *
     * @return Total quantity.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Returns the number of available equipment items.
     *
     * @return Number of available items.
     */
    public int getAvailable() {
        return available;
    }

    /**
     * Returns the number of loaned equipment items.
     *
     * @return Number of loaned items.
     */
    public int getLoaned() {
        return loaned;
    }

    /**
     * Updates the number of available equipment items.
     *
     * @param available Updated available quantity
     */
    public void setAvailable(int available) {
        this.available = available;
    }

    /**
     * Updates the number of loaned equipment items.
     *
     * @param loaned Updated loaned quantity.
     */
    public void setLoaned(int loaned) {
        this.loaned = loaned;
    }

    /**
     * Updates the equipment name.
     *
     * @param name New equipment name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Updates the total quantity of the equipment.
     *
     * @param quantity New total quantity.
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Returns a human-readable representation of the equipment.
     *
     * @return Formatted equipment information.
     */
    @Override
    public String toString() {
        return name + " | Total: " + quantity + " | Available: " + available + " | loaned: " + loaned;
    }

    /**
     * Returns a string representation of the equipment formatted for file storage.
     *
     * @return Equipment data formatted for saving to file.
     */
    public String toFileString() {
        return this.name + " | " + this.quantity + " | " + this.available + " | " + this.loaned;
    }
}
