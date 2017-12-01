import Component from 'ember-component';
import layout from './template';
import { later, schedule } from 'ember-runloop';
import { connect } from 'ember-redux';
import { closePreferencesPanel, resetPreferencesPanel, loadPreferences, updatePanelClicked } from 'preferences/actions/interaction-creators';
import { getContextualHelp } from 'preferences/reducers/preferences-panel/selectors';
import service from 'ember-service/inject';

const stateToComputed = ({ preferences }) => ({
  isExpanded: preferences.isExpanded,
  fieldPrefix: preferences.preferencesConfig.fieldPrefix,
  isClicked: preferences.isClicked,
  helpId: getContextualHelp(preferences)
});

const dispatchToActions = {
  closePreferencesPanel,
  resetPreferencesPanel,
  loadPreferences,
  updatePanelClicked
};

const PreferencesPanel = Component.extend({

  layout,

  classNames: ['rsa-preferences-panel'],
  classNameBindings: ['isExpanded'],

  eventBus: service(),
  contextualHelp: service(),

  init() {
    this._super(arguments);
    this.addObserver('isExpanded', this, this.loadOrResetPreferences);
  },
  click() {
    this.send('updatePanelClicked', true);
  },

  _closePreferencesPanel() {
    if (!this.get('isClicked')) {
      this.send('closePreferencesPanel');
    }
    this.send('updatePanelClicked', false);
  },
  didInsertElement() {
    schedule('afterRender', () => {
      this.get('eventBus').on('rsa-application-click', this, this._closePreferencesPanel);
      this.get('eventBus').on('rsa-application-header-click', this, this._closePreferencesPanel);
    });
  },
  /**
   * Unbind the events
   * @public
   */
  willDestroyElement() {
    this.get('eventBus').off('rsa-application-click', this, this._didApplicationClick);
    this.get('eventBus').off('rsa-application-header-click', this, this._didApplicationClick);
  },

  loadOrResetPreferences() {
    // wait for the panel to slide open/close completely before sending action
    later(() => {
      if (this.get('isExpanded')) {
        this.send('loadPreferences');
      } else {
        this.send('resetPreferencesPanel');
      }
    }, 650);
  },

  actions: {
    goToHelp() {
      const { moduleId, topicId } = this.get('helpId');
      this.get('contextualHelp').goToHelp(moduleId, topicId);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(PreferencesPanel);
