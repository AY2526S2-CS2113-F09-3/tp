package seedu.equipmentmaster.ui;

import java.util.ArrayList;
import java.util.stream.IntStream;

/**
 * Represents a table of rows to be displayed in the UI.
 *
 * @author XiaoGeNekidora
 */
public class UiTable {
    private ArrayList<UiTableRow> rows;

    /**
     * Constructs a new UiTable instance with an empty list of rows.
     */
    public UiTable() {
        rows = new ArrayList<>();
    }

    /**
     * Adds a row to the table.
     * All rows must have the same number of columns as the first row added.
     *
     * @param row The UiTableRow to be added.
     * @throws IllegalArgumentException If the new row has a different number of columns than existing rows.
     */
    public void addRow(UiTableRow row) {
        if (rows.isEmpty()) {
            rows.add(row);
        } else {
            if (rows.get(0).size() != row.size()) {
                throw new IllegalArgumentException("All rows must have the same number of columns");
            }

            rows.add(row);
        }
    }

    /**
     * Returns the maximum width of the content in the specified column across all rows.
     *
     * @param columnIndex The index of the column to check.
     * @return The maximum width of the column.
     */
    public int getColumnWidth(int columnIndex) {
        int maxWidth = 0;
        for (UiTableRow row : rows) {
            if (columnIndex < row.size()) {
                int cellWidth = row.length(columnIndex);
                if (cellWidth > maxWidth) {
                    maxWidth = cellWidth;
                }
            }
        }
        return maxWidth;
    }

    /**
     * Returns the number of columns in the table.
     *
     * @return The number of columns.
     * @throws IllegalArgumentException If no rows have been added to the table.
     */
    public int getColumnCount() {
        if (rows.isEmpty()) {
            throw new IllegalArgumentException("No rows have been added");
        }
        return rows.get(0).size();
    }

    /**
     * Returns the number of rows in the table.
     *
     * @return The number of rows.
     */
    public int getRowCount() {
        return rows.size();
    }

    /**
     * Returns a string representation of the table, including row indices and formatted columns.
     *
     * @return A formatted string representing the table.
     */
    @Override
    public String toString() {

        if(rows.isEmpty()){
            return "<empty table>";
        }

        int indexLength = ("" + getRowCount()).length();

        var widths = IntStream.range(0, getColumnCount()).map(this::getColumnWidth).toArray();

        StringBuilder tableString = new StringBuilder();

        int i = 1;
        for (UiTableRow row : rows) {
            //plus 1 for the dot and 1 for the space after the dot
            String indexString = String.format("%-" + (indexLength + 2) + "s", i + ".");
            tableString.append(indexString).append(row.toString(widths)).append("\n");

            i++;
        }

        return tableString.toString();
    }
}
