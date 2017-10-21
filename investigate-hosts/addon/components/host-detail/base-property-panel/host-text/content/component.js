import Component from 'ember-component';
import run from 'ember-runloop';

export default Component.extend({

  classNames: ['tool-tip-value'],

  text: null,

  /**
   * Initialize clipboard for the element
   * @public
   */
  didInsertElement() {
    run.schedule('afterRender', this, function() {
      if (window.Clipboard) {
        this.clipboard = new window.Clipboard('.js-copy-trigger');
      }
    });
  },

  /**
   * Teardown for the onclick listener in Clipboard JS.
   * @public
   */
  willDestroyElement() {
    if (this.clipboard) {
      this.clipboard.destroy();
      this.clipboard = null;
    }
  }
});
