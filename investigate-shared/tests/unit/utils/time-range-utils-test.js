import { module, test } from 'qunit';
import { getTimeRangeIdFromRange } from 'investigate-shared/utils/time-range-utils';

module('Unit | Utils | Time Range Utils');
test('_getTimeRangeIdFromRange returns the TimeRangeId correctly', function(assert) {
  assert.expect(4);
  const result1 = getTimeRangeIdFromRange(1522698300, 1522698599); // last 5 mins
  assert.equal(result1, 'LAST_5_MINUTES');
  const result2 = getTimeRangeIdFromRange(1522496700, 1522669499); // last 2 days
  assert.equal(result2, 'LAST_2_DAYS');
  const result3 = getTimeRangeIdFromRange(1520107680, 1522699679); // last 30 days
  assert.equal(result3, 'LAST_30_DAYS');
  const result4 = getTimeRangeIdFromRange(1520879520, 1522698599); // ALL_DATA
  assert.equal(result4, 'ALL_DATA');
});
