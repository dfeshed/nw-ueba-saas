import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { bootstrapInvestigateFiles } from 'investigate-files/actions/data-creators';

export default Route.extend({
  redux: service(),

  model() {
    const redux = this.get('redux');
    return redux.dispatch(bootstrapInvestigateFiles());
  }
});
