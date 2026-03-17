package seedu.equipmentmaster.equipment;

import seedu.equipmentmaster.semester.AcademicSemester;

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
    private AcademicSemester purchaseSem;
    private double lifespanYears;

    /**
     * Constructs an Equipment object with full lifecycle attributes.
     * @param name Name of the equipment.
     * @param total Initial total quantity.
     */
    public Equipment(String name, int total) {
        this.name = name;
        this.quantity = total;
        this.available = total;
        this.loaned = 0;
    }

    /**
     * Constructs an Equipment object with initial available and loaned quantity.
     * @param name Name of the equipment.
     * @param quantity Initial total quantity.
     * @param available Initial available quantity
     * @param loaned Initial loaned quantity
     */
    public Equipment(String name, int quantity, int available, int loaned) {
        this.name = name;
        this.quantity = quantity;
        this.available = available;
        this.loaned = loaned;
    }

    /**
     * Constructs a new Equipment object with lifecycle data.
     * Available quantity is initially set to total quantity, and loaned is set to 0.
     * This is primarily used when adding a brand-new equipment via the CLI.
     *
     * @param name Name of the equipment.
     * @param total Initial total quantity.
     * @param purchaseSem The academic semester when the equipment was purchased.
     * @param lifespanYears The expected lifespan in years (e.g., 2.5).
     */
    public Equipment(String name, int total, AcademicSemester purchaseSem, double lifespanYears) {
        this.name = name;
        this.quantity = total;
        this.available = total;
        this.loaned = 0;
        this.purchaseSem = purchaseSem;
        this.lifespanYears = lifespanYears;
    }

    /**
     * Constructs an Equipment object with all details, including lifecycle data.
     * This is primarily used when loading existing data from the storage file.
     *
     * @param name Name of the equipment.
     * @param quantity Total quantity of the equipment.
     * @param available Number of available items.
     * @param loaned Number of loaned items.
     * @param purchaseSem The academic semester when the equipment was purchased.
     * @param lifespanYears The expected lifespan in years.
     */
    public Equipment(String name, int quantity, int available, int loaned,
                     AcademicSemester purchaseSem, double lifespanYears) {
        this.name = name;
        this.quantity = quantity;
        this.available = available;
        this.loaned = loaned;
        this.purchaseSem = purchaseSem;
        this.lifespanYears = lifespanYears;
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
     * Retrieves the academic semester when the equipment was purchased.
     *
     * @return The purchase semester of the equipment, or null if not set.
     */
    public AcademicSemester getPurchaseSem() {
        return purchaseSem;
    }

    /**
     * Sets the academic semester when the equipment was purchased.
     *
     * @param purchaseSem The semester to set as the purchase date.
     */
    public void setPurchaseSem(AcademicSemester purchaseSem) {
        this.purchaseSem = purchaseSem;
    }

    /**
     * Retrieves the expected lifespan of the equipment in years.
     *
     * @return The lifespan in years (e.g., 2.5).
     */
    public double getLifespanYears() {
        return lifespanYears;
    }

    /**
     * Sets the expected lifespan of the equipment in years.
     *
     * @param lifespanYears The expected lifespan in years. Can include fractional values (e.g., 0.5).
     */
    public void setLifespanYears(double lifespanYears) {
        this.lifespanYears = lifespanYears;
    }

    /**
     * Returns a human-readable representation of the equipment.
     *
     * @return Formatted equipment information.
     */
    @Override
    public String toString() {
        return name + " | Total: " + quantity + " | Available: " + available + " | loaned: " + loaned + " | Purchase: " + purchaseSem + " | Lifespan: " + lifespanYears;
    }

    /**
     * Returns a string representation of the equipment formatted for file storage.
     *
     * @return Equipment data formatted for saving to file.
     */
    public String toFileString() {
        return this.name + " | " + this.quantity + " | " + this.available + " | " + this.loaned + " | " + this.purchaseSem.toString() + " | " + this.lifespanYears;
    }
}
