export default {
  subscriptionDestination: '/user/queue/investigate/predicate/recent-queries',
  requestDestination: '/ws/investigate/predicate/get-recent-by-filter',
  message(frame) {
    const { predicateRequest } = JSON.parse(frame.body);
    const [ query ] = predicateRequest;
    const { filterText } = query;
    const recentQueries = [
      'medium = 32',
      'medium = 32 || medium = 1',
      'action = \'get\'',
      'action = \'get\' || action = \'put\'',
      '(ip.dst = 10.2.54.11 && ip.src = 1.1.1.1 || ip.dst = 10.2.54.1 && ip.src = 1.1.3.3) && medium = 32',
      'service = 80 || service = 90',
      'foo = bar && bar = foo'
    ];

    const filterItems = (arr, query) => {
      return arr.filter((el) => el.toLowerCase().indexOf(query.toLowerCase()) !== -1);
    };
    return {
      code: 0,
      data: filterItems(recentQueries, filterText)
    };
  }
};