import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  parserRules,
  selectedParserRuleIndex,
  isLoadingParserRules,
  isLoadingParserRulesError
} from 'configure/reducers/content/log-parser-rules/selectors';
import parserRuleCreators from 'configure/actions/creators/content/log-parser-rule-creators';

const stateToComputed = (state) => ({
  isLoadingParserRules: isLoadingParserRules(state),
  isLoadingParserRulesError: isLoadingParserRulesError(state),
  parserRules: parserRules(state),
  selectedParserRuleIndex: selectedParserRuleIndex(state)
});

const dispatchToActions = {
  selectParserRule: parserRuleCreators.selectParserRule
};

const ParserRulesList = Component.extend({
  classNames: ['parser-rules-list']
});
export default connect(stateToComputed, dispatchToActions)(ParserRulesList);
