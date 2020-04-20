import Component from '@ember/component';
import { connect } from 'ember-redux';
import { riskyUserCount, watchedUserCount, isWatched, isAdmin, isRisky, getUserFilter } from 'investigate-users/reducers/users/selectors';
import { updateFilter } from 'investigate-users/actions/user-tab-actions';

const dispatchToActions = {
  updateFilter
};

const stateToComputed = (state) => ({
  riskyUserCount: riskyUserCount(state),
  watchedUserCount: watchedUserCount(state),
  isRisky: isRisky(state),
  isAdmin: isAdmin(state),
  isWatched: isWatched(state),
  filter: getUserFilter(state)
});
const UserTabCategoryFilterComponent = Component.extend({
  classNames: 'users-tab_filter_user',
  actions: {
    applyFilter(filterFor) {
      let filter = null;
      if (filterFor === 'risky') {
        filter = this.get('isRisky') ? { minScore: null } : { minScore: 0 };
      } else if (filterFor === 'admin') {
        filter = this.get('isAdmin') ? { userTags: null } : { userTags: ['admin'] };
      } else if (filterFor === 'watched') {
        filter = this.get('isWatched') ? { isWatched: false } : { isWatched: true };
      }
      filter = this.get('filter').merge(filter);
      this.send('updateFilter', filter);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(UserTabCategoryFilterComponent);