import Component from '@ember/component';
import { connect } from 'ember-redux';
import { inject } from '@ember/service';
import {
  hasSelectedParserRule,
  selectedParserRuleName,
  isOotb
} from 'configure/reducers/content/log-parser-rules/selectors';
import { deleteParserRule, addNewParserRule } from 'configure/actions/creators/content/log-parser-rule-creators';

const stateToComputed = (state) => ({
  hasSelectedParserRule: hasSelectedParserRule(state),
  selectedParserRuleName: selectedParserRuleName(state),
  isOotb: isOotb(state)
});

const dispatchToActions = {
  deleteParserRule
};

const ButtonsContainer = Component.extend({
  tagName: 'div',
  classNames: ['buttonsContainer'],
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
export default connect(stateToComputed, dispatchToActions)(ButtonsContainer);
