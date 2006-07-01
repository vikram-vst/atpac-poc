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
 *@author     Andy Zeneski (jaz@ofbiz.org)
 *@author     Eric.Barbier@nereide.biz (migration to uiLabelMap)
 *@version    $Rev$
 *@since      2.1
-->

<div class="head1">${uiLabelMap.AccountingBillingAccountInvoices}</div>

<br/>
<table width="100%" border="0" cellpadding="0" cellspacing="0"> 
  <tr>
    <td><div class="tableheadtext">${uiLabelMap.AccountingInvoice} #</div></td>
    <td><div class="tableheadtext">${uiLabelMap.AccountingInvoiceDate}</div></td>
    <td><div class="tableheadtext">${uiLabelMap.AccountingDueDate}</div></td>
    <td align="right"><div class="tableheadtext">${uiLabelMap.CommonTotal}</div></td>
    <td>&nbsp;</td>
  </tr> 
  <tr><td colspan="5"><hr class="sepbar"></td></tr>
  <#list invoices as invoice>
  <tr>
    <td><div class="tabletext">${invoice.invoiceId?if_exists}</div></td>
    <td><div class="tabletext">${invoice.invoiceDate?if_exists}</div></td>
    <td><div class="tabletext">${invoice.dueDate?if_exists}</div></td>
    <td align="right"><div class="tabletext">${invoice.invoiceTotal}</div></td>
    <td align="right">
      <a href="<@ofbizUrl>invoiceOverview?invoiceId=${invoice.invoiceId}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonEdit}]</a>
    </td>
  </tr>
  </#list>
</table>
