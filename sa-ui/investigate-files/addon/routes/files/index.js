import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { bootstrapInvestigateFiles } from 'investigate-files/actions/data-creators';
import { next } from '@ember/runloop';
import * as ACTION_TYPES from '../../actions/types';

export default Route.extend({
  redux: service(),

  model({ query }) {
    next(() => {
      const redux = this.get('redux');
      redux.dispatch(bootstrapInvestigateFiles(query));
    });
  },

  deactivate() {
    const redux = this.get('redux');
    redux.dispatch({ type: ACTION_TYPES.RESET_FILES });
  },
  actions: {
    navigateToCertificateView(thumbprint) {
      this.transitionTo('files.certificates', thumbprint, {
        queryParams: {
          thumbprint
        }
      });
    }

  }

});
