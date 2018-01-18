
export default {
  subscriptionDestination: '/user/queue/notifications/update',
  requestDestination: '/ws/respond/notifications/update',
  message(frame) {
    const { body } = frame;
    const bodyParsed = JSON.parse(body);
    const { selectedEmailServer, socManagers, notificationSettings } = bodyParsed;
    return { selectedEmailServer, socManagers, notificationSettings };
  }
};
