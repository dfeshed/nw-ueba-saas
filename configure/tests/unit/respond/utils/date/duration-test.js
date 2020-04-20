import { module, test } from 'qunit';
import { parseDuration } from 'configure/utils/date/duration';

module('Unit | Utility | Date Duration');

test('the parseDuration() function properly parses the duration string into the expected values/units', function(assert) {
  assert.deepEqual(parseDuration('10m'), { value: 10, unit: 'MINUTE' });
  assert.deepEqual(parseDuration('70m'), { value: 70, unit: 'MINUTE' });
  assert.deepEqual(parseDuration('120m'), { value: 2, unit: 'HOUR' });
  assert.deepEqual(parseDuration('60m'), { value: 1, unit: 'HOUR' });
  assert.deepEqual(parseDuration('10h'), { value: 10, unit: 'HOUR' });
  assert.deepEqual(parseDuration('25h'), { value: 25, unit: 'HOUR' });
  assert.deepEqual(parseDuration('24h'), { value: 1, unit: 'DAY' });
  assert.deepEqual(parseDuration('10d'), { value: 10, unit: 'DAY' });
  assert.deepEqual(parseDuration('4d3h'), { value: 99, unit: 'HOUR' });
});