import Component from '@ember/component';
import { inject } from '@ember/service';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import { getPriorityTypes, getStatusTypes } from 'respond/selectors/dictionaries';
import { getAssigneeOptions } from 'respond/selectors/users';

const stateToComputed = (state) => {
  return {
    priorityTypes: getPriorityTypes(state),
    statusTypes: getStatusTypes(state),
    users: getAssigneeOptions(state)
  };
};

const IncidentOverview = Component.extend({
  accessControl: inject(),
  i18n: inject(),
  classNames: ['rsa-incident-overview'],

  /**
   * Incident summary data fetched from server.
   *
   * Includes top-level incident properties (e.g., id, name, priority, status, created) but not the storyline nor alerts list.
   * @property info
   * @type {object}
   * @public
   */
  info: null,

  @computed()
  unassignedLabel() {
    const i18n = this.get('i18n');
    return i18n.t('respond.assignee.none');
  },

  @computed('info.assignee')
  assigneeName(assignee) {
    // if there's no assignee, return the unassigned label, otherwise the name or ID of the assignee
    return !assignee ? this.get('unassignedLabel') : (assignee.name || assignee.id);
  },

  @computed('info.assignee', 'users')
  selectedUserOption(assignee, users = []) {
    // if the user is null/undefined, return the unassign user option (first option), otherwise lookup by id
    return !assignee ? users[0] : users.findBy('id', assignee.id);
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
      this.get('updateItem')(entityId, field, updatedValue);
    },

    handleUpdateAssignee(entityId, field, updatedValue) {
      // Ensure an unassigned "user" results in setting the value to null
      const value = updatedValue.id === 'UNASSIGNED' ? null : updatedValue;
      this.send('update', entityId, field, value);
    }
  }
});

export default connect(stateToComputed)(IncidentOverview);