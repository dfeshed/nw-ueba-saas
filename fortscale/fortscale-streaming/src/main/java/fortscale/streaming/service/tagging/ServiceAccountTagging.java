package fortscale.streaming.service.tagging;

import fortscale.streaming.model.tagging.AccountMachineAccess;

/**
 * Created by idanp on 7/7/2014.
 * This is interface for tagging process
 */
public interface ServiceAccountTagging {

    public void tag(AccountMachineAccess account);
}
