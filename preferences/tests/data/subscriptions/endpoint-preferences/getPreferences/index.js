export default {
  subscriptionDestination: '/user/queue/endpoint/preferences/get',
  requestDestination: '/ws/endpoint/preferences/get',
  count: 0,
  delay: 1,
  message(/* frame */) {
    const data = {
      filePreference: {
        visibleColumns: [
          'firstSeenTime',
          'firstFileName',
          'machineOsType',
          'size',
          'entropy',
          'checksumSha256',
          'pe.sectionNames'
        ],
        sortField: '{ "sortField": "size", "isSortDescending": false }'
      },
      machinePreference: {
        visibleColumns: [
          'machineIdentity.machineOsType',
          'machineIdentity.machineName',
          'machine.scanStartTime',
          'machine.users.name',
          'agentStatus.lastSeenTime',
          'agentStatus.scanStatus'
        ],
        sortField: '{ "key": "machineIdentity.machineOsType", "descending": true }'
      }
    };
    return {
      data,
      meta: {
        complete: true
      }
    };
  }
};
