import { profiles } from '..';

export default {
  delay: 100,
  subscriptionDestination: '/user/queue/investigate/profile/set',
  requestDestination: '/ws/investigate/profile/set',
  message(frame) {
    const body = JSON.parse(frame.body);
    const num = Date.now();

    // Save off to profile cache
    profiles.push(body.profileRequest);

    return {
      data: {
        ...body.profileRequest,
        id: num,
        contentType: 'USER'
      }
    };
  }
};
