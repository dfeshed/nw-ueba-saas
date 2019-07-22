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
    title: 'investigateHosts.detailsColumns.processContext',
    width: '8vw'
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
    width: '4vw',
    disableSort: true,
    visible: false
  },
  {
    field: 'fileProperties.reputationStatus',
    title: 'investigateHosts.detailsColumns.reputationStatus'
  },
  {
    field: 'fileProperties.fileStatus',
    title: 'investigateHosts.detailsColumns.fileStatus'
  },
  {
    field: 'signature',
    title: 'investigateHosts.detailsColumns.signature',
    format: 'SIGNATURE'
  },
  {
    field: 'fileProperties.downloadInfo',
    title: 'investigateHosts.detailsColumns.downloadInfo',
    label: 'investigateHosts.files.fields.downloaded',
    format: 'DOWNLOADSTATUS',
    width: 100
  },
  {
    field: 'path',
    title: 'investigateHosts.detailsColumns.filePath',
    width: '19vw'
  },
  {
    field: 'checksumSha256',
    title: 'investigateHosts.detailsColumns.hash',
    width: 500
  }
];
let columnsConfig = {
  mac: [
    {
      field: 'timeCreated',
      title: 'investigateHosts.detailsColumns.timeCreated',
      format: 'DATE',
      width: '12vw'
    }
  ],
  windows: [
    {
      field: 'timeCreated',
      title: 'investigateHosts.detailsColumns.timeCreated',
      format: 'DATE',
      width: '12vw'
    }
  ],
  linux: [
    {
      field: 'timeModified',
      title: 'investigateHosts.detailsColumns.timeModified',
      format: 'DATE',
      width: '12vw'
    }
  ]
};

columnsConfig = generateColumns(columnsConfig, defaultColumns);

export default columnsConfig;
