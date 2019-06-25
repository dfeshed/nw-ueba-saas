package presidio.data.domain.event.network;

import presidio.data.domain.Location;
import presidio.data.domain.event.Event;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

public class NetworkEvent extends Event implements Serializable {
    private static final long serialVersionUID = 1L;

    private String eventId;
    private Instant dateTime;
    private List<String> fqdn;
    private String sourceIp;
    private String dstIp;

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
    private int sourcePort;
    private String dataSource;

    private Location srcLocation;
    private Location dstLocation;

    private String sslSubject;
    private String sslCa;
    private int sessionSplit;
    private boolean isSelfSigned;

    public NetworkEvent(Instant dateTime) {
        this.dateTime = dateTime;
    }

    public NetworkEvent(String eventId, Instant dateTime, List<String> fqdn, String sourceIp, String dstIp,
                        String destinationOrganization, String destinationASN, long numOfBytesSent, long numOfBytesReceived,
                        String sourceNetname, String destinationNetname, String ja3, String ja3s, NETWORK_DIRECTION_TYPE direction,
                        int destinationPort, int sourcePort, String dataSource, Location srcLocation,
                        Location dstLocation, String sslSubject, String sslCa, int sessionSplit, boolean isSelfSigned) {
        this.eventId = eventId;
        this.dateTime = dateTime;
        this.fqdn = fqdn;
        this.sourceIp = sourceIp;
        this.dstIp = dstIp;
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
        this.sourcePort = sourcePort;
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

    public List<String> getFqdn() {
        return fqdn;
    }

    public void setFqdn(List<String> fqdn) {
        this.fqdn = fqdn;
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

    public int getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort(int sourcePort) {
        this.sourcePort = sourcePort;
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

    public int getSessionSplit() {
        return sessionSplit;
    }

    public void setSessionSplit(int sessionSplit) {
        this.sessionSplit = sessionSplit;
    }

    public boolean getIsSelfSigned() {
        return isSelfSigned;
    }

    public void setIsSelfSigned(boolean isSelfSigned) {
        this.isSelfSigned = isSelfSigned;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    public String getDstIp() {
        return dstIp;
    }

    public void setDstIp(String dstIp) {
        this.dstIp = dstIp;
    }

    @Override
    public String toString() {
        return "NetworkEvent{" +
                "eventId='" + eventId + '\'' +
                ", dateTime=" + dateTime +
                ", fqdn=" + fqdn +
                ", sourceIp='" + sourceIp + '\'' +
                ", dstIp='" + dstIp + '\'' +
                ", destinationOrganization='" + destinationOrganization + '\'' +
                ", destinationASN='" + destinationASN + '\'' +
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
                ", sessionSplit=" + sessionSplit +
                ", isSelfSigned=" + isSelfSigned +
                '}';
    }
}
