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
package com.espertech.esperio.db.core;

public class StoreExceptionDBRel extends RuntimeException {
    /**
     * Ctor.
     *
     * @param message error message
     * @param cause   inner exception
     */
    public StoreExceptionDBRel(String message, Throwable cause) {
        super(message, cause);
    }
}
