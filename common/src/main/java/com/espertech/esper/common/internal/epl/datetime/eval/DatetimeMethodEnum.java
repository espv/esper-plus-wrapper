/*
 ***************************************************************************************
 *  Copyright (C) 2006 EsperTech, Inc. All rights reserved.                            *
 *  http://www.espertech.com/esper                                                     *
 *  http://www.espertech.com                                                           *
 *  ---------------------------------------------------------------------------------- *
 *  The software in this package is published under the terms of the GPL license       *
 *  a copy of which has been included with this distribution in the license.txt file.  *
 ***************************************************************************************
 */
package com.espertech.esper.common.internal.epl.datetime.eval;

public enum DatetimeMethodEnum {

    // calendar op
    WITHTIME,
    WITHDATE,
    PLUS,
    MINUS,
    WITHMAX,
    WITHMIN,
    SET,
    ROUNDCEILING,
    ROUNDFLOOR,
    ROUNDHALF,

    // reformat op
    GET,
    FORMAT,
    TOCALENDAR,
    TODATE,
    TOMILLISEC,
    GETMINUTEOFHOUR,
    GETMONTHOFYEAR,
    GETDAYOFMONTH,
    GETDAYOFWEEK,
    GETDAYOFYEAR,
    GETERA,
    GETHOUROFDAY,
    GETMILLISOFSECOND,
    GETSECONDOFMINUTE,
    GETWEEKYEAR,
    GETYEAR,
    BETWEEN,

    // interval op
    BEFORE,
    AFTER,
    COINCIDES,
    DURING,
    INCLUDES,
    FINISHES,
    FINISHEDBY,
    MEETS,
    METBY,
    OVERLAPS,
    OVERLAPPEDBY,
    STARTS,
    STARTEDBY,

    // plug-in
    PLUGIN
}
