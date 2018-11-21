import Component from '@ember/component';
import computed, { or } from 'ember-computed-decorators';
import {
  isCoreServiceNotUpdated,
  isSummaryDataInvalid,
  getServiceDisplayName,
  hasSummaryData
} from 'investigate-shared/selectors/services/selectors';

import { lookup } from 'ember-dependency-lookup';

import { inject as service } from '@ember/service';

import layout from './template';

const ServiceSelector = Component.extend({
  layout,

  classNames: ['rsa-investigate-query-container__service-selector'],

  i18n: service(),

  appVersion: service(),

  services: null,

  isCoreServiceNotUpdated: false,

  isSummaryDataInvalid: false,

  serviceDisplayName: '',

  isServicesLoading: false,

  isServicesRetrieveError: false,

  isSummaryLoading: false,

  serviceData: null,

  summaryErrorMessage: '',

  onServiceSelection: null,

  didReceiveAttrs() {
    this._super(...arguments);
    const services = this.get('services');
    const state = {
      services,
      serviceId: this.get('serviceId')
    };
    if (services) {
      this.setProperties(
        {
          isCoreServiceNotUpdated: isCoreServiceNotUpdated(state, lookup('service:appVersion').get('minServiceVersion')),
          isSummaryDataInvalid: isSummaryDataInvalid(state),
          serviceDisplayName: getServiceDisplayName(state),
          isServicesLoading: services.isServicesLoading,
          isServicesRetrieveError: services.isServicesRetrieveError,
          isSummaryLoading: services.isSummaryLoading,
          serviceData: services.serviceData,
          summaryErrorMessage: services.summaryErrorMessage,
          hasSummaryData: hasSummaryData(state)
        }
      );
    }
  },

  @computed()
  panelId() {
    return `queryServiceTooltip-${this.get('elementId')}`;
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
  @computed('isCoreServiceNotUpdated', 'isServicesRetrieveError', 'isSummaryDataInvalid', 'hasSummaryData', 'summaryErrorMessage', 'i18n', 'i18n.locale')
  selectedServiceMessage(isCoreServiceNotUpdated, isServicesRetrieveError, isSummaryDataInvalid, hasSummaryData, summaryErrorMessage, i18n) {
    let title = null;
    if (isCoreServiceNotUpdated) {
      const version = this.get('appVersion.marketingVersion');
      const minVersion = this.get('appVersion.minServiceVersion');
      title = i18n.t('investigate.services.coreServiceNotUpdated', { version, minVersion });
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

export default ServiceSelector;
