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
 *@author     David E. Jones (jonesde@ofbiz.org) 
 *@version    $Rev$
 *@since      2.1
-->
  <p class="head1">${uiLabelMap.PartyChangePassword}</p>

    &nbsp;<a href="<@ofbizUrl>authview/${donePage}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonGoBack}]</a>
    &nbsp;<a href="javascript:document.changepasswordform.submit()" class="buttontext">[${uiLabelMap.CommonSave}]</a>

  <form method="post" action="<@ofbizUrl>updatePassword/${donePage}</@ofbizUrl>" name="changepasswordform">
  <table width="90%" border="0" cellpadding="2" cellspacing="0" class="tabletext">
    <tr>
      <td width="26%" align="right"><div class="tabletext">${uiLabelMap.PartyOldPassword}</div></td>
      <td width="74%">
        <input type="password" class='inputBox' name="currentPassword" size="20" maxlength="20"/>
      *</td>
    </tr>
    <tr>
      <td width="26%" align="right"><div class="tabletext">${uiLabelMap.PartyNewPassword}</div></td>
      <td width="74%">
        <input type="password" class='inputBox' name="newPassword" size="20" maxlength="20"/>
      *</td>
    </tr>
    <tr>
      <td width="26%" align="right"><div class="tabletext">${uiLabelMap.PartyNewPasswordVerify}</div></td>
      <td width="74%">
        <input type="password" class='inputBox' name="newPasswordVerify" size="20" maxlength="20"/>
      *</td>
    </tr>
    <tr>
      <td width="26%" align="right"><div class="tabletext">${uiLabelMap.PartyPasswordHint}</div></td>
      <td width="74%">
        <input type="text" class='inputBox' size="40" maxlength="100" name="passwordHint" value="${userLoginData.passwordHint?if_exists}"/>
      </td>
    </tr>
  </table>
  </form>
<div class="tabletext">${uiLabelMap.CommonFieldsMarkedAreRequired}</div>

    &nbsp;<a href="<@ofbizUrl>authview/${donePage}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonGoBack}]</a>
    &nbsp;<a href="javascript:document.changepasswordform.submit()" class="buttontext">[${uiLabelMap.CommonSave}]</a>
