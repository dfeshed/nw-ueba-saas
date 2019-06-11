package presidio.data.domain.event.network;

import presidio.data.domain.Location;
import presidio.data.domain.MachineEntity;
import presidio.data.domain.User;
import presidio.data.domain.event.Event;

import java.io.Serializable;
import java.time.Instant;

public class NetworkEvent extends Event implements Serializable {
    private static final long serialVersionUID = 1L;

    private String eventId;
    private Instant dateTime;
    private MachineEntity srcMachineEntity;
    private MachineEntity dstMachineEntity;

    private String destinationOrganization;
    private String destinationASN;
    private long numOfBytesSent;
    private long numOfBytesReceived;

    private String sourceNetname;
    private String destinationNetname;
    private String ja3;
    private String ja3s;
    private NETWORK_DIRECTION_TYPE direction;
    private int destinationPort;
    private String dataSource;

    private Location srcLocation;
    private Location dstLocation;

    private String sslSubject;
    private String sslCa;
    private String sessionSplit;
    private String isSelfSigned;

    public NetworkEvent(Instant dateTime) {
        this.dateTime = dateTime;
    }

    public NetworkEvent(String eventId, Instant dateTime, MachineEntity srcMachineEntity, MachineEntity dstMachineEntity,
                        String destinationOrganization, String destinationASN, long numOfBytesSent, long numOfBytesReceived,
                        String sourceNetname, String destinationNetname, String ja3, String ja3s,
                        NETWORK_DIRECTION_TYPE direction, int destinationPort, String dataSource, Location srcLocation,
                        Location dstLocation, String sslSubject, String sslCa, String sessionSplit, String isSelfSigned) {
        this.eventId = eventId;
        this.dateTime = dateTime;
        this.srcMachineEntity = srcMachineEntity;
        this.dstMachineEntity = dstMachineEntity;
        this.destinationOrganization = destinationOrganization;
        this.destinationASN = destinationASN;
        this.numOfBytesSent = numOfBytesSent;
        this.numOfBytesReceived = numOfBytesReceived;
        this.sourceNetname = sourceNetname;
        this.destinationNetname = destinationNetname;
        this.ja3 = ja3;
        this.ja3s = ja3s;
        this.direction = direction;
        this.destinationPort = destinationPort;
        this.dataSource = dataSource;
        this.srcLocation = srcLocation;
        this.dstLocation = dstLocation;
        this.sslSubject = sslSubject;
        this.sslCa = sslCa;
        this.sessionSplit = sessionSplit;
        this.isSelfSigned = isSelfSigned;
    }
    
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Instant getDateTime() {
        return dateTime;
    }

    public void setDateTime(Instant dateTime) {
        this.dateTime = dateTime;
    }

    public MachineEntity getSrcMachineEntity() {
        return srcMachineEntity;
    }

    public void setSrcMachineEntity(MachineEntity srcMachineEntity) {
        this.srcMachineEntity = srcMachineEntity;
    }

    public MachineEntity getDstMachineEntity() {
        return dstMachineEntity;
    }

    public void setDstMachineEntity(MachineEntity dstMachineEntity) {
        this.dstMachineEntity = dstMachineEntity;
    }

    public String getDestinationOrganization() {
        return destinationOrganization;
    }

    public void setDestinationOrganization(String destinationOrganization) {
        this.destinationOrganization = destinationOrganization;
    }

    public String getDestinationASN() {
        return destinationASN;
    }

    public void setDestinationASN(String destinationASN) {
        this.destinationASN = destinationASN;
    }

    public long getNumOfBytesSent() {
        return numOfBytesSent;
    }

    public void setNumOfBytesSent(long numOfBytesSent) {
        this.numOfBytesSent = numOfBytesSent;
    }

    public long getNumOfBytesReceived() {
        return numOfBytesReceived;
    }

    public void setNumOfBytesReceived(long numOfBytesReceived) {
        this.numOfBytesReceived = numOfBytesReceived;
    }

    public String getSourceNetname() {
        return sourceNetname;
    }

    public void setSourceNetname(String sourceNetname) {
        this.sourceNetname = sourceNetname;
    }

    public String getDestinationNetname() {
        return destinationNetname;
    }

    public void setDestinationNetname(String destinationNetname) {
        this.destinationNetname = destinationNetname;
    }

    public String getJa3() {
        return ja3;
    }

    public void setJa3(String ja3) {
        this.ja3 = ja3;
    }

    public String getJa3s() {
        return ja3s;
    }

    public void setJa3s(String ja3s) {
        this.ja3s = ja3s;
    }

    public NETWORK_DIRECTION_TYPE getDirection() {
        return direction;
    }

    public void setDirection(NETWORK_DIRECTION_TYPE direction) {
        this.direction = direction;
    }

    public int getDestinationPort() {
        return destinationPort;
    }

    public void setDestinationPort(int destinationPort) {
        this.destinationPort = destinationPort;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public Location getSrcLocation() {
        return srcLocation;
    }

    public void setSrcLocation(Location srcLocation) {
        this.srcLocation = srcLocation;
    }

    public Location getDstLocation() {
        return dstLocation;
    }

    public void setDstLocation(Location dstLocation) {
        this.dstLocation = dstLocation;
    }

    public String getSslSubject() {
        return sslSubject;
    }

    public void setSslSubject(String sslSubject) {
        this.sslSubject = sslSubject;
    }

    public String getSslCa() {
        return sslCa;
    }

    public void setSslCa(String sslCa) {
        this.sslCa = sslCa;
    }

    public String getSessionSplit() {
        return sessionSplit;
    }

    public void setSessionSplit(String sessionSplit) {
        this.sessionSplit = sessionSplit;
    }

    public String getIsSelfSigned() {
        return isSelfSigned;
    }

    public void setIsSelfSigned(String isSelfSigned) {
        this.isSelfSigned = isSelfSigned;
    }

    @Override
    public String toString() {
        return "NetworkEvent{" +
                "eventId='" + eventId + '\'' +
                ", dateTime=" + dateTime +
                ", srcMachineEntity=" + srcMachineEntity +
                ", dstMachineEntity=" + dstMachineEntity +
                ", destinationOrganization='" + destinationOrganization + '\'' +
                ", destinationASN=" + destinationASN +
                ", numOfBytesSent=" + numOfBytesSent +
                ", numOfBytesReceived=" + numOfBytesReceived +
                ", sourceNetname='" + sourceNetname + '\'' +
                ", destinationNetname='" + destinationNetname + '\'' +
                ", ja3='" + ja3 + '\'' +
                ", ja3s='" + ja3s + '\'' +
                ", direction=" + direction +
                ", destinationPort=" + destinationPort +
                ", dataSource='" + dataSource + '\'' +
                ", srcLocation=" + srcLocation +
                ", dstLocation=" + dstLocation +
                ", sslSubject='" + sslSubject + '\'' +
                ", sslCa='" + sslCa + '\'' +
                ", sessionSplit='" + sessionSplit + '\'' +
                ", isSelfSigned='" + isSelfSigned + '\'' +
                '}';
    }
}
