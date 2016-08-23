package org.exmaralda.partitureditor.svgPanel;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

class SimpleNamespaceContext implements NamespaceContext {
	
  private Map<String, String> urisByPrefix = new HashMap<String, String>();

  private Map<String, Set> prefixesByURI = new HashMap<String, Set>();

  public SimpleNamespaceContext() {
    addNamespace("ns", "http://www.w3.org/2000/svg");
  }

  public synchronized void addNamespace(String prefix, String namespaceURI) {
    urisByPrefix.put(prefix, namespaceURI);
    if (prefixesByURI.containsKey(namespaceURI)) {
      (prefixesByURI.get(namespaceURI)).add(prefix);
    } else {
      Set<String> set = new HashSet<String>();
      set.add(prefix);
      prefixesByURI.put(namespaceURI, set);
    }
  }

  public String getNamespaceURI(String prefix) {
    if (prefix == null)
      throw new IllegalArgumentException("prefix cannot be null");
    if (urisByPrefix.containsKey(prefix))
      return (String) urisByPrefix.get(prefix);
    else
      return XMLConstants.NULL_NS_URI;
  }

  public String getPrefix(String namespaceURI) {
    return (String) getPrefixes(namespaceURI).next();
  }

  public Iterator getPrefixes(String namespaceURI) {
    if (namespaceURI == null)
      throw new IllegalArgumentException("namespaceURI cannot be null");
    if (prefixesByURI.containsKey(namespaceURI)) {
      return ((Set) prefixesByURI.get(namespaceURI)).iterator();
    } else {
      return Collections.EMPTY_SET.iterator();
    }
  }

}
