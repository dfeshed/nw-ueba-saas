import Component from '@ember/component';
import computed, { or } from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import {
  isCoreServiceNotUpdated,
  isSummaryDataInvalid,
  getServiceDisplayName,
  hasSummaryData
} from 'investigate-events/reducers/investigate/services/selectors';
import { setService } from 'investigate-events/actions/interaction-creators';
import { lookup } from 'ember-dependency-lookup';
import { inject as service } from '@ember/service';

const dispatchToActions = { setService };

const stateToComputed = (state) => ({
  isCoreServiceNotUpdated: isCoreServiceNotUpdated(state, lookup('service:appVersion').version),
  isSummaryDataInvalid: isSummaryDataInvalid(state),
  serviceDisplayName: getServiceDisplayName(state),
  isServicesLoading: state.investigate.services.isServicesLoading,
  isServicesRetrieveError: state.investigate.services.isServicesRetrieveError,
  isSummaryLoading: state.investigate.services.isSummaryLoading,
  services: state.investigate.services.serviceData,
  summaryErrorMessage: state.investigate.services.summaryErrorMessage,
  hasSummaryData: hasSummaryData(state)
});

const ServiceCrumb = Component.extend({
  classNames: ['rsa-investigate-breadcrumb', 'service-breadcrumb', 'js-test-investigate-events-service-breadcrumb'],

  i18n: service(),

  @computed()
  panelId() {
    return `breadCrumbServiceTooltip-${this.get('elementId')}`;
  },

  @computed('isLoading', 'selectedServiceMessage', 'serviceDisplayName', 'i18n')
  title(isLoading, selectedServiceMessage, serviceDisplayName, i18n) {
    if (isLoading) {
      return i18n.t('investigate.generic.loading');
    } else if (selectedServiceMessage) {
      return selectedServiceMessage;
    } else {
      return serviceDisplayName;
    }
  },

  @computed('isCoreServiceNotUpdated', 'isServicesRetrieveError', 'isSummaryDataInvalid')
  iconDetails(isCoreServiceNotUpdated, isServicesRetrieveError, isSummaryDataInvalid) {
    const isError = (isCoreServiceNotUpdated || isServicesRetrieveError || isSummaryDataInvalid);
    return isError ?
      { class: 'disclaimer', name: 'report-problem-triangle' } :
      { class: '', name: 'server-3' };
  },

  @computed('isServicesLoading', 'isSummaryLoading', 'serviceDisplayName', 'i18n')
  selectedServiceName(isServicesLoading, isSummaryLoading, serviceDisplayName, i18n) {
    if (isServicesLoading) {
      return i18n.t('investigate.services.loading');
    } else if (isSummaryLoading) {
      return i18n.t('investigate.summary.loading');
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
  @computed('isCoreServiceNotUpdated', 'isServicesRetrieveError', 'isSummaryDataInvalid', 'hasSummaryData', 'summaryErrorMessage', 'i18n')
  selectedServiceMessage(isCoreServiceNotUpdated, isServicesRetrieveError, isSummaryDataInvalid, hasSummaryData, summaryErrorMessage, i18n) {
    let title = null;
    if (isCoreServiceNotUpdated) {
      title = i18n.t('investigate.services.coreServiceNotUpdated');
    } else if (isServicesRetrieveError) {
      title = i18n.t('investigate.services.error.description');
    } else if (isSummaryDataInvalid && summaryErrorMessage) {
      // Regex explained - `.*?` Makes the `.*` quantifier lazy, causing it to
      // match as few characters as possible.
      const classPath = /(.*?:)/;
      title = summaryErrorMessage.replace(classPath, '');
    } else if (!hasSummaryData) {
      title = i18n.t('investigate.services.noData');
    }
    return title;
  }
});

export default connect(stateToComputed, dispatchToActions)(ServiceCrumb);
