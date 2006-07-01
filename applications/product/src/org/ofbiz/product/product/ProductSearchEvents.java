/*
 * $Id: ProductSearchEvents.java 5462 2005-08-05 18:35:48Z jonesde $
 *
 *  Copyright (c) 2001-2005 The Open For Business Project - www.ofbiz.org
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
 */
package org.ofbiz.product.product;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.webapp.stats.VisitHandler;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.product.product.ProductSearch.ProductSearchContext;
import org.ofbiz.product.product.ProductSearch.ResultSortOrder;

/**
 * Product Search Related Events
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Rev$
 * @since      3.0
 */
public class ProductSearchEvents {

    public static final String module = ProductSearchEvents.class.getName();
    public static final String resource = "ProductUiLabels";

    /** Removes the results of a search from the specified category
     *@param request The HTTPRequest object for the current request
     *@param response The HTTPResponse object for the current request
     *@return String specifying the exit status of this event
     */
    public static String searchRemoveFromCategory(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        String productCategoryId = request.getParameter("SE_SEARCH_CATEGORY_ID");
        String errMsg=null;

        EntityListIterator eli = getProductSearchResults(request);
        if (eli == null) {
            errMsg = UtilProperties.getMessage(resource,"productsearchevents.no_results_found_probably_error_constraints", UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        try {
            boolean beganTransaction = TransactionUtil.begin();
            try {
                int numRemoved = 0;
                GenericValue searchResultView = null;
                while ((searchResultView = (GenericValue) eli.next()) != null) {
                    String productId = searchResultView.getString("productId");
                    numRemoved += delegator.removeByAnd("ProductCategoryMember", UtilMisc.toMap("productCategoryId", productCategoryId, "productId", productId )) ;
                }
                eli.close();
                TransactionUtil.commit(beganTransaction);
                Map messageMap = UtilMisc.toMap("numRemoved", Integer.toString(numRemoved));
                errMsg = UtilProperties.getMessage(resource,"productsearchevents.removed_x_items", messageMap, UtilHttp.getLocale(request));
                request.setAttribute("_EVENT_MESSAGE_", errMsg);
            } catch (GenericEntityException e) {
                Map messageMap = UtilMisc.toMap("errSearchResult", e.toString());
                errMsg = UtilProperties.getMessage(resource,"productsearchevents.error_getting_search_results", messageMap, UtilHttp.getLocale(request));
                Debug.logError(e, errMsg, module);
                request.setAttribute("_ERROR_MESSAGE_", errMsg);
                TransactionUtil.rollback(beganTransaction, errMsg, e);
                return "error";
            }
        } catch (GenericTransactionException e) {
            Map messageMap = UtilMisc.toMap("errSearchResult", e.toString());
            errMsg = UtilProperties.getMessage(resource,"productsearchevents.error_getting_search_results", messageMap, UtilHttp.getLocale(request));
            Debug.logError(e, errMsg, module);
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }
        return "success";
    }

   /** Sets the thru date of the results of a search to the specified date for the specified catogory
    *@param request The HTTPRequest object for the current request
    *@param response The HTTPResponse object for the current request
    *@return String specifying the exit status of this event
    */
   public static String searchExpireFromCategory(HttpServletRequest request, HttpServletResponse response) {
       GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
       String productCategoryId = request.getParameter("SE_SEARCH_CATEGORY_ID");
       String thruDateStr = request.getParameter("thruDate");
       String errMsg=null;

       Timestamp thruDate;
       try {
           thruDate = Timestamp.valueOf(thruDateStr);
       } catch (RuntimeException e) {
           Map messageMap = UtilMisc.toMap("errDateFormat", e.toString());
           errMsg = UtilProperties.getMessage(resource,"productsearchevents.thruDate_not_formatted_properly", messageMap, UtilHttp.getLocale(request));
           Debug.logError(e, errMsg, module);
           request.setAttribute("_ERROR_MESSAGE_", errMsg);
           return "error";
       }

       EntityListIterator eli = getProductSearchResults(request);
       if (eli == null) {
           errMsg = UtilProperties.getMessage(resource,"productsearchevents.no_results_found_probably_error_constraints", UtilHttp.getLocale(request));
           request.setAttribute("_ERROR_MESSAGE_", errMsg);
           return "error";
       }

       try {
           boolean beganTransaction = TransactionUtil.begin();
           try {

               GenericValue searchResultView = null;
               int numExpired=0;
               while ((searchResultView = (GenericValue) eli.next()) != null) {
                   String productId = searchResultView.getString("productId");
                   //get all tuples that match product and category
                   List pcmList = delegator.findByAnd("ProductCategoryMember", UtilMisc.toMap("productCategoryId", productCategoryId, "productId", productId ));

                   //set those thrudate to that specificed maybe remove then add new one
                   Iterator pcmListIter=pcmList.iterator();
                   while (pcmListIter.hasNext()) {
                       GenericValue pcm = (GenericValue) pcmListIter.next();
                       if (pcm.get("thruDate") == null) {
                           pcm.set("thruDate", thruDate);
                           pcm.store();
                           numExpired++;
                       }
                   }
               }
               Map messageMap = UtilMisc.toMap("numExpired", Integer.toString(numExpired));
               errMsg = UtilProperties.getMessage(resource,"productsearchevents.expired_x_items", messageMap, UtilHttp.getLocale(request));
               request.setAttribute("_EVENT_MESSAGE_", errMsg);
               eli.close();
               TransactionUtil.commit(beganTransaction);
           } catch (GenericEntityException e) {
               Map messageMap = UtilMisc.toMap("errSearchResult", e.toString());
               errMsg = UtilProperties.getMessage(resource,"productsearchevents.error_getting_search_results", messageMap, UtilHttp.getLocale(request));
               Debug.logError(e, errMsg, module);
               request.setAttribute("_ERROR_MESSAGE_", errMsg);
               TransactionUtil.rollback(beganTransaction, errMsg, e);
               return "error";
           }
       } catch (GenericTransactionException e) {
           Map messageMap = UtilMisc.toMap("errSearchResult", e.toString());
           errMsg = UtilProperties.getMessage(resource,"productsearchevents.error_getting_search_results", messageMap, UtilHttp.getLocale(request));
           Debug.logError(e, errMsg, module);
           request.setAttribute("_ERROR_MESSAGE_", errMsg);
           return "error";
       }

       return "success";
   }

   /**  Adds the results of a search to the specified catogory
    *@param request The HTTPRequest object for the current request
    *@param response The HTTPResponse object for the current request
    *@return String specifying the exit status of this event
    */
   public static String searchAddToCategory(HttpServletRequest request, HttpServletResponse response) {
       GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
       String productCategoryId = request.getParameter("SE_SEARCH_CATEGORY_ID");
       String fromDateStr = request.getParameter("fromDate");
       Timestamp fromDate = null;
       String errMsg = null;

       try {
           fromDate = Timestamp.valueOf(fromDateStr);
        } catch (RuntimeException e) {
           Map messageMap = UtilMisc.toMap("errDateFormat", e.toString());
           errMsg = UtilProperties.getMessage(resource,"productsearchevents.fromDate_not_formatted_properly", messageMap, UtilHttp.getLocale(request));
           request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

       EntityListIterator eli = getProductSearchResults(request);
       if (eli == null) {
           errMsg = UtilProperties.getMessage(resource,"productsearchevents.no_results_found_probably_error_constraints", UtilHttp.getLocale(request));
           request.setAttribute("_ERROR_MESSAGE_", errMsg);
           return "error";
       }

       try {
           boolean beganTransaction = TransactionUtil.begin();
           try {

               GenericValue searchResultView = null;
               int numAdded = 0;
               while ((searchResultView = (GenericValue) eli.next()) != null) {
                   String productId = searchResultView.getString("productId");

                   GenericValue pcm=delegator.makeValue("ProductCategoryMember", null);
                   pcm.set("productCategoryId", productCategoryId);
                   pcm.set("productId", productId);
                   pcm.set("fromDate", fromDate);
                   pcm.create();

                   numAdded++;
               }
               Map messageMap = UtilMisc.toMap("numAdded", Integer.toString(numAdded));
               errMsg = UtilProperties.getMessage(resource,"productsearchevents.added_x_product_category_members", messageMap, UtilHttp.getLocale(request));
               request.setAttribute("_EVENT_MESSAGE_", errMsg);
               eli.close();
               TransactionUtil.commit(beganTransaction);
           } catch (GenericEntityException e) {
               Map messageMap = UtilMisc.toMap("errSearchResult", e.toString());
               errMsg = UtilProperties.getMessage(resource,"productsearchevents.error_getting_search_results", messageMap, UtilHttp.getLocale(request));
               Debug.logError(e, errMsg, module);
               request.setAttribute("_ERROR_MESSAGE_", errMsg);
               TransactionUtil.rollback(beganTransaction, errMsg, e);
               return "error";
           }
       } catch (GenericTransactionException e) {
           Map messageMap = UtilMisc.toMap("errSearchResult", e.toString());
           errMsg = UtilProperties.getMessage(resource,"productsearchevents.error_getting_search_results", messageMap, UtilHttp.getLocale(request));
           Debug.logError(e, errMsg, module);
           request.setAttribute("_ERROR_MESSAGE_", errMsg);
           return "error";
       }

       return "success";
   }

    /** Adds a feature to search results
     *@param request The HTTPRequest object for the current request
     *@param response The HTTPResponse object for the current request
     *@return String specifying the exit status of this event
     */
    public static String searchAddFeature(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        Locale locale = UtilHttp.getLocale(request);

        String productFeatureId = request.getParameter("productFeatureId");
        String fromDateStr = request.getParameter("fromDate");
        String thruDateStr = request.getParameter("thruDate");
        String amountStr = request.getParameter("amount");
        String sequenceNumStr = request.getParameter("sequenceNum");
        String productFeatureApplTypeId = request.getParameter("productFeatureApplTypeId");

        Timestamp thruDate = null;
        Timestamp fromDate = null;
        Double amount = null;
        Long sequenceNum = null;

        try {
            if (UtilValidate.isNotEmpty(fromDateStr)) {
                fromDate = Timestamp.valueOf(fromDateStr);
            }
            if (UtilValidate.isNotEmpty(thruDateStr)) {
                thruDate = Timestamp.valueOf(thruDateStr);
            }
            if (UtilValidate.isNotEmpty(amountStr)) {
                amount = Double.valueOf(amountStr);
            }
            if (UtilValidate.isNotEmpty(sequenceNumStr)) {
                sequenceNum= Long.valueOf(sequenceNumStr);
            }
        } catch (RuntimeException e) {
            String errorMsg = UtilProperties.getMessage(resource, "productSearchEvents.error_casting_types", locale) + " : " + e.toString();
            request.setAttribute("_ERROR_MESSAGE_", errorMsg);
            Debug.logError(e, errorMsg, module);
            return "error";
        }

        EntityListIterator eli = getProductSearchResults(request);
        if (eli == null) {
            String errMsg = UtilProperties.getMessage(resource,"productsearchevents.no_results_found_probably_error_constraints", UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        try {
            boolean beganTransaction = TransactionUtil.begin();
            try {
                GenericValue searchResultView = null;
                int numAdded = 0;
                while ((searchResultView = (GenericValue) eli.next()) != null) {
                    String productId = searchResultView.getString("productId");
                    GenericValue pfa=delegator.makeValue("ProductFeatureAppl", null);
                    pfa.set("productId", productId);
                    pfa.set("productFeatureId", productFeatureId);
                    pfa.set("fromDate", fromDate);
                    pfa.set("thruDate", thruDate);
                    pfa.set("productFeatureApplTypeId", productFeatureApplTypeId);
                    pfa.set("amount", amount);
                    pfa.set("sequenceNum", sequenceNum);
                    pfa.create();
                    numAdded++;
                }
                Map messageMap = UtilMisc.toMap("numAdded", new Integer(numAdded), "productFeatureId", productFeatureId);
                String eventMsg = UtilProperties.getMessage(resource, "productSearchEvents.added_param_features", messageMap, locale) + ".";
                request.setAttribute("_EVENT_MESSAGE_", eventMsg);
                eli.close();
                TransactionUtil.commit(beganTransaction);
            } catch (GenericEntityException e) {
                String errorMsg = UtilProperties.getMessage(resource, "productSearchEvents.error_getting_results", locale) + " : " + e.toString();
                request.setAttribute("_ERROR_MESSAGE_", errorMsg);
                Debug.logError(e, errorMsg, module);
                TransactionUtil.rollback(beganTransaction, errorMsg, e);
                return "error";
            }
        } catch (GenericTransactionException e) {
            String errorMsg = UtilProperties.getMessage(resource, "productSearchEvents.error_getting_results", locale) + " : " + e.toString();
            request.setAttribute("_ERROR_MESSAGE_", errorMsg);               
            Debug.logError(e, errorMsg, module);
            return "error";
        }

        return "success";
    }

    /** Removes a feature from search results
     *@param request The HTTPRequest object for the current request
     *@param response The HTTPResponse object for the current request
     *@return String specifying the exit status of this event
     */
    public static String searchRemoveFeature(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        Locale locale = UtilHttp.getLocale(request);

        String productFeatureId = request.getParameter("productFeatureId");

        EntityListIterator eli = getProductSearchResults(request);
        if (eli == null) {
            String errMsg = UtilProperties.getMessage(resource,"productsearchevents.no_results_found_probably_error_constraints", UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        try {
            boolean beganTransaction = TransactionUtil.begin();
            try {
                GenericValue searchResultView = null;
                int numRemoved = 0;
                while ((searchResultView = (GenericValue) eli.next()) != null) {
                    String productId = searchResultView.getString("productId");
                    numRemoved += delegator.removeByAnd("ProductFeatureAppl", UtilMisc.toMap("productId", productId, "productFeatureId", productFeatureId));
                }
                Map messageMap = UtilMisc.toMap("numRemoved", new Integer(numRemoved), "productFeatureId", productFeatureId);
                String eventMsg = UtilProperties.getMessage(resource, "productSearchEvents.removed_param_features", messageMap, locale) + ".";
                request.setAttribute("_EVENT_MESSAGE_", eventMsg);
                eli.close();
                TransactionUtil.commit(beganTransaction);
            } catch (GenericEntityException e) {
                String errorMsg = UtilProperties.getMessage(resource, "productSearchEvents.error_getting_results", locale) + " : " + e.toString();
                request.setAttribute("_ERROR_MESSAGE_", errorMsg);
                Debug.logError(e, errorMsg, module);
                TransactionUtil.rollback(beganTransaction, errorMsg, e);
                return "error";
            }
        } catch (GenericTransactionException e) {
            String errorMsg = UtilProperties.getMessage(resource, "productSearchEvents.error_getting_results", locale) + " : " + e.toString();
            request.setAttribute("_ERROR_MESSAGE_", errorMsg);               
            Debug.logError(e, errorMsg, module);
            return "error";
        }

        return "success";
    }

    public static EntityListIterator getProductSearchResults(HttpServletRequest request) {
        HttpSession session = request.getSession();
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        String visitId = VisitHandler.getVisitId(session);

        List productSearchConstraintList = ProductSearchSession.ProductSearchOptions.getConstraintList(session);
        // if no constraints, don't do a search...
        if (productSearchConstraintList != null && productSearchConstraintList.size() > 0) {
            ResultSortOrder resultSortOrder = ProductSearchSession.ProductSearchOptions.getResultSortOrder(session);
            ProductSearchSession.checkSaveSearchOptionsHistory(session);
            ProductSearchContext productSearchContext = new ProductSearchContext(delegator, visitId);

            productSearchContext.addProductSearchConstraints(productSearchConstraintList);
            productSearchContext.setResultSortOrder(resultSortOrder);

            return productSearchContext.doQuery(delegator);
        } else {
            return null;
        }
    }
}
