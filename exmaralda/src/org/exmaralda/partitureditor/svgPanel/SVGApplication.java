package org.exmaralda.partitureditor.svgPanel;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public class SVGApplication {

    public static void main(String[] args) {
        // Create a new JFrame.
        JFrame f = new JFrame("Batik");
        PrototypeSVGPanel app = new PrototypeSVGPanel(f);
        
        // Add components to the frame.
        f.getContentPane().add(app);

        // Display the frame.
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        f.setSize(900, 600);
        f.setVisible(true);
    }

    
}