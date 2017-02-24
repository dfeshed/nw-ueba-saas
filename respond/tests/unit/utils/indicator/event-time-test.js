import indicatorEventTime from 'respond/utils/indicator/event-time';
import { module, test } from 'qunit';

module('Unit | Utility | indicator/event time');

test('it works', function(assert) {
  let result;
  result = indicatorEventTime({ time: 10 });
  assert.equal(result, 10, 'Expected time property to be read.');

  result = indicatorEventTime({ timestamp: 20 });
  assert.equal(result, 20, 'Expected timestamp property to be read.');

  const now = new Date();
  const nowString = String(now);

  result = indicatorEventTime({ timestamp: nowString });
  assert.notOk(isNaN(result), 'Expected timestamp string to be converted to a number.');

  result = indicatorEventTime({ });
  assert.equal(result, undefined, 'Expected empty input to return undefined.');

});

