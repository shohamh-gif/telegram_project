package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class StyledDialogs {

    public static void showSuccess(Component parent, String message) {
        show(parent, "הצלחה", message, DashboardFrame.PROGRESS_DONE_COLOR, VectorIcons.check(Color.WHITE, 18));
    }

    public static void showWarning(Component parent, String message) {
        Color accent = DashboardFrame.PROGRESS_ACTIVE_COLOR;
        show(parent, "שימו לב", message, accent, VectorIcons.exclamation(Color.WHITE, 18));
    }

    public static void showError(Component parent, String message) {
        show(parent, "שגיאה", message, new Color(180, 92, 70), VectorIcons.cross(Color.WHITE, 18));
    }

    private static void show(Component parent, String title, String message, Color accent, Icon badgeIcon) {
        Window owner = SwingUtilities.getWindowAncestor(parent);
        JDialog dialog = new JDialog(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));

        int width = 440;

        JPanel content = new RoundedPanel(22, Color.WHITE, new Color(230, 227, 228));
        content.setLayout(new BorderLayout(0, 0));
        content.setBorder(BorderFactory.createEmptyBorder(22, 24, 18, 24));

        JPanel topRow = new JPanel(new BorderLayout(14, 0));
        topRow.setOpaque(false);

        CircleBadge badge = new CircleBadge(badgeIcon, accent);
        badge.setPreferredSize(new Dimension(40, 40));

        JLabel titleLabel = new JLabel(title, SwingConstants.RIGHT);
        titleLabel.setFont(new Font(DashboardFrame.FONT_NAME, Font.BOLD, 18));
        titleLabel.setForeground(DashboardFrame.DARK_TEXT);

        topRow.add(titleLabel, BorderLayout.CENTER);
        topRow.add(badge, BorderLayout.EAST);

        int contentTextWidth = 280;
        JTextArea messageArea = new JTextArea(message);
        messageArea.setEditable(false);
        messageArea.setFocusable(false);
        messageArea.setOpaque(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setFont(new Font(DashboardFrame.FONT_NAME, Font.PLAIN, 14));
        messageArea.setForeground(DashboardFrame.DARK_TEXT);
        messageArea.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        messageArea.setSize(contentTextWidth, Short.MAX_VALUE);
        int wrappedTextHeight = messageArea.getPreferredSize().height;

        messageArea.setBorder(BorderFactory.createEmptyBorder(14, 0, 18, 54));
        messageArea.setPreferredSize(new Dimension(contentTextWidth + 54, wrappedTextHeight + 14 + 18));

        JButton okBtn = new JButton("אישור");
        okBtn.setFont(new Font(DashboardFrame.FONT_NAME, Font.BOLD, 14));
        okBtn.setBackground(accent);
        okBtn.setForeground(Color.WHITE);
        okBtn.setFocusPainted(false);
        okBtn.setBorder(BorderFactory.createEmptyBorder(9, 26, 9, 26));
        okBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        okBtn.addActionListener(e -> dialog.dispose());

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonRow.setOpaque(false);
        buttonRow.add(okBtn);

        content.add(topRow, BorderLayout.NORTH);
        content.add(messageArea, BorderLayout.CENTER);
        content.add(buttonRow, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.pack();
        dialog.setSize(width, Math.max(180, dialog.getHeight()));
        dialog.setShape(new RoundRectangle2D.Double(0, 0, dialog.getWidth(), dialog.getHeight(), 22, 22));
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
    }
}