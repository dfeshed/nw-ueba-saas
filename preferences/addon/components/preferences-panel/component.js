import Component from 'ember-component';
import layout from './template';
import run from 'ember-runloop';
import { connect } from 'ember-redux';
import { closePreferencesPanel, resetPreferencesPanel, loadPreferences } from 'preferences/actions/interaction-creators';
import computed from 'ember-computed-decorators';
import service from 'ember-service/inject';

const stateToComputed = ({ preferences: { launchFor, expanded } }) => ({
  isExpanded: expanded,
  launchFor
});

const dispatchToActions = {
  closePreferencesPanel,
  resetPreferencesPanel,
  loadPreferences
};

const PreferencesPanel = Component.extend({

  layout,

  classNames: ['rsa-preferences-panel'],
  classNameBindings: ['isExpanded'],

  preferences: service(),

  init() {
    this._super(arguments);
    this.addObserver('isExpanded', this, this.loadOrResetPreferences);
  },

  loadOrResetPreferences() {
    // wait for the panel to slide open/close completely before sending action
    run.later(() => {
      if (this.get('isExpanded')) {
        this.send('loadPreferences', this.get('preferences'));
      } else {
        this.send('resetPreferencesPanel');
      }
    }, 650);
  },

  @computed('launchFor')
  preferencesFor(preferenceFor) {
    if (preferenceFor) {
      return `${preferenceFor}-preferences`;
    }
  },

  actions: {
    closePreferencesPanel() {
      this.send('closePreferencesPanel', false);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(PreferencesPanel);