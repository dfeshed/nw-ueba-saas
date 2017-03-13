import Ember from 'ember';
import connect from 'ember-redux/components/connect';
import * as UIStateActions from 'respond/actions/ui-state-creators';
import { storypointCount, storyEventCount } from 'respond/selectors/storyline';

const {
  Component
} = Ember;

const stateToComputed = (state) => {
  const { respond: { incident: { id, info, infoStatus, viewMode } } } = state;
  return {
    incidentId: id,
    info,
    infoStatus,
    viewMode,
    storypointCount: storypointCount(state),
    storyEventCount: storyEventCount(state)
  };
};

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
  setViewModeAction: null,
  storypointCount: null,
  storyEventCount: null
});

/**
 * @class Incident Inspector
 * A Container for displaying information about an Incident in various view modes.
 *
 * @public
 */
export default connect(stateToComputed, dispatchToActions)(IncidentInspector);