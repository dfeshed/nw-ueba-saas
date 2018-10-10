import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getExistAlertTypes, getExistAnomalyTypes, getSelectedAlertTypes, getSelectedAnomalyTypes, getUserFilter } from 'investigate-users/reducers/users/selectors';
import { updateFilter } from 'investigate-users/actions/user-tab-actions';
import _ from 'lodash';

const stateToComputed = (state) => ({
  existAlertTypes: getExistAlertTypes(state),
  existAnomalyTypes: getExistAnomalyTypes(state),
  selectedAlertTypes: getSelectedAlertTypes(state),
  selectedAnomalyTypes: getSelectedAnomalyTypes(state),
  filter: getUserFilter(state)
});

const dispatchToActions = {
  updateFilter
};

const UsersTabFilterFilterComponent = Component.extend({
  classNames: 'users-tab_filter_filter',
  actions: {
    updateFilterForAnomalyTypes(selections) {
      const filter = this.get('filter').merge({ indicatorTypes: _.map(selections, 'id') });
      this.send('updateFilter', filter);
    },
    updateFilterForAlertTypes(selections) {
      const filter = this.get('filter').merge({ alertTypes: _.map(selections, 'id') });
      this.send('updateFilter', filter);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(UsersTabFilterFilterComponent);