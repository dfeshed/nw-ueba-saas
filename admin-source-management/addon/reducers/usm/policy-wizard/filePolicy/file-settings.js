export const ENABLED_CONFIG = {
  name: 'enabled',
  label: 'adminUsm.policyWizard.filePolicy.enabled',
  type: 'radioGroup',
  items: [
    {
      name: false,
      label: 'adminUsm.policyWizard.filePolicy.radioOptionDisabled'
    },
    {
      name: true,
      label: 'adminUsm.policyWizard.filePolicy.radioOptionEnabled'
    }
  ]
};

export const SEND_TEST_LOG_CONFIG = {
  name: 'send-test-log',
  label: 'adminUsm.policyWizard.filePolicy.sendTestLog',
  type: 'radioGroup',
  items: [
    {
      name: false,
      label: 'adminUsm.policyWizard.filePolicy.radioOptionDisabled'
    },
    {
      name: true,
      label: 'adminUsm.policyWizard.filePolicy.radioOptionEnabled'
    }
  ]
};

export const ALL_RADIO_OPTIONS = [
  { id: 'enabled', options: ENABLED_CONFIG },
  { id: 'sendTestLog', options: SEND_TEST_LOG_CONFIG }
];