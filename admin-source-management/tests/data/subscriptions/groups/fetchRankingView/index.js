import dataEdr from './data';
import dataWindow from './dataWindow';

export default {
  subscriptionDestination: '/user/queue/usm/group/rank/effective-policy',
  requestDestination: '/ws/usm/group/rank/effective-policy',
  message(frame) {
    const body = JSON.parse(frame.body);
    let data = dataEdr;
    const sourceType = body.data.policyType;
    if (sourceType == 'windowsLogPolicy') {
      data = dataWindow;
    }
    return {
      code: 0,
      data
    };
  }
};

