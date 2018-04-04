package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.util.Date;

/**
 * Created by Ido on 20/03/2018.
 */
public class SeasonTable extends JTable {

    private static final long serialVersionUID = 1L;

    private static final Object[] SEASON_INFO_COLUMNS = {"Title", "IMDB-Rating", "Last-Seen",
            "Fully Watched"};

    public SeasonTable(Object[][] data) {
        super(data, SEASON_INFO_COLUMNS);
        columnModel.setColumnSelectionAllowed(false);
        setDragEnabled(false);
        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < SEASON_INFO_COLUMNS.length-1; i++){  // leave checkbox as is
            columnModel.getColumn(i).setCellRenderer(centerRender);
        }
    }

    /*@Override
    public Class getColumnClass(int column) {
    return getValueAt(0, column).getClass();
    }*/
    @Override
    public Class getColumnClass(int column) {
        switch (column){
            case 2:
                return Date.class;
            case 3:
                return Boolean.class;
            default:
                return String.class;
        }
    }

    /**
     * Returns true if the cell at <code>row</code> and <code>column</code>
     * is editable.  Otherwise, invoking <code>setValueAt</code> on the cell
     * will have no effect.
     * <p/>
     * <b>Note</b>: The column is specified in the table view's display
     * order, and not in the <code>TableModel</code>'s column
     * order.  This is an important distinction because as the
     * user rearranges the columns in the table,
     * the column at a given index in the view will change.
     * Meanwhile the user's actions never affect the model's
     * column ordering.
     *
     * @param row    the row whose value is to be queried
     * @param column the column whose value is to be queried
     * @return true if the cell is editable
     * @see #setValueAt
     */
    @Override
    public boolean isCellEditable(int row, int column) {
        return column == SEASON_INFO_COLUMNS.length-1; // set Watched as the only editable column
    }
}
