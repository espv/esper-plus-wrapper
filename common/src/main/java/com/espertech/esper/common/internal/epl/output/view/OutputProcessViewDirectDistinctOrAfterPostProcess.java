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
package com.espertech.esper.common.internal.epl.output.view;

import com.espertech.esper.common.client.EventBean;
import com.espertech.esper.common.internal.collection.UniformPair;
import com.espertech.esper.common.internal.context.util.AgentInstanceContext;
import com.espertech.esper.common.internal.epl.resultset.core.ResultSetProcessor;
import com.espertech.esper.common.internal.statement.dispatch.UpdateDispatchView;

/**
 * Output process view that does not enforce any output policies and may simply
 * hand over events to child views, but works with distinct and after-output policies
 */
public class OutputProcessViewDirectDistinctOrAfterPostProcess extends OutputProcessViewDirectDistinctOrAfter {
    private final OutputStrategyPostProcess postProcessor;

    public OutputProcessViewDirectDistinctOrAfterPostProcess(AgentInstanceContext agentInstanceContext, ResultSetProcessor resultSetProcessor, Long afterConditionTime, Integer afterConditionNumberOfEvents, boolean afterConditionSatisfied, OutputProcessViewDirectDistinctOrAfterFactory parent, OutputStrategyPostProcess postProcessor) {
        super(agentInstanceContext, resultSetProcessor, afterConditionTime, afterConditionNumberOfEvents, afterConditionSatisfied, parent);
        this.postProcessor = postProcessor;
    }

    @Override
    protected void postProcess(boolean force, UniformPair<EventBean[]> newOldEvents, UpdateDispatchView childView) {
        postProcessor.output(force, newOldEvents, childView);
    }
}