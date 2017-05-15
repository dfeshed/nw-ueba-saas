import Controller from 'ember-controller';
import service from 'ember-service/inject';
import computed from 'ember-computed-decorators';

export default Controller.extend({
  routing: service('-routing'),

  /**
   * Returns the leaf route name for the current fully qualified route
   * @property routePath
   * @param currentRouteName
   * @public
   * @returns {*}
   */
  @computed('routing.currentRouteName')
  routePath(currentRouteName) {
    const paths = currentRouteName.split('.');
    const path = paths.pop();
    return path === 'index' ? paths.pop() || '' : path;
  },
  actions: {
    navigateTo(routeName) {
      this.transitionToRoute(routeName);
    }
  }
});
