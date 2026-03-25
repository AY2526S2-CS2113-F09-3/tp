package seedu.equipmentmaster.equipmentlist;

import java.util.ArrayList;
import java.util.Objects;

import seedu.equipmentmaster.equipment.Equipment;

/**
 * Represents a list that stores all equipment objects in the system.
 */
public class EquipmentList {
    private ArrayList<Equipment> equipments;

    /**
     * Creates an EquipmentList using an existing list of equipment.
     *
     * @param equipments List of equipment to initialize the EquipmentList with.
     */
    public EquipmentList(ArrayList<Equipment> equipments) {
        this.equipments = equipments;
    }

    /**
     * Creates an empty EquipmentList.
     */
    public EquipmentList() {
        equipments = new ArrayList<>();
    }

    /**
     * Adds a new equipment item to the list.
     *
     * @param newEquipment Equipment object to be added.
     */
    public void addEquipment(Equipment newEquipment) {
        for (Equipment existingItem : equipments) {
            boolean isNameMatch = existingItem.getName().equalsIgnoreCase(newEquipment.getName());
            boolean isLifespanMatch = Objects.equals(existingItem.getLifespanYears(), newEquipment.getLifespanYears());
            boolean isPurchaseSemMatch = Objects.equals(existingItem.getPurchaseSem(), newEquipment.getPurchaseSem());
            if (isNameMatch && isLifespanMatch && isPurchaseSemMatch) {
                existingItem.setQuantity(existingItem.getQuantity() + newEquipment.getQuantity());
                existingItem.setAvailable(existingItem.getAvailable() + newEquipment.getAvailable());
                existingItem.setLoaned(existingItem.getLoaned() + newEquipment.getLoaned());

                for (String moduleCode : newEquipment.getModuleCodes()) {
                    existingItem.addModuleCode(moduleCode);
                }
                return;
            }
        }
        equipments.add(newEquipment);
    }

    /**
     * Finds an equipment item by its name (case-insensitive).
     *
     * @param name The name of the equipment to search for.
     * @return The Equipment object if found, otherwise null.
     */
    public Equipment findByName(String name) {
        for (Equipment equipment : equipments) {
            if (equipment.getName().equalsIgnoreCase(name)) {
                return equipment;
            }
        }
        return null;
    }

    /**
     * Retrieves an equipment item at the specified index.
     *
     * @param index Index of the equipment in the list.
     * @return The equipment at the specified index.
     */
    public Equipment getEquipment(int index) {
        return equipments.get(index);
    }

    /**
     * Returns the total number of equipment items in the list.
     *
     * @return Size of the equipment list.
     */
    public int getSize() {
        return equipments.size();
    }

    /**
     * Checks whether the equipment list is empty.
     *
     * @return True if the list is empty, otherwise false.
     */
    public boolean isEmpty() {
        return equipments.isEmpty();
    }

    /**
     * Returns the entire list of equipment.
     *
     * @return ArrayList containing all equipment objects.
     */
    public ArrayList<Equipment> getAllEquipments() {
        return equipments;
    }

    /**
     * Removes an equipment item at the specified index.
     *
     * @param index Index of the equipment to be removed.
     */
    public void removeEquipment(int index) {
        equipments.remove(index);
    }

    /**
     * Removes a specific equipment object from the list.
     * This is useful when the equipment's quantity drops to zero.
     *
     * @param equipment The equipment object to be removed.
     */
    public void removeEquipment(Equipment equipment) {
        equipments.remove(equipment);
    }

    /**
     * Checks if equipment exists in the list by its name.
     *
     * @param equipmentName Name of the equipment to be checked
     */
    public boolean hasEquipment(String equipmentName) {
        return findByName(equipmentName) != null;
    }
}
