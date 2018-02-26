export default {
  subscriptionDestination: '/user/queue/endpoint/machine/export',
  requestDestination: '/ws/endpoint/machine/export',
  message(/* frame */) {
    const now = Number(new Date());
    const id = `1/FilesExported.zip?datetime=${now}`;
    return {
      meta: {
        complete: true
      },
      data: {
        statusCode: 'OK',
        id
      }
    };
  }
};
