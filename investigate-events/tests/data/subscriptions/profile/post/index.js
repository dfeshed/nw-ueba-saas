import profiles from '..';

export default {
  delay: 100,
  subscriptionDestination: '/user/queue/investigate/profile/set',
  requestDestination: '/ws/investigate/profile/set',
  message(frame) {
    const body = JSON.parse(frame.body);
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

    const editingExistingProfile = profiles.find((item) => item.id === toReturn.data.id);
    // editing existing profile
    if (editingExistingProfile) {
      const groupIndex = profiles.indexOf((item) => item.id === toReturn.data.id);
      profiles.splice(groupIndex, 1);
    }
    profiles.push(toReturn.data);
    return toReturn;
  }
};
