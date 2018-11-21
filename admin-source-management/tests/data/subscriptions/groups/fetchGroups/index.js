import data from './data';

const sortBy = function(field, descending, primer) {
  const key = primer ?
    function(x) {
      return primer(x[field]);
    } :
    function(x) {
      return x[field];
    };
  descending = !descending ? 1 : -1;
  return function(a, b) {
    return a = key(a), b = key(b), descending * ((a > b) - (b > a));
  };
};

export default {
  subscriptionDestination: '/user/queue/usm/groups/search',
  requestDestination: '/ws/usm/groups/search',
  message(frame) {
    /* this function mocks some of the sorting using single column
      and works on the non-composite sorted columns: name, description
    */
    const body = JSON.parse(frame.body);
    let sortedData = data;
    /* eslint-disable */
    const sortColumn = body.data.sort.keys[0];
    const descending = body.data.sort.descending;
    /* eslint-disable no-console */
    console.log('sortColumn=', sortColumn);
    console.log('descending=', descending);
    /* eslint-enable */
    switch (sortColumn) {
      case 'name':
      case 'description':
        sortedData = data.sort(sortBy(sortColumn, descending, function(a) {
          return a.toUpperCase();
        }));
        break;
      case 'sourceCount':
        sortedData = data.sort(sortBy(sortColumn, descending, parseInt));
        break;
      default:
        break;
    }
    return {
      data: {
        items: sortedData,
        totalItems: 14
      }
    };
  }
};
