/*
 * $Id: RecurrenceInfo.java 5462 2005-08-05 18:35:48Z jonesde $
 *
 * Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.ofbiz.service.calendar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

/**
 * Recurrence Info Object
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      2.0
 */
public class RecurrenceInfo {
    
    public static final String module = RecurrenceInfo.class.getName();

    protected GenericValue info;
    protected Date startDate;
    protected List rRulesList;
    protected List eRulesList;
    protected List rDateList;
    protected List eDateList;;

    /** Creates new RecurrenceInfo */
    public RecurrenceInfo(GenericValue info) throws RecurrenceInfoException {
        this.info = info;
        if (!info.getEntityName().equals("RecurrenceInfo"))
            throw new RecurrenceInfoException("Invalid RecurrenceInfo Value object.");
        init();
    }

    /** Initializes the rules for this RecurrenceInfo object. */
    public void init() throws RecurrenceInfoException {

        if (info.get("startDateTime") == null)
            throw new RecurrenceInfoException("Recurrence startDateTime cannot be null.");

        // Get start date
        long startTime = info.getTimestamp("startDateTime").getTime();

        if (startTime > 0) {
            int nanos = info.getTimestamp("startDateTime").getNanos();

            startTime += (nanos / 1000000);
        } else {
            throw new RecurrenceInfoException("Recurrence startDateTime must have a value.");
        }
        startDate = new Date(startTime);

        // Get the recurrence rules objects
        try {
            Collection c = info.getRelated("RecurrenceRule");
            Iterator i = c.iterator();

            rRulesList = new ArrayList();
            while (i.hasNext()) {
                rRulesList.add(new RecurrenceRule((GenericValue) i.next()));
            }
        } catch (GenericEntityException gee) {
            rRulesList = null;
        } catch (RecurrenceRuleException rre) {
            throw new RecurrenceInfoException("Illegal rule format.", rre);
        }

        // Get the exception rules objects
        try {
            Collection c = info.getRelated("ExceptionRecurrenceRule");
            Iterator i = c.iterator();

            eRulesList = new ArrayList();
            while (i.hasNext()) {
                eRulesList.add(new RecurrenceRule((GenericValue) i.next()));
            }
        } catch (GenericEntityException gee) {
            eRulesList = null;
        } catch (RecurrenceRuleException rre) {
            throw new RecurrenceInfoException("Illegal rule format", rre);
        }

        // Get the recurrence date list
        rDateList = RecurrenceUtil.parseDateList(StringUtil.split(info.getString("recurrenceDateTimes"), ","));
        // Get the exception date list
        eDateList = RecurrenceUtil.parseDateList(StringUtil.split(info.getString("exceptionDateTimes"), ","));

        // Sort the lists.
        Collections.sort(rDateList);
        Collections.sort(eDateList);
    }

    /** Returns the primary key for this value object */
    public String getID() {
        return info.getString("recurrenceInfoId");
    }

    /** Returns the startDate Date object. */
    public Date getStartDate() {
        return this.startDate;
    }

    /** Returns the long value of the startDate. */
    public long getStartTime() {
        return this.startDate.getTime();
    }

    /** Returns a recurrence rule iterator */
    public Iterator getRecurrenceRuleIterator() {
        return rRulesList.iterator();
    }

    /** Returns a sorted recurrence date iterator */
    public Iterator getRecurrenceDateIterator() {
        return rDateList.iterator();
    }

    /** Returns a exception recurrence iterator */
    public Iterator getExceptionRuleIterator() {
        return eRulesList.iterator();
    }

    /** Returns a sorted exception date iterator */
    public Iterator getExceptionDateIterator() {
        return eDateList.iterator();
    }

    /** Returns the current count of this recurrence. */
    public long getCurrentCount() {
        if (info.get("recurrenceCount") != null)
            return info.getLong("recurrenceCount").longValue();
        return 0;
    }

    /** Increments the current count of this recurrence and updates the record. */
    public void incrementCurrentCount() throws GenericEntityException {
        incrementCurrentCount(true);
    }

    /** Increments the current count of this recurrence. */
    public void incrementCurrentCount(boolean store) throws GenericEntityException {
        Long count = new Long(getCurrentCount() + 1);

        if (store) {
            info.set("recurrenceCount", count);
            info.store();
        }
    }

    /** Removes the recurrence from persistant store. */
    public void remove() throws RecurrenceInfoException {
        List rulesList = new ArrayList();

        rulesList.addAll(rRulesList);
        rulesList.addAll(eRulesList);
        Iterator i = rulesList.iterator();

        try {
            while (i.hasNext())
                ((RecurrenceRule) i.next()).remove();
            info.remove();
        } catch (RecurrenceRuleException rre) {
            throw new RecurrenceInfoException(rre.getMessage(), rre);
        } catch (GenericEntityException gee) {
            throw new RecurrenceInfoException(gee.getMessage(), gee);
        }
    }

    /** Returns the first recurrence. */
    public long first() {
        return startDate.getTime();
        // First recurrence is always the start time
    }

    /** Returns the estimated last recurrence. */
    public long last() {
        // TODO: find the last recurrence.
        return 0;
    }

    /** Returns the next recurrence from now. */
    public long next() {
        return next(RecurrenceUtil.now());
    }

    /** Returns the next recurrence from the specified time. */
    public long next(long fromTime) {
        // Check for the first recurrence (StartTime is always the first recurrence)
        if (getCurrentCount() == 0 || fromTime == 0 || fromTime == startDate.getTime()) {
            return first();
        }
            
        if (Debug.verboseOn()) {
            Debug.logVerbose("Date List Size: " + (rDateList == null ? 0 : rDateList.size()), module);
            Debug.logVerbose("Rule List Size: " + (rRulesList == null ? 0 : rRulesList.size()), module);
        }            

        // Check the rules and date list
        if (rDateList == null && rRulesList == null) {
            return 0;
        }

        long nextRuleTime = fromTime;
        boolean hasNext = true;

        // Get the next recurrence from the rule(s).
        Iterator rulesIterator = getRecurrenceRuleIterator();
        while (rulesIterator.hasNext()) {
            RecurrenceRule rule = (RecurrenceRule) rulesIterator.next();
            while (hasNext) {
                // Gets the next recurrence time from the rule.
                nextRuleTime = getNextTime(rule, nextRuleTime);
                // Tests the next recurrence against the rules.
                if (nextRuleTime == 0 || isValid(nextRuleTime)) {
                    hasNext = false;
                }
            }
        }
        return nextRuleTime;
    }

    private long getNextTime(RecurrenceRule rule, long fromTime) {
        long nextTime = rule.next(getStartTime(), fromTime, getCurrentCount());
        if (Debug.verboseOn()) Debug.logVerbose("Next Time Before Date Check: " + nextTime, module);
        return checkDateList(rDateList, nextTime, fromTime);
    }

    private long checkDateList(List dateList, long time, long fromTime) {
        long nextTime = time;

        if (dateList != null && dateList.size() > 0) {
            Iterator dateIterator = dateList.iterator();

            while (dateIterator.hasNext()) {
                Date thisDate = (Date) dateIterator.next();

                if (nextTime > 0 && thisDate.getTime() < nextTime && thisDate.getTime() > fromTime)
                    nextTime = thisDate.getTime();
                else if (nextTime == 0 && thisDate.getTime() > fromTime)
                    nextTime = thisDate.getTime();
            }
        }
        return nextTime;
    }

    private boolean isValid(long time) {
        Iterator exceptRulesIterator = getExceptionRuleIterator();

        while (exceptRulesIterator.hasNext()) {
            RecurrenceRule except = (RecurrenceRule) exceptRulesIterator.next();

            if (except.isValid(getStartTime(), time) || eDateList.contains(new Date(time)))
                return false;
        }
        return true;
    }

    public String primaryKey() {
        return info.getString("recurrenceInfoId");
    }
    
    public static RecurrenceInfo makeInfo(GenericDelegator delegator, long startTime, int frequency,
            int interval, int count) throws RecurrenceInfoException {
        return makeInfo(delegator, startTime, frequency, interval, count, 0);
    }

    public static RecurrenceInfo makeInfo(GenericDelegator delegator, long startTime, int frequency,
            int interval, long endTime) throws RecurrenceInfoException {
        return makeInfo(delegator, startTime, frequency, interval, -1, endTime);
    }

    public static RecurrenceInfo makeInfo(GenericDelegator delegator, long startTime, int frequency,
            int interval, int count, long endTime) throws RecurrenceInfoException {
        try {
            RecurrenceRule r = RecurrenceRule.makeRule(delegator, frequency, interval, count, endTime);
            String ruleId = r.primaryKey();
            String infoId = delegator.getNextSeqId("RecurrenceInfo").toString();
            GenericValue value = delegator.makeValue("RecurrenceInfo", UtilMisc.toMap("recurrenceInfoId", infoId));

            value.set("recurrenceRuleId", ruleId);
            value.set("startDateTime", new java.sql.Timestamp(startTime));
            delegator.create(value);
            RecurrenceInfo newInfo = new RecurrenceInfo(value);

            return newInfo;
        } catch (RecurrenceRuleException re) {
            throw new RecurrenceInfoException(re.getMessage(), re);
        } catch (GenericEntityException ee) {
            throw new RecurrenceInfoException(ee.getMessage(), ee);
        } catch (RecurrenceInfoException rie) {
            throw rie;
        }
    }
}
