let freeJobId = 0;

export default {
  subscriptionDestination: '/user/queue/investigate/extract/file',
  requestDestination: '/ws/investigate/extract/file',
  message(frame, helpers) {
    const jobId = freeJobId++;

    setTimeout(function() {
      if (typeof helpers.sendNotificationMessage === 'function') {
        helpers.sendNotificationMessage({
          link: `http://www.google.com/#q=rsa-job-${jobId}`
        });
      }
    }, 1000);

    return {
      meta: {
        complete: true
      },
      data: {
        jobId
      }
    };
  }
};