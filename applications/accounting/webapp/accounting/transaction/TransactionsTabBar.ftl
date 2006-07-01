<#--
 *  Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
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
 *
 *@author     Leon Torres (leon@opensourcestrategies.com)
 *@version    $Rev$
-->
<#if (requestAttributes.uiLabelMap)?exists><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>
<#assign unselectedClassName = "tabButton">
<#assign selectedClassMap = {page.tabButtonItem?default("void") : "tabButtonSelected"}>

<div class="tabContainer">
    <a href="<@ofbizUrl>/AuthorizeTransaction</@ofbizUrl>" class="${selectedClassMap.authorizetransactiontab?default(unselectedClassName)}">${uiLabelMap.AccountingAuthorize}</a>
    <a href="<@ofbizUrl>/CaptureTransaction</@ofbizUrl>" class="${selectedClassMap.capturetransactiontab?default(unselectedClassName)}">${uiLabelMap.AccountingCapture}</a>
    <a href="<@ofbizUrl>/FindGatewayResponses</@ofbizUrl>" class="${selectedClassMap.gatewayresponsestab?default(unselectedClassName)}">${uiLabelMap.AccountingGatewayResponses}</a>
    <a href="<@ofbizUrl>/ManualTransaction</@ofbizUrl>" class="${selectedClassMap.manualtransactiontab?default(unselectedClassName)}">${uiLabelMap.AccountingManualTransaction}</a>
</div>
