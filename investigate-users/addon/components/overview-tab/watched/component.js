import Component from '@ember/component';
import { connect } from 'ember-redux';
import { riskyUserCount, watchedUserCount, getUserFilter } from 'investigate-users/reducers/users/selectors';
import { updateFilter } from 'investigate-users/actions/user-tab-actions';

const stateToComputed = (state) => ({
  riskyUserCount: riskyUserCount(state),
  watchedUserCount: watchedUserCount(state),
  filter: getUserFilter(state)
});

const dispatchToActions = {
  updateFilter
};

const WatchedUserComponent = Component.extend({
  actions: {
    applyFilter(filterFor) {
      this.send('updateFilter', 'RESET', true);
      let filter = null;
      if (filterFor === 'risky') {
        filter = { minScore: 0 };
      } else if (filterFor === 'admin') {
        filter = { userTags: ['admin'] };
      } else if (filterFor === 'watched') {
        filter = { isWatched: true };
      }
      this.get('applyUserFilter')(this.get('filter').merge(filter));
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(WatchedUserComponent);