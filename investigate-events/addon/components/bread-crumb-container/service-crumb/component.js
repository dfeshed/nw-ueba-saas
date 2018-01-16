import Component from 'ember-component';
import computed, { or } from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { isCoreServiceNotUpdated, isSummaryDataInvalid, getServiceDisplayName } from 'investigate-events/reducers/investigate/services/selectors';
import { setService } from 'investigate-events/actions/interaction-creators';
import { lookup } from 'ember-dependency-lookup';
import service from 'ember-service/inject';

const dispatchToActions = { setService };

const stateToComputed = (state) => ({
  isCoreServiceNotUpdated: isCoreServiceNotUpdated(state, lookup('service:appVersion').version),
  isSummaryDataInvalid: isSummaryDataInvalid(state),
  serviceDisplayName: getServiceDisplayName(state),
  isServicesLoading: state.investigate.services.isServicesLoading,
  isSummaryLoading: state.investigate.services.isSummaryLoading,
  services: state.investigate.services.serviceData,
  summaryErrorMessage: state.investigate.services.summaryErrorMessage
});

const ServiceCrumb = Component.extend({
  classNames: ['rsa-investigate-breadcrumb', 'js-test-investigate-events-service-breadcrumb'],

  i18n: service(),

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

  @computed('isCoreServiceNotUpdated', 'isSummaryDataInvalid')
  iconClass: (isCoreServiceNotUpdated, isSummaryDataInvalid) => {
    return isCoreServiceNotUpdated || isSummaryDataInvalid ? 'disclaimer' : '';
  },

  @computed('isCoreServiceNotUpdated', 'isSummaryDataInvalid')
  iconName: (isCoreServiceNotUpdated, isSummaryDataInvalid) => {
    return isCoreServiceNotUpdated || isSummaryDataInvalid ? 'report-problem-triangle' : 'server-3';
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
  isLoading: false,


/**
 * For a selected service, we could have several messages. These are:
 * 1. Some error message returned from the server
 * 2. There is "No Data" for the service
 * 3. No message at all
 *
 * Bumped up the coreServiceNotUpdated message to the top
 * as summary call will always return an error when in mixed mode.
 * And so it was never showing up the coreDeviceNotUpdated message
 *
 * The error message from the server is trimmed like so:
 * Before - rsa.com.nextgen.classException: Failed to connect to broker:50003
 * After  - Failed to connect to broker:50003
 * Before - java.lang.NullPointerException
 * After  - java.lang.NullPointerException
 * @public
 */
  @computed('isCoreServiceNotUpdated', 'isSummaryDataInvalid', 'hasSummaryData', 'summaryErrorMessage', 'i18n')
  selectedServiceMessage(isCoreServiceNotUpdated, isSummaryDataInvalid, hasSummaryData, summaryErrorMessage, i18n) {

    let title = null;
    if (isCoreServiceNotUpdated) {
      title = i18n.t('investigate.services.coreServiceNotUpdated');
    } else if (isSummaryDataInvalid && summaryErrorMessage) {
      // Regex explained - `.*?` Makes the `.*` quantifier lazy, causing it to
      // match as few characters as possible.
      title = summaryErrorMessage.replace(/(.*?:)/, '');
    } else if (!hasSummaryData) {
      title = i18n.t('investigate.services.noData');
    }
    return title;
  }

});

export default connect(stateToComputed, dispatchToActions)(ServiceCrumb);
