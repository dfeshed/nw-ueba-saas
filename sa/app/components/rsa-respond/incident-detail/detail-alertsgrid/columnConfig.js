export default [
  {
    field: 'alert.risk_score',
    title: 'incident.details.alertsGrid.severity',
    width: '100',
    class: 'rsa-riskscore',
    dataType: 'custom',
    componentClass: 'rsa-content-badge-score',
    isDescending: true
  },
  {
    field: 'receivedTime',
    title: 'incident.details.alertsGrid.dateCreated',
    class: 'rsa-createddate spacer',
    width: '120',
    dataType: 'date-time',
    componentClass: 'rsa-content-datetime'
  },
  {
    field: 'alert.numEvents',
    title: 'incident.details.alertsGrid.events',
    width: '100',
    class: 'rsa-alerts-events spacer',
    dataType: 'text'
  },
  {
    field: 'alert.groupby_source_ip',
    title: 'incident.details.alertsGrid.host',
    width: '100',
    class: 'rsa-alerts-host spacer',
    dataType: 'text'
  },
  {
    title: '',
    width: '30',
    componentClass: 'rsa-icon',
    class: 'spacer',
    dataType: 'arrow-image'
  },
  {
    field: 'alert.groupby_domain',
    title: 'incident.details.alertsGrid.domain',
    width: '100',
    class: 'rsa-domain spacer',
    dataType: 'text'
  },
  {
    field: 'alert.source',
    title: 'incident.details.alertsGrid.source',
    width: '120',
    class: 'rsa-alert-source',
    dataType: 'custom',
    isDescending: true
  }
];
