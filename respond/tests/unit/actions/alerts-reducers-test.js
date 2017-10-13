import Immutable from 'seamless-immutable';
import { module, test } from 'qunit';
import alertsReducer from 'respond/reducers/respond/alerts/alerts';
import ACTION_TYPES from 'respond/actions/types';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../helpers/make-pack-action';

const alertNotPartOfIncident = Immutable.from({
  id: '5833fee2a7c89226086a0956',
  partOfIncident: false,
  incidentId: null
});

const anotherAlertNotPartOfIncident = Immutable.from({
  id: '5845fedca7c89226086a0812',
  partOfIncident: false,
  incidentId: null
});

const initialState = Immutable.from({
  isTransactionUnderway: false,
  items: [alertNotPartOfIncident, anotherAlertNotPartOfIncident]
});

const createIncidentResponsePayload = {
  'data': {
    'id': 'INC-123'
  },
  'request': {
    'data': {
      'associated': ['5833fee2a7c89226086a0956']
    }
  }
};

const addAlertsToIncidentResponsePayload = {
  data: ['5845fedca7c89226086a0812'],
  request: {
    data: {
      entity: {
        id: 'INC-123'
      },
      associated: ['5845fedca7c89226086a0812']
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
    items: [testAlert, anotherAlertNotPartOfIncident]
  };
  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.CREATE_INCIDENT,
    payload: createIncidentResponsePayload
  });
  const endState = alertsReducer(initialState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('The ALERTS_ADD_TO_INCIDENT reducer updates the alerts with incidentId and partOfIncident', function(assert) {
  const testAlert = anotherAlertNotPartOfIncident.asMutable();
  testAlert.partOfIncident = true;
  testAlert.incidentId = 'INC-123';
  const expectedEndState = {
    ...initialState,
    items: [alertNotPartOfIncident, testAlert]
  };
  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.ALERTS_ADD_TO_INCIDENT,
    payload: addAlertsToIncidentResponsePayload
  });
  const endState = alertsReducer(initialState, action);
  assert.deepEqual(endState, expectedEndState);
});
