package seedu.equipmentmaster.ui;

import seedu.equipmentmaster.equipment.Equipment;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Represents a row in the UiTable.
 * Each row contains a list of columns (cells).
 *
 * @author XiaoGeNekidora
 */
public class UiTableRow {
    private ArrayList<String> columns;

    /**
     * Constructs a UiTableRow from an Equipment object.
     * The row will contain the equipment's name, total quantity, available quantity, and loaned quantity.
     *
     * @param equipment The Equipment object to represent in the row.
     */
    public UiTableRow(Equipment equipment) {
        columns = new ArrayList<>();
        columns.add(equipment.getName());
        columns.add("Total: " + equipment.getQuantity());
        columns.add("Available: " + equipment.getAvailable());
        columns.add("Loaned: " + equipment.getLoaned());
        columns.add(equipment.getPurchaseSem() == null
                ? "Purchase: <N/A>"
                : "Purchase: " + equipment.getPurchaseSem());
        columns.add(equipment.getLifespanYears() <= 0.0
                ? "Life: <N/A>"
                : "Life: " + equipment.getLifespanYears()
                + (equipment.getLifespanYears() == 1.0 ? " year" : " years"));
        if (equipment.getModuleCodes() != null && !equipment.getModuleCodes().isEmpty()) {
            columns.add("Modules: " + equipment.getModuleCodes());
        }
    }

    /**
     * Constructs a UiTableRow with a given list of column values.
     *
     * @param columns The list of strings representing the cell values.
     */
    public UiTableRow(ArrayList<String> columns) {
        this.columns = columns;
    }

    /**
     * Constructs a UiTableRow with a given array of column values.
     *
     * @param columns The variable arguments of strings representing the cell values.
     */
    public UiTableRow(String... columns) {
        this.columns = new ArrayList<>();
        this.columns.addAll(Arrays.asList(columns));
    }

    /**
     * Returns a string representation of the row with default formatting.
     * Columns are separated by " | ".
     *
     * @return The string representation of the row.
     */
    @Override
    public String toString() {
        return String.join(" | ", columns);
    }

    /**
     * Returns a formatted string representation of the row based on column widths.
     * Each column is padded to match the specified width.
     *
     * @param widths An array of integers specifying the width for each column.
     * @return The formatted string representation of the row.
     */
    public String toString(int[] widths) {
        StringBuilder rowString = new StringBuilder();
        for (int i = 0; i < columns.size(); i++) {
            String cell = String.format("%-" + widths[i] + "s", columns.get(i));
            rowString.append(cell);
            if (i < columns.size() - 1) {
                rowString.append(" | ");
            }
        }
        return rowString.toString();
    }

    /**
     * Returns the length of the content in a specific column.
     *
     * @param columnIndex The index of the column.
     * @return The length of the string in the specified column.
     * @throws IndexOutOfBoundsException If the column index is out of range.
     */
    public int length(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= columns.size()) {
            throw new IndexOutOfBoundsException("Column index out of bounds");
        }

        return columns.get(columnIndex).length();
    }

    /**
     * Returns the number of columns in the row.
     *
     * @return The number of columns.
     */
    public int size() {
        return columns.size();
    }
}
