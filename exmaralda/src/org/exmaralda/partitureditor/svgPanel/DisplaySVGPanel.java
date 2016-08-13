/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.partitureditor.svgPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.JSVGScrollPane;
import org.apache.batik.swing.gvt.GVTTreeRendererAdapter;
import org.apache.batik.swing.gvt.GVTTreeRendererEvent;
import org.apache.batik.swing.svg.GVTTreeBuilderAdapter;
import org.apache.batik.swing.svg.GVTTreeBuilderEvent;
import org.apache.batik.swing.svg.SVGDocumentLoaderAdapter;
import org.apache.batik.swing.svg.SVGDocumentLoaderEvent;
import org.apache.batik.util.SVGConstants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGLocatable;


/**
 *
 * @author Julia Damerow
 */
public class DisplaySVGPanel extends javax.swing.JPanel implements Observer {
	
	// The frame.
    protected JFrame frame;

    // The "Load" button, which displays up a file chooser upon clicking.
    protected JButton button = new JButton("Load...");

    // The status label.
    protected JLabel label = new JLabel();

    // The SVG canvas.
    protected JSVGCanvas svgCanvas = new JSVGCanvas();
    protected SVGDocument svgDoc;

	private JTextField textField;

    public DisplaySVGPanel(JFrame f) {
        frame = f;
        XPointerObservable._instance.addObserver(this);
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

    	
    	final JPanel panel = new JPanel(new BorderLayout());
    	panel.setSize(frame.getSize());
    	this.add(panel);
    	
    	JSVGScrollPane scroller = new JSVGScrollPane(svgCanvas);
    	
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.add(button);
        p.add(label);
        
        // add components for showing selected xpointer
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        JLabel label1 = new JLabel("XPointer for selected Element",
                JLabel.LEFT);
        rightPanel.add(label1);
        textField = new JTextField(15);
        textField.setEditable(false);
        textField.setMaximumSize(new Dimension(400, 40));
        rightPanel.add(textField);
        
        panel.add(BorderLayout.PAGE_START, p);
        panel.add(BorderLayout.CENTER, scroller);
        panel.add(BorderLayout.LINE_END, rightPanel);
        

        // Set the button action.
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                JFileChooser fc = new JFileChooser(".");
                int choice = fc.showOpenDialog(panel);
                if (choice == JFileChooser.APPROVE_OPTION) {
                    File f = fc.getSelectedFile();
                    try {
                    	 svgCanvas.setURI(f.toURL().toString());
                    	 svgCanvas.setDocumentState(JSVGCanvas.ALWAYS_DYNAMIC);
                         
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        // Set the JSVGCanvas listeners.
        svgCanvas.addSVGDocumentLoaderListener(new SVGDocumentLoaderAdapter() {
            public void documentLoadingStarted(SVGDocumentLoaderEvent e) {
                label.setText("Document Loading...");
            }
            public void documentLoadingCompleted(SVGDocumentLoaderEvent e) {
            	svgDoc = svgCanvas.getSVGDocument();
                Element docNode = svgDoc.getDocumentElement();
                NodeList pathNodes = docNode.getElementsByTagName("*");
                 
                List<SVGLocatable> pathElements = new ArrayList<SVGLocatable>();
                for(int i = 0; i < pathNodes.getLength(); i++){
                
                	Node node = pathNodes.item(i);
                	org.w3c.dom.events.EventTarget t = (EventTarget) node;
                	
                	if ((t instanceof SVGLocatable)) {
                		pathElements.add((SVGLocatable)t);
                	}
                	
                	t.addEventListener(SVGConstants.SVG_EVENT_CLICK, new MouseListener(svgDoc, svgCanvas, pathElements), false);
                }
                
                svgDoc.getRootElement().setAttributeNS(null,
                        SVGConstants.SVG_WIDTH_ATTRIBUTE, frame.getWidth()-270  + "");
                svgDoc.getRootElement().setAttributeNS(null,
                        SVGConstants.SVG_HEIGHT_ATTRIBUTE, frame.getHeight()-80 + "");
                
            }
        });

        svgCanvas.addGVTTreeBuilderListener(new GVTTreeBuilderAdapter() {
            public void gvtBuildStarted(GVTTreeBuilderEvent e) {
                label.setText("Build Started...");
            }
            public void gvtBuildCompleted(GVTTreeBuilderEvent e) {
                label.setText("Build Done.");
                frame.repaint();
            }
        });

        svgCanvas.addGVTTreeRendererListener(new GVTTreeRendererAdapter() {
            public void gvtRenderingPrepare(GVTTreeRendererEvent e) {
                label.setText("Rendering Started...");
            }
            public void gvtRenderingCompleted(GVTTreeRendererEvent e) {
                label.setText("");
                AffineTransform at = new AffineTransform();
                int panelHeight = panel.getHeight() - 80;
	            int panelWidth = panel.getWidth() - 270;
	            
	            GraphicsNode gvtRoot = svgCanvas.getCanvasGraphicsNode();
                        
                Rectangle2D rect = gvtRoot.getSensitiveBounds();
                
	            double ratio = panelWidth/rect.getWidth() > panelHeight/rect.getHeight() ? panelWidth/rect.getWidth() : panelHeight/rect.getHeight(); 
                at.scale(ratio, ratio);
                svgCanvas.setRenderingTransform(at, true);
                svgCanvas.repaint();
            }
        });

    }

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof XPointerObservable) {
			String xpointer = arg.toString();
			textField.setText(xpointer);
		}
	}

//    private void sendXPointerTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendXPointerTextFieldActionPerformed
//        // TODO add your handling code here:
//    }//GEN-LAST:event_sendXPointerTextFieldActionPerformed


//    // Variables declaration - do not modify//GEN-BEGIN:variables
//    private javax.swing.JPanel controlsPanel;
//    private javax.swing.JComboBox fileSelectionComboBox;
//    private javax.swing.JPanel fileSelectionPanel;
//    private javax.swing.JButton getButton;
//    private javax.swing.JPanel graphicsPanel;
//    private javax.swing.JButton sendButton;
//    private javax.swing.JPanel sendPanel;
//    private javax.swing.JTextField sendXPointerTextField;
//    private javax.swing.JPanel showPanel;
//    private javax.swing.JLabel showXPointerLabel;
//    // End of variables declaration//GEN-END:variables
 
//    public void addXPointerListener(XPointerListener listener){
//        if (!xPointerListenerList.contains(listener)){
//            xPointerListenerList.add(listener);
//        }
//    }
//    
//    public void removeXPointerListener(XPointerListener listener){
//        xPointerListenerList.remove(listener);
//    }
//    
//    private void loadImage() {
//        java.net.URL imgURL = getClass().getResource("/org/exmaralda/partitureditor/svgPanel/ExampleDiagram.png");
//        ImageIcon image = new ImageIcon(imgURL);
//        ScrollablePicture scrollablePicture = new ScrollablePicture(image, 20);            
//        graphicsPanel.add(scrollablePicture);
//        java.awt.Dimension size = new java.awt.Dimension(image.getIconWidth(), image.getIconHeight());
//        System.out.println(image.getIconWidth() + " / " + image.getIconHeight());
//        graphicsPanel.setPreferredSize(size);
//        graphicsPanel.setMaximumSize(size);
//    }
//
//    @Override
//    public void processXPointer(String xPointer) {
//        showXPointerLabel.setText(xPointer);
//    }
//    
//    /* set the list of SVG files available 
//     in the fileSelectionComboBox 
//    */
//    public void setFileList(File[] files){
//        
//    }
//    
//    /* send the current XPointer to whoever 
//       is listening
//    */
//    public void sendXPointer(){
//        String xPointer = sendXPointerTextField.getText();
//        for (XPointerListener listener : xPointerListenerList){
//            listener.processXPointer(xPointer);
//        }
//    }
//    
}
