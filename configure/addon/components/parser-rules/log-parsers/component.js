import Component from '@ember/component';
import { connect } from 'ember-redux';
import { findAllLogParsers, getFirstLogName, getFirstRuleName, getClickedLogIndex } from 'configure/reducers/logs/parser-rules/selectors';
import parserRuleCreators from 'configure/actions/creators/logs/parser-rule-creators';
import { inject } from '@ember/service';

const stateToComputed = (state) => ({
  logs: findAllLogParsers(state),
  firstLogName: getFirstLogName(state),
  firstRuleName: getFirstRuleName(state),
  clickedLogIndex: getClickedLogIndex(state)
});

const LogParsers = Component.extend({
  classNames: ['log-parsers'],
  redux: inject(),
  actions: {
    selectLogParser(name, index) {
      const redux = this.get('redux');
      redux.dispatch(parserRuleCreators.selectLogParser(name, index));
      redux.dispatch(parserRuleCreators.getRules(name));
    }
  },
  init() {
    this._super(...arguments);
    const redux = this.get('redux');
    const myThis = this;
    setTimeout(function() { // will not brake on slow network but some data will be missing in UI on page load
      const firstLogName = myThis.get('firstLogName');
      redux.dispatch(parserRuleCreators.getRules(firstLogName));
      redux.dispatch(parserRuleCreators.selectLogParser(firstLogName, 0));
    }, 1500);
    setTimeout(function() {
      const firstRuleName = myThis.get('firstRuleName');
      redux.dispatch(parserRuleCreators.selectParserRule(firstRuleName, 0));
    }, 3500);
  }
});
export default connect(stateToComputed)(LogParsers);
