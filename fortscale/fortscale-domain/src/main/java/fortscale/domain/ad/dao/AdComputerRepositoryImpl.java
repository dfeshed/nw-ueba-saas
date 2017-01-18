package fortscale.domain.ad.dao;


import fortscale.domain.ad.AdComputer;

public class AdComputerRepositoryImpl extends AdObjectRepository implements AdComputerRepositoryCustom{
    @Override
    public String getCollectionName() {
        return AdComputer.COLLECTION_NAME;
    }
}
