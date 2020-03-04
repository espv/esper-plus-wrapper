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
package com.espertech.esper.runtime.internal.kernel.thread;

import com.espertech.esper.common.internal.context.util.EPStatementAgentInstanceHandle;
import com.espertech.esper.runtime.internal.kernel.service.EPEventServiceImpl;
import com.espertech.esper.runtime.internal.kernel.service.EPServicesContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Timer unit for multiple callbacks for a statement.
 */
public class TimerUnitMultiple implements TimerUnit {
    private static final Logger log = LoggerFactory.getLogger(TimerUnitMultiple.class);

    private final EPServicesContext services;
    private final EPEventServiceImpl runtime;
    private final Object callbackObject;
    private final EPStatementAgentInstanceHandle handle;

    /**
     * Ctor.
     *
     * @param services       runtime services
     * @param runtime        runtime to process
     * @param handle         statement handle
     * @param callbackObject callback list
     */
    public TimerUnitMultiple(EPServicesContext services, EPEventServiceImpl runtime, EPStatementAgentInstanceHandle handle, Object callbackObject) {
        this.services = services;
        this.handle = handle;
        this.runtime = runtime;
        this.callbackObject = callbackObject;
    }

    public void run() {
        try {
            EPEventServiceImpl.processStatementScheduleMultiple(handle, callbackObject, services);

            // Let listeners know of results
            runtime.dispatch();

            // Work off the event queue if any events accumulated in there via a route()
            runtime.processThreadWorkQueue();
        } catch (RuntimeException e) {
            log.error("Unexpected error processing multiple timer execution: " + e.getMessage(), e);
        }
    }
}
