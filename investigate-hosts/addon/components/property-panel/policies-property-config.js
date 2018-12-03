export default [
  {
    sectionName: 'General',
    fields: [
      {
        field: 'edrPolicy.name',
        labelKey: 'name'
      },
      {
        field: 'policyStatus',
        labelKey: 'policyStatus'
      },
      {
        field: 'evaluatedTime',
        labelKey: 'evaluatedTime',
        format: 'DATE'
      },
      {
        field: 'message',
        labelKey: 'errorDescription'
      }
    ]
  },
  {
    sectionName: 'Scan Schedule',
    fields: [
      {
        field: 'edrPolicy.scheduledScanConfig.enabled',
        labelKey: 'edrPolicy.schedOrManScan'
      },
      {
        field: 'edrPolicy.scheduledScanConfig.recurrentSchedule.scheduleStartDate',
        labelKey: 'edrPolicy.effectiveDate'
      },
      {
        field: 'edrPolicy.scheduledScanConfig.scanInterval',
        labelKey: 'edrPolicy.scanFrequency'
      },
      {
        field: 'edrPolicy.scheduledScanConfig.recurrentSchedule.runAtTime',
        labelKey: 'edrPolicy.startTime'
      },
      {
        field: 'edrPolicy.scheduledScanConfig.scanOptions.cpuMax',
        labelKey: 'edrPolicy.cpuMax'
      },
      {
        field: 'edrPolicy.scheduledScanConfig.scanOptions.cpuMaxVm',
        labelKey: 'edrPolicy.vmMax'
      }
    ]
  },
  {
    sectionName: 'Scan Settings',
    fields: [
      {
        field: 'edrPolicy.scheduledScanConfig.scanOptions.captureFloatingCode',
        labelKey: 'edrPolicy.captureFloatingCode'
      },
      {
        field: 'edrPolicy.scheduledScanConfig.scanOptions.downloadMbr',
        labelKey: 'edrPolicy.downloadMbr'
      },
      {
        field: 'edrPolicy.scheduledScanConfig.scanOptions.filterSignedHooks',
        labelKey: 'edrPolicy.filterSignedHooks'
      },
      {
        field: 'edrPolicy.serverConfig.requestScanOnRegistration',
        labelKey: 'edrPolicy.requestScanOnRegistration'
      }
    ]
  },
  {
    sectionName: 'Invasive Actions',
    fields: [
      {
        field: 'edrPolicy.blockingConfig.enabled',
        labelKey: 'edrPolicy.blockingEnabled'
      }
    ]
  },
  {
    sectionName: 'Endpoint Server',
    fields: [
      {
        field: 'edrPolicy.transportConfig.primary.address',
        labelKey: 'edrPolicy.primaryAddress'
      },
      {
        field: 'edrPolicy.transportConfig.primary.httpsPort',
        labelKey: 'edrPolicy.primaryHttpsPort'
      },
      {
        field: 'edrPolicy.transportConfig.primary.httpsBeaconIntervalInSeconds',
        labelKey: 'edrPolicy.primaryHttpsBeaconInterval'
      },
      {
        field: 'edrPolicy.transportConfig.primary.udpPort',
        labelKey: 'edrPolicy.primaryUdpPort'
      },
      {
        field: 'edrPolicy.transportConfig.primary.udpBeaconIntervalInSeconds',
        labelKey: 'edrPolicy.primaryUdpBeaconInterval'
      }
    ]
  },
  {
    sectionName: 'Agent Settings',
    fields: [
      {
        field: 'edrPolicy.agentMode',
        labelKey: 'edrPolicy.agentMode'
      }
    ]
  }
];