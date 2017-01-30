export default [
  {
    dataSourceGroup: 'Incidents',
    header: 'context.incident.header',
    footer: '',
    title: 'context.incident.title',
    columns: [
      {
        field: 'averageAlertRiskScore',
        title: 'context.incident.averageAlertRiskScore',
        width: '100',
        nested: 'averageAlertRiskScore',
        class: 'rsa-module-riskscore',
        componentClass: 'rsa-content-badge-score',
        dataType: 'riskscore'
      },
      {
        field: '_id',
        title: 'context.incident._id',
        nested: '_id',
        width: '80',
        dataType: 'link',
        path: '/do/respond/incident/{0}'

      },
      {
        field: 'name',
        title: 'context.incident.name',
        width: '50',
        nested: ''
      },
      {
        field: 'created',
        title: 'context.incident.created',
        width: '100',
        dataType: 'datetime',
        nested: 'created.$date'
      },
      {
        field: 'status',
        title: 'context.incident.status',
        width: '100',
        nested: ''
      },

      {
        field: 'name',
        title: 'context.incident.assignee',
        width: '50',
        nested: 'assignee'
      },
      {
        field: 'priority',
        title: 'context.incident.priority',
        width: '50',
        nested: ''
      },
      {
        field: 'alertCount',
        title: 'context.incident.alertCount',
        width: '50',
        nested: ''
      }
    ]
  },
  {
    dataSourceGroup: 'Alerts',
    header: 'context.alerts.header',
    footer: '',
    title: 'context.alerts.title',
    columns: [
      {
        field: 'risk_score',
        title: 'context.alerts.risk_score',
        width: '100',
        dataType: 'riskscore',
        nested: 'alert.risk_score',
        class: 'rsa-module-riskscore',
        componentClass: 'rsa-content-badge-score'
      },
      {
        field: 'source',
        title: 'context.alerts.source',
        nested: 'alert',
        width: '100'
      },
      {
        field: 'name',
        title: 'context.alerts.name',
        width: '100',
        nested: 'alert'
      },
      {
        field: 'numEvents',
        title: 'context.alerts.numEvents',
        width: '100',
        nested: 'alert'
      },
      {
        field: 'created',
        title: 'context.alerts.created',
        width: '100',
        dataType: 'datetime',
        nested: 'alert.timestamp.$date'
      },
      {
        field: 'incidentId',
        title: 'context.alerts.id',
        width: '80',
        nested: 'incidentId',
        dataType: 'link',
        path: '/do/respond/incident/{0}/details/catalyst'


      }
    ]
  }

];
