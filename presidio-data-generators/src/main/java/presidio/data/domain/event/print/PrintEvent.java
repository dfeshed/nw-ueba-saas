package presidio.data.domain.event.print;

import presidio.data.domain.MachineEntity;
import presidio.data.domain.User;
import presidio.data.domain.event.Event;

import java.io.Serializable;
import java.time.Instant;

public class PrintEvent extends Event implements Serializable {
    private static final long serialVersionUID = 1L;

    private Instant dateTime;
    private String eventId;
    private String dataSource;
    private User user;                       //(userId, userName, userDisplayName)
    private PrintFileOperation printLogOperation; //(OperationType(operationType, operationTypeCategory), result, resultCode, FileEntity(srcFilePath, fileSize, isSrcDriveShared))
    private MachineEntity srcMachineEntity;  //(srcMachineId, srcMachineName)
    private MachineEntity dstMachineEntity;  //(printerId, printerName)

    public PrintEvent(Instant dateTime, String eventId, String dataSource, User user, PrintFileOperation printLogOperation, MachineEntity srcMachineEntity, MachineEntity dstMachineEntity) {
        this.dateTime = dateTime;
        this.eventId = eventId;
        this.dataSource = dataSource;
        this.user = user;
        this.printLogOperation = printLogOperation;
        this.srcMachineEntity = srcMachineEntity;
        this.dstMachineEntity = dstMachineEntity;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public void setDateTime(Instant dateTime) {
        this.dateTime = dateTime;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public PrintFileOperation getPrintLogOperation() {
        return printLogOperation;
    }

    public void setPrintLogOperation(PrintFileOperation printLogOperation) {
        this.printLogOperation = printLogOperation;
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

    @Override
    public String toString() {
        return "PrintEvent{" +
                "dateTime=" + dateTime +
                ", eventId='" + eventId + '\'' +
                ", dataSource='" + dataSource + '\'' +
                ", user=" + user +
                ", printLogOperation=" + printLogOperation +
                ", srcMachineEntity=" + srcMachineEntity +
                ", dstMachineEntity=" + dstMachineEntity +
                '}';
    }

    @Override
    public Instant getDateTime() { return dateTime; }
}
