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
package com.espertech.esper.common.client.util;

/**
 * Type of the statement.
 */
public enum StatementType {
    /**
     * Select statement that may contain one or more patterns.
     */
    SELECT,

    /**
     * Create a named window statement.
     */
    CREATE_WINDOW,

    /**
     * Create a variable statement.
     */
    CREATE_VARIABLE,

    /**
     * Create a table statement.
     */
    CREATE_TABLE,

    /**
     * Create-schema statement.
     */
    CREATE_SCHEMA,

    /**
     * Create-index statement.
     */
    CREATE_INDEX,

    /**
     * Create-context statement.
     */
    CREATE_CONTEXT,

    /**
     * Create-graph statement.
     */
    CREATE_DATAFLOW,

    /**
     * Create-expression statement.
     */
    CREATE_EXPRESSION,

    /**
     * On-merge statement.
     */
    ON_MERGE,

    /**
     * On-merge statement.
     */
    ON_SPLITSTREAM,

    /**
     * On-delete statement.
     */
    ON_DELETE,

    /**
     * On-select statement.
     */
    ON_SELECT,

    /**
     * On-insert statement.
     */
    ON_INSERT,

    /**
     * On-set statement.
     */
    ON_SET,

    /**
     * On-update statement.
     */
    ON_UPDATE,

    /**
     * Update statement.
     */
    UPDATE,

    /**
     * EsperIO.
     */
    ESPERIO,

    /**
     * Statement for compiling an expression.
     */
    INTERNAL_USE_API_COMPILE_EXPR;

    /**
     * Returns true for on-action statements that operate against named windows or tables.
     *
     * @return indicator
     */
    public boolean isOnTriggerInfra() {
        return this == ON_SELECT || this == ON_INSERT || this == ON_DELETE || this == ON_MERGE || this == ON_UPDATE;
    }
}
