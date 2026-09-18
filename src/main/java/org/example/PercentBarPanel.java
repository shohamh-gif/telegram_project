package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class PercentBarPanel extends JPanel {
    private final double percentage;
    private final String text;
    private final Color fillColor;
    private final Color trackColor;
    private final String fontName;

    public PercentBarPanel(double percentage, String text, Color fillColor, Color trackColor, String fontName) {
        this.percentage = percentage;
        this.text = text;
        this.fillColor = fillColor;
        this.trackColor = trackColor;
        this.fontName = fontName;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int arc = h;

        g2.setColor(this.trackColor);
        g2.fill(new RoundRectangle2D.Double(0, 0, w, h, arc, arc));

        int fillWidth = (int) Math.round(w * Math.min(100, Math.max(0, this.percentage)) / 100.0);
        if (fillWidth > 0) {
            g2.setColor(this.fillColor);
            int drawWidth = Math.max(fillWidth, h); // כדי שהפינות המעוגלות ייראו תקין גם באחוז נמוך
            drawWidth = Math.min(drawWidth, w);
            g2.fill(new RoundRectangle2D.Double(0, 0, drawWidth, h, arc, arc));
        }

        g2.setFont(new Font(this.fontName, Font.BOLD, 13));
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(this.text);
        int textX = (w - textWidth) / 2;
        int textY = (h + fm.getAscent() - fm.getDescent()) / 2;

        boolean textOverFill = (w / 2) < fillWidth;
        g2.setColor(textOverFill ? Color.WHITE : new Color(90, 85, 88));
        g2.drawString(this.text, textX, textY);

        g2.dispose();
    }
}