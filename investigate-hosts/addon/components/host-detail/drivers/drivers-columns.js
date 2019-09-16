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
    format: 'FILENAME',
    width: '150px'
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
    field: 'fileProperties.hostCount',
    title: 'investigateHosts.detailsColumns.hostCount',
    width: '6vw'
  },
  {
    field: 'fileProperties.reputationStatus',
    title: 'investigateHosts.detailsColumns.reputationStatus',
    width: '130px'
  },
  {
    'field': 'fileProperties.fileStatus',
    'title': 'investigateHosts.detailsColumns.fileStatus',
    'label': 'investigateHosts.files.fields.fileStatus',
    'disableSort': false,
    width: '150px'
  },
  {
    field: 'signature',
    title: 'investigateHosts.detailsColumns.signature',
    format: 'SIGNATURE',
    width: '200px'
  },
  {
    field: 'fileProperties.downloadInfo',
    title: 'investigateHosts.detailsColumns.downloadInfo',
    label: 'investigateHosts.detailsColumns.downloadInfo',
    format: 'DOWNLOADSTATUS',
    width: '150px'
  },
  {
    field: 'path',
    title: 'investigateHosts.detailsColumns.path',
    width: '18vw'
  }
];

const machineOsBasedConfig = {
  mac: [
    {
      field: 'timeCreated',
      title: 'investigateHosts.detailsColumns.timeCreated',
      format: 'DATE',
      width: '150px'
    }
  ],
  windows: [
    {
      field: 'timeCreated',
      title: 'investigateHosts.detailsColumns.timeCreated',
      format: 'DATE',
      width: '150px'
    }
  ],
  linux: []
};

const columnsConfig = generateColumns(machineOsBasedConfig, defaultColumns);

export default columnsConfig;
