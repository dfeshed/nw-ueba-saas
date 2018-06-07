import Component from '@ember/component';
import { connect } from 'ember-redux';
import { next } from '@ember/runloop';
import { inject as service } from '@ember/service';
import {
  selectedLogParserName,
  hasDeployableRules
} from 'configure/reducers/content/log-parser-rules/selectors';
import { deployLogParser } from 'configure/actions/creators/content/log-parser-rule-creators';

const stateToComputed = (state) => ({
  selectedLogParserName: selectedLogParserName(state),
  hasNoDeployableRules: !hasDeployableRules(state)
});

const dispatchToActions = {
  deployLogParser
};

const LogParsersToolbar = Component.extend({
  classNames: ['log-parser-toolbar', 'buttons-container'],
  eventBus: service(),
  actions: {
    showModal(modalId) {
      this.set('activeModalId', modalId);
      next(() => {
        this.get('eventBus').trigger(`rsa-application-modal-open-${modalId}`);
      });
    },
    closeModal(modalId) {
      this.get('eventBus').trigger(`rsa-application-modal-close-${modalId}`);
      this.set('activeModalId', null);
    },
    addLogParser() {
      this.send('showModal', 'add-log-parser');
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(LogParsersToolbar);


