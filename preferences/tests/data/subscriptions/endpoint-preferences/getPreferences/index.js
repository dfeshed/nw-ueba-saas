export default {
  subscriptionDestination: '/user/queue/endpoint/preferences/get',
  requestDestination: '/ws/endpoint/preferences/get',
  count: 0,
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
        sortField: 'firstSeenTime',
        filter: 'Linux'
      },
      machinePreference: {
        visibleColumns: [
          'machine.machineOsType',
          'machine.machineName',
          'machine.scanStartTime',
          'machine.users.name',
          'agentStatus.lastSeenTime',
          'agentStatus.scanStatus'
        ],
        sortField: 'machine.scanStartTime'
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
