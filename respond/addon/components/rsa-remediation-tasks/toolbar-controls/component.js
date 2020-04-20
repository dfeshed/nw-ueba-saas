import Component from '@ember/component';
import { gt } from '@ember/object/computed';
import { inject as service } from '@ember/service';

/**
 * @class IncidentsControls
 * Represents the bulk action controls for updating, deleting incidents
 *
 * @public
 */
export default Component.extend({
  accessControl: service(),
  i18n: service(),

  isBulkSelection: gt('itemsSelected.length', 1),

  deleteConfirmationDialogId: 'delete-entities',

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
    deleteTask() {
      const { itemsSelected, confirm, i18n, deleteConfirmationDialogId } =
        this.getProperties('itemsSelected', 'confirm', 'i18n', 'deleteConfirmationDialogId');
      const deleteItems = this._delete(itemsSelected);

      confirm(deleteConfirmationDialogId, {
        count: itemsSelected.length,
        warning: i18n.t('respond.remediationTasks.actions.actionMessages.deleteWarning') }, deleteItems);
    }
  }
});
