import Component from '@ember/component';
import { connect } from 'ember-redux';
import _ from 'lodash';
import { getFilter, getExistAnomalyTypes, getSelectedFeedBack, getSelectedAnomalyTypes, getSelectedSeverity, severityFilter, feedbackFilter } from 'investigate-users/reducers/alerts/selectors';
import { updateFilter, updateDateRangeFilter } from 'investigate-users/actions/alert-details';
import { dateTimeFilterOptionsForAlerts } from 'investigate-users/utils/column-config';

const stateToComputed = (state) => ({
  existAnomalyTypes: getExistAnomalyTypes(state),
  selectedFeedBack: getSelectedFeedBack(state),
  selectedAnomalyTypes: getSelectedAnomalyTypes(state),
  selectedSeverity: getSelectedSeverity(state),
  filter: getFilter(state),
  feedbackFilter,
  severityFilter
});

const dispatchToActions = {
  updateFilter,
  updateDateRangeFilter
};

const AlertTabFilterComponent = Component.extend({
  classNames: 'alerts-tab_filter',
  selections: null,
  filterOptions: dateTimeFilterOptionsForAlerts,

  actions: {
    updateAnomalyFilter(selections) {
      const filter = this.get('filter').merge({ indicator_types: _.map(selections, 'id') });
      this.send('updateFilter', filter);
    },
    updateFeedBackFilter(selection) {
      const filter = this.get('filter').merge({ feedback: selection });
      this.send('updateFilter', filter);
    },
    updateSeverityFilter(selection) {
      const filter = this.get('filter').merge({ severity: selection });
      this.send('updateFilter', filter);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(AlertTabFilterComponent);