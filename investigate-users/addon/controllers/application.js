import Controller from '@ember/controller';
import { inject as service } from '@ember/service';
import computed from 'ember-computed-decorators';


export default Controller.extend({
  classNames: 'user-header',
  accessControl: service(),
  routing: service('-routing'),
  redux: service(),

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
    return path === 'index' ? 'overview' : path;
  },

  actions: {
    controllerNavigateTo(routeName) {
      this.send('navigateTo', routeName);
    },
    controllerApplyUserFilter(filterFor) {
      this.send('applyUserFilter', filterFor);
    }
  }
});