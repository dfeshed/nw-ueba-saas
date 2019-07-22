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
    field: 'fileName',
    title: 'investigateHosts.detailsColumns.fileName',
    format: 'FILENAME'
  },
  {
    field: 'machineFileScore',
    title: 'investigateHosts.detailsColumns.machineFileScore',
    width: '8vw'
  },
  {
    field: 'fileProperties.score',
    title: 'investigateHosts.detailsColumns.globalScore',
    width: '8vw'
  },
  {
    field: 'machineCount',
    title: 'investigateHosts.detailsColumns.machineCount',
    width: '6vw',
    disableSort: true,
    visible: false
  },
  {
    field: 'fileProperties.reputationStatus',
    title: 'investigateHosts.detailsColumns.reputationStatus'
  },
  {
    field: 'fileProperties.fileStatus',
    title: 'investigateHosts.detailsColumns.fileStatus',
    width: '8vw'
  },
  {
    field: 'signature',
    title: 'investigateHosts.detailsColumns.signature',
    format: 'SIGNATURE',
    width: '10vw'
  },
  {
    field: 'fileProperties.downloadInfo',
    title: 'investigateHosts.detailsColumns.downloadInfo',
    label: 'investigateHosts.detailsColumns.downloadInfo',
    format: 'DOWNLOADSTATUS',
    width: 100
  },
  {
    field: 'timeCreated',
    title: 'investigateHosts.detailsColumns.timeCreated',
    format: 'DATE',
    width: '10vw'
  }
];
let columnsConfig = {
  mac: [
    {
      field: 'path',
      title: 'investigateHosts.detailsColumns.path'
    }
  ],
  windows: [
    {
      field: 'registryPath',
      title: 'investigateHosts.detailsColumns.registryPath',
      width: '45vw'
    }
  ],
  linux: [
    {
      field: 'path',
      title: 'investigateHosts.detailsColumns.path'
    }
  ]
};

columnsConfig = generateColumns(columnsConfig, defaultColumns);

export default columnsConfig;
