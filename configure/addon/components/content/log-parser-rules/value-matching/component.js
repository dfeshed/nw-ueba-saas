import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  parserRuleFormatNames,
  selectedParserRuleFormat,
  parserRuleRegex
} from 'configure/reducers/content/log-parser-rules/selectors';
import { selectFormatValue } from 'configure/actions/creators/content/log-parser-rule-creators';

const stateToComputed = (state) => ({
  parserRuleFormatNames: parserRuleFormatNames(state),
  selectedParserRuleFormat: selectedParserRuleFormat(state),
  parserRuleRegex: parserRuleRegex(state)
});

const dispatchToActions = {
  selectFormatValue
};

const ValueMatching = Component.extend({
  classNames: ['value-matching']
});
export default connect(stateToComputed, dispatchToActions)(ValueMatching);
