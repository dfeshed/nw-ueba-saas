export default {
  class: 'rsa-context-panel__grid__reputation-server',
  dataSourceGroup: 'FileReputationServer',
  header: '',
  headerRequired: false,
  footerRequired: true,
  footer: '',
  title: 'context.reputation.title',
  rows: [{
    cell: [
      {
        field: 'status',
        title: 'context.reputation.status'
      },
      {
        field: 'scannerMatch',
        title: 'context.reputation.scannerMatch'
      },
      {
        field: 'platform',
        title: 'context.reputation.platform'
      },
      {
        field: 'type',
        title: 'context.reputation.type'
      }]
  },
  {
    cell: [
      {
        field: 'familyName',
        title: 'context.reputation.familyName'
      }]
  }]
};