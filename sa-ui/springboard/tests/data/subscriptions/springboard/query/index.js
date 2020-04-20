import { getHostData } from './data';

export default {
  subscriptionDestination: '/user/queue/springboard/widget/query',
  requestDestination: '/ws/springboard/widget/query',
  message(frame) {
    const { body } = frame;
    const bodyParsed = JSON.parse(body);
    const { leadType, aggregate, size = 25 } = bodyParsed;
    let data = [];
    switch (leadType) {
      case 'hosts':
        data = getHostData(aggregate, null, size);
        break;
      case 'files':
        break;
      case 'alert':
        break;
    }

    return {
      data
    };
  }
};


