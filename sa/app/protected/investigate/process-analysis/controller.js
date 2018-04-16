import Controller from '@ember/controller';

export default Controller.extend({

  queryParams: [ 'serviceId', 'agentId', 'startTime', 'endTime', 'processName', 'checksum'],

  serviceId: null,

  startTime: null,

  endTime: null,

  processName: null,

  checksum: null
});
