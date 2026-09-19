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
        dialog.setBackground(new Color(0, 0, 0, 0)); // חלון שקוף מאחורי הפינות המעוגלות

        int width = 400;

        JPanel content = new JPanel(new BorderLayout(0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 22, 22));
                g2.setColor(new Color(230, 227, 228));
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 22, 22));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(22, 24, 18, 24));

        // --- רצועת עליון צבעונית + תגית עגולה עם סמל ---
        JPanel topRow = new JPanel(new BorderLayout(14, 0));
        topRow.setOpaque(false);
        topRow.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JLabel badge = new JLabel(String.valueOf(SwingConstants.CENTER)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(accent);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setPreferredSize(new Dimension(40, 40));
        badge.setIcon(badgeIcon);
        badge.setHorizontalAlignment(SwingConstants.CENTER);
        badge.setVerticalAlignment(SwingConstants.CENTER);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font(DashboardFrame.FONT_NAME, Font.BOLD, 18));
        titleLabel.setForeground(DashboardFrame.DARK_TEXT);

        topRow.add(badge, BorderLayout.EAST);
        topRow.add(titleLabel, BorderLayout.CENTER);

        JLabel messageLabel = new JLabel("<html><div style='text-align:right; width:300px;'>" + message + "</div></html>");
        messageLabel.setFont(new Font(DashboardFrame.FONT_NAME, Font.PLAIN, 14));
        messageLabel.setForeground(DashboardFrame.DARK_TEXT);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(14, 0, 18, 0));
        messageLabel.setHorizontalAlignment(SwingConstants.RIGHT);

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
        content.add(messageLabel, BorderLayout.CENTER);
        content.add(buttonRow, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.pack();
        dialog.setSize(width, Math.max(180, dialog.getHeight()));
        dialog.setShape(new RoundRectangle2D.Double(0, 0, dialog.getWidth(), dialog.getHeight(), 22, 22));
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
    }
}