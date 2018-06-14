import Component from '@ember/component';
import { connect } from 'ember-redux';
import { next } from '@ember/runloop';
import {
  selectedLogParserName,
  selectedLogParser,
  hasDeployableRules
} from 'configure/reducers/content/log-parser-rules/selectors';
import {
  fetchParserRules,
  saveParserRule,
  deployLogParser } from 'configure/actions/creators/content/log-parser-rule-creators';

import { inject as service } from '@ember/service';


const stateToComputed = (state) => ({
  selectedLogParserName: selectedLogParserName(state),
  selectedLogParser: selectedLogParser(state),
  hasNoDeployableRules: !hasDeployableRules(state)
});

const dispatchToActions = {
  deployLogParser,
  fetchParserRules,
  saveParserRule
};

const SaveResetRule = Component.extend({
  classNames: ['save-reset-rule'],
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
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(SaveResetRule);