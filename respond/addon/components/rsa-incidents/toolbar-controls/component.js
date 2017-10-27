import Component from 'ember-component';
import { empty, gt } from 'ember-computed-decorators';
import service from 'ember-service/inject';

/**
 * @class IncidentsControls
 * Represents the bulk action controls for updating, deleting incidents
 *
 * @public
 */
export default Component.extend({
  accessControl: service(),
  i18n: service(),

  @empty('itemsSelected') hasNoSelections: true,

  @gt('itemsSelected.length', 1) isBulkSelection: false,

  updateConfirmationDialogId: 'bulk-update-entities',
  deleteConfirmationDialogId: 'delete-entities',

  /**
   * Creates a closure around the the updateItem call so that the function can be passed as a callback or invoked directly
   * @param entityIds
   * @param fieldName
   * @param value
   * @returns {function()}
   * @private
   */
  _update(entityIds, fieldName, value) {
    return () => {
      this.get('updateItem')(entityIds, fieldName, value);
    };
  },

  /**
   * Creates a closure around the deleteItem call so that the function can be passed as a callback or invoked directly
   * @param entityIds
   * @returns {function()}
   * @private
   */
  _delete(entityIds) {
    return () => {
      this.get('deleteItem')(entityIds);
    };
  },

  actions: {
    deleteIncident() {
      const { itemsSelected, confirm, i18n, deleteConfirmationDialogId } =
        this.getProperties('itemsSelected', 'confirm', 'i18n', 'deleteConfirmationDialogId');
      const deleteItems = this._delete(itemsSelected);

      confirm(deleteConfirmationDialogId, {
        count: itemsSelected.length,
        warning: i18n.t('respond.incidents.actions.actionMessages.deleteWarning')
      }, deleteItems);
    },

    updateIncidentStatus(entityIds, updatedValue) {
      const update = this._update(entityIds, 'status', updatedValue);

      if (this.get('isBulkSelection')) { // if a bulk update
        const confirm = this.get('confirm');
        const i18n = this.get('i18n');
        const fieldLabel = i18n.t('respond.incidents.list.status');
        const valueLabel = i18n.t(`respond.status.${updatedValue}`);
        confirm(this.get('updateConfirmationDialogId'), { fieldLabel, valueLabel, count: entityIds.length }, update);
      } else {
        update();
      }
    },

    updateIncidentPriority(entityIds, updatedValue) {
      const update = this._update(entityIds, 'priority', updatedValue);

      if (this.get('isBulkSelection')) { // if a bulk update
        const { confirm, i18n, updateConfirmationDialogId } = this.getProperties('confirm', 'i18n', 'updateConfirmationDialogId');
        const fieldLabel = i18n.t('respond.incidents.list.priority');
        const valueLabel = i18n.t(`respond.priority.${updatedValue}`);
        confirm(updateConfirmationDialogId, { fieldLabel, valueLabel, count: entityIds.length }, update);
      } else {
        update();
      }
    },

    updateIncidentAssignee(entityIds, updatedValue) {
      // If unassigning the incident, set the value to null
      if (updatedValue.id === 'UNASSIGNED') {
        updatedValue = null;
      }
      const update = this._update(entityIds, 'assignee', updatedValue);

      if (this.get('isBulkSelection')) { // if a bulk update
        const { confirm, i18n, updateConfirmationDialogId } = this.getProperties('confirm', 'i18n', 'updateConfirmationDialogId');
        const fieldLabel = i18n.t('respond.incidents.list.assignee');
        const valueLabel = updatedValue ? updatedValue.name || updatedValue.id : i18n.t('respond.assignee.none');
        confirm(updateConfirmationDialogId, { fieldLabel, valueLabel, count: entityIds.length }, update);
      } else {
        update();
      }
    }
  }
});