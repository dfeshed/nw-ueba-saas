import Component from 'ember-component';
import { connect } from 'ember-redux';
import {
  getServiceId
} from 'investigate-events/reducers/investigate/services/selectors';

const stateToComputed = (state) => {
  return {
    serviceId: getServiceId(state)
  };
};

const EventsPreferencesComponent = Component.extend({
});

export default connect(stateToComputed)(EventsPreferencesComponent);
