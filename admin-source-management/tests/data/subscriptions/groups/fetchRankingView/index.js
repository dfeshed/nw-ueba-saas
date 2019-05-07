import dataEdr from './data';
import dataWindow from './dataWindow';
import dataFile from './dataFile';

export default {
  subscriptionDestination: '/user/queue/usm/group/rank/effective-policy',
  requestDestination: '/ws/usm/group/rank/effective-policy',
  message(frame) {
    const body = JSON.parse(frame.body);
    let data = dataEdr;
    const sourceType = body.data.policyType;
    if (sourceType == 'windowsLogPolicy') {
      data = dataWindow;
    } else if (sourceType == 'filePolicy') {
      data = dataFile;
    }
    return {
      code: 0,
      data
    };
  }
};

