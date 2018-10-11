import { warn } from '@ember/debug';
import Service, { inject as service } from '@ember/service';

const DEFAULT_COMPLIANCE = { compliant: true, compliances: [ { status: 'OKAY' } ] };
const BANNER_DISMISSED_KEY = 'rsa-license-banner-dismissed';

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
      return data;
    } catch (error) {
      warn(`Could not get license compliance: ${error}`, { id: 'license.compliance.error' });
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
