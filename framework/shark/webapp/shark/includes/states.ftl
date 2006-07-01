<#--
$Id: states.ftl 7426 2006-04-26 23:35:58Z jonesde $

Copyright 2004-2006 The Apache Software Foundation

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
<#assign states = Static["org.ofbiz.common.CommonWorkers"].getStateList(delegator)>
<#list states as state>
    <option value='${state.geoId}'>${state.geoName?default(state.geoId)}</option>
</#list>

