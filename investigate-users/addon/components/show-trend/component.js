import Component from '@ember/component';
import { connect } from 'ember-redux';
import { sortOnTrending, trendRange } from 'investigate-users/reducers/users/selectors';
import { updateSortTrend, updateTrendRange } from 'investigate-users/actions/user-details';

const stateToComputed = (state) => ({
  sortOnTrending: sortOnTrending(state),
  trendRange: trendRange(state)
});

const dispatchToActions = {
  updateSortTrend,
  updateTrendRange
};
const ShowTrendComponent = Component.extend({
  classNames: 'show-trend',
  trendOptions: [{
    key: 1,
    name: 'lastDay'
  }, {
    key: 7,
    name: 'lastWeek'
  }]
});

export default connect(stateToComputed, dispatchToActions)(ShowTrendComponent);