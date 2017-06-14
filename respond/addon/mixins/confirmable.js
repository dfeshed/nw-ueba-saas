import Mixin from 'ember-metal/mixin';
import service from 'ember-service/inject';
import { next } from 'ember-runloop';
const NOOP = () => ({});

/**
 * @class Confirmable Mixin
 * Equips the consuming object with actions that allow for the display of a confirmation dialog for confirming actions
 * initiated by the user
 * @public
 */
export default Mixin.create({
  eventBus: service(),
  actions: {
    /**
     * Displays specified confirmation modal dialog
     * @method showConfirmationDialog
     * @public
     */
    showConfirmationDialog(confirmationDialogId, confirmationData = {}, confirmCallback = NOOP, cancelCallback = NOOP) {
      this.set('showConfirmationDialog', true);
      next(() => {
        this.setProperties({ confirmationDialogId, confirmationData, confirmCallback, cancelCallback });
        this.get('eventBus').trigger(`rsa-application-modal-open-${confirmationDialogId}`);
      });
    },

    /**
     * Closes/Cancels a specified confirmation 'dialog'
     * @method closeConfirmationDialog
     * @public
     */
    closeConfirmationDialog(confirmationDialogId) {
      this.setProperties({ showConfirmationDialog: false, confirmationDialogId: null, confirmationData: null, confirmCallback: NOOP, cancelCallback: NOOP });
      this.get('eventBus').trigger(`rsa-application-modal-close-${confirmationDialogId}`);
    },

    confirm() {
      const callback = this.get('confirmCallback');
      callback();
      this.send('closeConfirmationDialog', this.get('confirmationDialogId'));
    },

    cancel() {
      this.get('cancelCallback')();
      this.send('closeConfirmationDialog', this.get('confirmationDialogId'));
    }
  }
});