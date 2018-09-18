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
    field: 'fileProperties.signature.features',
    format: 'SIGNATURE'
  },
  {
    label: 'investigateHosts.process.path',
    field: 'path'
  },
  {
    label: 'investigateHosts.process.score',
    field: 'fileProperties.score'
  },
  {
    label: 'investigateHosts.process.reputationStatus',
    field: 'fileProperties.reputationStatus'
  },
  {
    label: 'investigateHosts.process.launchArguments',
    field: 'process.launchArguments',
    cssClass: 'col-xs-8 col-md-8'
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