import { computed } from '@ember/object';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';
import { getPriorityTypes } from 'respond-shared/selectors/create-incident/selectors';
import { getRemediationStatusTypes } from 'respond/selectors/dictionaries';

const closedStatuses = ['REMEDIATED', 'RISK_ACCEPTED', 'NOT_APPLICABLE'];

const stateToComputed = (state) => ({
  priorityTypes: getPriorityTypes(state),
  remediationStatusTypes: getRemediationStatusTypes(state)
});

/**
 * @class RemediationTaskOverview
 * Represents the Overview view in the Inspector to display the metadata properties of a Remediation Task
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
  isOpen: computed('info.status', function() {
    return !closedStatuses.includes(this.info?.status);
  }),

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

export default connect(stateToComputed)(RemediationTaskOverview);
