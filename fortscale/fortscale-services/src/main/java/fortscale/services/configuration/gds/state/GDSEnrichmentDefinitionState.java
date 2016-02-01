package fortscale.services.configuration.gds.state;

import java.util.ArrayList;
import java.util.List;

/**
 * Enrichment definition state
 *
 * @author gils
 * 31/12/2015
 */
public class GDSEnrichmentDefinitionState implements GDSConfigurationState{

    private List<UserNormalizationState> userNormalizationStates = new ArrayList<>();
    private List<IPResolvingState> ipResolvingStates = new ArrayList<>();
    private ComputerTaggingState computerTaggingState = new ComputerTaggingState();
    private List<GeoLocationState> geoLocationStates = new ArrayList<>();
    private UserMongoUpdateState userMongoUpdateState = new UserMongoUpdateState();
    private HDFSWriterState hdfsWriterEnrichedState = new HDFSWriterState();

    private HDFSWriterState hdfsWriterScoreState = new HDFSWriterState();


    private HDFSWriterState hdfsWriterTopScoreState = new HDFSWriterState();

    public List<UserNormalizationState> getUserNormalizationStates() {
        return userNormalizationStates;
    }

    public List<IPResolvingState> getIpResolvingStates() {
        return ipResolvingStates;
    }

    public List<GeoLocationState> getGeoLocationStates() {
        return geoLocationStates;
    }

    public UserMongoUpdateState getUserMongoUpdateState() {
        return userMongoUpdateState;
    }

    public HDFSWriterState getHdfsWriterEnrichedState() {
        return hdfsWriterEnrichedState;
    }

    public HDFSWriterState getHdfsWriterScoreState() {
        return hdfsWriterScoreState;
    }

    public HDFSWriterState getHdfsWriterTopScoreState() {
        return hdfsWriterTopScoreState;
    }

    public void setHdfsWriterTopScoreState(HDFSWriterState hdfsWriterTopScoreState) {
        this.hdfsWriterTopScoreState = hdfsWriterTopScoreState;
    }

    public void setHdfsWriterScoreState(HDFSWriterState hdfsWriterScoreState) {
        this.hdfsWriterScoreState = hdfsWriterScoreState;
    }


    public ComputerTaggingState getComputerTaggingState() {
        return computerTaggingState;
    }



    public static class UserNormalizationState extends GDSStreamingTaskState {
        private String normalizationBasedField;
        private String domainField;
        private String domainValue;
        private String normalizedUserNameField;
        private String normalizeServiceName;
        private String updateOnly;

        public String getNormalizationBasedField() {
            return normalizationBasedField;
        }

        public void setNormalizationBasedField(String normalizationBasedField) {
            this.normalizationBasedField = normalizationBasedField;
        }

        public String getDomainField() {
            return domainField;
        }

        public void setDomainField(String domainField) {
            this.domainField = domainField;
        }

        public String getDomainValue() {
            return domainValue;
        }

        public void setDomainValue(String domainValue) {
            this.domainValue = domainValue;
        }

        public String getNormalizedUserNameField() {
            return normalizedUserNameField;
        }

        public void setNormalizedUserNameField(String normalizedUserNameField) {
            this.normalizedUserNameField = normalizedUserNameField;
        }

        public String getNormalizeServiceName() {
            return normalizeServiceName;
        }

        public void setNormalizeServiceName(String normalizeServiceName) {
            this.normalizeServiceName = normalizeServiceName;
        }

        public String getUpdateOnly() {
            return updateOnly;
        }

        public void setUpdateOnly(String updateOnly) {
            this.updateOnly = updateOnly;
        }

        @Override
        public void reset() {
            super.reset();
            normalizationBasedField = null;
            domainField = null;
            domainValue = null;
            normalizedUserNameField = null;
            normalizeServiceName = null;
            updateOnly = null;
        }
    }

    public static class IPResolvingState extends GDSStreamingTaskState{

        private boolean restrictToAD;
        private boolean shortNameUsage;
        private boolean dropOnFailUsage;
        private boolean overrideIpWithHostNameUsage;
        private String ipField;
        private String hostField;
        private boolean removeLastDotUsage;

        public boolean isRemoveLastDotUsage() {
            return removeLastDotUsage;
        }

        public void setRemoveLastDotUsage(boolean removeLastDotUsage) {
            this.removeLastDotUsage = removeLastDotUsage;
        }

        public boolean isRestrictToAD() {
            return restrictToAD;
        }

        public void setRestrictToAD(boolean restrictToAD) {
            this.restrictToAD = restrictToAD;
        }

        public boolean isShortNameUsage() {
            return shortNameUsage;
        }

        public void setShortNameUsage(boolean shortNameUsage) {
            this.shortNameUsage = shortNameUsage;
        }

        public boolean isDropOnFailUsage() {
            return dropOnFailUsage;
        }

        public void setDropOnFailUsage(boolean dropOnFailUsage) {
            this.dropOnFailUsage = dropOnFailUsage;
        }

        public boolean isOverrideIpWithHostNameUsage() {
            return overrideIpWithHostNameUsage;
        }

        public void setOverrideIpWithHostNameUsage(boolean overrideIpWithHostNameUsage) {
            this.overrideIpWithHostNameUsage = overrideIpWithHostNameUsage;
        }

        public String getIpField() {
            return ipField;
        }

        public void setIpField(String ipField) {
            this.ipField = ipField;
        }

        public String getHostField() {
            return hostField;
        }

        public void setHostField(String hostField) {
            this.hostField = hostField;
        }

        @Override
        public void reset() {
            super.reset();
            restrictToAD = false;
            shortNameUsage = false;
            dropOnFailUsage = false;
            overrideIpWithHostNameUsage = false;
            ipField = null;
            hostField = null;
        }
    }

    public static class ComputerTaggingState extends GDSStreamingTaskState{

        private String sourceHost;
        private String targetHost;
        private String srcMachineClassifier;
        private String srcClusteringField;
        private boolean createNewComputerFlag;
        private String dstMachineClassifier;
        private String dstClusteringField;

        public String getSourceHost() {
            return sourceHost;
        }

        public void setSourceHost(String sourceHost) {
            this.sourceHost = sourceHost;
        }

        public String getTargetHost() {
            return targetHost;
        }

        public void setTargetHost(String targetHost) {
            this.targetHost = targetHost;
        }

        public String getSrcMachineClassifier() {
            return srcMachineClassifier;
        }

        public void setSrcMachineClassifier(String srcMachineClassifier) {
            this.srcMachineClassifier = srcMachineClassifier;
        }

        public String getSrcClusteringField() {
            return srcClusteringField;
        }

        public void setSrcClusteringField(String srcClusteringField) {
            this.srcClusteringField = srcClusteringField;
        }

        public boolean isCreateNewComputerFlag() {
            return createNewComputerFlag;
        }

        public void setCreateNewComputerFlag(boolean createNewComputerFlag) {
            this.createNewComputerFlag = createNewComputerFlag;
        }

        public String getDstMachineClassifier() {
            return dstMachineClassifier;
        }

        public void setDstMachineClassifier(String dstMachineClassifier) {
            this.dstMachineClassifier = dstMachineClassifier;
        }

        public String getDstClusteringField() {
            return dstClusteringField;
        }

        public void setDstClusteringField(String dstClusteringField) {
            this.dstClusteringField = dstClusteringField;
        }

        @Override
        public void reset() {
            super.reset();
            sourceHost = null;
            targetHost = null;
            srcMachineClassifier = null;
            srcClusteringField = null;
            createNewComputerFlag = false;
            dstMachineClassifier = null;
            dstClusteringField = null;
        }
    }

    public static class GeoLocationState extends GDSStreamingTaskState{
        private String ipField;
        private String countryField;
        private String longitudeField;
        private String latitudeField;
        private String countryIsoCodeField;
        private String regionField;
        private String cityField;
        private String ispField;
        private String usageTypeField;
        private boolean doSessionUpdateFlag;
        private boolean doDataBuckets;
        private boolean doGeoLocation;

        public String getIpField() {
            return ipField;
        }

        public void setIpField(String ipField) {
            this.ipField = ipField;
        }

        public String getCountryField() {
            return countryField;
        }

        public void setCountryField(String countryField) {
            this.countryField = countryField;
        }

        public String getLongitudeField() {
            return longitudeField;
        }

        public void setLongitudeField(String longitudeField) {
            this.longitudeField = longitudeField;
        }

        public String getLatitudeField() {
            return latitudeField;
        }

        public void setLatitudeField(String latitudeField) {
            this.latitudeField = latitudeField;
        }

        public String getCountryIsoCodeField() {
            return countryIsoCodeField;
        }

        public void setCountryIsoCodeField(String countryIsoCodeField) {
            this.countryIsoCodeField = countryIsoCodeField;
        }

        public String getRegionField() {
            return regionField;
        }

        public void setRegionField(String regionField) {
            this.regionField = regionField;
        }

        public String getCityField() {
            return cityField;
        }

        public void setCityField(String cityField) {
            this.cityField = cityField;
        }

        public String getIspField() {
            return ispField;
        }

        public void setIspField(String ispField) {
            this.ispField = ispField;
        }

        public String getUsageTypeField() {
            return usageTypeField;
        }

        public void setUsageTypeField(String usageTypeField) {
            this.usageTypeField = usageTypeField;
        }

        public boolean isDoSessionUpdateFlag() {
            return doSessionUpdateFlag;
        }

        public void setDoSessionUpdateFlag(boolean doSessionUpdateFlag) {
            this.doSessionUpdateFlag = doSessionUpdateFlag;
        }

        public boolean isDoDataBuckets() {
            return doDataBuckets;
        }

        public void setDoDataBuckets(boolean doDataBuckets) {
            this.doDataBuckets = doDataBuckets;
        }

        public boolean isDoGeoLocation() {
            return doGeoLocation;
        }

        public void setDoGeoLocation(boolean doGeoLocation) {
            this.doGeoLocation = doGeoLocation;
        }

        @Override
        public void reset() {
            super.reset();
            ipField = null;
            countryField= null;
            longitudeField= null;
            latitudeField= null;
            countryIsoCodeField= null;
            regionField= null;
            cityField= null;
            ispField= null;
            usageTypeField= null;
            doSessionUpdateFlag = false;
            doDataBuckets = false;
            doGeoLocation = false;
        }
    }

    public static class UserMongoUpdateState extends GDSStreamingTaskState{
        private boolean anyRow;
        private String statusFieldName;
        private String successValue;

        public boolean isAnyRow() {
            return anyRow;
        }

        public void setAnyRow(boolean anyRow) {
            this.anyRow = anyRow;
        }

        public String getStatusFieldName() {
            return statusFieldName;
        }

        public void setStatusFieldName(String statusFieldName) {
            this.statusFieldName = statusFieldName;
        }

        public String getSuccessValue() {
            return successValue;
        }

        public void setSuccessValue(String successValue) {
            this.successValue = successValue;
        }

        @Override
        public void reset() {
            super.reset();
            anyRow = false;
            statusFieldName = null;
            successValue = null;
        }
    }

    public static class HDFSWriterState extends GDSStreamingTaskState{
        private String fieldList;
        private String delimiter;
        private String hdfsPath;
        private String fileName;
        private String tableName;
        private String partitionStrategy;
        private String discriminatorsFields;
        private String levelDBSuffix;

        public String getFieldList() {
            return fieldList;
        }

        public void setFieldList(String fieldList) {
            this.fieldList = fieldList;
        }

        public String getDelimiter() {
            return delimiter;
        }

        public void setDelimiter(String delimiter) {
            this.delimiter = delimiter;
        }

        public String getHdfsPath() {
            return hdfsPath;
        }

        public void setHdfsPath(String hdfsPath) {
            this.hdfsPath = hdfsPath;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getTableName() {
            return tableName;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

        public String getPartitionStrategy() {
            return partitionStrategy;
        }

        public void setPartitionStrategy(String partitionStrategy) {
            this.partitionStrategy = partitionStrategy;
        }

        public String getDiscriminatorsFields() {
            return discriminatorsFields;
        }

        public void setDiscriminatorsFields(String discriminatorsFields) {
            this.discriminatorsFields = discriminatorsFields;
        }

        public String getLevelDBSuffix() {
            return levelDBSuffix;
        }

        public void setLevelDBSuffix(String levelDBSuffix) {
            this.levelDBSuffix = levelDBSuffix;
        }


        @Override
        public void reset() {
            super.reset();
            fieldList = null;
            delimiter = null;
            hdfsPath = null;
            fileName = null;
            tableName = null;
            partitionStrategy = null;
            discriminatorsFields = null;
        }
    }


    @Override
    public void reset() {
        userNormalizationStates.clear();
        ipResolvingStates.clear();
        computerTaggingState.reset();
        geoLocationStates.clear();
        userMongoUpdateState.reset();
        hdfsWriterEnrichedState.reset();
    }
}
