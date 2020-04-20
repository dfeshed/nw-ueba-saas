import profiles from '..';

export default {
  subscriptionDestination: '/user/queue/investigate/profile/remove',
  requestDestination: '/ws/investigate/profile/remove',
  message(frame) {
    const body = JSON.parse(frame.body);
    const { profileRequest } = body;
    const { id } = profileRequest;

    // Remove from profile cache
    const itemToRemove = profiles.find((d) => d.id === id);
    const index = profiles.indexOf(itemToRemove);
    const [ removedProfile ] = profiles.splice(index, 1);

    return {
      data: removedProfile,
      request: {
        id: body.id,
        profileRequest
      }
    };
  }
};
