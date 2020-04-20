import { computed } from '@ember/object';
import Component from '@ember/component';
import { inject as service } from '@ember/service';
import { connect } from 'ember-redux';
import { getStatusTypes } from 'respond/selectors/dictionaries';
import {
  getPriorityTypes,
  getAssigneeOptions
} from 'respond-shared/selectors/create-incident/selectors';
import { isIncidentClosed } from 'respond/helpers/is-incident-closed';
import enhance from 'respond/utils/enhance-users';

const stateToComputed = (state) => {
  return {
    priorityTypes: getPriorityTypes(state),
    statusTypes: getStatusTypes(state),
    users: getAssigneeOptions(state)
  };
};

const IncidentOverview = Component.extend({
  riac: service(),
  accessControl: service(),
  i18n: service(),
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

  canChangeAssignee: computed('riac.canChangeAssignee', 'info.status', function() {
    const isClosed = isIncidentClosed(this.info?.status);
    return !isClosed && this.riac?.canChangeAssignee;
  }),

  unassignedLabel: computed('i18n.locale', function() {
    const i18n = this.get('i18n');
    return i18n.t('respond.assignee.none');
  }),

  assigneeName: computed('info.assignee', 'unassignedLabel', function() {
    // if there's no assignee, return the unassigned label, otherwise the name or ID of the assignee
    return !this.info?.assignee ? this.unassignedLabel : (this.info?.assignee.name || this.info?.assignee.id);
  }),

  selectedUserOption: computed('info.assignee', 'users', function() {
    const users = this.users || [];
    // if the user is null/undefined, return the unassign user option (first option), otherwise lookup by id
    return !this.info?.assignee ? users[0] : users.findBy('id', this.info?.assignee.id);
  }),

  assigneeOptions: computed('users', 'accessControl.username', function() {
    return enhance(this.users, this.accessControl?.username);
  }),

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