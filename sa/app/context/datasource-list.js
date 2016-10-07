export default[
  {
    dataSourceType: 'LIST',
    header: ' ' ,
    footer: '10 Items | Displaying 1 of 5|LAST UPDATED:',
    title: 'context.list.title',

    columns: [
      {
        field: 'dataSourceName',
        title: 'context.list.dataSourceName',
        width: '20%',
        nested: 'dataSourceName',
        dataType: 'header'

      },
      {
        field: 'dataSourceDescription',
        title: 'context.list.dataSourceDescription',
        width: '20%',
        nested: 'dataSourceDescription',
        dataType: 'header'

      },
      {
        field: 'createdByUser',
        title: 'context.list.createdByUser',
        width: '20%',
        nested: 'dataSourceEntryMeta'

      },
      {
        field: 'createdTimeStamp',
        title: 'context.list.createdTimeStamp',
        nested: 'dataSourceEntryMeta.createdTimeStamp',
        width: '20%',
        dataType: 'datetime'
      },
      {
        field: 'lastModifiedTimeStamp',
        title: 'context.list.lastModifiedTimeStamp',
        width: '20%',
        nested: 'dataSourceEntryMeta.lastModifiedTimeStamp',
        dataType: 'datetime'
      }
    ]
  },
  {
    dataSourceType: 'Alerts',
    header: '' ,
    footer: '10 Items | Displaying 1 of 5|LAST UPDATED:' ,
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
  },

  { dataSourceType: 'Incidents',
    header: ' ' ,
    footer: '10 Items | Displaying 1 of 5|LAST UPDATED:',
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
        field: 'id',
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
        field: 'assignee',
        title: 'context.incident.assignee',
        width: '9%',
        nested: ''
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
  }

];
