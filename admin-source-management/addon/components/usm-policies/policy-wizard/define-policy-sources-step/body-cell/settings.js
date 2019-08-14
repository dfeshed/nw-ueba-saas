import _ from 'lodash';

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
      name: false,
      label: 'adminUsm.policyWizard.filePolicy.collectNew'
    },
    {
      name: true,
      label: 'adminUsm.policyWizard.filePolicy.collectAll'
    }
  ]
};

export const encodingOptions = _.sortBy([
  'BIG5',
  'EUC-JP',
  'EUC-KR',
  'GB18030',
  'GB2312-80',
  'GBK',
  'ISO-8859-1',
  'ISO-8859-2',
  'ISO-8859-3',
  'Local Encoding',
  'Shift-JIS',
  'UTF-8 / ASCII', // default
  'Wide Char',
  'Windows-1250',
  'Windows-1251',
  'Windows-1252'
]);

export const enableOnAgentConfig = () => ENABLE_ON_AGENT;
export const dataCollectionConfig = () => DATA_COLLECTION;
