import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  logParsers,
  selectedLogParserIndex
} from 'configure/reducers/content/log-parser-rules/selectors';
import parserRuleCreators from 'configure/actions/creators/content/log-parser-rule-creators';

const stateToComputed = (state) => ({
  logParsers: logParsers(state),
  selectedLogParserIndex: selectedLogParserIndex(state)
});

const dispatchToActions = {
  selectLogParser: parserRuleCreators.selectLogParser
};

const LogParsers = Component.extend({
  classNames: ['log-parsers']
});
export default connect(stateToComputed, dispatchToActions)(LogParsers);
