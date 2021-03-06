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
package com.espertech.esper.common.internal.epl.table.core;

import com.espertech.esper.common.client.EPException;
import com.espertech.esper.common.client.EventBean;
import com.espertech.esper.common.client.EventType;
import com.espertech.esper.common.client.util.MultiKey;
import com.espertech.esper.common.internal.collection.MultiKeyArrayWrap;
import com.espertech.esper.common.internal.context.util.AgentInstanceContext;
import com.espertech.esper.common.internal.epl.agg.core.AggregationRow;
import com.espertech.esper.common.internal.epl.index.base.EventTable;
import com.espertech.esper.common.internal.event.core.ObjectArrayBackedEventBean;

public abstract class TableInstanceGroupedBase extends TableInstanceBase implements TableInstanceGrouped {

    public TableInstanceGroupedBase(Table table, AgentInstanceContext agentInstanceContext) {
        super(table, agentInstanceContext);
    }

    public void addEvent(EventBean event) {
        agentInstanceContext.getInstrumentationProvider().qTableAddEvent(event);

        try {
            for (EventTable table : indexRepository.getTables()) {
                table.add(event, agentInstanceContext);
            }
        } catch (EPException ex) {
            for (EventTable table : indexRepository.getTables()) {
                table.remove(event, agentInstanceContext);
            }
            throw ex;
        } finally {
            agentInstanceContext.getInstrumentationProvider().aTableAddEvent();
        }
    }

    protected ObjectArrayBackedEventBean createRowIntoTable(Object groupKeys) {
        EventType eventType = table.getMetaData().getInternalEventType();
        AggregationRow aggregationRow = table.getAggregationRowFactory().make();
        Object[] data = new Object[eventType.getPropertyDescriptors().length];
        data[0] = aggregationRow;

        int[] groupKeyColNums = table.getMetaData().getKeyColNums();
        if (groupKeyColNums.length == 1) {
            if (groupKeys instanceof MultiKeyArrayWrap) {
                data[groupKeyColNums[0]] = ((MultiKeyArrayWrap) groupKeys).getArray();
            } else {
                data[groupKeyColNums[0]] = groupKeys;
            }
        } else {
            MultiKey mk = (MultiKey) groupKeys;
            for (int i = 0; i < groupKeyColNums.length; i++) {
                data[groupKeyColNums[i]] = mk.getKey(i);
            }
        }

        ObjectArrayBackedEventBean row = agentInstanceContext.getEventBeanTypedEventFactory().adapterForTypedObjectArray(data, eventType);
        addEvent(row);
        return row;
    }
}
