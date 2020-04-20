import Component from '@ember/component';
import { connect } from 'ember-redux';
import { and } from 'ember-computed-decorators';
import {
  isLoadingLogParser,
  selectedLogParserName,
  selectedParserRuleName,
  isLoadingLogParserError,
  hasSelectedParserRule,
  hasRuleFormats,
  isTransactionUnderway
} from 'configure/reducers/content/log-parser-rules/selectors';

const stateToComputed = (state) => ({
  isLoading: isLoadingLogParser(state),
  isLoadingError: isLoadingLogParserError(state),
  logParserName: selectedLogParserName(state),
  parserRuleName: selectedParserRuleName(state),
  hasSelectedParserRule: hasSelectedParserRule(state),
  hasRuleFormats: hasRuleFormats(state),
  isTransactionUnderway: isTransactionUnderway(state)
});

const ParserRules = Component.extend({
  classNames: ['log-parser-rules'],
  classNameBindings: ['isTransactionUnderway:transaction-in-progress'],
  @and('hasSelectedParserRule', 'hasRuleFormats')
  canShowSelectedParserRule: true
});
export default connect(stateToComputed)(ParserRules);
