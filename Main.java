package org.example;

import javax.swing.*;
public class Main {

    public static void main(String[] args) {

        if (args.length > 0 && args[0].equals("--console")) {
            ConsoleApp.run();
            return;
        }

        setLookAndFeel();
        SwingUtilities.invokeLater(() -> new Dashboard().setVisible(true));
    }

    private static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("TextComponent.arc", 10);
        } catch (Exception flatLafMissing) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
        }
    }
}
