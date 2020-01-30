import Route from '@ember/routing/route';
import { initializeSpringboard } from 'springboard/actions/creators/springboard';
import { inject as service } from '@ember/service';

export default class SpringboardRoute extends Route {

  @service('redux') redux;

  async model() {
    return this.redux.dispatch(initializeSpringboard());
  }

  redirect() {
    // For now redirect to first springboard,
    const { springboard: { springboards: [ { id }] } } = this.redux.getState();
    this.transitionTo('springboard.show', { id });
  }
}
