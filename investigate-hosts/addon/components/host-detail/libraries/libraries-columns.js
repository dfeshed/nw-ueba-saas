import { generateColumns } from 'investigate-hosts/util/util';

const defaultColumns = [
  {
    field: 'processContext',
    title: 'PROCESS CONTEXT'
  },
  {
    field: 'fileName',
    title: 'FILENAME'
  },
  {
    field: 'machineCount',
    title: 'MACHINE COUNT',
    disableSort: true
  },
  {
    field: 'signature',
    title: 'SIGNATURE',
    format: 'SIGNATURE',
    disableSort: true
  },
  {
    field: 'path',
    title: 'FILE PATH'
  },
  {
    field: 'checksumSha256',
    title: 'HASH',
    disableSort: true
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
      width: '20%'
    }
  ]
};

columnsConfig = generateColumns(columnsConfig, defaultColumns);

export default columnsConfig;