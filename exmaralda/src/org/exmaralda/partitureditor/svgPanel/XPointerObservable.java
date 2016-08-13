package org.exmaralda.partitureditor.svgPanel;

import java.util.Observable;

public class XPointerObservable extends Observable {

	public final static XPointerObservable _instance = new XPointerObservable();
	
	private XPointerObservable() {}
	
	public void setCurrentXPointer(String xpointer) {
		setChanged();
		notifyObservers(xpointer);
	}
}
