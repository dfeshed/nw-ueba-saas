import Component from '@ember/component';
import { gt } from 'ember-computed-decorators';
import { inject as service } from '@ember/service';
import { connect } from 'ember-redux';
import {
  getSelectedAlerts,
  hasSelectedAlertsBelongingToIncidents
} from 'respond/selectors/alerts';

const stateToComputed = (state) => {
  return {
    hasSelectedAlertsBelongingToIncidents: hasSelectedAlertsBelongingToIncidents(state),
    alertIds: getSelectedAlerts(state)
  };
};

/**
 * @class AlertControls
 * Represents the bulk action controls for updating, deleting alerts
 *
 * @public
 */
const AlertControls = Component.extend({
  classNames: ['rsa-alerts-toolbar-controls'],
  accessControl: service(),
  i18n: service(),

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
    deleteAlerts() {
      const { itemsSelected, confirm, i18n, deleteConfirmationDialogId } =
          this.getProperties('itemsSelected', 'confirm', 'i18n', 'deleteConfirmationDialogId');
      const deleteWarningTitle = i18n.t('respond.alerts.actions.actionMessages.deleteWarningTitle');
      const deleteWarnings = [
        i18n.t('respond.alerts.actions.actionMessages.removeFromIncidentWarning'),
        i18n.t('respond.alerts.actions.actionMessages.deleteIncidentWarning'),
        i18n.t('respond.alerts.actions.actionMessages.resetAlertNameFiltersWarning')
      ];
      const deleteItems = this._delete(itemsSelected);
      confirm(deleteConfirmationDialogId, {
        count: itemsSelected.length,
        warningTitle: deleteWarningTitle,
        warnings: deleteWarnings
      }, deleteItems);
    }
  }
});

export default connect(stateToComputed)(AlertControls);