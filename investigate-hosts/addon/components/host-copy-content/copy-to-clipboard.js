import run from 'ember-runloop';
import Mixin from 'ember-metal/mixin';

export default Mixin.create({
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