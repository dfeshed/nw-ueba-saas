import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  logParsers,
  selectedParserRuleName,
  selectedLogParserIndex,
  selectedLogParserName
} from 'configure/reducers/content/log-parser-rules/selectors';
import parserRuleCreators from 'configure/actions/creators/content/log-parser-rule-creators';
import { inject } from '@ember/service';

const stateToComputed = (state) => ({
  logParsers: logParsers(state),
  logParserName: selectedLogParserName(state),
  parserRuleName: selectedParserRuleName(state),
  selectedLogParserIndex: selectedLogParserIndex(state)
});

const LogParsers = Component.extend({
  classNames: ['log-parsers'],
  redux: inject(),
  actions: {
    selectLogParser(name, index) {
      const redux = this.get('redux');
      redux.dispatch(parserRuleCreators.selectLogParser(index));
      redux.dispatch(parserRuleCreators.fetchParserRules(name));
    }
  },
  init() {
    this._super(...arguments);
    const redux = this.get('redux');
    setTimeout(() => {// will not brake on slow network but some data will be missing in UI on page load
      const logParserName = this.get('logParserName');
      redux.dispatch(parserRuleCreators.selectLogParser(0));
      redux.dispatch(parserRuleCreators.fetchParserRules(logParserName));
    }, 1500);
    setTimeout(() => {
      redux.dispatch(parserRuleCreators.selectParserRule(0));
    }, 3500);
  }
});
export default connect(stateToComputed)(LogParsers);
