package org.flume.source.csv.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Objects;

public class GenericAuthenticationRawEvent extends GenericRawEvent {

    private String dateTime;
    private String srcMachineId;
    private String srcMachineName;
    private String dstMachineId;
    private String dstMachineName;
    private String dstMachineDomain;
    private String eventId;
    private String dataSource;
    private String userId;
    private String operationType;


    //Additional information
    public String adInfo1;
    public String adInfo2;
    public String adInfo3;
    public String adInfo4;
    public String adInfo5;
    public String adInfo6;
    public String adInfo7;
    public String adInfo8;
    public String adInfo9;
    public String adInfo10;

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getSrcMachineId() {
        return srcMachineId;
    }

    public void setSrcMachineId(String srcMachineId) {
        this.srcMachineId = srcMachineId;
    }

    public String getSrcMachineName() {
        return srcMachineName;
    }

    public void setSrcMachineName(String srcMachineName) {
        this.srcMachineName = srcMachineName;
    }

    public String getDstMachineId() {
        return dstMachineId;
    }

    public void setDstMachineId(String dstMachineId) {
        this.dstMachineId = dstMachineId;
    }

    public String getDstMachineName() {
        return dstMachineName;
    }

    public void setDstMachineName(String dstMachineName) {
        this.dstMachineName = dstMachineName;
    }

    public String getDstMachineDomain() {
        return dstMachineDomain;
    }

    public void setDstMachineDomain(String dstMachineDomain) {
        this.dstMachineDomain = dstMachineDomain;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getAdInfo1() {
        return adInfo1;
    }

    public void setAdInfo1(String adInfo1) {
        this.adInfo1 = adInfo1;
    }

    public String getAdInfo2() {
        return adInfo2;
    }

    public void setAdInfo2(String adInfo2) {
        this.adInfo2 = adInfo2;
    }

    public String getAdInfo3() {
        return adInfo3;
    }

    public void setAdInfo3(String adInfo3) {
        this.adInfo3 = adInfo3;
    }

    public String getAdInfo4() {
        return adInfo4;
    }

    public void setAdInfo4(String adInfo4) {
        this.adInfo4 = adInfo4;
    }

    public String getAdInfo5() {
        return adInfo5;
    }

    public void setAdInfo5(String adInfo5) {
        this.adInfo5 = adInfo5;
    }

    public String getAdInfo6() {
        return adInfo6;
    }

    public void setAdInfo6(String adInfo6) {
        this.adInfo6 = adInfo6;
    }

    public String getAdInfo7() {
        return adInfo7;
    }

    public void setAdInfo7(String adInfo7) {
        this.adInfo7 = adInfo7;
    }

    public String getAdInfo8() {
        return adInfo8;
    }

    public void setAdInfo8(String adInfo8) {
        this.adInfo8 = adInfo8;
    }

    public String getAdInfo9() {
        return adInfo9;
    }

    public void setAdInfo9(String adInfo9) {
        this.adInfo9 = adInfo9;
    }

    public String getAdInfo10() {
        return adInfo10;
    }

    public void setAdInfo10(String adInfo10) {
        this.adInfo10 = adInfo10;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenericAuthenticationRawEvent that = (GenericAuthenticationRawEvent) o;
        return Objects.equals(dateTime, that.dateTime) &&
                Objects.equals(srcMachineId, that.srcMachineId) &&
                Objects.equals(srcMachineName, that.srcMachineName) &&
                Objects.equals(dstMachineId, that.dstMachineId) &&
                Objects.equals(dstMachineName, that.dstMachineName) &&
                Objects.equals(dstMachineDomain, that.dstMachineDomain) &&
                Objects.equals(eventId, that.eventId) &&
                Objects.equals(dataSource, that.dataSource) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(operationType, that.operationType) &&
                Objects.equals(adInfo1, that.adInfo1) &&
                Objects.equals(adInfo2, that.adInfo2) &&
                Objects.equals(adInfo3, that.adInfo3) &&
                Objects.equals(adInfo4, that.adInfo4) &&
                Objects.equals(adInfo5, that.adInfo5) &&
                Objects.equals(adInfo6, that.adInfo6) &&
                Objects.equals(adInfo7, that.adInfo7) &&
                Objects.equals(adInfo8, that.adInfo8) &&
                Objects.equals(adInfo9, that.adInfo9) &&
                Objects.equals(adInfo10, that.adInfo10);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateTime, srcMachineId, srcMachineName, dstMachineId, dstMachineName, dstMachineDomain, eventId, dataSource, userId, operationType, adInfo1, adInfo2, adInfo3, adInfo4, adInfo5, adInfo6, adInfo7, adInfo8, adInfo9, adInfo10);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("dateTime", dateTime)
                .append("srcMachineId", srcMachineId)
                .append("srcMachineName", srcMachineName)
                .append("dstMachineId", dstMachineId)
                .append("dstMachineName", dstMachineName)
                .append("dstMachineDomain", dstMachineDomain)
                .append("eventId", eventId)
                .append("dataSource", dataSource)
                .append("userId", userId)
                .append("operationType", operationType)
                .append("adInfo1", adInfo1)
                .append("adInfo2", adInfo2)
                .append("adInfo3", adInfo3)
                .append("adInfo4", adInfo4)
                .append("adInfo5", adInfo5)
                .append("adInfo6", adInfo6)
                .append("adInfo7", adInfo7)
                .append("adInfo8", adInfo8)
                .append("adInfo9", adInfo9)
                .append("adInfo10", adInfo10)
                .toString();
    }
}
