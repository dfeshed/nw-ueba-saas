import Component from '@ember/component';
import { connect } from 'ember-redux';
import * as DictionaryActions from 'respond/actions/creators/dictionary-creators';
import * as IncidentsCreators from 'respond/actions/creators/incidents-creators';
import columns from './columns';
import { hasSelectedClosedIncidents, isEscalateAvailable } from 'respond/selectors/incidents';
import { getPriorityTypes, getStatusTypes } from 'respond/selectors/dictionaries';
import { getAssigneeOptions } from 'respond/selectors/users';

const stateToComputed = (state) => {
  return {
    priorityTypes: getPriorityTypes(state),
    statusTypes: getStatusTypes(state),
    users: getAssigneeOptions(state),
    hasSelectedClosedIncidents: hasSelectedClosedIncidents(state),
    isEscalateAvailable: isEscalateAvailable(state)
  };
};

const dispatchToActions = (dispatch) => {
  return {
    bootstrap() {
      dispatch(IncidentsCreators.getIncidentsSettings());
      dispatch(DictionaryActions.getAllPriorityTypes());
      dispatch(DictionaryActions.getAllStatusTypes());
      dispatch(DictionaryActions.getAllEnabledUsers());
      dispatch(DictionaryActions.getAllCategories());
      dispatch(DictionaryActions.getAllEscalationStatuses());
    }
  };
};

const Incidents = Component.extend({
  classNames: ['rsa-incidents'],
  columns
});

export default connect(stateToComputed, dispatchToActions)(Incidents);