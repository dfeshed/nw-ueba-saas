export default {
  serviceId: '63de7bb3-fcd3-415a-97bf-7639958cd5e6',
  serviceName: 'Node-X - Endpoint Server',
  usmRevision: 0,
  groups: [],
  policy: {
    edrPolicy: {
      name: 'Default EDR Policy',
      transportConfig: {
        primary: {
          address: '10.40.15.154',
          httpsPort: 443,
          httpsBeaconIntervalInSeconds: 900,
          udpPort: 444,
          udpBeaconIntervalInSeconds: 30
        }
      },
      agentMode: 'ADVANCED',
      scheduledScanConfig: {
        enabled: false,
        recurrentSchedule: {
          recurrence: {
            interval: 1,
            unit: 'DAYS'
          },
          runAtTime: '09:00:00',
          runOnDaysOfWeek: [1],
          scheduleStartDate: '2019-03-22'
        },
        scanOptions: {
          cpuMax: 25,
          cpuMaxVm: 10,
          scanMbr: false }
      },
      blockingConfig: {
        enabled: false
      },
      storageConfig: {
        diskCacheSizeInMb: 100
      },
      serverConfig: {
        requestScanOnRegistration: false
      }
    }
  },
  policyStatus: 'Updated',
  evaluatedTime: '2019-05-07T05:25:41.109+0000'
};