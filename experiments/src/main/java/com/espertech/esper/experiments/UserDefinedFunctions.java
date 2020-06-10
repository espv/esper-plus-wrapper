package com.espertech.esper.experiments;

public class UserDefinedFunctions {
    public static long computeDolToEur(long price) {
        return (long) (price * 0.89);
    }
}
