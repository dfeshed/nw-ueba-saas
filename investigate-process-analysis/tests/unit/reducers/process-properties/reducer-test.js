import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-process-analysis/reducers/process-properties/reducer';
import makePackAction from '../../../helpers/make-pack-action';
import { LIFECYCLE } from 'redux-pack';
import * as ACTION_TYPES from 'investigate-process-analysis/actions/types';

const initialState = Immutable.from({
  propertiesData: null
});

module('Unit | Reducers | process-properties', function() {

  test('should return the initial state', function(assert) {
    const result = reducer(undefined, {});
    assert.deepEqual(result, initialState);
  });

  test('FETCH_PROCESS_PROPERTIES set the data', function(assert) {
    const data = Immutable.from({
      propertiesData: null
    });
    const successAction = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.FETCH_PROCESS_PROPERTIES,
      payload: { data: [ { hostDetails: [ { machineOsType: 'windows' }] }] }
    });

    const result =
      [
        {
          'hostDetails': [
            {
              'machineOsType': 'windows'
            }
          ]
        }
      ];
    const newEndState = reducer(data, successAction);
    assert.deepEqual(newEndState.hostDetails, result);
  });

});