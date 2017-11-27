import { generateColumns } from 'investigate-hosts/util/util';

const defaultColumns = [
  {
    field: 'processContext',
    title: 'PROCESS CONTEXT',
    width: '20%'
  },
  {
    field: 'fileName',
    title: 'FILENAME',
    width: '10%'
  },
  {
    field: 'machineCount',
    title: 'MACHINE COUNT',
    width: '10%',
    disableSort: true
  },
  {
    field: 'signature',
    title: 'SIGNATURE',
    format: 'SIGNATURE',
    width: '10%',
    disableSort: true
  },
  {
    field: 'path',
    title: 'FILE PATH',
    width: '10%'
  },
  {
    field: 'checksumSha256',
    title: 'HASH',
    width: '10%',
    disableSort: true
  }
];
let columnsConfig = {
  mac: [
    {
      field: 'timeCreated',
      title: 'FILE CREATION TIME',
      format: 'DATE',
      width: '10%'
    }
  ],
  windows: [
    {
      field: 'timeCreated',
      title: 'FILE CREATION TIME',
      format: 'DATE',
      width: '15%'
    }
  ],
  linux: [
    {
      field: 'timeModified',
      title: 'LAST MODIFIED TIME',
      width: '15%',
      format: 'DATE'
    }
  ]
};

columnsConfig = generateColumns(columnsConfig, defaultColumns);

export default columnsConfig;