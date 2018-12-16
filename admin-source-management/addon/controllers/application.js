import Controller from '@ember/controller';
import { inject as service } from '@ember/service';
import computed from 'ember-computed-decorators';

export default Controller.extend({
  accessControl: service(),
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

  @computed('routing.currentRouteName')
  isGroupsActive() {
    const { router } = this.get('routing');
    let isActive = false;
    if (router.isActive('admin-source-management.groups') ||
        router.isActive('admin-source-management.group-wizard')) {
      isActive = true;
    }
    return isActive;
  },

  @computed('routing.currentRouteName')
  isPoliciesActive() {
    const { router } = this.get('routing');
    let isActive = false;
    if (router.isActive('admin-source-management.policies') ||
        router.isActive('admin-source-management.policy-wizard')) {
      isActive = true;
    }
    return isActive;
  },

  actions: {
    // let router handle this
    controllerNavigateToRoute(routeName) {
      this.send('navigateToRoute', routeName);
    },
    // let router handle this
    controllerRedirectToUrl(relativeUrl) {
      this.send('redirectToUrl', relativeUrl);
    }
  }
});
