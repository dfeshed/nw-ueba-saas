import { module, test } from 'qunit';
import reducer from 'investigate-events/reducers/investigate/services/reducer';
import * as ACTION_TYPES from 'investigate-events/actions/types';
import Immutable from 'seamless-immutable';

module('Unit | Reducer | Services', function() {

  test('SUMMARY_UPDATE will override summaryData when called', function(assert) {

    const prevState = Immutable.from({
      summaryData: {
        startMetaId: 1,
        endMetaId: 3113888,
        metaSize: 55296486,
        metaMax: 143043885793,
        startTime: 1202921710,
        endTime: 1526937000
      }
    });
    const action = {
      type: ACTION_TYPES.SUMMARY_UPDATE,
      payload: {
        endTime: 1800000000,
        endMetaId: 123456789,
        metaSize: 20494880,
        metaMax: 143043885793,
        startTime: 1202921710,
        startMetaId: 2
      }
    };
    const result = reducer(prevState, action);

    assert.deepEqual(result.summaryData, {
      startMetaId: 2,
      endMetaId: 123456789,
      metaSize: 20494880,
      metaMax: 143043885793,
      startTime: 1202921710,
      endTime: 1800000000
    });
  });
});