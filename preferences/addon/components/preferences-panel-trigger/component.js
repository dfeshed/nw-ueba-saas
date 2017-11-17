import Component from 'ember-component';
import layout from './template';
import { connect } from 'ember-redux';
import { togglePreferencesPanel } from 'preferences/actions/interaction-creators';
import observer from 'ember-metal/observer';

const stateToComputed = ({ preferences: { isExpanded, preferences } }) => ({
  isExpanded,
  preferences
});

const dispatchToActions = {
  togglePreferencesPanel
};

const PreferencesTrigger = Component.extend({
  layout,
  classNames: ['rsa-preferences-panel-trigger'],

  listenToPreferences: observer('preferences', function() {
    const preferences = this.get('preferences');
    if (preferences) {
      this.sendAction('publishPreferences', preferences);
    }
  })
});

export default connect(stateToComputed, dispatchToActions)(PreferencesTrigger);
