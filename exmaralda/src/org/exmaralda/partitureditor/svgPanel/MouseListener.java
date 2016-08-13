package org.exmaralda.partitureditor.svgPanel;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.dom.events.DOMMouseEvent;
import org.apache.batik.dom.svg.SVGOMRect;
import org.apache.batik.gvt.CanvasGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.swing.JSVGCanvas;
import org.w3c.dom.Node;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGImageElement;
import org.w3c.dom.svg.SVGLocatable;
import org.w3c.dom.svg.SVGRect;

public class MouseListener implements EventListener {
	
	private SVGDocument svgDoc;
	private List<SVGLocatable> pathElements;
	private JSVGCanvas canvas;
	
	public MouseListener(SVGDocument svgDoc, JSVGCanvas svgCanvas, List<SVGLocatable> pathElements) {
		this.svgDoc = svgDoc;
		this.pathElements = pathElements;
		this.canvas = svgCanvas;
	}

	@Override
	public void handleEvent(Event evt) {
		if (evt instanceof DOMMouseEvent) {
			EventTarget target = evt.getCurrentTarget();
			
			if (target instanceof SVGImageElement) {
				int x = ((DOMMouseEvent) evt).getClientX();
				int y = ((DOMMouseEvent) evt).getClientY();
				
				AffineTransform at;
                at = canvas.getViewBoxTransform();
                if (at != null) {
                	try {
						at = at.createInverse();
					} catch (NoninvertibleTransformException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    Point2D p2d =
                        at.transform(new Point2D.Float(x, y),
                                     null);
                    x = Math.round(((float)p2d.getX()));
                    y = Math.round((float)p2d.getY());
                }
				
				SVGRect rect = new SVGOMRect();
				rect.setHeight(1.0f);
				rect.setWidth(1.0f);
				rect.setX(new Float(x));
				rect.setY(new Float(y));
			
				 GraphicsNode gvtRoot = canvas.getCanvasGraphicsNode();
                 List<?> it = gvtRoot.getRoot().getChildren();
                 BridgeContext ctx = canvas.getUpdateManager().getBridgeContext();
                 
                 List<GraphicsNode> containingNodes = new ArrayList<>();
                 for (Object o : ((CanvasGraphicsNode)it.get(0)).getChildren()) {
                	 
                	 Rectangle2D rect2 = ((GraphicsNode)o).getSensitiveBounds();
                	 
                	 AffineTransform inAt = ((GraphicsNode)o).getInverseTransform();
                	 Point2D p2d =
                             inAt.transform(new Point2D.Float(x, y),
                                          null);
                     x = Math.round(((float)p2d.getX()));
                     y = Math.round((float)p2d.getY());
                	     
                	 if (rect2.getX() <= x && (rect2.getX() + rect2.getWidth() >= x))  {
                		 if (y >= rect2.getY() && y <= rect2.getY() + rect2.getHeight()) {
                			 containingNodes.add((GraphicsNode)o);
                		 }
                	 }
                 }
                 
                 GraphicsNode smallestNode = null;
                 if (!containingNodes.isEmpty()) {
                	 smallestNode = containingNodes.get(0);
	                 for (GraphicsNode node : containingNodes.subList(1, containingNodes.size())) {
	                	 if (smallestNode.getSensitiveBounds().contains(node.getSensitiveBounds())) {
	                		 smallestNode = node;
	                	 }
	                 }
                 }
                 
                 String xpointer = getXPath((SVGOMElement)ctx.getElement(smallestNode));
                 XPointerObservable._instance.setCurrentXPointer(xpointer);
			}
			
			if (target instanceof SVGOMElement && !(target instanceof SVGImageElement)) {
				String xpointer = getXPath(((SVGOMElement)target));
                XPointerObservable._instance.setCurrentXPointer(xpointer);
			}
		}
	}
	
	public String getXPath(SVGOMElement node)
	{
		if (node.hasAttribute("id")) {
			return "//*[@id='" + node.getAttribute("id") + "']";
		}
	    Node parent = node.getParentNode();
	    return getXPath((SVGOMElement)parent) + "/" + node.getLocalName();
	}

}
