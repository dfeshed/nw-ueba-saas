import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { bootstrapInvestigateFiles } from 'investigate-files/actions/data-creators';
import { next } from '@ember/runloop';

export default Route.extend({
  redux: service(),

  model() {
    next(() => {
      const redux = this.get('redux');
      redux.dispatch(bootstrapInvestigateFiles());
    });
  }
});
