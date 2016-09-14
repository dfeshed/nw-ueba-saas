export default [
  {
    field: 'alert.risk_score',
    title: 'incident.details.alertsGrid.severity',
    width: '10%',
    class: 'rsa-riskscore',
    dataType: 'custom',
    componentClass: 'rsa-content-badge-score',
    isDescending: true
  },
  {
    field: 'receivedTime',
    title: 'incident.details.alertsGrid.dateCreated',
    class: 'rsa-createddate',
    width: '15%',
    dataType: 'date-time',
    componentClass: 'rsa-content-datetime'
  },
  {
    field: 'alert.numEvents',
    title: 'incident.details.alertsGrid.events',
    width: '10%',
    class: 'rsa-alerts-events',
    dataType: 'text'
  },
  {
    field: 'alert.groupby_source_ip',
    title: 'incident.details.alertsGrid.host',
    width: '15%',
    class: 'rsa-alerts-host',
    dataType: 'text'
  },
  {
    title: '',
    width: '3%',
    componentClass: 'rsa-icon',
    dataType: 'arrow-image'
  },
  {
    field: 'alert.groupby_domain',
    title: 'incident.details.alertsGrid.domain',
    width: '30%',
    class: 'rsa-domain',
    dataType: 'text'
  },
  {
    field: 'alert.source',
    title: 'incident.details.alertsGrid.source',
    width: '12%',
    class: 'rsa-alert-source',
    dataType: 'custom',
    isDescending: true
  }
];
