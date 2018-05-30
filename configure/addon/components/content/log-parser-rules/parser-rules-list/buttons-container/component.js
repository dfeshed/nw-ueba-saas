import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  hasSelectedParserRule,
  selectedParserRuleName,
  isOotb
} from 'configure/reducers/content/log-parser-rules/selectors';
import { deleteParserRule } from 'configure/actions/creators/content/log-parser-rule-creators';

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
  classNames: ['buttonsContainer']
});
export default connect(stateToComputed, dispatchToActions)(ButtonsContainer);
