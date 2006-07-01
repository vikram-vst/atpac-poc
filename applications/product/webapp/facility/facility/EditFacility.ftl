<#--
 *  Copyright (c) 2001-2003 The Open For Business Project - www.ofbiz.org
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
 *@author     David E. Jones
 *@author     thierry.grauss@etu.univ-tours.fr (migration to uiLabelMap)
 *@version    $Rev$
 *@since      2.1
-->

<#if security.hasEntityPermission("FACILITY", "_VIEW", session)>

<div class="head1">${uiLabelMap.ProductFacility} <span class='head2'>${facility.facilityName?if_exists} [${uiLabelMap.CommonId}:${facilityId?if_exists}]</span></div>
<a href="<@ofbizUrl>EditFacility</@ofbizUrl>" class="buttontext">${uiLabelMap.ProductNewFacility}</a>
<#if facilityId?has_content>
    <a href="/workeffort/control/month?facilityId=${facilityId}&externalLoginKey=${requestAttributes.externalLoginKey?if_exists}" class="buttontext">${uiLabelMap.CommonViewCalendar}</a>
</#if>

<#if facility?exists && facilityId?has_content>
  <form action="<@ofbizUrl>UpdateFacility</@ofbizUrl>" method="post" style='margin: 0;'>
  <table border='0' cellpadding='2' cellspacing='0'>
  <input type="hidden" name="facilityId" value="${facilityId?if_exists}">
  <tr>
    <td align="right"><div class="tabletext">${uiLabelMap.ProductFacilityId}</div></td>
    <td>&nbsp;</td>
    <td>
      <b>${facilityId?if_exists}</b> (${uiLabelMap.ProductNotModificationRecrationFacility}.)
    </td>
  </tr>
<#else>
  <form action="<@ofbizUrl>CreateFacility</@ofbizUrl>" method="post" style='margin: 0;'>
  <table border='0' cellpadding='2' cellspacing='0'>
  <#if facilityId?exists>
    <h3>${uiLabelMap.ProductCouldNotFindFacilityWithId} "${facilityId?if_exists}".</h3>
  </#if>
</#if>
  <tr>
    <td width="26%" align="right"><div class="tabletext">${uiLabelMap.ProductFacilityTypeId}</div></td>
    <td>&nbsp;</td>
    <td width="74%">
      <select name="facilityTypeId" size="1" class='selectBox'>
        <option selected value='${facilityType.facilityTypeId?if_exists}'>${facilityType.get("description",locale)?if_exists}</option>
        <option value='${facilityType.facilityTypeId?if_exists}'>----</option>
        <#list facilityTypes as nextFacilityType>
          <option value='${nextFacilityType.facilityTypeId?if_exists}'>${nextFacilityType.get("description",locale)?if_exists}</option>
        </#list>
      </select>
    </td>
  </tr>
<#if ownerParties?has_content>
  <tr>
    <td width="26%" align="right"><div class="tabletext">${uiLabelMap.ProductFacilityOwner}</div></td>
    <td>&nbsp;</td>
    <td width="74%">
      <select name="ownerPartyId" size="1" class='selectBox'>
        <option selected value='${facility.ownerPartyId?if_exists}'>${facility.ownerPartyId?if_exists}</option>
        <option value='${facility.ownerPartyId?if_exists}'>----</option>
        <#list ownerParties as party>
          <option value='${party.partyId?if_exists}'>${party.partyId?if_exists}</option>
        </#list>
      </select>
    </td>
  </tr>
</#if>
  <tr>
    <td width="26%" align="right"><div class="tabletext">${uiLabelMap.ProductFacilityDefaultWeightUnit}</div></td>
    <td>&nbsp;</td>
    <td width="74%">
      <select name="defaultWeightUomId" size="1" class='selectBox'>
          <option value=''>${uiLabelMap.CommonNone}</option>
          <#list weightUomList as uom>                      
            <option value='${uom.uomId}'
               <#if (facility.defaultWeightUomId?has_content) && (uom.uomId == facility.defaultWeightUomId)>
               SELECTED
               </#if>
             >${uom.get("description",locale)?default(uom.uomId)}</option>
          </#list>
      </select>
    </td>
  </tr>
  <tr>
    <td width="26%" align="right"><div class="tabletext">${uiLabelMap.ProductFacilityDefaultInventoryItemType}</div></td>
    <td>&nbsp;</td>
    <td width="74%">
      <select name="defaultInventoryItemTypeId" size="1" class='selectBox'>
          <#list inventoryItemTypes as nextInventoryItemType>                      
            <option value='${nextInventoryItemType.inventoryItemTypeId}'
               <#if (facility.defaultInventoryItemTypeId?has_content) && (nextInventoryItemType.inventoryItemTypeId == facility.defaultInventoryItemTypeId)>
               SELECTED
               </#if>
             >${nextInventoryItemType.get("description",locale)?default(nextInventoryItemType.inventoryItemTypeId)}</option>
          </#list>
      </select>
    </td>
  </tr>

  <tr>
    <td width="26%" align="right"><div class="tabletext">${uiLabelMap.ProductName}</div></td>
    <td>&nbsp;</td>
    <td width="74%"><input type="text" class="inputBox" name="facilityName" value="${facility.facilityName?if_exists}" size="30" maxlength="60"></td>
  </tr>
  <tr>
    <td width="26%" align="right"><div class="tabletext">${uiLabelMap.ProductSquareFootage}</div></td>
    <td>&nbsp;</td>
    <td width="74%"><input type="text" class="inputBox" name="squareFootage" value="${facility.squareFootage?if_exists}" size="10" maxlength="20"></td>
  </tr>
  <tr>
    <td width="26%" align="right"><div class="tabletext">${uiLabelMap.ProductDescription}</div></td>
    <td>&nbsp;</td>
    <td width="74%"><input type="text" class="inputBox" name="description" value="${facility.description?if_exists}" size="60" maxlength="250"></td>
  </tr>

  <tr>
    <td colspan='2'>&nbsp;</td>
    <td colspan='1' align="left"><input type="submit" name="Update" value="${uiLabelMap.CommonUpdate}"></td>
  </tr>
</table>
</form>

<#else>
  <h3>${uiLabelMap.ProductFacilityViewPermissionError}</h3>
</#if>

