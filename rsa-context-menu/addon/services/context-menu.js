import EmberContextMenuService from 'ember-context-menu/services/context-menu';
import $ from 'jquery';

export default EmberContextMenuService.extend({

  removeDeactivateHandler() {
    const deactivate = this.get('deactivate');
    if (deactivate) {
      $(document.body).off('contextmenu', deactivate);
    }
    this._super(...arguments);
  },

  addDeactivateHandler() {
    this._super(...arguments);
    const deactivate = this.get('deactivate');
    $(document.body).one('contextmenu', (event) => {
      if (event.target.closest('.content-context-menu')) {
        // if context-menu target, do not call deactivate
        return;
      }
      deactivate();
    });
  }
});