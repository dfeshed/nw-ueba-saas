import dataEdr from './data';
import dataWindow from './dataWindow';
import dataFile from './dataFile';

export default {
  subscriptionDestination: '/user/queue/usm/group/rank/effective-policy',
  requestDestination: '/ws/usm/group/rank/effective-policy',
  message(frame) {
    const body = JSON.parse(frame.body);
    let data = dataEdr;
    const { policyType, groupIds } = body.data;
    if (policyType == 'windowsLogPolicy') {
      // no groupIds returns default windows log policy - else return windows log policy with all settings
      data = (groupIds.length === 0) ? dataWindow[0] : dataWindow[1];
    } else if (policyType == 'filePolicy') {
      // no groupIds returns default file policy - else return file policy with all settings
      data = (groupIds.length === 0) ? dataFile[0] : dataFile[1];
    }
    return {
      code: 0,
      data
    };
  }
};
