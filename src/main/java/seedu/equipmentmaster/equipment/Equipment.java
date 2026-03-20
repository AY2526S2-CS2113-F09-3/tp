package seedu.equipmentmaster.equipment;

import seedu.equipmentmaster.semester.AcademicSemester;

import java.util.ArrayList;

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
    private int minQuantity = 0;
    private ArrayList<String> moduleCodes;

    /**
     * Constructs an Equipment object with full lifecycle attributes.
     *
     * @param name  Name of the equipment.
     * @param total Initial total quantity.
     */
    public Equipment(String name, int total) {
        this(name, total, total, 0, null, 0.0, new ArrayList<>(), 0);
    }

    /**
     * Creates Equipment with full details.
     *
     * @param name      Name of the equipment.
     * @param quantity  Total quantity of the equipment.
     * @param available Number of available items.
     * @param loaned    Number of loaned items.
     */
    public Equipment(String name, int quantity, int available, int loaned) {
        this(name, quantity, available, loaned, null, 0.0, new ArrayList<>(), 0);
    }

    /**
     * Constructs an Equipment object with all details, including lifecycle data.
     * This is primarily used when loading existing data from the storage file.
     *
     * @param name          Name of the equipment.
     * @param quantity      Total quantity of the equipment.
     * @param available     Number of available items.
     * @param loaned        Number of loaned items.
     * @param purchaseSem   The academic semester when the equipment was purchased.
     * @param lifespanYears The expected lifespan in years.
     * @param moduleCodes   List of module codes associated with this equipment.
     */
    public Equipment(String name, int quantity, int available, int loaned,
                     AcademicSemester purchaseSem, double lifespanYears,
                     ArrayList<String> moduleCodes, int minQuantity) {
        this.name = name;
        this.quantity = quantity;
        this.available = available;
        this.loaned = loaned;
        this.purchaseSem = purchaseSem;
        this.lifespanYears = lifespanYears;
        this.moduleCodes = moduleCodes != null ? moduleCodes : new ArrayList<>();
        setMinQuantity(minQuantity); //trigger assertion
    }

    /**
     * Constructs an Equipment object with semester and lifespan but no modules.
     * This is primarily used when creating equipment with purchase information
     * but without module associations.
     *
     * @param name          Name of the equipment.
     * @param quantity      Total quantity of the equipment.
     * @param available     Number of available items.
     * @param loaned        Number of loaned items.
     * @param purchaseSem   The academic semester when the equipment was purchased.
     * @param lifespanYears The expected lifespan in years.
     */
    public Equipment(String name, int quantity, int available, int loaned,
                     AcademicSemester purchaseSem, double lifespanYears, int minQuantity) {
        this(name, quantity, available, loaned, purchaseSem, lifespanYears, new ArrayList<>(), minQuantity);
    }

    /**
     * Updates the minimum stock threshold.
     *
     * @param minQuantity New threshold value.
     */
    public void setMinQuantity(int minQuantity) {
        assert minQuantity >= 0 : "Minimum quantity threshold cannot be negative";
        this.minQuantity = minQuantity;
    }

    public int getMinQuantity() {
        return minQuantity;
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
     * Returns the module codes associated with this equipment.
     *
     * @return List of module codes.
     */
    public ArrayList<String> getModuleCodes() {
        return moduleCodes;
    }

    /**
     * Sets the module codes associated with this equipment.
     *
     * @param moduleCodes List of module codes.
     */
    public void setModuleCodes(ArrayList<String> moduleCodes) {
        this.moduleCodes = moduleCodes != null ? moduleCodes : new ArrayList<>();
    }

    /**
     * Adds a module code to this equipment (case-insensitive, no duplicates).
     *
     * @param moduleCode The module code to add.
     */
    public void addModuleCode(String moduleCode) {
        if (moduleCode == null || moduleCode.trim().isEmpty()) {
            return;
        }
        String upperCode = moduleCode.toUpperCase().trim();
        if (!moduleCodes.contains(upperCode)) {
            moduleCodes.add(upperCode);
        }
    }

    /**
     * Returns a human-readable representation of the equipment.
     *
     * @return Formatted equipment information.
     */
    @Override
    public String toString() {
        String result = name + " | Total: " + quantity + " | Available: " + available +
                " | loaned: " + loaned + " | Min: " + minQuantity;

        if (purchaseSem != null) {
            result += " | Purchase: " + purchaseSem + " | Lifespan: " + lifespanYears + " years";
        }

        if (moduleCodes != null && !moduleCodes.isEmpty()) {
            result += " | Modules: " + moduleCodes;
        }

        return result;
    }

    /**
     * Returns a string representation of the equipment formatted for file storage.
     *
     * @return Equipment data formatted for saving to file.
     */
    public String toFileString() {
        String purchaseSemStr = (purchaseSem != null) ? purchaseSem.toString() : "";
        String lifespanStr = (purchaseSem != null) ? String.valueOf(lifespanYears) : "";
        String modulesStr = (moduleCodes != null && !moduleCodes.isEmpty())
                ? String.join(",", moduleCodes)
                : "";

        return name + " | " + quantity + " | " + available + " | " + loaned
                + " | " + minQuantity + " | " + purchaseSemStr + " | " + lifespanStr + " | " + modulesStr;
    }
}
