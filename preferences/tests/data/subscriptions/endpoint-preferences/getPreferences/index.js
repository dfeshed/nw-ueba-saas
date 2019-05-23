export default {
  subscriptionDestination: '/user/queue/endpoint/preferences/get',
  requestDestination: '/ws/endpoint/preferences/get',
  count: 0,
  delay: 1,
  message(/* frame */) {
    const data = {
      filePreference: {
        columnConfig: [{
          tableId: 'files',
          columns: [
            {
              field: 'firstSeenTime',
              width: '7vw',
              displayIndex: 3
            },
            {
              field: 'reputationStatus',
              width: '10vw',
              displayIndex: 4
            },
            {
              field: 'size',
              width: '3vw',
              displayIndex: 5
            },
            {
              field: 'signature.features',
              width: '8vw',
              displayIndex: 6
            },
            {
              field: 'pe.resources.company',
              width: '6vw',
              displayIndex: 7
            },
            {
              field: 'fileStatus',
              width: '10vw',
              displayIndex: 8
            },
            {
              field: 'remediationAction',
              width: '10vw',
              displayIndex: 9
            },
            {
              field: 'downloadInfo.status',
              width: '10vw',
              displayIndex: 10
            },
            {
              field: 'machineOsType',
              width: 'vw',
              displayIndex: 11
            }
          ]
        }],
        sortField: '{ "sortField": "size", "isSortDescending": false }'
      },
      machinePreference: {
        columnConfig: [{
          tableId: 'hosts',
          columns: [
            {
              field: 'machineIdentity.machineOsType',
              width: '7vw',
              displayIndex: 3
            },
            {
              field: 'machine.scanStartTime',
              width: '9vw',
              displayIndex: 4
            },
            {
              field: 'machine.users.name',
              width: '15vw',
              displayIndex: 5
            },
            {
              field: 'agentStatus.lastSeenTime',
              width: '8vw',
              displayIndex: 6

            },
            {
              field: 'agentStatus.scanStatus',
              width: '8vw',
              displayIndex: 7
            },
            {
              field: 'groupPolicy.groups.name',
              width: '10vw',
              displayIndex: 8
            },
            {
              field: 'machineIdentity.networkInterfaces.ipv4',
              width: '5vw',
              displayIndex: 9
            },
            {
              field: 'groupPolicy.policyStatus',
              width: '6vw',
              displayIndex: 10
            },
            {
              field: 'machineIdentity.agentMode',
              width: '5vw',
              displayIndex: 11
            },
            {
              field: 'machineIdentity.agentVersion',
              width: '6vw',
              displayIndex: 12
            },
            {
              field: 'machineIdentity.agent.driverErrorCode',
              width: '10vw',
              displayIndex: 13
            }
          ]
        }
        ],
        sortField: '{ "key": "score", "descending": true }'
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
