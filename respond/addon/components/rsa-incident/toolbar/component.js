import Ember from 'ember';
import connect from 'ember-redux/components/connect';
import * as UIStateActions from 'respond/actions/ui-state-creators';

const {
  Component
} = Ember;

const stateToComputed = ({
  respond: {
    dictionaries,
    users,
    incident: {
      id,
      info,
      isJournalPanelOpen
    }
  }
}) => ({
  isJournalPanelOpen,
  priorityTypes: dictionaries && dictionaries.priorityTypes,
  statusTypes: dictionaries && dictionaries.statusTypes,
  users: users && users.users,
  incidentId: id,
  priority: info && info.priority,
  status: info && info.status,
  assigneeId: info && info.assignee && info.assignee.id
});

const dispatchToActions = (dispatch) => ({
  clickJournalAction: () => dispatch(UIStateActions.toggleJournalPanel())
});

const IncidentToolbar = Component.extend({
  tagName: 'hbox',
  classNames: [ 'rsa-incident-toolbar' ],
  isJournalPanelOpen: null,
  clickJournalAction: null
});

export default connect(stateToComputed, dispatchToActions)(IncidentToolbar);