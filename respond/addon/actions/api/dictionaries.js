import { lookup } from 'ember-dependency-lookup';
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
    const request = lookup('service:request');
    return request.promiseRequest({
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
    const request = lookup('service:request');
    return request.promiseRequest({
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
    const request = lookup('service:request');
    return request.promiseRequest({
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

    return RSVP.resolve(statusTypes);
  },

  /**
   * Currently returns a promise auto-resolved to a (hardcoded) object whose keys are the three different possible
   * targetQueues, and for each key the array of possible remediation types associated with that target queue.
   *
   * It is expected that this method will eventually make a websocket server call
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

    return RSVP.resolve({
      OPERATIONS,
      GRC,
      CONTENT_IMPROVEMENT
    });
  },

  /**
   * Currently returns an auto-resolved promise for a set of hardcoded alert type values.
   *
   * It is expected that this method will eventually make a websocket server call
   * @method getAllAlertTypes
   * @public
   * @returns {Promise}
   */
  getAllAlertTypes() {
    const alertTypes = ['Correlation', 'File Share', 'Instant IOC', 'Log', 'Manual Upload', 'Network', 'On Demand', 'Resubmit', 'Unknown', 'Web Threat Detection Incident'];
    return RSVP.resolve(alertTypes);
  },

  /**
   * Currently returns an auto-resolved promise for a set of hardcoded alert source values.
   *
   * It is expected that this method will eventually make a websocket server call
   * @method getAllAlertSources
   * @public
   * @returns {Promise}
   */
  getAllAlertSources() {
    const alertSources = ['ECAT', 'Event Stream Analysis', 'Malware Analysis', 'NetWitness Investigate', 'Reporting Engine', 'Web Threat Detection'];
    return RSVP.resolve(alertSources);
  },

  /**
   * Retrieves the unique/distinct set of alert rule names
   * @method getAllAlertRuleNames
   * @public
   * @returns {Promise}
   */
  getAllAlertNames() {
    const request = lookup('service:request');
    return request.promiseRequest({
      method: 'findAll',
      modelName: 'alert-names',
      query: {}
    });
  },

  /**
   * Returns a list of all known investigative milestone values
   * @method getAllMilestoneTypes
   * @returns {*}
   * @public
   */
  getAllMilestoneTypes() {
    const request = lookup('service:request');
    return request.promiseRequest({
      method: 'findAll',
      modelName: 'milestone-types',
      query: {}
    });
  }
};
