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
package com.espertech.esper.compiler.internal.util;

import com.espertech.esper.common.client.EventType;
import com.espertech.esper.common.internal.compile.util.CompileExpressionSPI;
import com.espertech.esper.common.internal.epl.expression.core.ExprNode;
import com.espertech.esper.compiler.client.EPCompileException;

public interface EPCompilerSPIExpression extends CompileExpressionSPI {
    ExprNode compileValidate(String expression) throws EPCompileException;
    ExprNode compileValidate(String expression, EventType[] eventTypes, String[] streamNnames) throws EPCompileException;
}
