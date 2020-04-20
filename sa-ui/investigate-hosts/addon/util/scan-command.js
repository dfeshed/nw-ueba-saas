import { success, failure } from 'investigate-shared/utils/flash-messages';
import { stopScan, startScan } from 'investigate-hosts/actions/data-creators/host';

export const stopScanCommand = (agentIds, serverId) => {
  const callBackOptions = {
    onSuccess: () => success('investigateHosts.hosts.cancelScan.success'),
    onFailure: (message) => failure(message, null, false)
  };
  stopScan(agentIds, callBackOptions, serverId);
};


export const startScanCommand = (agentIds, serverId) => {
  const callBackOptions = {
    onSuccess: () => success('investigateHosts.hosts.initiateScan.success'),
    onFailure: (message) => failure(message, null, false)
  };
  startScan(agentIds, callBackOptions, serverId);
};
