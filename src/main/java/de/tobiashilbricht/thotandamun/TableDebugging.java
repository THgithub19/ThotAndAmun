// Helper class to analyze jBookmarkTable

package de.tobiashilbricht.thotandamun;

import java.util.Arrays;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.RowSorter;
import javax.swing.table.TableModel;

public class TableDebugging {

    public static void installTableDebugging(JTable table, String tableName) {

        table.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                debugMouseEvent(table, tableName, "mousePressed", e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                debugMouseEvent(table, tableName, "mouseReleased", e);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                debugMouseEvent(table, tableName, "mouseClicked", e);
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            System.out.println();
            System.out.println("===== SELECTION EVENT: " + tableName + " =====");
            System.out.println("event firstIndex      = " + e.getFirstIndex());
            System.out.println("event lastIndex       = " + e.getLastIndex());
            System.out.println("event isAdjusting     = " + e.getValueIsAdjusting());
            dumpTableSelectionState(table, tableName, "during selection event");

            SwingUtilities.invokeLater(() -> {
                dumpTableSelectionState(table, tableName, "after selection event / invokeLater");
            });
        });
    }

    public static void installTableModelDebugging(JTable table, String tableName) {
        table.getModel().addTableModelListener(e -> {
            System.out.println();
            System.out.println("===== TABLE MODEL EVENT: " + tableName + " =====");
            System.out.println("event type       = " + e.getType());
            System.out.println("first row        = " + e.getFirstRow());
            System.out.println("last row         = " + e.getLastRow());
            System.out.println("column           = " + e.getColumn());
            System.out.println("model row count  = " + table.getModel().getRowCount());
            System.out.println("table row count  = " + table.getRowCount());

            System.out.println("--- Stacktrace ---");
            for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
                System.out.println("  at " + ste);
            }
        });
    }

    private static void debugMouseEvent(JTable table, String tableName, String phase, MouseEvent e) {

        System.out.println();
        System.out.println("===== MOUSE EVENT: " + tableName + " / " + phase + " =====");
        System.out.println("button              = " + e.getButton());
        System.out.println("left button         = " + SwingUtilities.isLeftMouseButton(e));
        System.out.println("right button        = " + SwingUtilities.isRightMouseButton(e));
        System.out.println("click count         = " + e.getClickCount());
        System.out.println("popup trigger       = " + e.isPopupTrigger());
        System.out.println("point               = " + e.getPoint());

        int viewRow = table.rowAtPoint(e.getPoint());
        int viewColumn = table.columnAtPoint(e.getPoint());

        System.out.println("rowAtPoint view     = " + viewRow);
        System.out.println("columnAtPoint view  = " + viewColumn);

        if (viewRow >= 0 && viewRow < table.getRowCount()) {
            int modelRow = table.convertRowIndexToModel(viewRow);
            System.out.println("rowAtPoint model    = " + modelRow);
        } else {
            System.out.println("rowAtPoint model    = <invalid>");
        }

        dumpTableSelectionState(table, tableName, "immediate in " + phase);

        SwingUtilities.invokeLater(() -> {
            dumpTableSelectionState(table, tableName, "after " + phase + " / invokeLater");
        });
    }

    private static void dumpTableSelectionState(JTable table, String tableName, String moment) {

        ListSelectionModel sm = table.getSelectionModel();

        System.out.println("--- " + tableName + " / " + moment + " ---");
        System.out.println("enabled             = " + table.isEnabled());
        System.out.println("focus owner         = " + table.isFocusOwner());
        System.out.println("row count           = " + table.getRowCount());
        System.out.println("column count        = " + table.getColumnCount());

        System.out.println("selected row view   = " + table.getSelectedRow());
        System.out.println("selected rows view  = " + Arrays.toString(table.getSelectedRows()));

        if (table.getSelectedRow() >= 0 && table.getSelectedRow() < table.getRowCount()) {
            System.out.println("selected row model  = " + table.convertRowIndexToModel(table.getSelectedRow()));
        } else {
            System.out.println("selected row model  = <invalid>");
        }

        System.out.println("row selection allowed    = " + table.getRowSelectionAllowed());
        System.out.println("column selection allowed = " + table.getColumnSelectionAllowed());
        System.out.println("cell selection enabled   = " + table.getCellSelectionEnabled());

        System.out.println("selection mode      = " + sm.getSelectionMode());
        System.out.println("selection empty     = " + sm.isSelectionEmpty());
        System.out.println("min selection index = " + sm.getMinSelectionIndex());
        System.out.println("max selection index = " + sm.getMaxSelectionIndex());
        System.out.println("anchor index        = " + sm.getAnchorSelectionIndex());
        System.out.println("lead index          = " + sm.getLeadSelectionIndex());
        System.out.println("value adjusting     = " + sm.getValueIsAdjusting());
    }

    public static void dumpSorterState(JTable table, String tableName) {
        System.out.println();
        System.out.println("===== SORTER STATE: " + tableName + " =====");

        RowSorter<? extends TableModel> sorter = table.getRowSorter();

        System.out.println("table model class        = " + table.getModel().getClass().getName());
        System.out.println("table model row count    = " + table.getModel().getRowCount());
        System.out.println("table getRowCount        = " + table.getRowCount());

        if (sorter == null) {
            System.out.println("row sorter               = <null>");
            return;
        }

        System.out.println("row sorter class         = " + sorter.getClass().getName());
        System.out.println("sorter model class       = " + sorter.getModel().getClass().getName());
        System.out.println("sorter model row count   = " + sorter.getModel().getRowCount());
        System.out.println("sorter view row count    = " + sorter.getViewRowCount());
        System.out.println("sorter model row count 2 = " + sorter.getModelRowCount());

        System.out.println("same model instance      = " + (sorter.getModel() == table.getModel()));
    }

}
