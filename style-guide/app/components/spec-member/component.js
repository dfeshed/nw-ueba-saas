import Component from '@ember/component';
import { run } from '@ember/runloop';

export default Component.extend({

  classNames: 'spec-member',

  title: null,

  subtitle: null,

  didInsertElement() {
    run.schedule('afterRender', this, function() {
      if (window.Clipboard) {
        this.clipboard = new window.Clipboard('.hljs', {
          target: (e) => {
            return e;
          }
        });
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
