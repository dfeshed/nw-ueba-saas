import Component from '@ember/component';
import layout from './template';


/**
 * Edit file status component to change the status of the file(s)
 * @public
 */
export default Component.extend({
  layout,

  classNames: 'edit-file-status',

  showOnlyIcons: false,

  isDisabled: false,

  showFileStatusModal: false,

  actions: {
    showEditFileStatusModal() {
      this.set('showFileStatusModal', true);
    },
    closeModal() {
      this.set('showFileStatusModal', false);
    }
  }
});
