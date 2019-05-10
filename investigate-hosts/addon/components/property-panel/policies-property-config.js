const policiesPropertyConfig = [
  {
    sectionName: 'General',
    fields: [
      {
        field: 'policyStatus',
        labelKey: 'policyWizard.policyStatus'
      },
      {
        field: 'evaluatedTime',
        labelKey: 'policyWizard.evaluatedTime',
        format: 'DATE'
      },
      {
        field: 'message',
        labelKey: 'policyWizard.errorDescription'
      }
    ]
  },
  {
    sectionName: 'Scan Schedule',
    fields: [
      {
        field: 'edrPolicy.scheduledScanConfig.enabled',
        labelKey: 'policyWizard.edrPolicy.scanType'
      },
      {
        field: 'edrPolicy.scheduledScanConfig.recurrentSchedule.scheduleStartDate',
        labelKey: 'policyWizard.edrPolicy.scanStartDate'
      },
      {
        field: 'edrPolicy.scheduledScanConfig.scanInterval',
        labelKey: 'policyWizard.edrPolicy.recurrenceInterval'
      },
      {
        field: 'edrPolicy.scheduledScanConfig.recurrentSchedule.runAtTime',
        labelKey: 'policyWizard.edrPolicy.scanStartTime'
      },
      {
        field: 'edrPolicy.scheduledScanConfig.scanOptions.cpuMax',
        labelKey: 'policyWizard.edrPolicy.cpuMax'
      },
      {
        field: 'edrPolicy.scheduledScanConfig.scanOptions.cpuMaxVm',
        labelKey: 'policyWizard.edrPolicy.cpuMaxVm'
      }
    ]
  },
  {
    sectionName: 'Scan Settings',
    fields: [
      {
        field: 'edrPolicy.scheduledScanConfig.scanOptions.scanMbr',
        labelKey: 'policyWizard.edrPolicy.scanMbr'
      },
      {
        field: 'edrPolicy.serverConfig.requestScanOnRegistration',
        labelKey: 'policyWizard.edrPolicy.requestScanOnRegistration'
      }
    ]
  },
  {
    sectionName: 'Invasive Actions',
    fields: [
      {
        field: 'edrPolicy.blockingConfig.enabled',
        labelKey: 'policyWizard.edrPolicy.blockingEnabled'
      }
    ]
  },
  {
    sectionName: 'Endpoint Server',
    fields: [
      {
        field: 'edrPolicy.transportConfig.primary.address',
        labelKey: 'policyWizard.edrPolicy.primaryAddress'
      },
      {
        field: 'edrPolicy.transportConfig.primary.httpsPort',
        labelKey: 'policyWizard.edrPolicy.primaryHttpsPort'
      },
      {
        field: 'edrPolicy.transportConfig.primary.httpsBeaconInterval',
        labelKey: 'policyWizard.edrPolicy.primaryHttpsBeaconInterval'
      },
      {
        field: 'edrPolicy.transportConfig.primary.udpPort',
        labelKey: 'policyWizard.edrPolicy.primaryUdpPort'
      },
      {
        field: 'edrPolicy.transportConfig.primary.udpBeaconInterval',
        labelKey: 'policyWizard.edrPolicy.primaryUdpBeaconInterval'
      }
    ]
  },
  {
    sectionName: 'Agent Settings',
    fields: [
      {
        field: 'edrPolicy.agentMode',
        labelKey: 'policyWizard.edrPolicy.agentMode'
      }
    ]
  }
];

const windowsLogPolicy = {
  sectionName: 'Windows Log Settings',
  fields: [
    {
      field: 'windowsLogPolicy.enabled',
      labelKey: 'policies.detail.windowsLogPolicyEnabled'
    },
    {
      field: 'windowsLogPolicy.primaryDestination',
      labelKey: 'policies.detail.primaryDestination'
    },
    {
      field: 'windowsLogPolicy.secondaryDestination',
      labelKey: 'policies.detail.secondaryDestination'
    },
    {
      field: 'windowsLogPolicy.protocol',
      labelKey: 'policies.detail.protocol'
    },
    {
      field: 'windowsLogPolicy.sendTestLog',
      labelKey: 'policies.detail.sendTestLog'
    }
  ]
};

export const getPoliciesPropertyConfig = (showWindowsLogPolicy, channelFiltersConfig) => {
  let config = policiesPropertyConfig;
  if (showWindowsLogPolicy) {
    if (channelFiltersConfig) {
      config = [...policiesPropertyConfig, windowsLogPolicy, channelFiltersConfig];
    } else {
      config = [...policiesPropertyConfig, windowsLogPolicy];
    }
  }
  return config;
};