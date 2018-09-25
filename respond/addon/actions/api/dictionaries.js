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
    const alertSources = ['ECAT', 'Event Stream Analysis', 'Malware Analysis', 'NetWitness Investigate', 'Reporting Engine', 'User Entity Behavior Analytics', 'Web Threat Detection'];
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
