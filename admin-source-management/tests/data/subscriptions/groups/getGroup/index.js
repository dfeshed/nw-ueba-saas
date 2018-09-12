import groups from '../findAll/data';

export default {
  subscriptionDestination: '/user/queue/usm/group/get',
  requestDestination: '/ws/usm/group/get',
  message(frame) {
    const body = JSON.parse(frame.body);
    let group = {};
    for (let index = 0; index < groups.length; index++) {
      if (groups[index].id === body.data) {
        group = groups[index];
        break;
      }
    }
    return {
      code: 0,
      data: group
    };
  }
};
