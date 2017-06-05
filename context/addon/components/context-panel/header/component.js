import connect from 'ember-redux/components/connect';
import Component from 'ember-component';
import layout from './template';
import computed from 'ember-computed-decorators';
import service from 'ember-service/inject';
import { contextHelpIds } from 'context/config/help-ids';
import { restoreDefault } from 'context/actions/context-creators';
import { pivotToInvestigateUrl } from 'context/util/context-data-modifier';
import { isEmpty } from 'ember-utils';

const stateToComputed = ({
  context
}) => ({
  activeTabName: context.activeTabName,
  lookupKey: context.lookupKey,
  toolbar: context.toolbar,
  meta: context.meta
});
const dispatchToActions = {
  restoreDefault
};

const HeaderComponent = Component.extend({
  layout,
  contextualHelp: service(),
  helpId: contextHelpIds.InvestigateHelpIds,
  classNames: 'rsa-context-panel__header',

  @computed('activeTabName')
  headerTitle(activeTabName) {
    if (activeTabName) {
      return this.get('i18n').t(`context.header.title.${activeTabName.camelize()}`);
    }
  },

  @computed('lookupKey', 'meta')
  pivotToInvestigateUrl(lookupKey, meta) {
    if (isEmpty(lookupKey)) {
      return '';
    }
    return pivotToInvestigateUrl(meta, lookupKey);
  },

  actions: {
    closeAction() {
      this.sendAction('closePanel');
      this.send('restoreDefault');
    },
    goToHelp(moduleId, topicId) {
      this.get('contextualHelp').goToHelp(moduleId, topicId);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(HeaderComponent);
