import Component from '@ember/component';
import { connect } from 'ember-redux';
import _ from 'lodash';
import { getFilter, getExistAnomalyTypes, getSelectedFeedBack, getSelectedAnomalyTypes, getSelectedSeverity, severityFilter, feedbackFilter, initialFilterState } from 'investigate-users/reducers/alerts/selectors';
import { updateFilter } from 'investigate-users/actions/alert-details';

const stateToComputed = (state) => ({
  existAnomalyTypes: getExistAnomalyTypes(state),
  selectedFeedBack: getSelectedFeedBack(state),
  selectedAnomalyTypes: getSelectedAnomalyTypes(state),
  selectedSeverity: getSelectedSeverity(state),
  filter: getFilter(state),
  feedbackFilter,
  severityFilter,
  initialFilterState
});

const dispatchToActions = {
  updateFilter
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
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(AlertTabFilterComponent);