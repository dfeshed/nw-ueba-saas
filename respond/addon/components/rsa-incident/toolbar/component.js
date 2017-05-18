import Ember from 'ember';
import connect from 'ember-redux/components/connect';
import { toggleJournalPanel } from 'respond/actions/creators/incidents-creators';

const {
  Component
} = Ember;

const stateToComputed = (state) => {
  const {
    respond: {
      incident: {
        id,
        isJournalPanelOpen
      }
    }
  } = state;

  return {
    isJournalPanelOpen,
    incidentId: id
  };
};

const dispatchToActions = (dispatch) => ({
  clickJournalAction: () => dispatch(toggleJournalPanel())
});

const IncidentToolbar = Component.extend({
  tagName: 'hbox',
  classNames: [ 'rsa-incident-toolbar' ],
  isJournalPanelOpen: null,
  clickJournalAction: null
});

export default connect(stateToComputed, dispatchToActions)(IncidentToolbar);