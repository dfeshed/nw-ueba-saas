import data from './data';

export default {
  subscriptionDestination: '/user/queue/administration/context/lookup',
  requestDestination: '/ws/administration/context/lookup',
  cancelDestination: '/ws/administration/context/cancel',
  message(frame) {
    // get the metaType out of the request
    const filters = JSON.parse(frame.body).filter;
    const metaType = filters.find((f) => f.field === 'meta').value;
    return {
      data: data[metaType] || data.IP,  // use IP as default
      meta: {
        complete: true
      }
    };
  }
};