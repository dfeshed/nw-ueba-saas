const _timeframesForDateTimeFilter = [
  {
    'name': 'LAST_SEVEN_DAYS',
    'unit': 'Days',
    'value': 7
  },
  {
    'name': 'LAST_TWO_WEEKS',
    'unit': 'Weeks',
    'value': 2
  },
  {
    'name': 'LAST_ONE_MONTH',
    'unit': 'Months',
    'value': 1
  },
  {
    'name': 'LAST_THREE_MONTH',
    'unit': 'Months',
    'value': 3
  },
  {
    'name': 'LAST_SIX_MONTH',
    'unit': 'Months',
    'value': 6
  }
];

export const columnsDataForIndicatorTable = [{
  field: 'name',
  width: '22vw',
  disableSort: true
}, {
  field: 'anomalyValue',
  width: '20vw',
  disableSort: true
}, {
  field: 'startDate',
  width: '14vw',
  disableSort: true
}, {
  field: 'numOfEvents',
  width: 'auto',
  disableSort: true
}];

export const columnConfigForUsers = [{
  field: 'score',
  width: '3.5vw',
  disableSort: true
}, {
  field: 'displayName',
  width: '20vw',
  disableSort: true
},
{
  field: 'followed',
  width: '5vw',
  disableSort: true
}, {
  field: 'isAdmin',
  width: '5vw',
  disableSort: true
}, {
  field: 'alerts',
  width: 'auto',
  disableSort: true
}];

export const severityMap = {
  Critical: 'danger',
  High: 'high',
  Medium: 'medium',
  Low: 'low'
};

export const columnDataForFavorites = [{
  title: 'favorites',
  field: 'filterName',
  width: '95%',
  disableSort: true
}];

export const sortOptions = [{
  id: 'score'
}, {
  id: 'name'
}, {
  id: 'alerts'
}];

export const dateTimeFilterOptionsForAlerts = {
  name: 'alertTimeRange',
  timeframes: _timeframesForDateTimeFilter,
  filterValue: {
    value: [ 3 ],
    unit: 'Months'
  }
};