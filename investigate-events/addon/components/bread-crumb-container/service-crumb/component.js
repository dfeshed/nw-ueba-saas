
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import {
  getServiceDisplayName,
  servicesWithURI
} from 'investigate-events/reducers/investigate/services/selectors';
import Component from 'ember-component';

const stateToComputed = (state) => ({
  serviceDisplayName: getServiceDisplayName(state),
  servicesWithURI: servicesWithURI(state)
});

const ServiceCrumb = Component.extend({
  classNames: 'rsa-investigate-breadcrumb',

  @computed()
  panelId() {
    return `breadCrumbServiceTooltip-${this.get('elementId')}`;
  }

});

export default connect(stateToComputed)(ServiceCrumb);
