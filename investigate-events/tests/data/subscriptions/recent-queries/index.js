import faker from 'faker';
import _ from 'lodash';

/**
 * List of recent queries
 */
export const recentQueries = [
  'medium = 32',
  'medium = 32 || medium = 1',
  'sessionid = 1 && sessionid = 80',
  'action = \'GET\' || action = \'PUT\'',
  '(ip.dst = 10.2.54.11 && ip.src = 1.1.1.1 || ip.dst = 10.2.54.1 && ip.src = 1.1.3.3) && medium = 32',
  'service = 80 || service = 90',
  'foo = bar && bar = foo'
];

const _asObject = (str) => ({
  id: faker.random.uuid(),
  query: str,
  displayName: faker.random.word(),
  createdBy: faker.random.word(),
  createdOn: faker.date.recent()
});
const asObject = _.memoize(_asObject);

const _getObjects = (array) => array.map((st) => asObject(st));
const getObjects = _.memoize(_getObjects);

const filteredRecentQueries = (filterText) => recentQueries.filter((el) => {
  return el.toLowerCase().indexOf(filterText.toLowerCase()) !== -1;
});

export default {
  subscriptionDestination: '/user/queue/investigate/predicate/get-recent-by-filter',
  requestDestination: '/ws/investigate/predicate/get-recent-by-filter',
  message(frame) {
    const { predicateRequests } = JSON.parse(frame.body);
    const [ query ] = predicateRequests;
    const { filterText } = query;
    return {
      code: 0,
      data: getObjects(filteredRecentQueries(filterText))
    };
  }
};