import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { changeActiveTab } from 'direct-access/actions/actions';

export default Route.extend({
  redux: service(),

  beforeModel() {
    this.get('redux').dispatch(changeActiveTab('dashboard'));
  }
});
