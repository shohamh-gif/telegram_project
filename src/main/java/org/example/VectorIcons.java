package org.example;

import javax.swing.*;
import java.awt.*;

public class VectorIcons {

    public static Icon dot(Color color, int size) {
        return new SimpleIcon(size, g2 -> {
            g2.setColor(color);
            g2.fillOval(0, 0, size, size);
        });
    }

    public static Icon check(Color color, int size) {
        return new SimpleIcon(size, g2 -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(Math.max(2f, size * 0.14f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine((int) (size * 0.18), (int) (size * 0.55), (int) (size * 0.42), (int) (size * 0.78));
            g2.drawLine((int) (size * 0.42), (int) (size * 0.78), (int) (size * 0.84), (int) (size * 0.24));
        });
    }

    public static Icon exclamation(Color color, int size) {
        return new SimpleIcon(size, g2 -> {
            g2.setColor(color);
            int barW = Math.max(2, (int) (size * 0.14));
            int x = (size - barW) / 2;
            g2.fillRoundRect(x, (int) (size * 0.15), barW, (int) (size * 0.48), barW, barW);
            g2.fillOval(x, (int) (size * 0.74), barW, barW);
        });
    }

    public static Icon cross(Color color, int size) {
        return new SimpleIcon(size, g2 -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(Math.max(2f, size * 0.14f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int m = (int) (size * 0.22);
            g2.drawLine(m, m, size - m, size - m);
            g2.drawLine(size - m, m, m, size - m);
        });
    }

    @FunctionalInterface
    private interface Painter {
        void paint(Graphics2D g2);
    }

    private static class SimpleIcon implements Icon {
        private final int size;
        private final Painter painter;

        SimpleIcon(int size, Painter painter) {
            this.size = size;
            this.painter = painter;
        }

        @Override
        public int getIconWidth() {
            return this.size;
        }

        @Override
        public int getIconHeight() {
            return this.size;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.translate(x, y);
            this.painter.paint(g2);
            g2.dispose();
        }
    }
}