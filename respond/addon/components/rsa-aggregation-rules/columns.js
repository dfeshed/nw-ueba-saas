// Search Incidents Results Table columns (for associating alerts with an incident)
export default [
  {
    id: 'drag',
    title: 'empty',
    width: '15px'
  },
  {
    id: 'select',
    title: 'respond.aggregationRules.select',
    width: '3%'
  },
  {
    field: 'order',
    title: 'respond.aggregationRules.order',
    width: '3%',
    dataType: 'text'
  },
  {
    field: 'enabled',
    title: 'respond.aggregationRules.enabled',
    width: '3%',
    dataType: 'text'
  },
  {
    field: 'name',
    title: 'respond.aggregationRules.name',
    width: '22%',
    dataType: 'text'
  },
  {
    field: 'description',
    title: 'respond.aggregationRules.description',
    width: '45%',
    dataType: 'text'
  },
  {
    field: 'lastMatched',
    title: 'respond.aggregationRules.lastMatched',
    width: '5%',
    dataType: 'date'
  },
  {
    field: 'alertsMatchedCount',
    title: 'respond.aggregationRules.alertsMatchedCount',
    width: '5%',
    dataType: 'number'
  },
  {
    field: 'incidentsCreatedCount',
    title: 'respond.aggregationRules.incidentsCreatedCount',
    width: '5%',
    dataType: 'number'
  }
];
