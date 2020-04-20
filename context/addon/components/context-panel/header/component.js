import { computed } from '@ember/object';
import { connect } from 'ember-redux';
import Component from '@ember/component';
import layout from './template';
import { inject as service } from '@ember/service';
import { contextHelpIds } from 'context/utils/help-ids';
import { restoreDefault } from 'context/actions/context-creators';

const stateToComputed = ({ context: { context: { lookupKey, errorMessage }, tabs: { activeTabName } } }) => ({
  activeTabName,
  lookupKey,
  errorMessage
});
const dispatchToActions = {
  restoreDefault
};

const HeaderComponent = Component.extend({
  layout,
  contextualHelp: service(),
  classNames: 'rsa-context-panel__header',
  testId: 'contextPanelHeader',

  attributeBindings: [
    'testId:test-id'
  ],

  headerTitle: computed('activeTabName', function() {
    if (this.activeTabName) {
      return this.get('i18n').t(`context.header.title.${this.activeTabName.camelize()}`);
    }
  }),

  actions: {
    goToHelp() {
      const { panelHelpId } = contextHelpIds();
      this.get('contextualHelp').goToHelp(panelHelpId.moduleId, panelHelpId.topicId);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(HeaderComponent);
