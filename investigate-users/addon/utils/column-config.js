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
  width: '3vw',
  disableSort: true
}, {
  field: 'trendingScore.daily',
  width: '7vw',
  disableSort: true
}, {
  field: 'trendingScore.weekly',
  width: '7vw',
  disableSort: true
}, {
  field: 'displayName',
  width: '10vw',
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
  id: 'alertsCount'
}, {
  id: 'trending.daily'
}, {
  id: 'trending.weekly'
}];