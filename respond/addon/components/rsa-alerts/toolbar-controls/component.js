import Component from '@ember/component';
import { gt } from 'ember-computed-decorators';
import { inject as service } from '@ember/service';
import { connect } from 'ember-redux';
import {
  hasSelectedAlertsBelongingToIncidents
} from 'respond/selectors/alerts';
import { clearSearchIncidentsResults } from 'respond/actions/creators/add-alerts-to-incident-creators';
import { next } from '@ember/runloop';

const stateToComputed = (state) => {
  return {
    hasSelectedAlertsBelongingToIncidents: hasSelectedAlertsBelongingToIncidents(state)
  };
};

const dispatchToActions = (dispatch) => {
  return {
    clearIncidentSearchResults() {
      dispatch(clearSearchIncidentsResults());
    }
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
  eventBus: service(),

  @gt('itemsSelected.length', 1) isBulkSelection: false,

  updateConfirmationDialogId: 'bulk-update-entities',
  deleteConfirmationDialogId: 'delete-entities',

  activeModalId: null,

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
    showModal(modalId) {
      this.set('activeModalId', modalId);
      next(() => {
        this.get('eventBus').trigger(`rsa-application-modal-open-${modalId}`);
      });
    },
    closeModal(modalId) {
      this.get('eventBus').trigger(`rsa-application-modal-close-${modalId}`);
      this.set('activeModalId', null);
    },
    createIncident() {
      this.send('showModal', 'create-incident');
    },
    addToIncident() {
      this.send('showModal', 'add-to-incident');
    },
    deleteAlerts() {
      const { itemsSelected, confirm, i18n, deleteConfirmationDialogId } =
        this.getProperties('itemsSelected', 'confirm', 'i18n', 'deleteConfirmationDialogId');
      const deleteItems = this._delete(itemsSelected);

      confirm(deleteConfirmationDialogId, {
        count: itemsSelected.length,
        warning: i18n.t('respond.alerts.actions.actionMessages.deleteWarning')
      }, deleteItems);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(AlertControls);
