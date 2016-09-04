/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.partitureditor.svgPanel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
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
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.batik.bridge.UpdateManager;
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
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGLocatable;

/**
 *
 * @author Julia Damerow
 */
public class DisplaySVGPanel extends javax.swing.JPanel implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2113076818698940197L;
	
	protected final String HIGHLIGHT = "stroke:red;fill:red;fill-opacity:0.25;stroke-width:0.15%";
	protected final int rightPanelWidth = 270;
	protected final int topPanelWidth = 80;
	
	// The frame (for getting size info)
	protected JFrame frame;

	// The "Load" button, which displays up a file chooser upon clicking.
	protected JButton button = new JButton("Load...");

	// The status label that shows what's happening
	protected JLabel label = new JLabel();

	// The SVG canvas.
	protected JSVGCanvas svgCanvas = new JSVGCanvas();
	protected SVGDocument svgDoc;

	private JTextField textField;

	private JTextField enterXpointerText;

	private SVGElement highlighted;
	
	// url of currently selected file
	private URL selectedFileUrl;

	public DisplaySVGPanel(JFrame frame) {
		this.frame = frame;
		XPointerObservable._instance.addObserver(this);
		initComponents();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	private void initComponents() {
		this.setLayout(new BorderLayout());
		
		final JPanel panel = new JPanel(new BorderLayout());
		this.add(panel);

		JSVGScrollPane scroller = new JSVGScrollPane(svgCanvas);

		// top bar
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		p.add(button);
		p.add(label);
		
		JButton zoomIn = new JButton("Zoom In");
		p.add(zoomIn);
		zoomIn.addActionListener(svgCanvas.getActionMap().get(JSVGCanvas.ZOOM_IN_ACTION));
		
		JButton zoomOut = new JButton("Zoom Out");
		p.add(zoomOut);
		zoomOut.addActionListener(svgCanvas.getActionMap().get(JSVGCanvas.ZOOM_OUT_ACTION));

		// add components for showing selected xpointer
		JPanel rightPanel = new JPanel();
		rightPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		JLabel label1 = new JLabel("XPointer for selected Element:",
				JLabel.LEFT);
		label1.setAlignmentX(Component.LEFT_ALIGNMENT);
		rightPanel.add(label1);

		textField = new JTextField();
		textField.setEditable(false);
		textField.setMaximumSize(new Dimension(400, 40));
		textField.setAlignmentX(Component.LEFT_ALIGNMENT);
		rightPanel.add(textField);

		panel.add(BorderLayout.PAGE_START, p);
		panel.add(BorderLayout.CENTER, scroller);
		panel.add(BorderLayout.LINE_END, rightPanel);
		
		// add components for entering xpointer
		JLabel labelEnterXPointer = new JLabel(
				"Enter an XPointer to highlight:", JLabel.LEFT);
		labelEnterXPointer.setAlignmentX(Component.LEFT_ALIGNMENT);
		rightPanel.add(labelEnterXPointer);

		enterXpointerText = new JTextField();
		enterXpointerText.setMaximumSize(new Dimension(400, 40));
		enterXpointerText.setAlignmentX(Component.LEFT_ALIGNMENT);
		rightPanel.add(enterXpointerText);

		JButton hightlightButton = new JButton();
		hightlightButton.setText("Highlight Component");
		hightlightButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String xpointer = enterXpointerText.getText();
				if (xpointer != null && !xpointer.trim().isEmpty()) {
					XPath xpath = XPathFactory.newInstance().newXPath();
					try {
						// get object identified by xpointer
						final Object obj = xpath.evaluate(xpointer, svgDoc.getRootElement(),
								XPathConstants.NODE);
						if (obj != null && obj instanceof SVGElement) {
							UpdateManager um = svgCanvas.getUpdateManager();
							um.getUpdateRunnableQueue().invokeLater(new Runnable() {

								@Override
								public void run() {
									removeHighlight();
									highlightElement((SVGElement) obj);
								}
							});
						}
					} catch (XPathExpressionException e1) {
						// TODO this should be handled appropriately
						e1.printStackTrace();
					}
				}
			}
		});
		rightPanel.add(hightlightButton);

		JButton removeHighlightBtn = new JButton("Remove Highlighting");
		removeHighlightBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				UpdateManager um = svgCanvas.getUpdateManager();
				um.getUpdateRunnableQueue().invokeLater(new Runnable() {

					@Override
					public void run() {
						removeHighlight();
					}
				});
			}
		});
		rightPanel.add(removeHighlightBtn);

		// Set the button action.
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JFileChooser fc = new JFileChooser(".");
				int choice = fc.showOpenDialog(panel);
				if (choice == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fc.getSelectedFile();
					try {
						setSelectedFileUrl(selectedFile.toURI().toURL());
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
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
				for (int i = 0; i < pathNodes.getLength(); i++) {

					Node node = pathNodes.item(i);
					org.w3c.dom.events.EventTarget t = (EventTarget) node;

					if ((t instanceof SVGLocatable)) {
						pathElements.add((SVGLocatable) t);
					}

					t.addEventListener(SVGConstants.SVG_EVENT_CLICK,
							new MouseListener(svgCanvas),
							false);
				}

				svgDoc.getRootElement().setAttributeNS(null,
						SVGConstants.SVG_WIDTH_ATTRIBUTE,
						frame.getWidth() - rightPanelWidth + "");
				svgDoc.getRootElement().setAttributeNS(null,
						SVGConstants.SVG_HEIGHT_ATTRIBUTE,
						frame.getHeight() - topPanelWidth + "");

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
				
				// make SVG fit panel width
				AffineTransform at = new AffineTransform();
				int panelHeight = panel.getHeight() - topPanelWidth;
				int panelWidth = panel.getWidth() - rightPanelWidth;

				GraphicsNode gvtRoot = svgCanvas.getCanvasGraphicsNode();

				Rectangle2D rect = gvtRoot.getSensitiveBounds();

				double ratio = panelWidth / rect.getWidth() > panelHeight
						/ rect.getHeight() ? panelWidth / rect.getWidth()
						: panelHeight / rect.getHeight();
				at.scale(ratio, ratio);
				svgCanvas.setRenderingTransform(at, true);
				svgCanvas.repaint();
			}
		});

	}

	private void highlightElement(SVGElement newHighlight) {
		// remove highlight css but keep any pre-existing styles
		String newElementStyleValue = newHighlight.getAttribute("style");
		if (newElementStyleValue == null) {
			newElementStyleValue = "";
		}
		newElementStyleValue = newElementStyleValue + HIGHLIGHT;
		newHighlight.setAttribute("style", newElementStyleValue);
		svgCanvas.repaint();

		highlighted = newHighlight;
	}

	private void removeHighlight() {
		if (highlighted != null) {
			String styleValue = highlighted.getAttribute("style");
			if (styleValue == null) {
				styleValue = "";
			}
			styleValue = styleValue.replace(HIGHLIGHT, "");
			highlighted.setAttribute("style", styleValue);
		}
	}
	
	/**
	 * Method to get currently loaded file.
	 * @return
	 */
	public URL getSelectedFileUrl() {
		return selectedFileUrl;
	}

	/**
	 * Method to set and load an SVG file. This method makes sure SVG document
	 * is set to dynamic and then loads a file.
	 * 
	 * @param selectedFileUrl URL to SVG file
	 */
	public void setSelectedFileUrl(URL selectedFileUrl) {
		this.selectedFileUrl = selectedFileUrl;
		svgCanvas.setDocumentState(JSVGCanvas.ALWAYS_DYNAMIC);
		svgCanvas.setURI(selectedFileUrl.toString());
	}


	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof XPointerObservable) {
			String xpointer = arg.toString();
			textField.setText(xpointer);
		}
	}
}
