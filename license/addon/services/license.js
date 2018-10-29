import { warn } from '@ember/debug';
import Service, { inject as service } from '@ember/service';
import moment from 'moment';

const DEFAULT_COMPLIANCE = { compliant: true, compliances: [ { status: 'OKAY' } ] };
const BANNER_DISMISSED_KEY = 'rsa-license-banner-dismissed';
const LAST_COMPLIANCE_DATE_KEY = 'compliance-last-fetched-date';
const MAX_OFFLINE_ALLOWED = moment.duration(4, 'days').asMilliseconds();

/**
 * @class License service
 * A convenient service for getting license compliance information, and to get/set/reset the banner dismissed
 * flag in the session storage
 *
 * @public
 */
export default Service.extend({

  request: service(),

  /**
   * This is an async function to get the license compliance from license server
   *
   * @returns {Object} license compliance snapshot
   * @public
   */
  async getCompliance() {
    try {
      const { data } = await this.get('request').promiseRequest({
        modelName: 'license-compliance',
        method: 'get',
        query: {}
      });
      localStorage.setItem(LAST_COMPLIANCE_DATE_KEY, (new Date()));
      return data;
    } catch (error) {
      warn(`Could not get license compliance: ${error}`, { id: 'license.compliance.error' });

      const lastComplianceCheckDate = localStorage.getItem(LAST_COMPLIANCE_DATE_KEY);
      if (moment().diff(moment(parseInt(lastComplianceCheckDate, 10))) > MAX_OFFLINE_ALLOWED) {
        return { compliant: false, compliances: [ { status: 'LICENSE_SERVER_DOWN' } ] };
      }

      return DEFAULT_COMPLIANCE;
    }
  },

  /**
   * Checks if the license banner was dismissed by checking in the sessionStorage
   * @returns {boolean} true if the banner dismissed flag exists in the session storage
   * @public
   */
  isBannerDismissed() {
    return sessionStorage.getItem(BANNER_DISMISSED_KEY) === 'true';
  },

  /**
   * Sets the license banner dismissed state in the sessionStorage
   * @public
   */
  setBannerDismissed() {
    sessionStorage.setItem(BANNER_DISMISSED_KEY, 'true');
  },

  /**
   * Resets the license banner dismissed state in the sessionStorage
   * @public
   */
  resetBannerDismissed() {
    sessionStorage.removeItem(BANNER_DISMISSED_KEY);
  }

});
