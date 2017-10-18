import Component from 'ember-component';
import layout from './template';
import run from 'ember-runloop';
import { connect } from 'ember-redux';
import { closePreferencesPanel, resetPreferencesPanel } from 'preferences/actions/interaction-creators';

const stateToComputed = ({ preferences }) => ({
  isExpanded: preferences.expanded,
  launchFor: preferences.launchFor
});

const dispatchToActions = {
  closePreferencesPanel,
  resetPreferencesPanel
};

const PreferencesPanel = Component.extend({

  layout,

  classNames: ['rsa-preferences-panel'],
  classNameBindings: ['isExpanded'],

  init() {
    this._super(arguments);
    this.addObserver('isExpanded', this, this.resetPanel);
  },

  resetPanel() {
    if (!this.get('isExpanded')) {
      // wait for the panel to close completely before sending reset action, else the
      // panel title gets cleared immediately while the panel is still sliding out
      run.later(() => {
        this.send('resetPreferencesPanel');
      }, 650);
    }
  },

  actions: {
    closePreferencesPanel() {
      this.send('closePreferencesPanel', false);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(PreferencesPanel);