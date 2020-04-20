import unfilteredData from './data';

export default {
  subscriptionDestination: '/user/queue/incidents',
  requestDestination: '/ws/respond/incidents',

  message() {
    return {
      data: unfilteredData,
      meta: {
        total: 41,
        complete: true
      }
    };
  }
};
