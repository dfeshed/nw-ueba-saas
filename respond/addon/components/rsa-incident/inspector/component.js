import Ember from 'ember';
import connect from 'ember-redux/components/connect';
import * as UIStateActions from 'respond/actions/ui-state-creators';

const {
  Component
} = Ember;

const stateToComputed = ({ respond: { incident: { id, info, infoStatus, viewMode } } }) => ({
  incidentId: id,
  info,
  infoStatus,
  viewMode
});

const dispatchToActions = (dispatch) => ({
  setViewModeAction(viewMode) {
    dispatch(UIStateActions.setViewMode(viewMode));
  }
});

const IncidentInspector = Component.extend({
  tagName: 'vbox',
  classNames: ['rsa-incident-inspector'],
  incidentId: null,
  info: null,
  infoStatus: null,
  viewMode: null,
  setViewModeAction: null
});

/**
 * @class Incident Inspector
 * A Container for displaying information about an Incident in various view modes.
 *
 * @public
 */
export default connect(stateToComputed, dispatchToActions)(IncidentInspector);