import Route from '@ember/routing/route';
import { inject } from '@ember/service';
import { initializeLogParserRules } from 'configure/actions/creators/content/log-parser-rule-creators';

export default Route.extend({
  redux: inject(),
  model() {
    const redux = this.get('redux');
    redux.dispatch(initializeLogParserRules());
  }
});
