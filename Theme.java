package org.example;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public final class Theme {

    private Theme() {}

    // Цвета
    public static final Color BACKGROUND   = new Color(0xF5F7FA);
    public static final Color SURFACE      = Color.WHITE;
    public static final Color SIDEBAR      = new Color(0x1E293B);
    public static final Color SIDEBAR_TEXT = new Color(0xCBD5E1);
    public static final Color BORDER       = new Color(0xE2E8F0);
    public static final Color TEXT         = new Color(0x0F172A);
    public static final Color MUTED        = new Color(0x64748B);
    public static final Color PRIMARY      = new Color(0x2563EB);
    public static final Color SUCCESS      = new Color(0x16A34A);
    public static final Color WARNING      = new Color(0xD97706);
    public static final Color DANGER       = new Color(0xDC2626);

    private static final Font BASE = UIManager.getFont("Label.font") != null
            ? UIManager.getFont("Label.font") : new Font(Font.SANS_SERIF, Font.PLAIN, 13);

    public static Font font(int style, float size) {
        return BASE.deriveFont(style, size);
    }

    public static final int GAP = 12;

    public static Border padding(int all) {
        return BorderFactory.createEmptyBorder(all, all, all, all);
    }

    public static Border card() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                padding(14));
    }

    public static JButton button(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(font(Font.BOLD, 13f));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setMargin(new Insets(8, 16, 8, 16));
        button.putClientProperty("JButton.buttonType", "roundRect"); // FlatLaf
        return button;
    }
}
