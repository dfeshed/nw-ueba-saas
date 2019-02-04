import groups from '../fetchGroups/data';

export default {
  subscriptionDestination: '/user/queue/usm/group/get',
  requestDestination: '/ws/usm/group/get',
  message(frame) {
    const body = JSON.parse(frame.body);
    let group = {};
    let returnCode = -1; // use the real error code if we ever add one for "Group does NOT exist"
    for (let index = 0; index < groups.length; index++) {
      if (groups[index].id === body.data) {
        group = groups[index];
        returnCode = 0;
        break;
      }
    }
    return {
      code: returnCode, // 0 == Ok, anything else == Error
      data: group // change this to error info if we ever add anything for "Group does NOT exist"
    };
  }
};
