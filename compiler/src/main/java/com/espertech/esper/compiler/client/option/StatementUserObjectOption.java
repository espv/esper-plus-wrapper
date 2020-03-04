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
package com.espertech.esper.compiler.client.option;

import java.io.Serializable;

/**
 * Implement this interface to provide a custom user object at compile-time for the statements when they are compiled.
 */
public interface StatementUserObjectOption {
    /**
     * Returns the user object to assign to a statement at compilation-time.
     * <p>
     * Implementations would typically interrogate the context object EPL expression
     * or module and module item information and determine the right user object to assign.
     * </p>
     *
     * @param env the statement's compile context
     * @return user object or null if none needs to be assigned
     */
    public Serializable getValue(StatementUserObjectContext env);
}
