/*
 * $Id: GenericDispatcher.java 7284 2006-04-12 18:39:42Z jaz $
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

import java.util.Map;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.base.util.Debug;

/**
 * Generic Services Local Dispatcher
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      2.0
 */
public class GenericDispatcher extends GenericAbstractDispatcher {

    public static final String module = GenericDispatcher.class.getName();

    public GenericDispatcher() {}

    public GenericDispatcher(String name, GenericDelegator delegator) {
        this(name, delegator, null);
    }

    public GenericDispatcher(String name, GenericDelegator delegator, ClassLoader loader) {
        if (loader == null) {
            try {
                loader = Thread.currentThread().getContextClassLoader();
            } catch (SecurityException e) {
                loader = this.getClass().getClassLoader();
            }
        }
        DispatchContext dc = new DispatchContext(name, null, loader, null);
        init(name, delegator, dc);
    }

    public GenericDispatcher(DispatchContext ctx, GenericDelegator delegator) {
        init(ctx.getName(), delegator, ctx);
    }

    public GenericDispatcher(DispatchContext ctx, ServiceDispatcher dispatcher) {
        this.dispatcher = dispatcher;
        this.ctx = ctx;
        this.name = ctx.getName();

        ctx.setDispatcher(this);
        ctx.loadReaders();
        dispatcher.register(name, ctx);
    }

    protected void init(String name, GenericDelegator delegator, DispatchContext ctx) {
        if (name == null || name.length() == 0)
            throw new IllegalArgumentException("The name of a LocalDispatcher cannot be a null or empty String");

        this.name = name;
        this.ctx = ctx;
        this.dispatcher = ServiceDispatcher.getInstance(name, ctx, delegator);

        ctx.setDispatcher(this);
        ctx.loadReaders();
        if (Debug.infoOn()) Debug.logInfo("[LocalDispatcher] : Created Dispatcher for: " + name, module);
    }

    public static LocalDispatcher getLocalDispatcher(String name, GenericDelegator delegator) throws GenericServiceException {
        ServiceDispatcher sd = ServiceDispatcher.getInstance(name, delegator);
        LocalDispatcher thisDispatcher = null;
        if (sd != null) {
            thisDispatcher = sd.getLocalDispatcher(name);
        }
        if (thisDispatcher == null) {
            thisDispatcher = new GenericDispatcher(name, delegator);
        }

        if (thisDispatcher != null) {
            return thisDispatcher;
        } else {
            throw new GenericServiceException("Unable to load dispatcher for name : " + name + " with delegator : " + delegator.getDelegatorName());
        }
    }

    // special method to obtain a new 'unique' reference
    public static LocalDispatcher newInstance(String name, GenericDelegator delegator, boolean enableJM, boolean enableJMS, boolean enableSvcs) throws GenericServiceException {
        ServiceDispatcher sd = new ServiceDispatcher(delegator, enableJM, enableJMS, enableSvcs);
        ClassLoader loader = null;
        try {
            loader = Thread.currentThread().getContextClassLoader();
        } catch (SecurityException e) {
            loader = GenericDispatcher.class.getClassLoader();
        }
        DispatchContext dc = new DispatchContext(name, null, loader, null);
        return new GenericDispatcher(dc, sd);        
    }

    /**
     * @see org.ofbiz.service.LocalDispatcher#runSync(java.lang.String, java.util.Map)
     */
    public Map runSync(String serviceName, Map context) throws ServiceValidationException, GenericServiceException {
        ModelService service = ctx.getModelService(serviceName);
        return dispatcher.runSync(this.name, service, context);
    }

    /**
     * @see org.ofbiz.service.LocalDispatcher#runSync(java.lang.String, java.util.Map, int, boolean)
     */
    public Map runSync(String serviceName, Map context, int transactionTimeout, boolean requireNewTransaction) throws ServiceAuthException, ServiceValidationException, GenericServiceException {
        ModelService service = ctx.getModelService(serviceName);
        // clone the model service for updates
        ModelService cloned = new ModelService(service);
        cloned.requireNewTransaction = requireNewTransaction;
        cloned.transactionTimeout = transactionTimeout;
        return dispatcher.runSync(this.name, cloned, context);
    }

    /**
     * @see org.ofbiz.service.LocalDispatcher#runSyncIgnore(java.lang.String, java.util.Map)
     */
    public void runSyncIgnore(String serviceName, Map context) throws GenericServiceException {
        ModelService service = ctx.getModelService(serviceName);
        dispatcher.runSyncIgnore(this.name, service, context);
    }

    /**
     * @see org.ofbiz.service.LocalDispatcher#runSyncIgnore(java.lang.String, java.util.Map)
     */
    public void runSyncIgnore(String serviceName, Map context, int transactionTimeout, boolean requireNewTransaction) throws ServiceAuthException, ServiceValidationException, GenericServiceException {
        ModelService service = ctx.getModelService(serviceName);
        // clone the model service for updates
        ModelService cloned = new ModelService(service);
        cloned.requireNewTransaction = requireNewTransaction;
        cloned.transactionTimeout = transactionTimeout;
        dispatcher.runSyncIgnore(this.name, cloned, context);
    }

    /**
     * @see org.ofbiz.service.LocalDispatcher#runAsync(java.lang.String, java.util.Map, org.ofbiz.service.GenericRequester, boolean, int, boolean)
     */
    public void runAsync(String serviceName, Map context, GenericRequester requester, boolean persist, int transactionTimeout, boolean requireNewTransaction) throws ServiceAuthException, ServiceValidationException, GenericServiceException {
        ModelService service = ctx.getModelService(serviceName);
        // clone the model service for updates
        ModelService cloned = new ModelService(service);
        cloned.requireNewTransaction = requireNewTransaction;
        cloned.transactionTimeout = transactionTimeout;
        dispatcher.runAsync(this.name, cloned, context, requester, persist);
    }

    /**
     * @see org.ofbiz.service.LocalDispatcher#runAsync(java.lang.String, java.util.Map, org.ofbiz.service.GenericRequester, boolean)
     */
    public void runAsync(String serviceName, Map context, GenericRequester requester, boolean persist) throws ServiceAuthException, ServiceValidationException, GenericServiceException {
        ModelService service = ctx.getModelService(serviceName);
        dispatcher.runAsync(this.name, service, context, requester, persist);
    }
   
    /**
     * @see org.ofbiz.service.LocalDispatcher#runAsync(java.lang.String, java.util.Map, org.ofbiz.service.GenericRequester)
     */
    public void runAsync(String serviceName, Map context, GenericRequester requester) throws ServiceAuthException, ServiceValidationException, GenericServiceException {
        runAsync(serviceName, context, requester, true);
    }
    
    /**
     * @see org.ofbiz.service.LocalDispatcher#runAsync(java.lang.String, java.util.Map, boolean)
     */
    public void runAsync(String serviceName, Map context, boolean persist) throws ServiceAuthException, ServiceValidationException, GenericServiceException {
        ModelService service = ctx.getModelService(serviceName);
        dispatcher.runAsync(this.name, service, context, persist);
    }
   
    /**
     * @see org.ofbiz.service.LocalDispatcher#runAsync(java.lang.String, java.util.Map)
     */
    public void runAsync(String serviceName, Map context) throws ServiceAuthException, ServiceValidationException, GenericServiceException {
        runAsync(serviceName, context, true);
    }
  
    /**
     * @see org.ofbiz.service.LocalDispatcher#runAsyncWait(java.lang.String, java.util.Map, boolean)
     */
    public GenericResultWaiter runAsyncWait(String serviceName, Map context, boolean persist) throws ServiceAuthException, ServiceValidationException, GenericServiceException {
        GenericResultWaiter waiter = new GenericResultWaiter();
        this.runAsync(serviceName, context, waiter, persist);
        return waiter;
    }
 
    /**
     * @see org.ofbiz.service.LocalDispatcher#runAsyncWait(java.lang.String, java.util.Map)
     */
    public GenericResultWaiter runAsyncWait(String serviceName, Map context) throws ServiceAuthException, ServiceValidationException, GenericServiceException {
        return runAsyncWait(serviceName, context, true);
    }  
}

