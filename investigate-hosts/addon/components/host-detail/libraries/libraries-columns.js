import { generateColumns } from 'investigate-hosts/util/util';

const defaultColumns = [
  {
    'dataType': 'checkbox',
    'width': 20,
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
    title: 'FILENAME'
  },
  {
    field: 'fileProperties.score',
    title: 'Risk Score'
  },
  {
    field: 'fileProperties.reputationStatus',
    title: 'Reputation Status'
  },
  {
    field: 'fileProperties.fileStatus',
    title: 'File Status'
  },
  {
    field: 'machineCount',
    title: 'MACHINE COUNT'
  },
  {
    field: 'signature',
    title: 'SIGNATURE',
    format: 'SIGNATURE'
  },
  {
    field: 'path',
    title: 'FILE PATH'
  },
  {
    field: 'checksumSha256',
    title: 'HASH'
  }
];
let columnsConfig = {
  mac: [
    {
      field: 'timeCreated',
      title: 'FILE CREATION TIME',
      format: 'DATE'
    }
  ],
  windows: [
    {
      field: 'timeCreated',
      title: 'FILE CREATION TIME',
      format: 'DATE'
    }
  ],
  linux: [
    {
      field: 'timeModified',
      title: 'LAST MODIFIED TIME',
      format: 'DATE'
    }
  ]
};

columnsConfig = generateColumns(columnsConfig, defaultColumns);

export default columnsConfig;