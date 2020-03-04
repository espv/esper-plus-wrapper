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
package com.espertech.esper.common.internal.serde.serdeset.builtin;

import com.espertech.esper.common.client.serde.DataInputOutputSerde;
import com.espertech.esper.common.client.serde.EventBeanCollatedWriter;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class DIOPrimitiveIntArrayNullableSerde implements DataInputOutputSerde<int[]> {
    public final static DIOPrimitiveIntArrayNullableSerde INSTANCE = new DIOPrimitiveIntArrayNullableSerde();

    private DIOPrimitiveIntArrayNullableSerde() {
    }

    public void write(int[] object, DataOutput output) throws IOException {
        writeInternal(object, output);
    }

    public int[] read(DataInput input) throws IOException {
        return readInternal(input);
    }

    public void write(int[] object, DataOutput output, byte[] unitKey, EventBeanCollatedWriter writer) throws IOException {
        writeInternal(object, output);
    }

    public int[] read(DataInput input, byte[] unitKey) throws IOException {
        return readInternal(input);
    }

    private void writeInternal(int[] object, DataOutput output) throws IOException {
        if (object == null) {
            output.writeInt(-1);
            return;
        }
        output.writeInt(object.length);
        for (int i : object) {
            output.writeInt(i);
        }
    }

    private int[] readInternal(DataInput input) throws IOException {
        int len = input.readInt();
        if (len == -1) {
            return null;
        }
        int[] array = new int[len];
        for (int i = 0; i < len; i++) {
            array[i] = input.readInt();
        }
        return array;
    }
}