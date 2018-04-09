import Component from '@ember/component';
import { connect } from 'ember-redux';
import { findAllLogParsers, getClickedLog } from 'configure/reducers/logs/parser-rules/selectors';
import parserRuleCreators from 'configure/actions/creators/logs/parser-rule-creators';

const stateToComputed = (state) => ({
  logs: findAllLogParsers(state),
  clickedLog: getClickedLog(state)
});

const dispatchToActions = {
  selectLogParser: parserRuleCreators.selectLogParser
};

const LogParsers = Component.extend({
  classNames: ['log-parsers']
});
export default connect(stateToComputed, dispatchToActions)(LogParsers);
