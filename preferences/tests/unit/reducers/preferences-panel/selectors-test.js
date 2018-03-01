import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import {
  getPreferencesSchema,
  getContextualHelp,
  getDbEndTime,
  getDbStartTime
} from 'preferences/reducers/preferences-panel/selectors';
import prefConfig from '../../../data/config';

module('Unit | Selectors | Preferences-Panel');

const state = Immutable.from({
  preferencesConfig: {
    items: prefConfig.items,
    helpIds: prefConfig.helpIds
  },
  investigate: {
    services: {
      summaryData: {
        startTime: '1234',
        endTime: '6789'
      }
    }
  }
});

test('get the preference schema', function(assert) {
  const response = prefConfig.items;
  const preferencesSchema = getPreferencesSchema(state);
  assert.deepEqual(preferencesSchema, response);
});

test('get the preference schema as null', function(assert) {
  const response = null;
  const missingPreferencesConfig = state.setIn(['preferencesConfig'], null);
  const preferencesSchema = getPreferencesSchema(missingPreferencesConfig);
  assert.equal(preferencesSchema, response);
});

test('Get the helpId while clicking on help icon', function(assert) {
  const response = {
    moduleId: 'investigation',
    topicId: 'investigateEventPreferences'
  };
  const helpIds = getContextualHelp(state);
  assert.deepEqual(helpIds, response);
});

test('Retrieve database end time', function(assert) {
  assert.equal(getDbEndTime(state), 6789, 'Gets database end time');
});

test('Retrieve database start time', function(assert) {
  assert.equal(getDbStartTime(state), 1234, 'Gets database start time');
});
