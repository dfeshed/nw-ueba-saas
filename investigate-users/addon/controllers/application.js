import Controller from '@ember/controller';
import { inject as service } from '@ember/service';
import computed from 'ember-computed-decorators';
import { fetchData } from 'investigate-users/actions/fetch/data';
import { flashErrorMessage } from 'investigate-users/utils/flash-message';
import _ from 'lodash';


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
    searchUsers(term) {
      return fetchData('searchUsers', { search_field_contains: term }).then((data) => {
        if (data === 'error') {
          flashErrorMessage('investigateUsers.errorMessages.unableToFindUsers');
          return;
        }
        return _.map(data.data, ({ id, displayName }) => ({ id, displayName }));
      });
    },
    controllerNavigateTo(routeName) {
      this.send('navigateTo', routeName);
    },
    controllerApplyUserFilter(filterFor) {
      this.send('applyUserFilter', filterFor);
    },
    controllerShowUserDetails({ id }) {
      this.send('showUserDetails', id);
    },
    controllerApplyAlertsFilter(filterFor) {
      this.send('applyAlertsFilter', filterFor);
    }
  }
});