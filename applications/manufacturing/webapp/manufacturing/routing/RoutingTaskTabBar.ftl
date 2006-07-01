<#--

Copyright 2001-2006 The Apache Software Foundation

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations
under the License.
-->

<#assign unselectedClassName = "tabButton">
<#assign selectedClassMap = {page.tabButtonItem?default("void") : "tabButtonSelected"}>

<#if routingTask?exists>
<div class='tabContainer'>
    <a href="<@ofbizUrl>EditRoutingTask?workEffortId=${routingTask.workEffortId}</@ofbizUrl>" class="${selectedClassMap.editRoutingTask?default(unselectedClassName)}">${uiLabelMap.ManufacturingEditRoutingTask}</a>
    <a href="<@ofbizUrl>EditRoutingTaskCosts?workEffortId=${routingTask.workEffortId}</@ofbizUrl>" class="${selectedClassMap.editRoutingTaskCosts?default(unselectedClassName)}">${uiLabelMap.ManufacturingRoutingTaskCosts}</a>
    <a href="<@ofbizUrl>ListRoutingTaskRoutings?workEffortId=${routingTask.workEffortId}</@ofbizUrl>" class="${selectedClassMap.listRoutingTaskRoutings?default(unselectedClassName)}">${uiLabelMap.ManufacturingListRoutings}</a>
    <a href="<@ofbizUrl>ListRoutingTaskProducts?workEffortId=${routingTask.workEffortId}</@ofbizUrl>" class="${selectedClassMap.listRoutingTaskProducts?default(unselectedClassName)}">${uiLabelMap.ManufacturingListProducts}</a>
</div>
</#if>