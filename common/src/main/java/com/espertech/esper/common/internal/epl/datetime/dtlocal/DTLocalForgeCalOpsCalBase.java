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
package com.espertech.esper.common.internal.epl.datetime.dtlocal;

import com.espertech.esper.common.internal.epl.datetime.calop.CalendarForge;

import java.util.List;

abstract class DTLocalForgeCalOpsCalBase {

    protected final List<CalendarForge> calendarForges;

    public DTLocalForgeCalOpsCalBase(List<CalendarForge> calendarForges) {
        this.calendarForges = calendarForges;
    }
}
