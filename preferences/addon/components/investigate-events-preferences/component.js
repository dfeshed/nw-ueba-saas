import Component from 'ember-component';
import layout from './template';
import eventConfig from './config';
import service from 'ember-service/inject';
import { connect } from 'ember-redux';
import { fetchPreferences, savePreferences } from 'preferences/actions/interaction-creators';
import computed from 'ember-computed-decorators';
import _ from 'lodash';

const stateToComputed = ({ preferences: { data, status } }) => ({
  data,
  status
});

const dispatchToActions = {
  fetchPreferences,
  savePreferences
};

const EventsPreferences = Component.extend({
  layout,
  preferences: service(),
  defaultEventView: eventConfig.defaultEventView,

  @computed('data', 'status')
  isReady(data, status) {
    const defaultAnalysisView = _.get(data, 'eventsPreferences.defaultAnalysisView');
    if (!defaultAnalysisView) {
      return;
    }
    if (status === 'success') {
      if (!(this.get('isDestroyed') || this.get('isDestroying'))) {
        this.set('defaultEventView.defaultValue', defaultAnalysisView);
        return true;
      }
    } else {
      this.set('defaultEventView.defaultValue', this.defaultEventView.defaultValue);
      return true;
    }
  },

  actions: {
    setSelectedEventView(selectedPreference) {
      const preferencesToSave = { 'eventsPreferences': { 'defaultAnalysisView': selectedPreference } };
      this.set('defaultEventView.defaultValue', selectedPreference);
      this.send('savePreferences', preferencesToSave);
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(EventsPreferences);
