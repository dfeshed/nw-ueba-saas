import Component from 'ember-component';
import { empty, gt } from 'ember-computed-decorators';
import service from 'ember-service/inject';

/**
 * @class AlertControls
 * Represents the bulk action controls for updating, deleting alerts
 *
 * @public
 */
export default Component.extend({

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
  }
});