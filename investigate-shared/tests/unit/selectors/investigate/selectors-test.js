import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import { setupTest } from 'ember-qunit';

import {
  timeRange,
  startTime,
  endTime
} from 'investigate-shared/selectors/investigate/selectors';

module('Unit | Selectors | investigate', function(hooks) {

  setupTest(hooks);

  test('Returns timeRange value', function(assert) {
    const state = Immutable.from({
      investigate: {
        timeRange: {
          value: 1,
          unit: 'days'
        }
      }
    });
    const data = timeRange(state);
    assert.equal(data.unit, 'days');
    assert.equal(data.value, 1);
  });

  test('Returns startTime value', function(assert) {
    const state = Immutable.from({
      investigate: {
        startTime: 1234567890
      }
    });
    const data = startTime(state);
    assert.equal(data, 1234567890);
  });

  test('Returns endTime value', function(assert) {
    const state = Immutable.from({
      investigate: {
        endTime: 1234567891
      }
    });
    const data = endTime(state);
    assert.equal(data, 1234567891);
  });

});
