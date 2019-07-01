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
  { field: 'fileType', title: 'adminUsm.policyWizard.filePolicy.logFileType', width: '30vw', displayType: 'fileTypeInput' },
  { field: 'sourceName', title: 'adminUsm.policyWizard.filePolicy.sourceName', width: '15vw', displayType: 'sourceNameInput' },
  { field: 'enabled', title: 'adminUsm.policyWizard.filePolicy.enableOnAgent', width: '15vw', displayType: 'enabledRadio' },
  { field: 'startOfEvents', title: 'adminUsm.policyWizard.filePolicy.dataCollection', width: '30vw', displayType: 'eventsRadio' },
  { field: 'fileEncoding', title: 'adminUsm.policyWizard.filePolicy.fileEncoding', width: '15vw', displayType: 'fileEncoding' },
  { field: 'paths', title: 'adminUsm.policyWizard.filePolicy.logFilePath', width: '100%', displayType: 'paths' },
  { field: 'exclusionFilters', title: 'adminUsm.policyWizard.filePolicy.exclusionFilters', width: '30vw', displayType: 'exclusionFilters' }

];
