package org.example;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Component;

public class StatusRowRenderer extends DefaultTableCellRenderer {
    private final Color GREEN_ROW_BG = new Color(226, 247, 231);
    private final Color YELLOW_ROW_BG = new Color(255, 246, 212);

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        setHorizontalAlignment(JLabel.CENTER);

        if (!isSelected) {
            Object statusVal = table.getValueAt(row, 2);
            if ("השלים".equals(statusVal)) {
                c.setBackground(this.GREEN_ROW_BG);
            } else if ("בתהליך".equals(statusVal)) {
                c.setBackground(this.YELLOW_ROW_BG);
            } else {
                c.setBackground(Color.WHITE);
            }
        }
        return c;
    }
}