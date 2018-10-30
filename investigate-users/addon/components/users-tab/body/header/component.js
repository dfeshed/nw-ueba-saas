import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getUsersSeverity, getSortField, getTotalUsers, getUserFilter, allWatched } from 'investigate-users/reducers/users/selectors';
import { updateFilter, exportUsers, followUsers, unfollowUsers } from 'investigate-users/actions/user-tab-actions';
import { sortOptions } from 'investigate-users/utils/column-config';

const stateToComputed = (state) => ({
  usersSeverity: getUsersSeverity(state),
  sortBy: getSortField(state),
  totalUsers: getTotalUsers(state),
  filter: getUserFilter(state),
  allWatched: allWatched(state)
});

const dispatchToActions = {
  updateFilter,
  exportUsers,
  followUsers,
  unfollowUsers
};

const UsersTabBodyHeaderComponent = Component.extend({
  options: sortOptions,
  actions: {
    updateSort({ id }) {
      const filter = this.get('filter').merge({ sortField: id });
      this.send('updateFilter', filter);
    },
    applyFilter(severity) {
      const filter = this.get('filter').merge({ severity });
      this.send('updateFilter', filter);
    },
    followUsersAction() {
      if (this.get('allWatched')) {
        this.send('unfollowUsers');
      } else {
        this.send('followUsers');
      }
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(UsersTabBodyHeaderComponent);