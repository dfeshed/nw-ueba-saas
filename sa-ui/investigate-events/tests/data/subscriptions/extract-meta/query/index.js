let freeJobId = 0;

export default {
  subscriptionDestination: '/user/queue/investigate/extract/meta',
  requestDestination: '/ws/investigate/extract/meta',
  message(frame, helpers) {
    const jobId = freeJobId++;

    setTimeout(function() {
      if (typeof helpers.sendNotificationMessage === 'function') {
        // after a wait, notify that requested file is ready for download
        // use the sample file in vendor, but create a unique URL for each job so we can test multiple times
        const now = Number(new Date());
        helpers.sendNotificationMessage({
          link: `/data/meta-extract-job.csv?datetime=${now}`
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
