import Component from 'ember-component';
import layout from './template';
import { connect } from 'ember-redux';
import { saveNewPreferences } from 'preferences/actions/interaction-creators';
import { getPreferencesConfig } from 'preferences/reducers/preferences-panel/selectors';

const stateToComputed = ({ preferences }) => ({
  launchFor: preferences.launchFor,
  preferences: preferences.preferences,
  preferencesConfig: getPreferencesConfig(preferences)
});

const dispatchToActions = {
  saveNewPreferences
};

const PreferencesDetails = Component.extend({
  layout
});

export default connect(stateToComputed, dispatchToActions)(PreferencesDetails);
