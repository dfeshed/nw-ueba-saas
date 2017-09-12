import Immutable from 'seamless-immutable';
import { module, test } from 'qunit';
import alertsReducer from 'respond/reducers/respond/alerts';
import ACTION_TYPES from 'respond/actions/types';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../helpers/make-pack-action';

const alertNotPartOfIncident = Immutable.from({
  id: '5833fee2a7c89226086a0956',
  partOfIncident: false,
  incidentId: null
});
const initialState = Immutable.from({
  isTransactionUnderway: false,
  items: [alertNotPartOfIncident]
});

const createIncidentResponsePayload = {
  'data': {
    'id': 'INC-123'
  },
  'request': {
    'data': {
      'associated': [
        {
          'id': '5833fee2a7c89226086a0956'
        }
      ]
    }
  }
};

module('Unit | Utility | Alerts Reducers (non-Explorer)');

test('The CREATE_INCIDENT reducer updates the alerts with incidentId and partOfIncident', function(assert) {
  const testAlert = alertNotPartOfIncident.asMutable();
  testAlert.partOfIncident = true;
  testAlert.incidentId = 'INC-123';
  const expectedEndState = {
    ...initialState,
    items: [testAlert]
  };
  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.CREATE_INCIDENT,
    payload: createIncidentResponsePayload
  });
  const endState = alertsReducer(initialState, action);
  assert.deepEqual(endState, expectedEndState);
});
