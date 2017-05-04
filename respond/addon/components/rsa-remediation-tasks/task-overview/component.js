import Component from 'ember-component';
import computed from 'ember-computed-decorators';
import connect from 'ember-redux/components/connect';

const stateToComputed = ({ respond: { dictionaries } }) => {
  return {
    priorityTypes: dictionaries.priorityTypes,
    remediationStatusTypes: dictionaries.remediationStatusTypes,
    remediationTypes: dictionaries.remediationTypes
  };
};
/**
 * @class RemediationTaskOverview
 * Represents the Overview view in the Inspector to display the metadata properties of a Remdiation Task
 *
 * @public
 */
const RemediationTaskOverview = Component.extend({
  tagName: 'vbox',
  classNames: ['rsa-remediation-task-overview'],

  /**
   * Using the target queue as the lookup, retrieves the array of remediation type options available for the
   * selected target queue.
   *
   * @private
   * @property remediationTypeOptions
   * @param targetQueue
   * @param remediationTypes
   * @returns {String[]}
   */
  @computed('info.targetQueue', 'remediationTypes')
  remediationTypeOptions(targetQueue, remediationTypes) {
    if (!targetQueue || !remediationTypes || !remediationTypes[targetQueue]) {
      return [];
    }
    return remediationTypes[targetQueue];
  },

  actions: {
    /**
     * Handles an update to a metadata property (e.g., priority or status) on the Remediation Task
     * @public
     * @param entityId {string} - The ID of the remediation task to update
     * @param field {string} - The name of the field on the record (e.g., 'priority' or 'status') to update
     * @param updatedValue {*} - The value to be set/updated on the record's field
     */
    update(entityId, field, updatedValue) {
      this.sendAction('updateEntity', entityId, field, updatedValue);
    }
  }
});

export default connect(stateToComputed, undefined)(RemediationTaskOverview);