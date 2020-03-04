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
package com.espertech.esper.regressionlib.support.bean;

import java.io.Serializable;

public class SupportRFIDEvent implements Serializable {
    private String locationReportId;
    private String mac;
    private String zoneID;

    public SupportRFIDEvent(String mac, String zoneID) {
        this(null, mac, zoneID);
    }

    public SupportRFIDEvent(String locationReportId, String mac, String zoneID) {
        this.locationReportId = locationReportId;
        this.mac = mac;
        this.zoneID = zoneID;
    }

    public String getLocationReportId() {
        return locationReportId;
    }

    public String getMac() {
        return mac;
    }

    public String getZoneID() {
        return zoneID;
    }
}
