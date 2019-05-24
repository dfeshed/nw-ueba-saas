import Component from '@ember/component';
import { connect } from 'ember-redux';
import columns from './columns';
import { hasSelectedClosedIncidents, isSendToArcherAvailable } from 'respond/selectors/incidents';
import { getStatusTypes } from 'respond/selectors/dictionaries';
import {
  getPriorityTypes,
  getAssigneeOptions
} from 'respond-shared/selectors/create-incident/selectors';
import creators from 'respond/actions/creators';

const stateToComputed = (state) => {
  return {
    priorityTypes: getPriorityTypes(state),
    statusTypes: getStatusTypes(state),
    users: getAssigneeOptions(state),
    hasSelectedClosedIncidents: hasSelectedClosedIncidents(state),
    isSendToArcherAvailable: isSendToArcherAvailable(state)
  };
};

const Incidents = Component.extend({
  classNames: ['rsa-incidents'],
  columns,
  creators: creators.incidents
});

export default connect(stateToComputed)(Incidents);