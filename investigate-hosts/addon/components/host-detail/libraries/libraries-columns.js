import { generateColumns } from 'investigate-hosts/util/util';

const defaultColumns = [
  {
    'dataType': 'checkbox',
    'width': '2vw',
    'class': 'rsa-form-row-checkbox',
    'componentClass': 'rsa-form-checkbox',
    'visible': true,
    'disableSort': true,
    'headerComponentClass': 'rsa-form-checkbox'
  },
  {
    field: 'processContext',
    title: 'PROCESS CONTEXT'
  },
  {
    field: 'fileName',
    title: 'FILENAME',
    format: 'FILENAME'
  },
  {
    field: 'fileProperties.score',
    title: 'Risk Score'
  },
  {
    field: 'machineCount',
    title: 'Active On',
    width: '6vw',
    disableSort: true
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
    field: 'path',
    title: 'FILE PATH',
    width: 600
  },
  {
    field: 'fileProperties.downloadInfo',
    title: 'downloaded',
    label: 'investigateHosts.files.fields.downloaded',
    disableSort: true,
    format: 'DOWNLOADSTATUS',
    width: 100
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
      width: '20%'
    }
  ],
  windows: [
    {
      field: 'timeCreated',
      title: 'FILE CREATION TIME',
      format: 'DATE',
      width: '20%'
    }
  ],
  linux: [
    {
      field: 'timeModified',
      title: 'LAST MODIFIED TIME',
      format: 'DATE',
      width: '20%'
    }
  ]
};

columnsConfig = generateColumns(columnsConfig, defaultColumns);

export default columnsConfig;
