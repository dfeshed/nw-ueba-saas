import Component from 'ember-component';
import computed, { or } from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { coreServiceNotUpdated, isSummaryDataInvalid, getServiceDisplayName, selectedServiceMessage } from 'investigate-events/reducers/investigate/services/selectors';
import { setService } from 'investigate-events/actions/interaction-creators';

const dispatchToActions = { setService };

const stateToComputed = (state) => ({
  coreServiceNotUpdated: coreServiceNotUpdated(state),
  isSummaryDataInvalid: isSummaryDataInvalid(state),
  selectedServiceMessage: selectedServiceMessage(state),
  serviceDisplayName: getServiceDisplayName(state),
  isServicesLoading: state.investigate.services.isServicesLoading,
  isSummaryLoading: state.investigate.services.isSummaryLoading,
  services: state.investigate.services.serviceData
});

const ServiceCrumb = Component.extend({
  classNames: 'rsa-investigate-breadcrumb',

  @computed()
  panelId() {
    return `breadCrumbServiceTooltip-${this.get('elementId')}`;
  },

  @computed('isLoading', 'selectedServiceMessage', 'serviceDisplayName')
  title(isLoading, selectedServiceMessage, serviceDisplayName) {
    if (isLoading) {
      return 'Loading data...';
    } else if (selectedServiceMessage) {
      return selectedServiceMessage;
    } else {
      return serviceDisplayName;
    }
  },

  @computed('coreServiceNotUpdated', 'isSummaryDataInvalid')
  iconClass: (coreServiceNotUpdated, isSummaryDataInvalid) => {
    return coreServiceNotUpdated || isSummaryDataInvalid ? 'disclaimer' : '';
  },

  @computed('coreServiceNotUpdated', 'isSummaryDataInvalid')
  iconName: (coreServiceNotUpdated, isSummaryDataInvalid) => {
    return coreServiceNotUpdated || isSummaryDataInvalid ? 'report-problem-triangle' : 'server-3';
  },

  @computed('isServicesLoading', 'isSummaryLoading', 'serviceDisplayName')
  selectedServiceName(isServicesLoading, isSummaryLoading, serviceDisplayName) {
    if (isServicesLoading) {
      return 'Loading Services';
    } else if (isSummaryLoading) {
      return 'Loading Summary';
    } else {
      return serviceDisplayName;
    }
  },

  @or('isSummaryLoading', 'isServicesLoading')
  isLoading: false
});

export default connect(stateToComputed, dispatchToActions)(ServiceCrumb);
