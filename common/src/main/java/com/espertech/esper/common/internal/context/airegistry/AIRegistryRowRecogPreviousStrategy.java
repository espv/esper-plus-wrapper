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
package com.espertech.esper.common.internal.context.airegistry;

import com.espertech.esper.common.internal.epl.rowrecog.core.RowRecogPreviousStrategy;

public interface AIRegistryRowRecogPreviousStrategy extends RowRecogPreviousStrategy {
    public void assignService(int serviceId, RowRecogPreviousStrategy strategy);

    public void deassignService(int serviceId);

    public int getInstanceCount();
}
