import Component from '@ember/component';
import { connect } from 'ember-redux';
import { inject } from '@ember/service';
import {
  selectedLogParserName,
  hasSelectedParserRule,
  selectedParserRuleName,
  isOotb
} from 'configure/reducers/content/log-parser-rules/selectors';
import { deleteParserRule, addNewParserRule } from 'configure/actions/creators/content/log-parser-rule-creators';

const stateToComputed = (state) => ({
  selectedLogParserName: selectedLogParserName(state),
  hasSelectedParserRule: hasSelectedParserRule(state),
  selectedParserRuleName: selectedParserRuleName(state),
  isOotb: isOotb(state)
});

const dispatchToActions = {
  deleteParserRule
};

const LogParserRulesToolbar = Component.extend({
  tagName: 'div',
  classNames: ['log-parser-rules-toolbar', 'buttons-container'],
  newRuleName: '',
  eventBus: inject(),
  redux: inject(),
  actions: {
    addNewParserRule(name) {
      if (name.trim() !== '') { // will do validation later
        const redux = this.get('redux');
        this.get('eventBus').trigger('rsa-application-modal-close-addNewRule');
        redux.dispatch(addNewParserRule(name));
        this.set('newRuleName', '');
      }
    }
  }
});
export default connect(stateToComputed, dispatchToActions)(LogParserRulesToolbar);
