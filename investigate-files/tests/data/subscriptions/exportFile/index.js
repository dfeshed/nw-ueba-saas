export default {
  subscriptionDestination: '/user/queue/endpoint/data/files/export',
  requestDestination: '/ws/endpoint/data/files/export',
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