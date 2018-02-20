import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import { getPreferencesSchema, getContextualHelp } from 'preferences/reducers/preferences-panel/selectors';
import prefConfig from '../../../data/config';

module('Unit | Selectors | Preferences-Panel');

test('get the preference schema', function(assert) {
  const response = prefConfig.items;
  const state = Immutable.from({
    preferencesConfig: {
      items: prefConfig.items
    }
  });
  const preferencesSchema = getPreferencesSchema(state);
  assert.deepEqual(preferencesSchema, response);
});

test('get the preference schema as null', function(assert) {
  const response = null;
  const state = Immutable.from({
    isExpanded: false,
    additionalFilters: null
  });
  const preferencesSchema = getPreferencesSchema(state);
  assert.equal(preferencesSchema, response);
});

test('Get the helpId while clicking on help icon', function(assert) {
  const response = {
    moduleId: 'investigation',
    topicId: 'investigateEventPreferences'
  };
  const state = Immutable.from({
    preferencesConfig: {
      helpIds: prefConfig.helpIds
    }
  });
  const helpIds = getContextualHelp(state);
  assert.deepEqual(helpIds, response);
});
