package fortscale.domain.ad.dao;


import fortscale.domain.ad.AdOU;

public class AdOURepositoryImpl extends AdObjectRepository implements AdOURepositoryCustom{
    @Override
    public String getCollectionName() {
        return AdOU.COLLECTION_NAME;
    }
}
