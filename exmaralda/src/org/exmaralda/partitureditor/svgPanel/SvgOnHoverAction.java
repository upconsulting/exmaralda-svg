package org.exmaralda.partitureditor.svgPanel;

import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.gvt.InteractorAdapter;
import org.w3c.dom.svg.SVGDocument;

public class SvgOnHoverAction extends InteractorAdapter {
	
	private SVGDocument svgDoc;
	private JSVGCanvas canvas;
	
	public SvgOnHoverAction(SVGDocument svgDoc, JSVGCanvas canvas) {
		this.svgDoc = svgDoc;
		this.canvas = canvas;
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		System.out.println("mouse clicked x" + arg0.getXOnScreen());
		System.out.println("mouse clicked " + arg0.getX());
		Component found = canvas.findComponentAt(arg0.getX(), arg0.getY());
		
	}

	@Override
	public boolean startInteraction(InputEvent arg0) {
		if (arg0.getID() == MouseEvent.MOUSE_CLICKED) {
			return true;
		}
		Component clickedComp = arg0.getComponent();
		System.out.println("clicked: " + arg0.paramString());
		System.out.println(clickedComp.getX());
		System.out.println(clickedComp.getY());
		Component found = canvas.findComponentAt(clickedComp.getX(), clickedComp.getY());
		System.out.println("found: " + found);
		return super.startInteraction(arg0);
	}

	

}
