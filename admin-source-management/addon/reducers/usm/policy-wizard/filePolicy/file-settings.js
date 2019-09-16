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

export const SOURCE_CONFIG = [
  { field: 'fileType', title: 'adminUsm.policyWizard.filePolicy.logFileType', width: '30vw', displayType: 'fileTypeInput', component: 'usm-policies/policy-wizard/define-policy-sources-step/body-cell' },
  { field: 'enabled', title: 'adminUsm.policyWizard.filePolicy.enableOnAgent', width: '30vw', displayType: 'enabledRadio', component: 'usm-policies/policy-wizard/define-policy-sources-step/body-cell' },
  { field: 'startOfEvents', title: 'adminUsm.policyWizard.filePolicy.dataCollection', width: '30vw', displayType: 'eventsRadio', component: 'usm-policies/policy-wizard/define-policy-sources-step/body-cell' },
  { field: 'paths', title: 'adminUsm.policyWizard.filePolicy.logFilePath', width: '100%', displayType: 'paths', component: 'usm-policies/policy-wizard/define-policy-sources-step/body-cell' },
  { field: 'exclusionFilters', title: 'adminUsm.policyWizard.filePolicy.exclusionFilters', width: '30vw', displayType: 'exclusionFilters', component: 'usm-policies/policy-wizard/define-policy-sources-step/body-cell' },
  { field: 'advancedSettingsCell', title: 'adminUsm.policyWizard.filePolicy.advancedSettings', width: '30vw', component: 'usm-policies/policy-wizard/define-policy-sources-step/advanced-settings-cell',
    config: [
      { field: 'sourceName', title: 'adminUsm.policyWizard.filePolicy.sourceName', width: '30vw', displayType: 'sourceNameInput' },
      { field: 'fileEncoding', title: 'adminUsm.policyWizard.filePolicy.fileEncoding', width: '30vw', displayType: 'fileEncoding' }
    ]
  }
];
