import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  getServiceId
} from 'investigate-events/reducers/investigate/services/selectors';
import { preferencesUpdated } from 'investigate-events/actions/preferences-creators';

const stateToComputed = (state) => ({
  serviceId: getServiceId(state),
  eventsPreferenceConfig: state.investigate.data.eventsPreferencesConfig
});

const dispatchToActions = {
  preferencesUpdated
};

const EventsPreferencesComponent = Component.extend({
});

export default connect(stateToComputed, dispatchToActions)(EventsPreferencesComponent);
