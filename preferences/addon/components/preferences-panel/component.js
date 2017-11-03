import Component from 'ember-component';
import layout from './template';
import { later, schedule, next } from 'ember-runloop';
import { connect } from 'ember-redux';
import { closePreferencesPanel, resetPreferencesPanel, loadPreferences, updatePanelState } from 'preferences/actions/interaction-creators';
import computed from 'ember-computed-decorators';
import service from 'ember-service/inject';

const stateToComputed = ({ preferences: { launchFor, expanded, clicked } }) => ({
  isExpanded: expanded,
  launchFor,
  clicked
});

const dispatchToActions = {
  closePreferencesPanel,
  resetPreferencesPanel,
  loadPreferences,
  updatePanelState
};

const PreferencesPanel = Component.extend({

  layout,

  classNames: ['rsa-preferences-panel'],
  classNameBindings: ['isExpanded'],

  preferences: service(),
  eventBus: service(),
  panelClicked: false,

  init() {
    this._super(arguments);
    this.addObserver('isExpanded', this, this.loadOrResetPreferences);
  },
  click() {
    this.send('updatePanelState', true);
  },

  _closePreferencesPanel() {
    next(() => {
      if (!this.get('clicked')) {
        this.send('closePreferencesPanel');
      }
      this.send('updatePanelState', false);
    });
  },
  didInsertElement() {
    schedule('afterRender', () => {
      this.get('eventBus').on('rsa-application-click', () => {
        this._closePreferencesPanel();
      });
      this.get('eventBus').on('rsa-application-header-click', () => {
        this._closePreferencesPanel();
      });
    });
  },
  /**
   * Unbind the events
   * @public
   */
  willDestroyElement() {
    this.get('eventBus').off('rsa-application-click');
    this.get('eventBus').off('rsa-application-header-click');
  },

  loadOrResetPreferences() {
    // wait for the panel to slide open/close completely before sending action
    later(() => {
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
