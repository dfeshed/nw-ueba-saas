import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { getRiskScoreContext } from 'investigate-files/actions/data-creators';
import { next } from '@ember/runloop';


export default Route.extend({
  redux: service(),

  model(params) {
    const redux = this.get('redux');
    next(() => {
      redux.dispatch(getRiskScoreContext(params.id));
    });
  }
});