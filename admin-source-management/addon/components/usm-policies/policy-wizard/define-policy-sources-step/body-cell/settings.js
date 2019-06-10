const ENABLE_ON_AGENT = {
  name: 'enable-on-agent',
  type: 'radioGroup',
  items: [
    {
      name: true,
      label: 'adminUsm.policyWizard.filePolicy.radioOptionEnable'
    },
    {
      name: false,
      label: 'adminUsm.policyWizard.filePolicy.radioOptionDisable'
    }
  ]
};

const DATA_COLLECTION = {
  name: 'data-collection',
  type: 'radioGroup',
  items: [
    {
      name: true,
      label: 'adminUsm.policyWizard.filePolicy.collectNew'
    },
    {
      name: false,
      label: 'adminUsm.policyWizard.filePolicy.collectAll'
    }
  ]
};

export const encodingOptions = [
  'UTF-8',
  'UTF-16',
  'Wide Char',
  'ASCII',
  'Local Encoding'
];

export const enableOnAgentConfig = () => ENABLE_ON_AGENT;
export const dataCollectionConfig = () => DATA_COLLECTION;
