package org.example;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class CircleBadge extends JLabel {
    private final Color circleColor;

    public CircleBadge(Icon icon, Color circleColor) {
        super(icon, SwingConstants.CENTER);
        this.circleColor = circleColor;
        setVerticalAlignment(SwingConstants.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(this.circleColor);
        g2.fillOval(0, 0, getWidth(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }
}
