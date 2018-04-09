import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getRules, getClickedRule } from 'configure/reducers/logs/parser-rules/selectors';
import parserRuleCreators from 'configure/actions/creators/logs/parser-rule-creators';

const stateToComputed = (state) => ({
  parserRules: getRules(state),
  clickedRule: getClickedRule(state)
});

const dispatchToActions = {
  selectParserRule: parserRuleCreators.selectParserRule
};

const RulesList = Component.extend({
  classNames: ['rules-list']
});
export default connect(stateToComputed, dispatchToActions)(RulesList);
