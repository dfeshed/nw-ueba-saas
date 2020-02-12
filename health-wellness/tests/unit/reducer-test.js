import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'health-wellness/reducers/reducers';
import * as ACTION_TYPES from 'health-wellness/actions/types';
import makePackAction from '../helpers/make-pack-action';
import { setupTest } from 'ember-qunit';
import { LIFECYCLE } from 'redux-pack';


module('Unit | Reducers | health-wellness', function(hooks) {
  setupTest(hooks);

  const monitors = {
    'items': [{
      'id': 'EkMnyG0BbK6JFp72teyl',
      'severity': 1,
      'monitor': 'Reporting Engine Shared Task Critical Utilization',
      'trigger': 'Shared Task Critical Utilization',
      'enabled': true,
      'suppressionConfigured': false
    },
    {
      'id': 'IUIEyG0BbK6JFp72n_Ka',
      'severity': 1,
      'monitor': 'Reporting Engine Schedule Task Pool Critical Utilization',
      'trigger': 'Scheduled Task Pool Critical utilization',
      'enabled': true,
      'suppressionConfigured': false
    }]
  };

  test('The GET_MONITORS increments the page number', function(assert) {

    const previous = Immutable.from({
      monitors: []
    });

    const newAction = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.GET_MONITORS,
      payload: { data: monitors }
    });

    const newState = reducer(previous, newAction);
    assert.equal(newState.monitors.length, 2);
  });

});
