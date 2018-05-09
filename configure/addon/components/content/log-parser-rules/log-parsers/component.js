import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  logParsers,
  firstLogParserName,
  firstParserRuleName,
  clickedLogParserIndex
} from 'configure/reducers/content/log-parser-rules/selectors';
import parserRuleCreators from 'configure/actions/creators/content/log-parser-rule-creators';
import { inject } from '@ember/service';

const stateToComputed = (state) => ({
  logParsers: logParsers(state),
  firstLogParserName: firstLogParserName(state),
  firstParserRuleName: firstParserRuleName(state),
  clickedLogParserIndex: clickedLogParserIndex(state)
});

const LogParsers = Component.extend({
  classNames: ['log-parsers'],
  redux: inject(),
  actions: {
    selectLogParser(name, index) {
      const redux = this.get('redux');
      redux.dispatch(parserRuleCreators.selectLogParser(name, index));
      redux.dispatch(parserRuleCreators.fetchParserRules(name));
    }
  },
  init() {
    this._super(...arguments);
    const redux = this.get('redux');
    setTimeout(() => {// will not brake on slow network but some data will be missing in UI on page load
      const firstLogParserName = this.get('firstLogParserName');
      redux.dispatch(parserRuleCreators.fetchParserRules(firstLogParserName));
      redux.dispatch(parserRuleCreators.selectLogParser(firstLogParserName, 0));
    }, 1500);
    setTimeout(() => {
      const firstParserRuleName = this.get('firstParserRuleName');
      redux.dispatch(parserRuleCreators.selectParserRule(firstParserRuleName, 0));
    }, 3500);
  }
});
export default connect(stateToComputed)(LogParsers);
