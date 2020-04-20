import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'health-wellness/reducers/reducers';
import * as ACTION_TYPES from 'health-wellness/actions/types';
import makePackAction from '../helpers/make-pack-action';
import { setupTest } from 'ember-qunit';
import { LIFECYCLE } from 'redux-pack';


module('Unit | Reducers | health-wellness', function(hooks) {
  setupTest(hooks);

  const monitors = [
    {
      'id': 'q6_IgW0B0ftGE-k_s-5s',
      'severity': 1,
      'monitor': 'Archiver Aggregation Stopped',
      'trigger': 'Aggregation Stopped',
      'enabled': true,
      'suppressionConfigured': true
    },
    {
      'id': 'q6_IgW0B0ftGE-k_s-5s',
      'severity': 1,
      'monitor': 'Archiver Aggregation Stopped',
      'trigger': 'Aggregation Stopped',
      'enabled': true,
      'suppressionConfigured': false
    },
    {
      'id': 'q6_IgW0B0ftGE-k_s-5s',
      'severity': 1,
      'monitor': 'Archiver Aggregation Stopped',
      'trigger': 'Aggregation Stopped',
      'enabled': true,
      'suppressionConfigured': false
    }
  ];

  test('The GET_MONITORS gets the monitors', function(assert) {

    const previous = Immutable.from({
      monitors: []
    });

    const newAction = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.GET_MONITORS,
      payload: { data: monitors }
    });

    const newState = reducer(previous, newAction);
    assert.equal(newState.monitors.length, 3);
  });

  test('The GET_MONITORS sets the error state', function(assert) {

    const previous = Immutable.from({
      isError: false
    });

    const newAction = makePackAction(LIFECYCLE.FAILURE, {
      type: ACTION_TYPES.GET_MONITORS
    });

    const newState = reducer(previous, newAction);
    assert.equal(newState.isError, true);
  });

});
