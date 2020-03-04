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
package com.espertech.esper.common.internal.epl.enummethod.eval;

import com.espertech.esper.common.client.EventBean;
import com.espertech.esper.common.internal.bytecodemodel.base.CodegenBlock;
import com.espertech.esper.common.internal.bytecodemodel.base.CodegenClassScope;
import com.espertech.esper.common.internal.bytecodemodel.base.CodegenMethod;
import com.espertech.esper.common.internal.bytecodemodel.base.CodegenMethodScope;
import com.espertech.esper.common.internal.bytecodemodel.model.expression.CodegenExpression;
import com.espertech.esper.common.internal.bytecodemodel.model.expression.CodegenExpressionField;
import com.espertech.esper.common.internal.context.module.EPStatementInitServices;
import com.espertech.esper.common.internal.epl.enummethod.codegen.EnumForgeCodegenNames;
import com.espertech.esper.common.internal.epl.enummethod.codegen.EnumForgeCodegenParams;
import com.espertech.esper.common.internal.epl.expression.codegen.ExprForgeCodegenSymbol;
import com.espertech.esper.common.internal.epl.expression.core.ExprEvaluator;
import com.espertech.esper.common.internal.epl.expression.core.ExprEvaluatorContext;
import com.espertech.esper.common.internal.event.arr.ObjectArrayEventBean;
import com.espertech.esper.common.internal.event.arr.ObjectArrayEventType;
import com.espertech.esper.common.internal.event.core.EventTypeUtility;
import com.espertech.esper.common.internal.util.JavaClassHelper;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.TreeMap;

import static com.espertech.esper.common.internal.bytecodemodel.model.expression.CodegenExpressionBuilder.*;

public class EnumOrderByAscDescScalarLambdaForgeEval implements EnumEval {

    private final EnumOrderByAscDescScalarLambdaForge forge;
    private final ExprEvaluator innerExpression;

    public EnumOrderByAscDescScalarLambdaForgeEval(EnumOrderByAscDescScalarLambdaForge forge, ExprEvaluator innerExpression) {
        this.forge = forge;
        this.innerExpression = innerExpression;
    }

    public Object evaluateEnumMethod(EventBean[] eventsLambda, Collection enumcoll, boolean isNewData, ExprEvaluatorContext context) {
        TreeMap<Comparable, Object> sort = new TreeMap<Comparable, Object>();
        boolean hasColl = false;

        ObjectArrayEventBean resultEvent = new ObjectArrayEventBean(new Object[1], forge.resultEventType);
        eventsLambda[forge.streamNumLambda] = resultEvent;
        Object[] props = resultEvent.getProperties();

        Collection<Object> values = (Collection<Object>) enumcoll;
        for (Object next : values) {

            props[0] = next;

            Comparable comparable = (Comparable) innerExpression.evaluate(eventsLambda, isNewData, context);
            Object entry = sort.get(comparable);

            if (entry == null) {
                sort.put(comparable, next);
                continue;
            }

            if (entry instanceof Collection) {
                ((Collection) entry).add(next);
                continue;
            }

            Deque<Object> coll = new ArrayDeque<Object>();
            coll.add(entry);
            coll.add(next);
            sort.put(comparable, coll);
            hasColl = true;
        }

        return EnumOrderByAscDescEventsForgeEval.enumOrderBySortEval(sort, hasColl, forge.descending);
    }

    public static CodegenExpression codegen(EnumOrderByAscDescScalarLambdaForge forge, EnumForgeCodegenParams args, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        CodegenExpressionField resultTypeMember = codegenClassScope.addFieldUnshared(true, ObjectArrayEventType.class, cast(ObjectArrayEventType.class, EventTypeUtility.resolveTypeCodegen(forge.resultEventType, EPStatementInitServices.REF)));
        Class innerBoxedType = JavaClassHelper.getBoxedType(forge.innerExpression.getEvaluationType());

        ExprForgeCodegenSymbol scope = new ExprForgeCodegenSymbol(false, null);
        CodegenMethod methodNode = codegenMethodScope.makeChildWithScope(Collection.class, EnumOrderByAscDescScalarLambdaForgeEval.class, scope, codegenClassScope).addParam(EnumForgeCodegenNames.PARAMS);

        CodegenBlock block = methodNode.getBlock()
                .declareVar(TreeMap.class, "sort", newInstance(TreeMap.class))
                .declareVar(boolean.class, "hasColl", constantFalse())
                .declareVar(ObjectArrayEventBean.class, "resultEvent", newInstance(ObjectArrayEventBean.class, newArrayByLength(Object.class, constant(1)), resultTypeMember))
                .assignArrayElement(EnumForgeCodegenNames.REF_EPS, constant(forge.streamNumLambda), ref("resultEvent"))
                .declareVar(Object[].class, "props", exprDotMethod(ref("resultEvent"), "getProperties"));

        block.forEach(Object.class, "next", EnumForgeCodegenNames.REF_ENUMCOLL)
                .assignArrayElement("props", constant(0), ref("next"))
                .declareVar(innerBoxedType, "value", forge.innerExpression.evaluateCodegen(innerBoxedType, methodNode, scope, codegenClassScope))
                .declareVar(Object.class, "entry", exprDotMethod(ref("sort"), "get", ref("value")))
                .ifCondition(equalsNull(ref("entry")))
                .expression(exprDotMethod(ref("sort"), "put", ref("value"), ref("next")))
                .blockContinue()
                .ifCondition(instanceOf(ref("entry"), Collection.class))
                .exprDotMethod(cast(Collection.class, ref("entry")), "add", ref("next"))
                .blockContinue()
                .declareVar(Deque.class, "coll", newInstance(ArrayDeque.class, constant(2)))
                .exprDotMethod(ref("coll"), "add", ref("entry"))
                .exprDotMethod(ref("coll"), "add", ref("next"))
                .exprDotMethod(ref("sort"), "put", ref("value"), ref("coll"))
                .assignRef("hasColl", constantTrue())
                .blockEnd();
        block.methodReturn(staticMethod(EnumOrderByAscDescEventsForgeEval.class, "enumOrderBySortEval", ref("sort"), ref("hasColl"), constant(forge.descending)));
        return localMethod(methodNode, args.getEps(), args.getEnumcoll(), args.getIsNewData(), args.getExprCtx());
    }
}
