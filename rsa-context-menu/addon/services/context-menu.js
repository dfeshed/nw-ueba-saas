import EmberContextMenuService from 'ember-context-menu/services/context-menu';
import $ from 'jquery';

export default EmberContextMenuService.extend({

  removeDeactivateHandler() {
    const deactivate = this.get('deactivate');
    if (deactivate) {
      $(document.body).off('contextmenu', deactivate);
    }

    // Clearing the previously set data, which was causing problems in multiple instance
    this.set('items', null);
    this.set('event', null);
    this.set('selection', null);
    this.set('details', null);

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
