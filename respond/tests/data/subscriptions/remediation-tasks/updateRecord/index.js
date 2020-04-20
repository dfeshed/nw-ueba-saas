import unfilteredData from '../query/data';

/**
 * Simple, targeted function to determine whether an item matches a particular incident filter. Used to simulate filtering via
 * the filter controls panel
 * @param item
 * @param filters
 * @public
 * @returns {boolean}
 */

export default {
  delay: 1,
  subscriptionDestination: '/user/queue/remediation/tasks/update',
  requestDestination: '/ws/respond/remediation/tasks/update',

  message(frame) {
    const requestBody = JSON.parse(frame.body);
    const { entityIds, updates } = requestBody;
    const data = [];

    unfilteredData.forEach((item) => {
      if (item.id === entityIds[0]) {
        item = { ...item, ...updates };
      }
      data.push(item);
    });

    return {
      data,
      meta: {
        total: 1099
      }
    };
  }
};
