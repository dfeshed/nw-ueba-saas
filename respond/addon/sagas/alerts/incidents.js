import * as ACTION_TYPES from 'respond/actions/types';
import * as ErrorHandlers from 'respond/actions/util/error-handlers';
import { Incidents } from 'respond/actions/api';
import { lookup } from 'ember-dependency-lookup';
import { call, put, takeLatest } from 'redux-saga/effects';
import { success, failure } from 'respond/sagas/flash-messages';
import Ember from 'ember';

const { Logger } = Ember;

function* createIncidentAsync(action) {
  try {
    const { incidentName, alertIds } = action;
    yield put({ type: ACTION_TYPES.START_TRANSACTION });
    const payload = yield call(Incidents.createIncidentFromAlerts, incidentName, alertIds);
    yield put({ type: ACTION_TYPES.CREATE_INCIDENT, payload });
    lookup('service:eventBus').trigger('rsa-application-modal-close-create-incident');
    success('respond.incidents.actions.actionMessages.incidentCreated', { incidentId: payload.data.id });
    Logger.debug(ACTION_TYPES.CREATE_INCIDENT, payload);
  } catch (e) {
    failure('respond.incidents.actions.actionMessages.incidentCreationFailed');
    ErrorHandlers.handleContentCreationError(e, 'create incident');
  } finally {
    yield put({ type: ACTION_TYPES.FINISH_TRANSACTION });
  }
}

export function* createIncident() {
  yield takeLatest(ACTION_TYPES.CREATE_INCIDENT_SAGA, createIncidentAsync);
}
