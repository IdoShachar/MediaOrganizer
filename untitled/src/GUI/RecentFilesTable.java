package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Ido on 31/03/2018.
 */
public class RecentFilesTable extends JTable {

    private static final long serialVersionUID = 1L;

    protected static final Object[] LAST_SEEN_INFO_COLUMNS = {"Title", "Fully Watched"};

    public RecentFilesTable(Object[][] data) {
        super(data, LAST_SEEN_INFO_COLUMNS);
        columnModel.setColumnSelectionAllowed(false);
        setDragEnabled(false);
        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(JLabel.CENTER);
        columnModel.getColumn(0).setCellRenderer(centerRender);  // leave checkbox as is
    }

    /*@Override
    public Class getColumnClass(int column) {
    return getValueAt(0, column).getClass();
    }*/
    @Override
    public Class getColumnClass(int column) {
        return (column == 0) ? String.class : Boolean.class;
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
        return false;
    }
}
