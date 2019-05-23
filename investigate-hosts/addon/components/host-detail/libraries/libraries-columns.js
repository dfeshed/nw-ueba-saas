import { generateColumns } from 'investigate-hosts/util/util';

const defaultColumns = [
  {
    'dataType': 'checkbox',
    'width': '2vw',
    'class': 'rsa-form-row-checkbox',
    'componentClass': 'rsa-form-checkbox',
    'visible': true,
    'disableSort': true,
    'headerComponentClass': 'rsa-form-checkbox',
    field: 'checkbox'
  },
  {
    field: 'processContext',
    title: 'PROCESS CONTEXT',
    width: '8vw'
  },
  {
    field: 'fileName',
    title: 'FILENAME',
    format: 'FILENAME'
  },
  {
    field: 'machineFileScore',
    title: 'Local Risk Score',
    width: '8vw'
  },
  {
    field: 'fileProperties.score',
    title: 'Global Risk Score',
    width: '8vw'
  },
  {
    field: 'machineCount',
    title: 'Active On',
    width: '4vw',
    disableSort: true,
    visible: false
  },
  {
    field: 'fileProperties.reputationStatus',
    title: 'Reputation'
  },
  {
    field: 'fileProperties.fileStatus',
    title: 'File Status'
  },
  {
    field: 'signature',
    title: 'SIGNATURE',
    format: 'SIGNATURE'
  },
  {
    field: 'fileProperties.downloadInfo',
    title: 'downloaded',
    label: 'investigateHosts.files.fields.downloaded',
    format: 'DOWNLOADSTATUS',
    width: 100
  },
  {
    field: 'path',
    title: 'FILE PATH',
    width: '19vw'
  },
  {
    field: 'checksumSha256',
    title: 'HASH',
    width: 500
  }
];
let columnsConfig = {
  mac: [
    {
      field: 'timeCreated',
      title: 'FILE CREATION TIME',
      format: 'DATE',
      width: '12vw'
    }
  ],
  windows: [
    {
      field: 'timeCreated',
      title: 'FILE CREATION TIME',
      format: 'DATE',
      width: '12vw'
    }
  ],
  linux: [
    {
      field: 'timeModified',
      title: 'LAST MODIFIED TIME',
      format: 'DATE',
      width: '12vw'
    }
  ]
};

columnsConfig = generateColumns(columnsConfig, defaultColumns);

export default columnsConfig;
