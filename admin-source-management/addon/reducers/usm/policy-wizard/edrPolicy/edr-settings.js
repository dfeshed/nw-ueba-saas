const PORT_MIN_VALUE = 1;
const PORT_MAX_VALUE = 65535;

export const RADIO_BUTTONS_CONFIG = {
  name: 'recurrence',
  label: 'adminUsm.policyWizard.edrPolicy.recurrenceInterval',
  type: 'radioGroup',
  items: [
    {
      name: 'DAYS',
      label: 'adminUsm.policyWizard.edrPolicy.recurrenceIntervalOptions.options.daily'
    },
    {
      name: 'WEEKS',
      label: 'adminUsm.policyWizard.edrPolicy.recurrenceIntervalOptions.options.weekly'
    }
  ]
};

export const SCAN_SCHEDULE_CONFIG = {
  name: 'scan-type',
  label: 'adminUsm.policyWizard.edrPolicy.scanType',
  type: 'radioGroup',
  items: [
    {
      name: 'DISABLED',
      label: 'adminUsm.policyWizard.edrPolicy.scanTypeManual'
    },
    {
      name: 'ENABLED',
      label: 'adminUsm.policyWizard.edrPolicy.scanTypeScheduled'
    }
  ]
};

export const CAPTURE_CODE_CONFIG = {
  name: 'capture-code',
  label: 'adminUsm.policyWizard.edrPolicy.captureFloatingCode',
  type: 'radioGroup',
  items: [
    {
      name: false,
      label: 'adminUsm.policyWizard.edrPolicy.radioOptionDisabled'
    },
    {
      name: true,
      label: 'adminUsm.policyWizard.edrPolicy.radioOptionEnabled'
    }
  ]
};

export const SCAN_MBR_CONFIG = {
  name: 'download-mbr',
  label: 'adminUsm.policyWizard.edrPolicy.scanMbr',
  type: 'radioGroup',
  items: [
    {
      name: false,
      label: 'adminUsm.policyWizard.edrPolicy.radioOptionDisabled'
    },
    {
      name: true,
      label: 'adminUsm.policyWizard.edrPolicy.radioOptionEnabled'
    }
  ]
};

export const FILTER_SIGNED_CONFIG = {
  name: 'filter-signed',
  label: 'adminUsm.policyWizard.edrPolicy.filterSignedHooks',
  type: 'radioGroup',
  items: [
    {
      name: false,
      label: 'adminUsm.policyWizard.edrPolicy.radioOptionDisabled'
    },
    {
      name: true,
      label: 'adminUsm.policyWizard.edrPolicy.radioOptionEnabled'
    }
  ]
};

export const REQUEST_SCAN_CONFIG = {
  name: 'request-scan',
  label: 'adminUsm.policyWizard.edrPolicy.requestScanOnRegistration',
  type: 'radioGroup',
  items: [
    {
      name: false,
      label: 'adminUsm.policyWizard.edrPolicy.radioOptionDisabled'
    },
    {
      name: true,
      label: 'adminUsm.policyWizard.edrPolicy.radioOptionEnabled'
    }
  ]
};

export const BLOCKING_ENABLED_CONFIG = {
  name: 'blocking-enabled',
  label: 'adminUsm.policyWizard.edrPolicy.blockingEnabled',
  type: 'radioGroup',
  items: [
    {
      name: false,
      label: 'adminUsm.policyWizard.edrPolicy.radioOptionDisabled'
    },
    {
      name: true,
      label: 'adminUsm.policyWizard.edrPolicy.radioOptionEnabled'
    }
  ]
};

export const RAR_CONFIG = {
  name: 'rar-enabled',
  label: 'adminUsm.policyWizard.edrPolicy.rarEnabled',
  type: 'radioGroup',
  items: [
    {
      name: false,
      label: 'adminUsm.policyWizard.edrPolicy.radioOptionDisabled'
    },
    {
      name: true,
      label: 'adminUsm.policyWizard.edrPolicy.radioOptionEnabled'
    }
  ]
};

export const AGENT_MODE_CONFIG = {
  name: 'agent-mode',
  label: 'adminUsm.policyWizard.edrPolicy.agentMode',
  type: 'radioGroup',
  items: [
    {
      name: 'INSIGHTS',
      label: 'adminUsm.policyWizard.edrPolicy.insights'
    },
    {
      name: 'ADVANCED',
      label: 'adminUsm.policyWizard.edrPolicy.advanced'
    }
  ]
};

export const ALL_RADIO_OPTIONS = [
  { id: 'scanType', options: SCAN_SCHEDULE_CONFIG },
  // { id: 'captureFloatingCode', options: CAPTURE_CODE_CONFIG },
  { id: 'scanMbr', options: SCAN_MBR_CONFIG },
  // { id: 'filterSignedHooks', options: FILTER_SIGNED_CONFIG },
  { id: 'requestScanOnRegistration', options: REQUEST_SCAN_CONFIG },
  { id: 'blockingEnabled', options: BLOCKING_ENABLED_CONFIG },
  { id: 'agentMode', options: AGENT_MODE_CONFIG },
  { id: 'rarEnabled', options: RAR_CONFIG }
];

// Utility function to check if the port number is valid
export function isBetween(value) {
  if (value === '') {
    return false;
  }
  return value >= PORT_MIN_VALUE && value <= PORT_MAX_VALUE;
}
