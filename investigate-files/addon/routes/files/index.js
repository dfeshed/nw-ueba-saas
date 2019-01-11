import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { bootstrapInvestigateFiles } from 'investigate-files/actions/data-creators';
import { next } from '@ember/runloop';
import * as ACTION_TYPES from '../../actions/types';

export default Route.extend({
  redux: service(),

  model() {
    next(() => {
      const redux = this.get('redux');
      redux.dispatch(bootstrapInvestigateFiles());
    });
  },

  deactivate() {
    const redux = this.get('redux');
    redux.dispatch({ type: ACTION_TYPES.RESET_FILES });
  }

});
