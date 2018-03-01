import Component from '@ember/component';
import layout from './template';
import { connect } from 'ember-redux';
import { togglePreferencesPanel } from 'preferences/actions/interaction-creators';
import { observer } from '@ember/object';
import _ from 'lodash';

const stateToComputed = ({ preferences: { isExpanded, preferences, shouldPublishPreferences, changedField } }) => ({
  isExpanded,
  preferences,
  shouldPublishPreferences,
  changedField
});

const dispatchToActions = {
  togglePreferencesPanel
};

const PreferencesTrigger = Component.extend({
  layout,
  classNames: ['rsa-preferences-panel-trigger'],

  listenToPreferences: observer('preferences', function() {
    const preferences = this.get('preferences');
    const changedField = this.get('changedField');
    if (preferences && changedField && this.get('shouldPublishPreferences')) {
      this.sendAction('publishPreferences', _.pick(preferences, [changedField]));
    }
  })
});

export default connect(stateToComputed, dispatchToActions)(PreferencesTrigger);
