/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gestionstock;

import View.LoginFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try {
            // Set Nimbus look and feel for better appearance
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            
            // Set custom colors for Nimbus
            UIManager.put("nimbusBase", new java.awt.Color(0, 102, 204));
            UIManager.put("nimbusBlueGrey", new java.awt.Color(240, 245, 255));
            UIManager.put("control", new java.awt.Color(240, 245, 255));
            
        } catch (Exception e) {
            // If Nimbus is not available, use system look and feel
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
