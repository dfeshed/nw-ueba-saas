import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { changeActiveTab, changeDirectory } from 'ngcoreui/actions/actions';

export default Route.extend({
  redux: service(),

  beforeModel() {
    const redux = this.get('redux');
    redux.dispatch(changeActiveTab('tree'));
    redux.dispatch(changeDirectory('/'));
  }
});
