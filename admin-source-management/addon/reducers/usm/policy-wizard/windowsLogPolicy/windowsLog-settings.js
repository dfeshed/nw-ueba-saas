export const ENABLED_CONFIG = {
  name: 'enabled',
  label: 'adminUsm.policyWizard.windowsLogPolicy.enabled',
  type: 'radioGroup',
  items: [
    {
      name: false,
      label: 'adminUsm.policyWizard.windowsLogPolicy.radioOptionDisabled'
    },
    {
      name: true,
      label: 'adminUsm.policyWizard.windowsLogPolicy.radioOptionEnabled'
    }
  ]
};

export const SEND_TEST_LOG_CONFIG = {
  name: 'send-test-log',
  label: 'adminUsm.policyWizard.windowsLogPolicy.sendTestLog',
  type: 'radioGroup',
  items: [
    {
      name: false,
      label: 'adminUsm.policyWizard.windowsLogPolicy.radioOptionDisabled'
    },
    {
      name: true,
      label: 'adminUsm.policyWizard.windowsLogPolicy.radioOptionEnabled'
    }
  ]
};

export const ALL_RADIO_OPTIONS = [
  { id: 'enabled', options: ENABLED_CONFIG },
  { id: 'sendTestLog', options: SEND_TEST_LOG_CONFIG }
];

export const CHANNEL_CONFIG = [
  { field: 'channel', title: 'adminUsm.policyWizard.windowsLogPolicy.channel.name', width: '10vw', displayType: 'channelInput' },
  { field: 'filter', title: 'adminUsm.policyWizard.windowsLogPolicy.channel.filter', width: '6vw', displayType: 'dropdown' },
  { field: 'eventId', title: 'adminUsm.policyWizard.windowsLogPolicy.channel.event', width: '7vw', displayType: 'EventInput' },
  { field: 'delete', title: 'adminUsm.policyWizard.windowsLogPolicy.channel.empty', width: '35px', displayType: 'icon' }
];