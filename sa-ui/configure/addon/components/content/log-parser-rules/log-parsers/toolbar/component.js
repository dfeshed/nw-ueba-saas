import Component from '@ember/component';
import { connect } from 'ember-redux';
import { next } from '@ember/runloop';
import { inject as service } from '@ember/service';
import computed from 'ember-computed-decorators';

import {
  selectedLogParser
} from 'configure/reducers/content/log-parser-rules/selectors';

import {
  deployLogParser,
  deleteLogParser
} from 'configure/actions/creators/content/log-parser-rule-creators';

const stateToComputed = (state) => ({
  selectedLogParser: selectedLogParser(state)
});

const dispatchToActions = {
  deployLogParser,
  deleteLogParser
};

const LogParsersToolbar = Component.extend({
  classNames: ['log-parser-toolbar'],
  eventBus: service(),
  accessControl: service(),
  showToolTip: false,
  @computed('selectedLogParser', 'accessControl.canManageLogParsers')
  cannotDeleteParser(selectedLogParser, canManageLogParsers) {
    return !canManageLogParsers || !selectedLogParser || selectedLogParser.outOfBox;
  },

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
    toolTip(display) {
      (display && this.get('cannotDeleteParser')) ? this.set('showToolTip', true) : this.set('showToolTip', false);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(LogParsersToolbar);


