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
package com.espertech.esper.common.internal.epl.join.support;

import com.espertech.esper.common.internal.epl.join.queryplan.IndexNameAndDescPair;

public class QueryPlanIndexDescSubquery extends QueryPlanIndexDescBase {
    private final int subqueryNum;
    private final String tableLookupStrategy;

    public QueryPlanIndexDescSubquery(IndexNameAndDescPair[] tables, int subqueryNum, String tableLookupStrategy) {
        super(tables);
        this.subqueryNum = subqueryNum;
        this.tableLookupStrategy = tableLookupStrategy;
    }

    public int getSubqueryNum() {
        return subqueryNum;
    }

    public String getTableLookupStrategy() {
        return tableLookupStrategy;
    }
}
