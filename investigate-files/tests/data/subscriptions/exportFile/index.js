export default {
  subscriptionDestination: '/user/queue/endpoint/file/property/download',
  requestDestination: '/ws/endpoint/file/property/download',
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
