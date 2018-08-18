import data from './data';
import ueba from './ueba';

const len = data.length;

let counter = 0;

export default {
  subscriptionDestination: '/user/queue/alerts/events',
  requestDestination: '/ws/respond/alerts/events',
  message(frame) {
    // const end = Math.max(1, Math.round(Math.random() * len));
    const body = JSON.parse(frame.body);
    const filters = body && body.filter || [];
    const uebaAlertId = '586ecfc0ecd25950034e1318';
    if (filters.length > 0 && filters[0].value === uebaAlertId) {
      return {
        data: ueba
      };
    } else {
      const end = counter % len;
      counter++;
      return {
        data: data.slice(0, end + 1)
      };
    }
  }
};
