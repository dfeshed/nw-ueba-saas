import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';

const closedStatuses = ['REMEDIATED', 'RISK_ACCEPTED', 'NOT_APPLICABLE'];

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
  accessControl: service(),

  /**
   * Returns true if the status is one of the open types, or false if one of the closed types (Remediated, Risk
   * Accepted, or Not Applicable)
   * @param status
   * @returns {boolean}
   * @public
   */
  @computed('info.status')
  isOpen(status) {
    return !closedStatuses.includes(status);
  },

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
    update(entityId, field, updatedValue, revertCallback) {
      this.get('updateItem')(entityId, field, updatedValue, revertCallback);
    },

    selectionChange(entityId, field, updatedValue) {
      this.send('update', entityId, field, updatedValue);
    },

    editableFieldChange(entityId, field, updatedValue, originalValue, revertCallback) {
      this.send('update', entityId, field, updatedValue, revertCallback);
    }
  }
});

export default connect(stateToComputed, undefined)(RemediationTaskOverview);
