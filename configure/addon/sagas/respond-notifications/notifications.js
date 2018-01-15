import * as ACTION_TYPES from 'configure/actions/types/respond';
import respondAPI from 'configure/actions/api/respond';
import { call, put, takeLatest } from 'redux-saga/effects';
import { success, failure } from 'configure/sagas/flash-messages';

const { notifications } = respondAPI;

function* fetchNotificationSettingsAsync() {
  try {
    yield put({ type: ACTION_TYPES.FETCH_NOTIFICATION_SETTINGS_STARTED });
    const payload = yield call(notifications.getNotificationSettings);
    yield put({ type: ACTION_TYPES.FETCH_NOTIFICATION_SETTINGS, payload });
  } catch (e) {
    yield put({ type: ACTION_TYPES.FETCH_NOTIFICATION_SETTINGS_FAILED });
  }
}

function* updateNotificationSettingsAsync(action) {
  const { notificationSettings } = action;
  try {
    yield put({ type: ACTION_TYPES.UPDATE_NOTIFICATION_SETTINGS_STARTED });
    const payload = yield call(notifications.updateNotificationSettings, notificationSettings);
    yield put({ type: ACTION_TYPES.UPDATE_NOTIFICATION_SETTINGS, payload });
    success('configure.notifications.actionMessages.updateSuccess');
  } catch (e) {
    yield put({ type: ACTION_TYPES.UPDATE_NOTIFICATION_SETTINGS_FAILED });
    failure('configure.notifications.actionMessages.updateFailure');
  }
}

export function* fetchNotificationSettings() {
  yield takeLatest(ACTION_TYPES.FETCH_NOTIFICATION_SETTINGS_SAGA, fetchNotificationSettingsAsync);
}

export function* updateNotificationSettings() {
  yield takeLatest(ACTION_TYPES.UPDATE_NOTIFICATION_SETTINGS_SAGA, updateNotificationSettingsAsync);
}
