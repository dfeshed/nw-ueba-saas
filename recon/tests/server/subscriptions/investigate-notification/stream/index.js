export default {
  subscriptionDestination: '/user/queue/investigate/notification',
  requestDestination: '/ws/investigate/notification',
  page(frame, sendMessage, helpers) {

    // No immediate response is expected. Instead, the responses should be sent as jobs get done (e.g., a job
    // to extract file contents).  Therefore here we simply define a method on the server app that can be used
    // elsewhere, by job request handlers, to send notifications as jobs get done.
    helpers.sendNotificationMessage = ({ link }) => {
      sendMessage({
        data: { link }
      });
    };
  }
};