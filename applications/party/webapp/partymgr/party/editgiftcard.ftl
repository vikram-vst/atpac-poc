<#--
 *  Copyright (c) 2003-2005 The Open For Business Project - www.ofbiz.org
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
 *@author     Andy Zeneski (jaz@ofbiz.org)
 *@author     Catherine.Heintz@nereide.biz (migration to UiLabel)
 *@version    $Rev$
 *@since      3.0
-->

    <#if !giftCard?exists>
      <p class="head1">${uiLabelMap.AccountingCreateNewGiftCard}</p>
      <form method="post" action="<@ofbizUrl>createGiftCard?DONE_PAGE=${donePage}</@ofbizUrl>" name="editgiftcardform" style="margin: 0;">
    <#else>
      <p class="head1">${uiLabelMap.AccountingEditGiftCard}</p>
      <form method="post" action="<@ofbizUrl>updateGiftCard?DONE_PAGE=${donePage}</@ofbizUrl>" name="editgiftcardform" style="margin: 0;">
        <input type="hidden" name="paymentMethodId" value="${paymentMethodId}">
    </#if>

    <input type="hidden" name="partyId" value="${partyId}"/>
    &nbsp;<a href="<@ofbizUrl>${donePage}?partyId=${partyId}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonCancelDone}]</a>
    &nbsp;<a href="javascript:document.editgiftcardform.submit()" class="buttontext">[${uiLabelMap.CommonSave}]</a>

    <table width="90%" border="0" cellpadding="2" cellspacing="0">
    <tr>
      <td width="26%" align="right" valign="top"><div class="tabletext">${uiLabelMap.AccountingCardNumber}</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <input type="text" class="inputBox" size="20" maxlength="60" name="cardNumber" value="${giftCardData.cardNumber?if_exists}">
      </td>
    </tr>
    <tr>
      <td width="26%" align="right" valign="top"><div class="tabletext">${uiLabelMap.AccountingPinNumber}</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <input type="text" class="inputBox" size="10" maxlength="60" name="pinNumber" value="${giftCardData.pinNumber?if_exists}">
      </td>
    </tr>
    <tr>
      <td width="26%" align="right" valign="top"><div class="tabletext">${uiLabelMap.CommonExpireDate}</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <#assign expMonth = "">
        <#assign expYear = "">
        <#if giftCardData?exists && giftCardData.expireDate?exists>
          <#assign expDate = giftCard.expireDate>
          <#if (expDate?exists && expDate.indexOf("/") > 0)>
            <#assign expMonth = expDate.substring(0,expDate.indexOf("/"))>
            <#assign expYear = expDate.substring(expDate.indexOf("/")+1)>
          </#if>
        </#if>
        <select name="expMonth" class="selectBox" onchange="javascript:makeExpDate();">
          <#if giftCardData?has_content && expMonth?has_content>
            <#assign ccExprMonth = expMonth>
          <#else>
            <#assign ccExprMonth = requestParameters.expMonth?if_exists>
          </#if>
          <#if ccExprMonth?has_content>
            <option value="${ccExprMonth?if_exists}">${ccExprMonth?if_exists}</option>
          </#if>
          ${screens.render("component://common/widget/CommonScreens.xml#ccmonths")}
        </select>
        <select name="expYear" class="selectBox" onchange="javascript:makeExpDate();">
          <#if giftCard?has_content && expYear?has_content>
            <#assign ccExprYear = expYear>
          <#else>
            <#assign ccExprYear = requestParameters.expYear?if_exists>
          </#if>
          <#if ccExprYear?has_content>
            <option value="${ccExprYear?if_exists}">${ccExprYear?if_exists}</option>
          </#if>
          ${screens.render("component://common/widget/CommonScreens.xml#ccyears")}
        </select>
      </td>
    </tr>
  </table>
  </form>

  &nbsp;<a href="<@ofbizUrl>${donePage}?partyId=${partyId}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonCancelDone}]</a>
  &nbsp;<a href="javascript:document.editgiftcardform.submit()" class="buttontext">[${uiLabelMap.CommonSave}]</a>
