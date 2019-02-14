import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { changeActiveTab, changeDirectory } from 'ngcoreui/actions/actions';

export default Route.extend({
  redux: service(),

  model(params) {
    let path = `/${params.path}`;
    const redux = this.get('redux');
    redux.dispatch(changeActiveTab('tree'));
    if (path === '/tree') {
      path = '/';
    }
    // Bad paths are handled in the `changeDirectory` action
    redux.dispatch(changeDirectory(path));
  }
});
