import { module, test } from 'qunit';
import { createQueryHash } from 'investigate-events/util/query-hash';

module('Unit | Util | query-hash');

test('creates proper query hash', function(assert) {
  const pills = [
    { meta: 'a', operator: 'b', value: 'c', complexFilterText: 'd' },
    { meta: 'e', operator: 'f', value: 'g', complexFilterText: 'h' }
  ];
  const hash = createQueryHash('service', 'sTime', 'eTime', pills);
  assert.equal(
    hash,
    'service-sTime-eTime-a-b-c-d-e-f-g-h',
    'hash is created properly'
  );
});
test('creates proper query hash for values which are null or undefined', function(assert) {
  const pills = [
    { meta: 'a', operator: 'b', value: null, complexFilterText: undefined },
    { meta: 'e', operator: 'f', value: 'g', complexFilterText: 'h' }
  ];
  const hash = createQueryHash('service', 'sTime', 'eTime', pills);
  assert.equal(
    hash,
    'service-sTime-eTime-a-b-undefined-undefined-e-f-g-h',
    'hash is created properly'
  );
});