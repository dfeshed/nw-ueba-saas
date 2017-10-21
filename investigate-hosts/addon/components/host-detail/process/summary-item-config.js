// Process view header configuration
export default [
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
    label: 'investigateHosts.process.userName',
    field: 'process.owner'
  },
  {
    label: 'investigateHosts.process.hostCount',
    field: 'process.hostCount'
  },
  {
    label: 'investigateHosts.process.creationTime',
    field: 'process.createTime',
    format: 'DATE'
  },
  {
    label: 'investigateHosts.process.hashlookup',
    field: 'hashlookup'
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
