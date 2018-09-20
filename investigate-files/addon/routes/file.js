import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { getAlerts } from 'investigate-files/actions/data-creators';
import { next } from '@ember/runloop';


export default Route.extend({
  redux: service(),

  model(params) {
    const redux = this.get('redux');
    next(() => {
      redux.dispatch(getAlerts(params));
    });
  }
});