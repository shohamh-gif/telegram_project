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

    public static Icon user(Color color, int size) {
        return new SimpleIcon(size, g2 -> {
            g2.setColor(color);
            int headSize = (int)(size * 0.4);
            g2.fillOval((size - headSize) / 2, (int)(size * 0.1), headSize, headSize);
            g2.fillArc((int)(size * 0.15), (int)(size * 0.55), (int)(size * 0.7), (int)(size * 0.8), 0, 180);
        });
    }

    public static Icon plus(Color color, int size) {
        return new SimpleIcon(size, g2 -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(Math.max(2f, size * 0.12f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int m = (int)(size * 0.2);
            int c = size / 2;
            g2.drawLine(c, m, c, size - m);
            g2.drawLine(m, c, size - m, c);
        });
    }

    public static Icon play(Color color, int size) {
        return new SimpleIcon(size, g2 -> {
            g2.setColor(color);
            int[] x = {(int)(size * 0.3), (int)(size * 0.3), (int)(size * 0.8)};
            int[] y = {(int)(size * 0.2), (int)(size * 0.8), (int)(size * 0.5)};
            g2.fillPolygon(x, y, 3);
        });
    }

    public static Icon chart(Color color, int size) {
        return new SimpleIcon(size, g2 -> {
            g2.setColor(color);
            int w = (int)(size * 0.18);
            int gap = (int)(size * 0.12);
            int x1 = (int)(size * 0.15);
            g2.fillRect(x1, (int)(size * 0.4), w, (int)(size * 0.5));
            g2.fillRect(x1 + w + gap, (int)(size * 0.2), w, (int)(size * 0.7));
            g2.fillRect(x1 + 2 * (w + gap), (int)(size * 0.5), w, (int)(size * 0.4));
        });
    }
}