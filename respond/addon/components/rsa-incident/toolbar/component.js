import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  toggleTasksAndJournalPanel,
  setHideViz
} from 'respond/actions/creators/incidents-creators';

const stateToComputed = (state) => {
  const {
    respond: {
      incident: {
        id,
        isShowingTasksAndJournal,
        hideViz
      }
    }
  } = state;

  return {
    isShowingTasksAndJournal,
    hideViz,
    incidentId: id
  };
};

const dispatchToActions = (dispatch) => ({
  toggleTasksAndJournalPanel: () => dispatch(toggleTasksAndJournalPanel()),
  setHideVizAction: (hideViz) => dispatch(setHideViz(hideViz))
});

const IncidentToolbar = Component.extend({
  tagName: 'hbox',
  classNames: [ 'rsa-incident-toolbar' ],
  hideViz: null,
  setHideVizAction: null
});

export default connect(stateToComputed, dispatchToActions)(IncidentToolbar);