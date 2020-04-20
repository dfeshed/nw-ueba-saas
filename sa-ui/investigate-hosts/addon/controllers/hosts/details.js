import Controller from '@ember/controller';
import { resetExploreSearch } from 'investigate-hosts/actions/data-creators/explore';
import { inject as service } from '@ember/service';

export default Controller.extend({
  // Query Params
  queryParams: ['sid'],

  sid: null,

  redux: service(),

  actions: {
    controllerNavigateToTab(category) {
      const redux = this.get('redux');
      if (!category) {
        // reset search query
        redux.dispatch(resetExploreSearch());
      }
      this.send('navigateToTab', category);
    }
  }
});
