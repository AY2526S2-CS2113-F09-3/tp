package seedu.equipmentmaster.equipmentlist;

import java.util.ArrayList;

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
        equipments.add(newEquipment);
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
}
