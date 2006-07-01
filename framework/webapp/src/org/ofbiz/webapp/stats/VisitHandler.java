/*
 * $Id: VisitHandler.java 7678 2006-05-25 19:35:47Z jonesde $
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
package org.ofbiz.webapp.stats;

import java.net.InetAddress;
import java.sql.Timestamp;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

/**
 * Handles saving and maintaining visit information
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Rev$
 * @since      2.0
 */
public class VisitHandler {
    // Debug module name
    public static final String module = VisitHandler.class.getName();
    
    public static final String visitorCookieName = "OFBiz.Visitor";

    // this is not an event because it is required to run; as an event it could be disabled.
    public static void setInitialVisit(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();

        // init the visitor
        getVisitor(request, response);
        
        String webappName = UtilHttp.getApplicationName(request);
        StringBuffer fullRequestUrl = UtilHttp.getFullRequestUrl(request);
        String initialLocale = request.getLocale() != null ? request.getLocale().toString() : "";
        String initialRequest = fullRequestUrl.toString();
        String initialReferrer = request.getHeader("Referer") != null ? request.getHeader("Referer") : "";
        String initialUserAgent = request.getHeader("User-Agent") != null ? request.getHeader("User-Agent") : "";

        session.setAttribute("_CLIENT_LOCALE_", request.getLocale());
        session.setAttribute("_CLIENT_REQUEST_", initialRequest);
        session.setAttribute("_CLIENT_USER_AGENT_", initialUserAgent);
        session.setAttribute("_CLIENT_REFERER_", initialUserAgent);
        VisitHandler.setInitials(request, session, initialLocale, initialRequest, initialReferrer, initialUserAgent, webappName);
    }

    public static void setInitials(HttpServletRequest request, HttpSession session, String initialLocale, String initialRequest, String initialReferrer, String initialUserAgent, String webappName) {
        GenericValue visit = getVisit(session);

        if (visit != null) {
            visit.set("initialLocale", initialLocale);
            if (initialRequest != null) visit.set("initialRequest", initialRequest.length() > 250 ? initialRequest.substring(0, 250) : initialRequest);
            if (initialReferrer != null) visit.set("initialReferrer", initialReferrer.length() > 250 ? initialReferrer.substring(0, 250) : initialReferrer);
            if (initialUserAgent != null) visit.set("initialUserAgent", initialUserAgent.length() > 250 ? initialUserAgent.substring(0, 250) : initialUserAgent);
            visit.set("webappName", webappName);
            if (UtilProperties.propertyValueEquals("serverstats", "stats.proxy.enabled", "true")){
                visit.set("clientIpAddress", request.getHeader("X-Forwarded-For"));
            } else {
                visit.set("clientIpAddress", request.getRemoteAddr());
            }
            visit.set("clientHostName", request.getRemoteHost());
            visit.set("clientUser", request.getRemoteUser());

            try {
                visit.store();
            } catch (GenericEntityException e) {
                Debug.logError(e, "Could not update visit:", module);
            }
        }
    }

    public static void setUserLogin(HttpSession session, GenericValue userLogin, boolean userCreated) {
        if (userLogin == null) return;

        GenericValue visitor = (GenericValue) session.getAttribute("visitor");
        if (visitor != null) {
            visitor.set("userLoginId", userLogin.get("userLoginId"));
            visitor.set("partyId", userLogin.get("partyId"));
            try {
                visitor.store();
            } catch (GenericEntityException e) {
                Debug.logError(e, "Could not update visitor: ", module);
            }
        }
        
        GenericValue visit = getVisit(session);
        if (visit != null) {
            visit.set("userLoginId", userLogin.get("userLoginId"));
            visit.set("partyId", userLogin.get("partyId"));
            visit.set("userCreated", new Boolean(userCreated));
            
            // make sure the visitorId is still in place
            if (visitor != null) {
                visit.set("visitorId", visitor.get("visitorId"));
            }
            
            try {
                visit.store();
            } catch (GenericEntityException e) {
                Debug.logError(e, "Could not update visit: ", module);
            }
        }
    }

    public static String getVisitId(HttpSession session) {
        GenericValue visit = getVisit(session);

        if (visit != null) {
            return visit.getString("visitId");
        } else {
            return null;
        }
    }

    /** Get the visit from the session, or create if missing */
    public static GenericValue getVisit(HttpSession session) {
        // this defaults to true: ie if anything but "false" it will be true
        if (!UtilProperties.propertyValueEqualsIgnoreCase("serverstats", "stats.persist.visit", "false")) {
            GenericValue visit = (GenericValue) session.getAttribute("visit");

            if (visit == null) {
                GenericDelegator delegator = null;
                String delegatorName = (String) session.getAttribute("delegatorName");

                if (UtilValidate.isNotEmpty(delegatorName)) {
                    delegator = GenericDelegator.getGenericDelegator(delegatorName);
                }
                if (delegator == null) {
                    Debug.logError("Could not find delegator with delegatorName [" + delegatorName + "] in session, not creating Visit entity", module);
                } else {
                    visit = delegator.makeValue("Visit", null);
                    visit.set("visitId", delegator.getNextSeqId("Visit"));
                    visit.set("sessionId", session.getId());
                    visit.set("fromDate", new Timestamp(session.getCreationTime()));
                    
                    // get the visitorId
                    GenericValue visitor = (GenericValue) session.getAttribute("visitor");
                    if (visitor != null) {
                        visit.set("visitorId", visitor.get("visitorId"));
                    }

                    // get localhost ip address and hostname to store
                    try {
                        InetAddress address = InetAddress.getLocalHost();

                        if (address != null) {
                            visit.set("serverIpAddress", address.getHostAddress());
                            visit.set("serverHostName", address.getHostName());
                        } else {
                            Debug.logError("Unable to get localhost internet address, was null", module);
                        }
                    } catch (java.net.UnknownHostException e) {
                        Debug.logError("Unable to get localhost internet address: " + e.toString(), module);
                    }
                    try {
                        visit.create();
                        session.setAttribute("visit", visit);
                    } catch (GenericEntityException e) {
                        Debug.logError(e, "Could not create new visit:", module);
                        visit = null;
                    }
                }
            }
            if (visit == null) {
                Debug.logWarning("Could not find or create the visit...", module);
            }
            return visit;
        } else {
            return null;
        }
    }

    public static GenericValue getVisitor(HttpServletRequest request, HttpServletResponse response) {
        // this defaults to true: ie if anything but "false" it will be true
        if (!UtilProperties.propertyValueEqualsIgnoreCase("serverstats", "stats.persist.visitor", "false")) {
            HttpSession session = request.getSession();
            GenericValue visitor = (GenericValue) session.getAttribute("visitor");
    
            if (visitor == null) {
                GenericDelegator delegator = null;
                String delegatorName = (String) session.getAttribute("delegatorName");
    
                if (UtilValidate.isNotEmpty(delegatorName)) {
                    delegator = GenericDelegator.getGenericDelegator(delegatorName);
                }
                if (delegator == null) {
                    Debug.logError("Could not find delegator with delegatorName [" + delegatorName + "] in session, not creating/getting Visitor entity", module);
                } else {
                    // first try to get the current ID from the visitor cookie
                    String visitorId = null;
                    Cookie[] cookies = request.getCookies();
                    if (Debug.verboseOn()) Debug.logVerbose("Cookies:" + cookies, module);
                    if (cookies != null) {
                        for (int i = 0; i < cookies.length; i++) {
                            if (cookies[i].getName().equals(visitorCookieName)) {
                                visitorId = cookies[i].getValue();
                                break;
                            }
                        }
                    }
                    
                    if (visitorId == null) {
                        // no visitor cookie? create visitor and send back cookie too
                        visitorId = delegator.getNextSeqId("Visitor");
    
                        visitor = delegator.makeValue("Visitor", null);
                        visitor.set("visitorId", visitorId);
                        try {
                            visitor.create();
                        } catch (GenericEntityException e) {
                            Debug.logError(e, "Could not create new visitor:", module);
                            visitor = null;
                        }
                    } else {
                        try {
                            visitor = delegator.findByPrimaryKey("Visitor", UtilMisc.toMap("visitorId", visitorId));
                        } catch (GenericEntityException e) {
                            Debug.logError(e, "Could not find visitor with ID from cookie: " + visitorId, module);
                            visitor = null;
                        }
                    }
                }
                
                if (visitor != null) {
                    // we got one, and it's a new one since it was null before
                    session.setAttribute("visitor", visitor);
    
                    // create the cookie and send it back, this may be done over and over, in effect frequently refreshing the cookie
                    Cookie visitorCookie = new Cookie(visitorCookieName, visitor.getString("visitorId"));
                    visitorCookie.setMaxAge(60 * 60 * 24 * 365);
                    visitorCookie.setPath("/");
                    response.addCookie(visitorCookie);
                }
            }
            if (visitor == null) {
                Debug.logWarning("Could not find or create the visitor...", module);
            }
            return visitor;
        } else {
            return null;
        }
    }
}
