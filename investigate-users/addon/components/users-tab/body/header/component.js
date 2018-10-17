import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getUsersSeverity, getSortField, getFilterSeverity, getTotalUsers, getUserFilter } from 'investigate-users/reducers/users/selectors';
import { updateFilter, exportUsers } from 'investigate-users/actions/user-tab-actions';
import { sortOptions } from 'investigate-users/utils/column-config';

const stateToComputed = (state) => ({
  usersSeverity: getUsersSeverity(state),
  sortBy: getSortField(state),
  filterSeverity: getFilterSeverity(state),
  totalUsers: getTotalUsers(state),
  filter: getUserFilter(state)
});

const dispatchToActions = {
  updateFilter,
  exportUsers
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
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(UsersTabBodyHeaderComponent);