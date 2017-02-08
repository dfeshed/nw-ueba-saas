export default [
  {
    dataSourceType: 'Modules',
    header: 'context.modules.header',
    footer: ' ',
    title: 'context.modules.title',
    columns: [
      {
        field: 'Score',
        title: 'context.modules.iiocScore',
        dataType: 'riskscore',
        width: '15%',
        class: 'rsa-module-riskscore',
        componentClass: 'rsa-content-badge-score',
        nested: 'IOCScore.Score'
      },
      {
        field: 'Name',
        title: 'context.modules.moduleName',
        width: '25%',
        class: 'rsa-module-moduleName',
        nested: ''
      },
      {
        field: 'AnalyticsScore',
        title: 'context.modules.analyticsScore',
        width: '15%',
        class: 'rsa-module-riskScore',
        nested: ''
      },
      {
        field: 'GlobalMachineCount',
        title: 'context.modules.machineCount',
        width: '15%',
        class: 'rsa-module-machineCount',
        nested: ''
      },
      {
        field: 'Signature',
        title: 'context.modules.signature',
        width: '30%',
        class: 'rsa-module-signature',
        nested: ''
      }]
  },

  {
    dataSourceType: 'IOC',
    header: 'context.iiocs.header',
    footer: '',
    title: 'context.iiocs.title',
    columns: [
      {
        field: 'LastExecuted',
        title: 'context.iiocs.lastExecuted',
        width: '25%',
        dataType: 'text',
        nested: ''
      },
      {
        field: 'Description',
        title: 'context.iiocs.description',
        width: '25%',
        dataType: 'text',
        nested: ''
      },
      {
        field: 'IOCLevel',
        title: 'context.iiocs.iOCLevel',
        width: '25%',
        dataType: 'text',
        nested: ''
      }
    ]
  },

  { dataSourceType: 'Incidents',
    header: 'context.incident.header',
    footer: '',
    title: 'context.incident.title',
    columns: [
      {
        field: 'averageAlertRiskScore',
        title: 'context.incident.averageAlertRiskScore',
        width: '9%',
        nested: 'averageAlertRiskScore',
        class: 'rsa-module-riskscore',
        componentClass: 'rsa-content-badge-score',
        dataType: 'riskscore'
      },
      {
        field: '_id',
        title: 'context.incident._id',
        nested: '',
        width: '7%'
      },
      {
        field: 'name',
        title: 'context.incident.name',
        width: '24%',
        nested: ''
      },
      {
        field: 'created',
        title: 'context.incident.created',
        width: '20%',
        dataType: 'datetime',
        nested: 'created.$date'
      },
      {
        field: 'status',
        title: 'context.incident.status',
        width: '9%',
        nested: ''
      },

      {
        field: 'name',
        title: 'context.incident.assignee',
        width: '9%',
        nested: 'assignee'
      },
      {
        field: 'priority',
        title: 'context.incident.priority',
        width: '9%',
        nested: ''
      },
      {
        field: 'alertCount',
        title: 'context.incident.alertCount',
        width: '8%',
        nested: ''
      }]
  },
  {
    dataSourceType: 'Alerts',
    header: 'context.alerts.header',
    footer: '',
    title: 'context.alerts.title',
    columns: [
      {
        field: 'risk_score',
        title: 'context.alerts.risk_score',
        width: '9%',
        dataType: 'riskscore',
        nested: 'alert.risk_score',
        class: 'rsa-module-riskscore',
        componentClass: 'rsa-content-badge-score'
      },
      {
        field: 'source',
        title: 'context.alerts.source',
        nested: 'alert',
        width: '22%'
      },
      {
        field: 'name',
        title: 'context.alerts.name',
        width: '9%',
        nested: 'alert'
      },
      {
        field: 'numEvents',
        title: 'context.alerts.numEvents',
        width: '5%',
        nested: 'alert'
      },
      {
        field: 'created',
        title: 'context.alerts.created',
        width: '25%',
        dataType: 'datetime',
        nested: 'alert.timestamp.$date'
      },
      {
        field: 'incidentId',
        title: 'context.alerts.id',
        width: '30%',
        nested: ''
      }
    ]
  }

];