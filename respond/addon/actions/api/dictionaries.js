import { promiseRequest } from 'streaming-data/services/data-access/requests';
import RSVP from 'rsvp';

export default {
  /**
   * Executes a websocket fetch call for all priority types that can be applied to an incident.
   *
   * @method getAllPriorityTypes
   * @public
   * @returns {Promise}
   */
  getAllPriorityTypes() {
    return promiseRequest({
      method: 'findAll',
      modelName: 'priority-types',
      query: {}
    });
  },

  /**
   * Executes a websocket fetch call for all status types that can be applied to an incident.
   *
   * @method getAllStatusTypes
   * @public
   * @returns {Promise}
   */
  getAllStatusTypes() {
    return promiseRequest({
      method: 'findAll',
      modelName: 'status-types',
      query: {}
    });
  },

  /**
   * Executes a websocket fetch call for the hierarchical (two-level) set of categories (e.g., Error/Malfunction,
   * Environmental/Flood, etc) which can be tagged to Incidents
   * @method getAllCategories
   * @public
   * @returns {*}
   */
  getAllCategories() {
    return promiseRequest({
      method: 'findAll',
      modelName: 'category-tags',
      query: {}
    });
  },

  /**
   * Currently returns a promise auto-resolved to a (hardcoded) set of remediation status types.
   *
   * It is expected this this method will eventually make a websocket server call
   * @method getAllRemediationStatusTypes
   * @public
   * @return {Promise}
   */
  getAllRemediationStatusTypes() {
    const statusTypes = ['NEW', 'ASSIGNED', 'IN_PROGRESS', 'REMEDIATED', 'RISK_ACCEPTED', 'NOT_APPLICABLE'];

    return new RSVP.Promise(function(resolve) {
      resolve(statusTypes);
    });
  },

  /**
   * Currently returns a promise auto-resolved to a (hardcoded) object whose keys are the three different possible
   * targetQueues, and for each key the array of possible remediation types associated with that target queue.
   *
   * It is expected this this method will eventually make a websocket server call
   * @method getAllRemediationTypes
   * @public
   * @return {Promise}
   */
  getAllRemediationTypes() {
    const OPERATIONS = ['QUARANTINE_HOST', 'QUARANTINE_NETORK_DEVICE', 'BLOCK_IP_PORT', 'BLOCK_EXTERNAL_ACCESS_TO_DMZ',
      'BLOCK_VPN_ACCESS', 'REIMAGE_HOST', 'UPDATE_FIREWALL_POLICY', 'UPDATE_IDS_IPS_POLICY', 'UPDATE_WEB_PROXY_POLICY',
      'UPDATE_ACCESS_POLICY', 'UPDATE_VPN_POLICY', 'CUSTOM'];

    const GRC = ['MITIGATE_RISK', 'MITIGATE_COMPLIANCE_VIOLATION', 'MITIGATE_VULNERABILITY_THREAT',
      'UPDATE_CORPORATE_BUSINESS_POLICY', 'NOTIFY_BC_DR_TEAM', 'CUSTOM'];

    const CONTENT_IMPROVEMENT = ['UPDATE_RULES', 'UPDATE_FEEDS', 'CUSTOM'];

    return new RSVP.Promise(function(resolve) {
      resolve({
        OPERATIONS,
        GRC,
        CONTENT_IMPROVEMENT
      });
    });
  }

};
