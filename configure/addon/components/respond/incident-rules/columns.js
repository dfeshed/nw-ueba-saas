// Search Incidents Results Table columns (for associating alerts with an incident)
export default [
  {
    id: 'drag',
    title: 'empty',
    width: '15px'
  },
  {
    id: 'select',
    title: 'configure.incidentRules.select',
    width: '3%'
  },
  {
    field: 'order',
    title: 'configure.incidentRules.order',
    width: '3%',
    dataType: 'text'
  },
  {
    field: 'enabled',
    title: 'configure.incidentRules.enabled',
    width: '3%',
    dataType: 'text'
  },
  {
    field: 'name',
    title: 'configure.incidentRules.name',
    width: '20%',
    dataType: 'text'
  },
  {
    field: 'description',
    title: 'configure.incidentRules.description',
    width: '45%',
    dataType: 'text'
  },
  {
    field: 'lastMatched',
    title: 'configure.incidentRules.lastMatched',
    width: '5%',
    dataType: 'date'
  },
  {
    field: 'alertsMatchedCount',
    title: 'configure.incidentRules.alertsMatchedCount',
    width: '5%',
    dataType: 'number'
  },
  {
    field: 'incidentsCreatedCount',
    title: 'configure.incidentRules.incidentsCreatedCount',
    width: '5%',
    dataType: 'number'
  }
];
