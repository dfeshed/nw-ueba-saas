export default {
  subscriptionDestination: '/user/queue/administration/locales/get',
  requestDestination: '/ws/administration/locales/get',
  message() {
    return {
      code: 0,
      data: [
        { name: 'spanish_es.js', type: 'file', mtime: 'Fri, 30 Mar 2018 19:29:02 GMT', size: 288 },
        { name: 'german_de-DE.js', type: 'file', mtime: 'Fri, 30 Mar 2018 19:27:55 GMT', size: 289 }
      ]
    };
  }
};
