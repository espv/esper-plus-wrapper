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
import java.sql.Date;

public class DIOSqlDateArrayNullableSerde implements DataInputOutputSerde<Date[]> {
    public final static DIOSqlDateArrayNullableSerde INSTANCE = new DIOSqlDateArrayNullableSerde();

    private DIOSqlDateArrayNullableSerde() {
    }

    public void write(java.sql.Date[] object, DataOutput output) throws IOException {
        writeInternal(object, output);
    }

    public java.sql.Date[] read(DataInput input) throws IOException {
        return readInternal(input);
    }

    public void write(java.sql.Date[] object, DataOutput output, byte[] unitKey, EventBeanCollatedWriter writer) throws IOException {
        writeInternal(object, output);
    }

    public java.sql.Date[] read(DataInput input, byte[] unitKey) throws IOException {
        return readInternal(input);
    }

    private void writeInternal(java.sql.Date[] object, DataOutput output) throws IOException {
        if (object == null) {
            output.writeInt(-1);
            return;
        }
        output.writeInt(object.length);
        for (java.sql.Date i : object) {
            DIOSqlDateSerde.INSTANCE.write(i, output);
        }
    }

    private java.sql.Date[] readInternal(DataInput input) throws IOException {
        int len = input.readInt();
        if (len == -1) {
            return null;
        }
        java.sql.Date[] array = new java.sql.Date[len];
        for (int i = 0; i < len; i++) {
            array[i] = DIOSqlDateSerde.INSTANCE.read(input);
        }
        return array;
    }
}
