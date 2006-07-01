/*
 * $Id: ResourceBundleMapWrapper.java 5720 2005-09-13 03:10:59Z jonesde $
 *
 *  Copyright (c) 2001-2005 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.ofbiz.base.util.collections;

import java.io.Serializable;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import org.ofbiz.base.util.UtilProperties;


/**
 * Generic ResourceBundle Map Wrapper, given ResourceBundle allows it to be used as a Map
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Rev$
 * @since      3.1
 */
public class ResourceBundleMapWrapper implements Map, Serializable {
    
    protected MapStack rbmwStack;
    protected ResourceBundle initialResourceBundle;

    protected ResourceBundleMapWrapper() {
        rbmwStack = MapStack.create();
    }

    /**
     * When creating new from a InternalRbmWrapper the one passed to the constructor should be the most specific or local InternalRbmWrapper, with more common ones pushed onto the stack progressively.
     */
    public ResourceBundleMapWrapper(InternalRbmWrapper initialInternalRbmWrapper) {
        this.initialResourceBundle = initialInternalRbmWrapper.getResourceBundle();
        this.rbmwStack = MapStack.create(initialInternalRbmWrapper);
    }
    
    /**
     * When creating new from a ResourceBundle the one passed to the constructor should be the most specific or local ResourceBundle, with more common ones pushed onto the stack progressively.
     */
    public ResourceBundleMapWrapper(ResourceBundle initialResourceBundle) {
        if (initialResourceBundle == null) {
            throw new IllegalArgumentException("Cannot create ResourceBundleMapWrapper with a null initial ResourceBundle.");
        }
        this.initialResourceBundle = initialResourceBundle;
        this.rbmwStack = MapStack.create(new InternalRbmWrapper(initialResourceBundle));
    }
    
    /** Puts ResourceBundle on the BOTTOM of the stack (bottom meaning will be overriden by higher layers on the stack, ie everything else already there) */
    public void addBottomResourceBundle(ResourceBundle topResourceBundle) {
        this.rbmwStack.addToBottom(new InternalRbmWrapper(topResourceBundle));
    }

    /** Puts InternalRbmWrapper on the BOTTOM of the stack (bottom meaning will be overriden by higher layers on the stack, ie everything else already there) */
    public void addBottomResourceBundle(InternalRbmWrapper topInternalRbmWrapper) {
        this.rbmwStack.addToBottom(topInternalRbmWrapper);
    }

    /** Don't pass the locale to make sure it has the same locale as the base */
    public void addBottomResourceBundle(String resource) {
        if (this.initialResourceBundle == null) {
            throw new IllegalArgumentException("Cannot add bottom resource bundle, this wrapper was not properly initialized (there is no base/initial ResourceBundle).");
        }
        this.addBottomResourceBundle(UtilProperties.getInternalRbmWrapper(resource, this.initialResourceBundle.getLocale()));
    }

    /** In general we don't want to use this, better to start with the more specific ResourceBundle and add layers of common ones...
     * Puts ResourceBundle on the top of the stack (top meaning will override lower layers on the stack) 
     */
    public void pushResourceBundle(ResourceBundle topResourceBundle) {
        this.rbmwStack.push(new InternalRbmWrapper(topResourceBundle));
    }

    public ResourceBundle getInitialResourceBundle() {
        return this.initialResourceBundle;
    }

    public void clear() {
        this.rbmwStack.clear();
    }
    public boolean containsKey(Object arg0) {
        return this.rbmwStack.containsKey(arg0);
    }
    public boolean containsValue(Object arg0) {
        return this.rbmwStack.containsValue(arg0);
    }
    public Set entrySet() {
        return this.rbmwStack.entrySet();
    }
    public Object get(Object arg0) {
        Object value = this.rbmwStack.get(arg0);
        if (value == null) {
            value = arg0;
        }
        return value;
    }
    public boolean isEmpty() {
        return this.rbmwStack.isEmpty();
    }
    public Set keySet() {
        return this.keySet();
    }
    public Object put(Object key, Object value) {
        return this.rbmwStack.put(key, value);
    }
    public void putAll(Map arg0) {
        this.rbmwStack.putAll(arg0);
    }
    public Object remove(Object arg0) {
        return this.rbmwStack.remove(arg0);
    }
    public int size() {
        return this.rbmwStack.size();
    }
    public Collection values() {
        return this.rbmwStack.values();
    }
    
    public static class InternalRbmWrapper implements Map, Serializable {
        protected ResourceBundle resourceBundle;
        protected Map topLevelMap;
        
        public InternalRbmWrapper(ResourceBundle resourceBundle) {
            if (resourceBundle == null) {
                throw new IllegalArgumentException("Cannot create InternalRbmWrapper with a null ResourceBundle.");
            }
            this.resourceBundle = resourceBundle;
            topLevelMap = new HashMap();
            // NOTE: this does NOT return all keys, ie keys from parent ResourceBundles, so we keep the resourceBundle object to look at when the main Map doesn't have a certain value 
            if (resourceBundle != null) {
                Enumeration keyNum = resourceBundle.getKeys();
                while (keyNum.hasMoreElements()) {
                    String key = (String) keyNum.nextElement();
                    //resourceBundleMap.put(key, bundle.getObject(key));
                    Object value = resourceBundle.getObject(key);
                    topLevelMap.put(key, value);
                }
            }
            topLevelMap.put("_RESOURCE_BUNDLE_", resourceBundle);
        }
        
        /* (non-Javadoc)
         * @see java.util.Map#size()
         */
        public int size() {
            // this is an approximate size, won't include elements from parent bundles
            return topLevelMap.size() - 1;
        }
    
        /* (non-Javadoc)
         * @see java.util.Map#isEmpty()
         */
        public boolean isEmpty() {
            return topLevelMap.isEmpty();
        }
    
        /* (non-Javadoc)
         * @see java.util.Map#containsKey(java.lang.Object)
         */
        public boolean containsKey(Object arg0) {
            if (topLevelMap.containsKey(arg0)) {
                return true;
            } else {
                try {
                    if (this.resourceBundle.getObject((String) arg0) != null) {
                        return true;
                    }
                } catch (MissingResourceException e) {
                    // nope, not found... nothing, will automatically return false below
                }
            }
            return false;
        }
    
        /* (non-Javadoc)
         * @see java.util.Map#containsValue(java.lang.Object)
         */
        public boolean containsValue(Object arg0) {
            throw new RuntimeException("Not implemented for ResourceBundleMapWrapper");
        }
    
        /* (non-Javadoc)
         * @see java.util.Map#get(java.lang.Object)
         */
        public Object get(Object arg0) {
            Object value = this.topLevelMap.get(arg0);
            if (resourceBundle != null) {
                if (value == null) {
                    try {
                        value = this.resourceBundle.getObject((String) arg0);
                    } catch(MissingResourceException mre) {
                        // do nothing, this will be handled by recognition that the value is still null
                    }
                }
                if (value == null) {
                    try {
                        value = this.resourceBundle.getString((String) arg0);
                    } catch(MissingResourceException mre) {
                        // do nothing, this will be handled by recognition that the value is still null
                    }
                }
            }
            /* we used to do this here, but now we'll do it in the top-level class since doing it here would prevent searching down the stack
            if (value == null) {
                value = arg0;
            }
            */
            return value;
        }
    
        /* (non-Javadoc)
         * @see java.util.Map#put(java.lang.Object, java.lang.Object)
         */
        public Object put(Object arg0, Object arg1) {
            throw new RuntimeException("Not implemented/allowed for ResourceBundleMapWrapper");
        }
    
        /* (non-Javadoc)
         * @see java.util.Map#remove(java.lang.Object)
         */
        public Object remove(Object arg0) {
            throw new RuntimeException("Not implemented for ResourceBundleMapWrapper");
        }
    
        /* (non-Javadoc)
         * @see java.util.Map#putAll(java.util.Map)
         */
        public void putAll(Map arg0) {
            throw new RuntimeException("Not implemented for ResourceBundleMapWrapper");
        }
    
        /* (non-Javadoc)
         * @see java.util.Map#clear()
         */
        public void clear() {
            throw new RuntimeException("Not implemented for ResourceBundleMapWrapper");
        }
    
        /* (non-Javadoc)
         * @see java.util.Map#keySet()
         */
        public Set keySet() {
            return this.topLevelMap.keySet();
        }
    
        /* (non-Javadoc)
         * @see java.util.Map#values()
         */
        public Collection values() {
            return this.topLevelMap.values();
        }
    
        /* (non-Javadoc)
         * @see java.util.Map#entrySet()
         */
        public Set entrySet() {
            return this.topLevelMap.entrySet();
        }
        
        public ResourceBundle getResourceBundle() {
            return this.resourceBundle;
        }
        
        /*public String toString() {
            return this.topLevelMap.toString();
        }*/
    }
}
