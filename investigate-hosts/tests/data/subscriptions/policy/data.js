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
          udpBeaconIntervalInSeconds: 30,
          rar: {
            servers: [{
              address: '11.22.33.44',
              httpsPort: '111',
              httpsBeaconIntervalInSeconds: 999999
            }]
          }
        }
      },
      agentMode: 'ADVANCED',
      scheduledScanConfig: {
        enabled: true,
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
    },

    windowsLogPolicy: {
      name: 'Default Windows Log Policy',
      enabled: true,
      sendTestLog: false,
      primaryDestination: '',
      secondaryDestination: '',
      protocol: 'TLS',
      channelFilters: [
        {
          channel: 'Security',
          eventId: '620,630,640',
          filterType: 'EXCLUDE'
        }
      ]
    },
    filePolicy: {
      name: 'Test File Policy',
      enabled: true,
      sendTestLog: false,
      primaryDestination: '',
      secondaryDestination: '',
      protocol: 'TLS',
      customConfig: '"enabled" : true,"sendTestLog" : false,"protocol" : "UDP","policyType" : "filePolicy","name" : "Test File Policy","description" : "Test File Policy Description."',
      sources: [
        {
          fileType: 'apache',
          enabled: false,
          startOfEvents: true,
          fileEncoding: 'utf-8',
          paths: ['/*foo/bar*/*.txt'],
          sourceName: 'testSource1',
          exclusionFilters: ['exclude-string-1'],
          typeSpec: {
            parserId: 'file.apache',
            processorType: 'generic',
            dataStartLine: '1',
            fieldDelim: '0x20'
          }
        },
        {
          fileType: 'apache',
          enabled: true,
          startOfEvents: true,
          fileEncoding: 'utf-8',
          paths: ['/*foo/bar*/*.txt'],
          sourceName: 'testSource2',
          exclusionFilters: ['exclude-string-1'],
          typeSpec: {
            parserId: 'file.apache',
            processorType: 'generic',
            dataStartLine: '1',
            fieldDelim: '0x20'
          }
        },
        {
          fileType: 'exchange',
          enabled: false,
          startOfEvents: true,
          fileEncoding: 'utf-8',
          paths: ['/*foo/bar*/*.txt'],
          sourceName: 'testSource3',
          exclusionFilters: ['exclude-string-1'],
          typeSpec: {
            parserId: 'file.exchange',
            processorType: 'generic',
            dataStartLine: '1',
            fieldDelim: '0x20'
          }
        }
      ]
    }
  },
  policyStatus: 'Updated',
  evaluatedTime: '2019-05-07T05:25:41.109+0000'
};
