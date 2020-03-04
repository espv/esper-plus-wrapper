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
package com.espertech.esper.common.internal.epl.output.core;

import com.espertech.esper.common.client.EventBean;
import com.espertech.esper.common.client.EventType;
import com.espertech.esper.common.internal.collection.MultiKeyArrayOfKeys;
import com.espertech.esper.common.internal.collection.UniformPair;
import com.espertech.esper.common.internal.context.util.AgentInstanceContext;
import com.espertech.esper.common.internal.context.util.AgentInstanceStopServices;
import com.espertech.esper.common.internal.context.util.StatementResultService;
import com.espertech.esper.common.internal.epl.expression.core.ExprEvaluatorContext;
import com.espertech.esper.common.internal.epl.output.condition.OutputCondition;

import java.util.Iterator;
import java.util.Set;

public class OutputProcessViewSimple extends OutputProcessView {
    private final AgentInstanceContext agentInstanceContext;

    public OutputProcessViewSimple(AgentInstanceContext agentInstanceContext) {
        this.agentInstanceContext = agentInstanceContext;
    }

    public int getNumChangesetRows() {
        return 0;
    }

    public OutputCondition getOptionalOutputCondition() {
        return null;
    }

    public void stop(AgentInstanceStopServices services) {
    }

    public void process(Set<MultiKeyArrayOfKeys<EventBean>> newEvents, Set<MultiKeyArrayOfKeys<EventBean>> oldEvents, ExprEvaluatorContext exprEvaluatorContext) {
    }

    public void update(EventBean[] newData, EventBean[] oldData) {
        StatementResultService statementResultService = agentInstanceContext.getStatementResultService();
        boolean isGenerateSynthetic = statementResultService.isMakeSynthetic();
        boolean isGenerateNatural = statementResultService.isMakeNatural();

        if ((!isGenerateSynthetic) && (!isGenerateNatural)) {
            return;
        }

        child.newResult(new UniformPair<>(newData, oldData));
    }

    public EventType getEventType() {
        return getParent().getEventType();
    }

    public Iterator<EventBean> iterator() {
        return getParent().iterator();
    }

    public void terminated() {
    }
}
