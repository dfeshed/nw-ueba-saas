import Component from '@ember/component';
import { connect } from 'ember-redux';
import columns from './columns';
import { hasSelectedClosedIncidents, isEscalateAvailable } from 'respond/selectors/incidents';
import { getPriorityTypes, getStatusTypes } from 'respond/selectors/dictionaries';
import { getAssigneeOptions } from 'respond/selectors/users';
import creators from 'respond/actions/creators';

const stateToComputed = (state) => {
  return {
    priorityTypes: getPriorityTypes(state),
    statusTypes: getStatusTypes(state),
    users: getAssigneeOptions(state),
    hasSelectedClosedIncidents: hasSelectedClosedIncidents(state),
    isEscalateAvailable: isEscalateAvailable(state)
  };
};

const Incidents = Component.extend({
  classNames: ['rsa-incidents'],
  columns,
  creators: creators.incidents
});

export default connect(stateToComputed)(Incidents);