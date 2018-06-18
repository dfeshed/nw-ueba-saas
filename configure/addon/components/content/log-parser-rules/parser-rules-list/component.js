import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  parserRules,
  selectedParserRuleIndex,
  isLoadingParserRules,
  isLoadingParserRulesError,
  isDeletingParserRule,
  isDeletingParserRuleError,
  isSavingParserRule,
  isSavingParserRuleError,
  invalidRules,
  highlightedRuleNames
} from 'configure/reducers/content/log-parser-rules/selectors';
import { selectParserRule } from 'configure/actions/creators/content/log-parser-rule-creators';

const stateToComputed = (state) => ({
  isLoadingParserRules: isLoadingParserRules(state),
  isLoadingParserRulesError: isLoadingParserRulesError(state),
  parserRules: parserRules(state),
  selectedParserRuleIndex: selectedParserRuleIndex(state),
  isDeletingParserRule: isDeletingParserRule(state),
  isDeletingParserRuleError: isDeletingParserRuleError(state),
  isSavingParserRule: isSavingParserRule(state),
  isSavingParserRuleError: isSavingParserRuleError(state),
  invalidRules: invalidRules(state),
  highlightedRuleNames: highlightedRuleNames(state)
});

const dispatchToActions = {
  selectParserRule
};

const ParserRulesList = Component.extend({
  classNames: ['parser-rules-list']
});
export default connect(stateToComputed, dispatchToActions)(ParserRulesList);
