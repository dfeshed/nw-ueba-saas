import SummariesCache from 'dummy/utils/summaries-cache';
import { module, test } from 'qunit';

module('Unit | Utility | summaries cache');

// Generate some dummy requests for entity type + id pairs.
const requestsCount = 3;
const requests = (new Array(requestsCount))
  .fill(0)
  .map((d, i) => ({ type: 'foo', id: String(i) }));

const [ first, second, third ] = requests;

test('add() will cache the latest requests up to a configurable requestsLimit', function(assert) {
  const cache = SummariesCache.create({
    requestsLimit: 2
  });
  assert.ok(cache);

  cache.add([ first, second ]);

  assert.equal(cache.get('requests.length'), 2);
  assert.ok(cache.find(first.type, first.id));
  assert.ok(cache.find(second.type, second.id));

  cache.add([ third ]);

  assert.equal(cache.get('requests.length'), 2);
  assert.notOk(cache.find(requests[0].type, requests[0].id));
  assert.ok(cache.find(second.type, second.id));
  assert.ok(cache.find(third.type, third.id));
});

test('calling add() twice with the same entities does not create duplicate entries', function(assert) {
  const cache = SummariesCache.create();
  cache.add(requests);

  assert.equal(cache.get('requests.length'), requestsCount);

  cache.add(requests);

  assert.equal(cache.get('requests.length'), requestsCount);
});

test('the requests list is kept sorted chronologically (descending)', function(assert) {
  const cache = SummariesCache.create();
  cache.add(requests);

  // The cache's `requests` list should be in the reverse order that the entities were passed into `add()`.
  assert.equal(cache.get('requests.firstObject.id'), third.id);
  assert.equal(cache.get('requests.lastObject.id'), first.id);

  cache.add([ second ]);

  // Now the latest request passed into `add()` should be bumped to the top of the cache's `requests` list.
  assert.equal(cache.get('requests.firstObject.id'), second.id);
});

test('remove works as expected', function(assert) {
  const cache = SummariesCache.create();
  cache.add(requests);

  // find() should return a result for any of the added requests
  const { type, id } = first;
  assert.ok(cache.find(type, id));

  cache.remove([ first ]);

  // find() shouldn't find the removed request anymore
  assert.notOk(cache.find(type, id));

  // the cached list should be reduced by one
  assert.equal(cache.get('requests.length'), requestsCount - 1);
});