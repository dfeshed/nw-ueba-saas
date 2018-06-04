import Immutable from 'seamless-immutable';
import { module, test } from 'qunit';
import incidentsReducer from 'respond/reducers/respond/incidents';
import ACTION_TYPES from 'respond/actions/types';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../helpers/make-pack-action';

module('Unit | Utility | Incidents Reducer (non-Explorer)', function() {

  const incident = {
    id: 'INC-2'
  };

  test('When SEND_INCIDENT_TO_ARCHER succeeds, the state is updated', function(assert) {
    const initState = {
      items: [
        {
          id: 'INC-1'
        },
        incident
      ],
      focusedItem: incident
    };
    const expectedEndState = {
      items: [
        {
          id: 'INC-1'
        },
        {
          id: 'INC-2',
          sentToArcher: true
        }
      ],
      focusedItem: {
        id: 'INC-2',
        sentToArcher: true
      }
    };
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.SEND_INCIDENT_TO_ARCHER,
      payload: { data: { id: 'INC-2', sentToArcher: true } }
    });
    const endState = incidentsReducer(Immutable.from(initState), action);
    assert.deepEqual(endState, expectedEndState);
  });

  test('When FETCH_INCIDENT_SETTINGS succeeds, the state is updated', function(assert) {
    const initState = {
      isSendToArcherAvailable: false
    };
    const expectedEndState = {
      isSendToArcherAvailable: true
    };
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.FETCH_INCIDENTS_SETTINGS,
      payload: { data: { isArcherDataSourceConfigured: true } }
    });
    const endState = incidentsReducer(Immutable.from(initState), action);
    assert.deepEqual(endState, expectedEndState);
  });
});