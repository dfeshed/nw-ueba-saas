import Ember from 'ember';
import connect from 'ember-redux/components/connect';
import * as UIStateActions from 'respond/actions/ui-state-creators';

const {
  Component
} = Ember;

const stateToComputed = ({ respond: { incident: { isJournalPanelOpen } } }) => ({
  isJournalPanelOpen
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