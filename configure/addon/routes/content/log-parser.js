import Route from '@ember/routing/route';
import { inject } from '@ember/service';
import parserRuleCreators from 'configure/actions/creators/logs/parser-rule-creators';

export default Route.extend({
  redux: inject(),
  model() {
    const redux = this.get('redux');
    redux.dispatch(parserRuleCreators.findAllLogParsers());
    redux.dispatch(parserRuleCreators.getFormats());
  }
});
