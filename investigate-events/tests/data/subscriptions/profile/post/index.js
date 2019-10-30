import profiles from '..';

export default {
  delay: 100,
  subscriptionDestination: '/user/queue/investigate/profile/set',
  requestDestination: '/ws/investigate/profile/set',
  message(frame) {
    const body = JSON.parse(frame.body);

    // Save profile cache
    profiles.push(body.profileRequest);

    const toReturn = {
      data: {
        ...body.profileRequest,
        contentType: 'USER'
      }
    };

    // add id if new profile
    // existing profile edited will have id
    if (!toReturn.data.id) {
      const num = Date.now();
      toReturn.data.id = num;
    }
    return toReturn;
  }
};
