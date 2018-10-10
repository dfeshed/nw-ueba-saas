import Component from '@ember/component';
import { connect } from 'ember-redux';
import { riskyUserCount, adminUserCount, watchedUserCount } from 'investigate-users/reducers/users/selectors';

const stateToComputed = (state) => ({
  riskyUserCount: riskyUserCount(state),
  adminUserCount: adminUserCount(state),
  watchedUserCount: watchedUserCount(state)
});

const WatchedUserComponent = Component.extend({
  actions: {
    applyFilter(filterFor) {
      let filter = null;
      if (filterFor === 'risky') {
        filter = { minScore: 0 };
      } else if (filterFor === 'admin') {
        filter = { userTags: ['admin'] };
      } else if (filterFor === 'watched') {
        filter = { isWatched: true };
      }
      this.get('applyUserFilter')(filter);
    }
  }
});

export default connect(stateToComputed)(WatchedUserComponent);