
import faker from 'faker';

function getObjects(array) {
  const obArray = array.map((st) => {
    return {
      id: faker.random.uuid(),
      query: st,
      displayName: faker.random.word(),
      createdBy: faker.random.word(),
      createdOn: faker.date.recent()
    };
  });
  return obArray;
}
export default {
  subscriptionDestination: '/user/queue/investigate/predicate/get-recent-by-filter',
  requestDestination: '/ws/investigate/predicate/get-recent-by-filter',
  message(frame) {
    const { predicateRequests } = JSON.parse(frame.body);
    const [ query ] = predicateRequests;
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

    const addtionalQueries = [
      'somethinhg is qeeual to nothing',
      'zilch = some text',
      'what = \'dunno\'',
      'have tp be = \'get\' || action = \'put\'',
      '(ip.dst = 10.2.54.11 && ip.src = 1.1.1.1 && medium = 32',
      'qwehqwkej = 80 || sdsad = 90',
      'bar = bar && baz = foo',
      'zzzzzqqq'
    ];

    const filterItems = (query) => {
      if (query.length > 0) {
        const array = getObjects(recentQueries).concat(getObjects(addtionalQueries));
        return array.filter((el) => el.query.toLowerCase().indexOf(query.toLowerCase()) !== -1);
      } else {
        return getObjects(recentQueries).filter((el) => el.query.toLowerCase().indexOf(query.toLowerCase()) !== -1);
      }
    };
    return {
      code: 0,
      data: filterItems(filterText)
    };
  }
};