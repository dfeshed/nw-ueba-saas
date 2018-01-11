// Process view header configuration
import { generateColumns } from 'investigate-hosts/util/util';

const defaultSummaryConfig = [
  {
    label: 'investigateHosts.process.processName',
    field: 'fileName'
  },
  {
    label: 'investigateHosts.process.pid',
    field: 'process.pid'
  },
  {
    label: 'investigateHosts.process.parentId',
    field: 'process.parentPid'
  },
  {
    label: 'investigateHosts.process.owner',
    field: 'process.owner'
  },
  {
    label: 'investigateHosts.process.signature',
    field: 'process.signature',
    format: 'SIGNATURE'
  },
  {
    label: 'investigateHosts.process.path',
    field: 'path'
  },
  {
    label: 'investigateHosts.process.launchArguments',
    field: 'process.launchArguments',
    cssClass: 'col-xs-12 col-md-12'
  }
];

const machineOsBasedSummaryConfig = {
  mac: [{
    label: 'investigateHosts.process.creationTime',
    field: 'process.createUtcTime',
    format: 'DATE'
  }],
  windows: [{
    label: 'investigateHosts.process.creationTime',
    field: 'process.createUtcTime',
    format: 'DATE'
  }],
  linux: []
};

const summaryItems = generateColumns(machineOsBasedSummaryConfig, defaultSummaryConfig);

export default summaryItems;