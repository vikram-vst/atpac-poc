/*
 * $Id: ServiceValidationException.java 5462 2005-08-05 18:35:48Z jonesde $
 *
 * Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.ofbiz.service;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * ServiceValidationException
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      2.0
 */
public class ServiceValidationException extends GenericServiceException {

    protected List messages = new ArrayList();
    protected List missingFields = new ArrayList();
    protected List extraFields = new ArrayList();
    protected String errorMode = null;
    protected ModelService service = null;
    
    public ServiceValidationException(ModelService service, List missingFields, List extraFields, String errorMode) {
        super();
        this.service = service;
        this.errorMode = errorMode;
        if (missingFields != null) {
            this.missingFields = missingFields;
        }
        if (extraFields != null) {
            this.extraFields = extraFields;
        }
    }

    public ServiceValidationException(String str, ModelService service) {
        super(str);
        this.service = service;
    }

    public ServiceValidationException(String str, ModelService service, List missingFields, List extraFields, String errorMode) {
        super(str);
        this.service = service;
        this.errorMode = errorMode;
        if (missingFields != null) {
            this.missingFields = missingFields;
        }
        if (extraFields != null) {
            this.extraFields = extraFields;
        }
    }

    public ServiceValidationException(String str, Throwable nested, ModelService service) {
        super(str, nested);
        this.service = service;
    }

    public ServiceValidationException(String str, Throwable nested, ModelService service, List missingFields, List extraFields, String errorMode) {
        super(str, nested);
        this.service = service;
        this.errorMode = errorMode;
        if (missingFields != null) {
            this.missingFields = missingFields;
        }
        if (extraFields != null) {
            this.extraFields = extraFields;
        }
    }

    public ServiceValidationException(List messages, ModelService service, List missingFields, List extraFields, String errorMode) {
        super();
        this.messages = messages;
        this.service = service;
        this.errorMode = errorMode;
        if (missingFields != null) {
            this.missingFields = missingFields;
        }
        if (extraFields != null) {
            this.extraFields = extraFields;
        }
    }

    public ServiceValidationException(List messages, ModelService service, String errorMode) {
        this(messages, service, null, null, errorMode);
    }

    public List getExtraFields() {
        return extraFields;
    }

    public List getMissingFields() {
        return missingFields;
    }

    public List getMessageList() {
        if (this.messages == null || this.messages.size() == 0) {
            return null;
        }
        return this.messages;
    }

    public ModelService getModelService() {
        return service;
    }

    public String getMode() {
        return errorMode;
    }

    public String getServiceName() {
        if (service != null) {
            return service.name;
        } else {
            return null;
        }
    }

    public String getMessage() {
        String msg = super.getMessage();
        if (this.messages != null && this.messages.size() > 0) {
            if (msg != null) {
                msg += "\n";
            } else {
                msg = "";
            }
            Iterator i = this.messages.iterator();
            while (i.hasNext()) {
                msg += i.next();
            }
        }
        return msg;
    }
}

