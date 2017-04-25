import data from './data';

export default {
  subscriptionDestination: '/user/queue/administration/context/lookup',
  requestDestination: '/ws/administration/context/lookup',
  cancelDestination: '/ws/administration/context/cancel',
  page(frame, sendMessage) {
    const filters = JSON.parse(frame.body).filter;
    if (filters) {
      const metaType = filters.find((f) => f.field === 'meta').value;
      const dataForMeta = data[metaType] || data.IP;
      dataForMeta.forEach((dataObj, index) => {
      // one sec delay
        setTimeout(() => {
          sendMessage({
            data: [dataObj]
          });
        }, 1000 * (index + 1));
      });
    }
  }
};