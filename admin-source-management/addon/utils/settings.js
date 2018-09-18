export const RADIO_BUTTONS_CONFIG = {
  name: 'recurrence',
  label: 'adminUsm.policy.scheduleConfiguration.recurrenceInterval.title',
  type: 'radioGroup',
  items: [
    {
      name: 'DAYS',
      label: 'adminUsm.policy.scheduleConfiguration.recurrenceInterval.options.daily'
    },
    {
      name: 'WEEKS',
      label: 'adminUsm.policy.scheduleConfiguration.recurrenceInterval.options.weekly'
    }
  ]
};

export const SCAN_SCHEDULE_CONFIG = {
  name: 'scan-type',
  label: 'adminUsm.policy.scheduleConfiguration.scanType.title',
  type: 'radioGroup',
  items: [
    {
      name: 'MANUAL',
      label: 'adminUsm.policy.scheduleConfiguration.scanType.options.manual'
    },
    {
      name: 'SCHEDULED',
      label: 'adminUsm.policy.scheduleConfiguration.scanType.options.scheduled'
    }
  ]
};

export const CAPTURE_CODE_CONFIG = {
  name: 'capture-code',
  label: 'adminUsm.policy.captureFloatingCode',
  type: 'radioGroup',
  items: [
    {
      name: false,
      label: 'adminUsm.policy.radioOptionDisabled'
    },
    {
      name: true,
      label: 'adminUsm.policy.radioOptionEnabled'
    }
  ]
};

export const DOWNLOAD_MBR_CONFIG = {
  name: 'download-mbr',
  label: 'adminUsm.policy.downloadMbr',
  type: 'radioGroup',
  items: [
    {
      name: false,
      label: 'adminUsm.policy.radioOptionDisabled'
    },
    {
      name: true,
      label: 'adminUsm.policy.radioOptionEnabled'
    }
  ]
};

export const FILTER_SIGNED_CONFIG = {
  name: 'filter-signed',
  label: 'adminUsm.policy.filterSignedHooks',
  type: 'radioGroup',
  items: [
    {
      name: false,
      label: 'adminUsm.policy.radioOptionDisabled'
    },
    {
      name: true,
      label: 'adminUsm.policy.radioOptionEnabled'
    }
  ]
};

export const REQUEST_SCAN_CONFIG = {
  name: 'request-scan',
  label: 'adminUsm.policy.requestScanOnRegistration',
  type: 'radioGroup',
  items: [
    {
      name: false,
      label: 'adminUsm.policy.radioOptionDisabled'
    },
    {
      name: true,
      label: 'adminUsm.policy.radioOptionEnabled'
    }
  ]
};

export const BLOCKING_ENABLED_CONFIG = {
  name: 'blocking-enabled',
  label: 'adminUsm.policy.blockingEnabled',
  type: 'radioGroup',
  items: [
    {
      name: false,
      label: 'adminUsm.policy.radioOptionDisabled'
    },
    {
      name: true,
      label: 'adminUsm.policy.radioOptionEnabled'
    }
  ]
};

export const AGENT_MODE_CONFIG = {
  name: 'agent-mode',
  label: 'adminUsm.policy.agentMode',
  type: 'radioGroup',
  items: [
    {
      name: 'NO_MONITORING',
      label: 'adminUsm.policy.noMonitoring'
    },
    {
      name: 'FULL_MONITORING',
      label: 'adminUsm.policy.fullMonitoring'
    }
  ]
};

export const ALL_RADIO_OPTIONS = [
  { id: 'scanType', options: SCAN_SCHEDULE_CONFIG },
  { id: 'captureFloatingCode', options: CAPTURE_CODE_CONFIG },
  { id: 'downloadMbr', options: DOWNLOAD_MBR_CONFIG },
  { id: 'filterSignedHooks', options: FILTER_SIGNED_CONFIG },
  { id: 'requestScanOnRegistration', options: REQUEST_SCAN_CONFIG },
  { id: 'blockingEnabled', options: BLOCKING_ENABLED_CONFIG },
  { id: 'agentMode', options: AGENT_MODE_CONFIG }
];