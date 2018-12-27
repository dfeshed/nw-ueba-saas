import { success, failure } from 'investigate-shared/utils/flash-messages';
import { stopScan, startScan } from 'investigate-hosts/actions/data-creators/host';

export const stopScanCommand = (agentIds) => {
  const callBackOptions = {
    onSuccess: () => success('investigateHosts.hosts.cancelScan.success'),
    onFailure: (message) => failure(message)
  };
  stopScan(agentIds, callBackOptions);
};


export const startScanCommand = (agentIds) => {
  const callBackOptions = {
    onSuccess: () => success('investigateHosts.hosts.initiateScan.success'),
    onFailure: (message) => failure(message)
  };
  startScan(agentIds, callBackOptions);
};
