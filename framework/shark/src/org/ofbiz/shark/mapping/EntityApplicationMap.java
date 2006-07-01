/*
 * $Id: EntityApplicationMap.java 7426 2006-04-26 23:35:58Z jonesde $
 *
 * Copyright 2004-2006 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.ofbiz.shark.mapping;

import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.shark.container.SharkContainer;

import org.enhydra.shark.api.internal.appmappersistence.ApplicationMap;
import org.enhydra.shark.api.RootException;

/**
 * Shark Application Map Implementation
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @since      3.1
 */
public class EntityApplicationMap implements ApplicationMap {

    protected GenericDelegator delegator = null;
    protected GenericValue application = null;
    protected boolean isNew = false;
    
    protected EntityApplicationMap(GenericDelegator delegator, String packageId, String processDefId, String applicationDefId) throws RootException {
        this.delegator = delegator;
        try {
            this.application = delegator.findByPrimaryKey("WfApplicationMap", UtilMisc.toMap("packageId", packageId, "processDefId", processDefId, "applicationDefId", applicationDefId));
        } catch (GenericEntityException e) {
            throw new RootException(e);
        }
    }

    protected EntityApplicationMap(GenericValue application) {
        this.application = application;
        this.delegator = application.getDelegator();
    }

    public EntityApplicationMap(GenericDelegator delegator) {
        this.isNew = true;
        this.delegator = delegator;
        this.application = delegator.makeValue("SharkApplicationMap", null);
    }

    public static EntityApplicationMap getInstance(GenericValue application) {
        EntityApplicationMap app = new EntityApplicationMap(application);
        if (app.isLoaded()) {
            return app;
        }
        return null;
    }

    public static EntityApplicationMap getInstance(String packageId, String processDefId, String applicationDefId) throws RootException {
        EntityApplicationMap act = new EntityApplicationMap(SharkContainer.getDelegator(), packageId, processDefId, applicationDefId);
        if (act.isLoaded()) {
            return act;
        }
        return null;
    }

    public boolean isLoaded() {
        if (application == null) {
            return false;
        }
        return true;
    }

    public void setApplicationDefinitionId(String applicationDefId) {
        application.set("applicationDefId", applicationDefId);
    }

    public String getApplicationDefinitionId() {
        return application.getString("applicationDefId");
    }

    public void setPackageId(String packageId) {
        application.set("packageId", packageId);
    }

    public String getPackageId() {
        return application.getString("applicationName");
    }

    public void setProcessDefinitionId(String processDefId) {
        application.set("processDefId", processDefId);
    }

    public String getProcessDefinitionId() {
        return application.getString("processDefId");
    }

    public void setToolAgentClassName(String toolAgentName) {
        application.set("toolAgentName", toolAgentName);
    }

    public String getToolAgentClassName() {
        return application.getString("toolAgentName");
    }

    public void setUsername(String userName) {
        application.set("userName", userName);
    }

    public String getUsername() {
        return application.getString("userName");
    }

    public void setPassword(String password) {
        application.set("password", password);
    }

    public String getPassword() {
        return application.getString("password");
    }

    public void setApplicationName(String name) {
        application.set("applicationName", name);
    }

    public String getApplicationName() {
        return application.getString("applicationName");
    }

    public void setApplicationMode(Integer mode) {
        application.set("applicationMode", mode);
    }

    public Integer getApplicationMode() {
        return application.getInteger("applicationMode");
    }

    public boolean equalsByKeys(ApplicationMap applicationMap) {
        if (applicationMap == null ) return false;

        if ((applicationMap.getPackageId() != null && this.getPackageId() != null))
            if (!(applicationMap.getPackageId().equals(this.getPackageId())))
                return false;

        if ((applicationMap.getProcessDefinitionId() != null && this.getProcessDefinitionId() != null))
            if( !(applicationMap.getProcessDefinitionId().equals(this.getProcessDefinitionId())))
                return false;

        if ((applicationMap.getApplicationDefinitionId() != null && this.getApplicationDefinitionId() != null))
            if (!(applicationMap.getApplicationDefinitionId().equals(this.getApplicationDefinitionId())))
                return false;

        if ((applicationMap.getToolAgentClassName() != null && this.getToolAgentClassName() != null))
            if (!(applicationMap.getToolAgentClassName().equals(this.getToolAgentClassName())))
                return false;

        return true;        
    }

    public void store() throws RootException {
        if (isNew) {
            try {
                delegator.create(application);
            } catch (GenericEntityException e) {
                throw new RootException(e);
            }
        } else {
            try {
                delegator.store(application);
            } catch (GenericEntityException e) {
                throw new RootException(e);
            }
        }
    }

    public void reload() throws RootException {
        if (!isNew) {
            try {
                delegator.refresh(application);
            } catch (GenericEntityException e) {
                throw new RootException(e);
            }
        }
    }

    public void remove() throws RootException {
        if (!isNew) {
            try {
                delegator.removeValue(application);
            } catch (GenericEntityException e) {
                throw new RootException(e);
            }
        }
    }
}
