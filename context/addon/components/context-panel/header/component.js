import { connect } from 'ember-redux';
import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';
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

  @computed('activeTabName')
  headerTitle(activeTabName) {
    if (activeTabName) {
      return this.get('i18n').t(`context.header.title.${activeTabName.camelize()}`);
    }
  },

  actions: {
    closeAction() {
      this.sendAction('closePanel');
      this.send('restoreDefault');
    },
    goToHelp() {
      const { panelHelpId } = contextHelpIds();
      this.get('contextualHelp').goToHelp(panelHelpId.moduleId, panelHelpId.topicId);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(HeaderComponent);
