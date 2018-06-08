import Component from '@ember/component';
import { connect } from 'ember-redux';
import { next } from '@ember/runloop';
import { inject as service } from '@ember/service';

import {
  selectedLogParser,
  hasDeployableRules
} from 'configure/reducers/content/log-parser-rules/selectors';

import {
  deployLogParser,
  deleteLogParser
} from 'configure/actions/creators/content/log-parser-rule-creators';

const stateToComputed = (state) => ({
  selectedLogParser: selectedLogParser(state),
  hasNoDeployableRules: !hasDeployableRules(state)
});

const dispatchToActions = {
  deployLogParser,
  deleteLogParser
};

const LogParsersToolbar = Component.extend({
  classNames: ['log-parser-toolbar'],
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

export default connect(stateToComputed, dispatchToActions)(LogParsersToolbar);


