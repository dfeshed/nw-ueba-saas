import Ember from 'ember';
import connect from 'ember-redux/components/connect';
import { toggleJournalPanel, setHideViz } from 'respond/actions/creators/incidents-creators';

const {
  Component
} = Ember;

const stateToComputed = (state) => {
  const {
    respond: {
      incident: {
        id,
        isJournalPanelOpen,
        hideViz
      }
    }
  } = state;

  return {
    isJournalPanelOpen,
    hideViz,
    incidentId: id
  };
};

const dispatchToActions = (dispatch) => ({
  clickJournalAction: () => dispatch(toggleJournalPanel()),
  setHideVizAction: (hideViz) => dispatch(setHideViz(hideViz))
});

const IncidentToolbar = Component.extend({
  tagName: 'hbox',
  classNames: [ 'rsa-incident-toolbar' ],
  isJournalPanelOpen: null,
  hideViz: null,
  clickJournalAction: null,
  setHideVizAction: null
});

export default connect(stateToComputed, dispatchToActions)(IncidentToolbar);