import Route from '@ember/routing/route';
import { setActiveSpringboardId } from 'springboard/actions/creators/springboard';
import { inject as service } from '@ember/service';

export default class ShowRoute extends Route {
  @service('redux') redux;

  model({ id }) {
    return this.redux.dispatch(setActiveSpringboardId(id));
  }
}