package com.rsa.netwitness.presidio.automation.domain.file;


import com.google.gson.annotations.Expose;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "enriched_file")
public class FileEnrichStoredData  {

    @Id
    private String id;

    @Indexed
    @Expose
    @Field("userId")
    private String userId;

    @Expose
    @Field("absoluteSrcFilePath")
    private String absoluteSrcFilePath;

    @Expose
    @Field("absoluteDstFilePath")
    private String absoluteDstFilePath;

    @Expose
    @Field("absoluteSrcFolderFilePath")
    private String absoluteSrcFolderFilePath;

    @Expose
    @Field("absoluteDstFolderFilePath")
    private String absoluteDstFolderFilePath;

    @Expose
    @Field("fileSize")
    private Long fileSize;

    @Expose
    @Field("operationType")
    private String operationType;

    @Expose
    @Field("isSrcDriveShared")
    private Boolean isSrcDriveShared;

    @Expose
    @Field("isDstDriveShared")
    private Boolean isDstDriveShared;

    @Expose
    @Field("result")
    private String result;

    @Expose
    @Field("operationTypeCategories")
    private String[] operationTypeCategories;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNormalizedUsername() {
        return userId;
    }

    public void setNormalizedUsername(String userId) {
        this.userId = userId;
    }

    public String getAbsoluteSrcFilePath() {
        return absoluteSrcFilePath;
    }

    public void setAbsoluteSrcFilePath(String absoluteSrcFilePath) {
        this.absoluteSrcFilePath = absoluteSrcFilePath;
    }

    public String getAbsoluteDstFilePath() {
        return absoluteDstFilePath;
    }

    public void setAbsoluteDstFilePath(String absoluteDstFilePath) {
        this.absoluteDstFilePath = absoluteDstFilePath;
    }

    public String getAbsoluteSrcFolderFilePath() {
        return absoluteSrcFolderFilePath;
    }

    public void setAbsoluteSrcFolderFilePath(String absoluteSrcFolderFilePath) {
        this.absoluteSrcFolderFilePath = absoluteSrcFolderFilePath;
    }

    public String getAbsoluteDstFolderFilePath() {
        return absoluteDstFolderFilePath;
    }

    public void setAbsoluteDstFolderFilePath(String absoluteDstFolderFilePath) {
        this.absoluteDstFolderFilePath = absoluteDstFolderFilePath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public Boolean getSrcDriveShared() {
        return isSrcDriveShared;
    }

    public void setSrcDriveShared(Boolean srcDriveShared) {
        isSrcDriveShared = srcDriveShared;
    }

    public Boolean getDstDriveShared() {
        return isDstDriveShared;
    }

    public void setDstDriveShared(Boolean dstDriveShared) {
        isDstDriveShared = dstDriveShared;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String[] getOperationTypeCategories() {
        return operationTypeCategories;
    }

    public void setOperationTypeCategories(String[] operationTypeCategories) {
        this.operationTypeCategories = operationTypeCategories;
    }

    @Override
    public String toString() {
        return null;
    }

}
