import Component from '@ember/component';
import { connect } from 'ember-redux';
import _ from 'lodash';
import { getFilter, getExistAnomalyTypes, getSelectedFeedBack, getSelectedAnomalyTypes, getSelectedSeverity, severityFilter, feedbackFilter, dateTimeFilterOptionsForAlerts, selectedEntities } from 'investigate-users/reducers/alerts/selectors';
import { entityFilter } from 'investigate-users/reducers/users/selectors';
import { updateFilter, updateDateRangeFilter } from 'investigate-users/actions/alert-details';

const stateToComputed = (state) => ({
  existAnomalyTypes: getExistAnomalyTypes(state),
  selectedFeedBack: getSelectedFeedBack(state),
  selectedAnomalyTypes: getSelectedAnomalyTypes(state),
  selectedSeverity: getSelectedSeverity(state),
  filter: getFilter(state),
  dateTimeFilterOptionsForAlerts: dateTimeFilterOptionsForAlerts(state),
  selectedEntities: selectedEntities(state),
  feedbackFilter,
  severityFilter,
  entityFilter
});

const dispatchToActions = {
  updateFilter,
  updateDateRangeFilter
};

const AlertTabFilterComponent = Component.extend({
  classNames: 'alerts-tab_filter',
  selections: null,
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
    },
    updateEntityTypes(selection) {
      const filter = this.get('filter').merge({ entityType: selection });
      this.send('updateFilter', filter);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(AlertTabFilterComponent);