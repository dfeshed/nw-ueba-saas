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

  test('When ESCALATE_INCIDENT succeeds, the state is updated', function(assert) {
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
          escalationStatus: 'ESCALATED'
        }
      ],
      focusedItem: {
        id: 'INC-2',
        escalationStatus: 'ESCALATED'
      },
      isTransactionUnderway: false
    };
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.ESCALATE_INCIDENT,
      payload: { data: { id: 'INC-2', escalationStatus: 'ESCALATED' } }
    });
    const endState = incidentsReducer(Immutable.from(initState), action);
    assert.deepEqual(endState, expectedEndState);
  });

  test('When FETCH_INCIDENT_SETTINGS succeeds, the state is updated', function(assert) {
    const initState = {
      isEscalateAvailable: false
    };
    const expectedEndState = {
      isEscalateAvailable: true
    };
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.FETCH_INCIDENTS_SETTINGS,
      payload: { data: { isArcherDataSourceConfigured: true } }
    });
    const endState = incidentsReducer(Immutable.from(initState), action);
    assert.deepEqual(endState, expectedEndState);
  });
});