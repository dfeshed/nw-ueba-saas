import Component from 'ember-component';
import layout from './template';
import { connect } from 'ember-redux';
import { saveNewPreferences } from 'preferences/actions/interaction-creators';
import { getPreferencesSchema } from 'preferences/reducers/preferences-panel/selectors';

const stateToComputed = ({ preferences }) => ({
  preferences: preferences.preferences,
  preferencesSchema: getPreferencesSchema(preferences),
  fieldPrefix: preferences.preferencesConfig.fieldPrefix
});

const dispatchToActions = {
  saveNewPreferences
};

const PreferencesDetails = Component.extend({
  layout
});

export default connect(stateToComputed, dispatchToActions)(PreferencesDetails);
