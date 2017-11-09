import Component from 'ember-component';
import { connect } from 'ember-redux';
import {
  selectedService,
  servicesWithURI
} from 'investigate-events/reducers/investigate/services/selectors';

const stateToComputed = (state) => {
  return {
    serviceObject: selectedService(state),
    servicesWithURI: servicesWithURI(state),
    startTime: state.investigate.queryNode.startTime,
    endTime: state.investigate.queryNode.endTime
  };
};

const BreadCrumbComponent = Component.extend({
  tagName: 'nav',
  classNames: 'rsa-investigate-breadcrumb'
});

export default connect(stateToComputed)(BreadCrumbComponent);
