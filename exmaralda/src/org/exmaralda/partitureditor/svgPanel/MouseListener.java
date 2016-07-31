package org.exmaralda.partitureditor.svgPanel;

import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.anim.dom.SVGOMGElement;
import org.apache.batik.anim.dom.SVGOMPathElement;
import org.apache.batik.anim.dom.SVGOMSVGElement;
import org.apache.batik.dom.events.DOMMouseEvent;
import org.w3c.dom.Node;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGSVGElement;

public class MouseListener implements EventListener {
	
	private SVGDocument svgDoc;
	
	public MouseListener(SVGDocument svgDoc) {
		this.svgDoc = svgDoc;
	}

	@Override
	public void handleEvent(Event evt) {
		if (evt instanceof DOMMouseEvent) {
			EventTarget target = evt.getCurrentTarget();
			System.out.println("target " + target);
			if (target instanceof SVGOMGElement) {
				String name = ((SVGOMGElement)target).getNodeName();
				System.out.println("name " + ((SVGOMGElement)target).getAttribute("id"));
			}
			if (target instanceof SVGOMSVGElement) {
				String name = ((SVGOMSVGElement)target).getNodeName();
				System.out.println("other name " + ((SVGOMSVGElement)target).getAttribute("id"));
			}
			//SVGOMPathElement
			
			if (target instanceof SVGOMElement) {
				System.out.println("xpath: " + getXPath(((SVGOMElement)target)));
			}
		}
	}
	
	public String getXPath(SVGOMElement node)
	{
	    Node parent = node.getParentNode();
	    if (parent == null || parent instanceof SVGOMDocument) {
	        return "/" + node.getLocalName();
	    }
	    return getXPath((SVGOMElement)parent) + "/" + "[@id='" + node.getAttribute("id") + "']";
	}

}
