import Component from '@ember/component';
import { run } from '@ember/runloop';

export default Component.extend({

  classNames: 'color-swatch',

  classNameBindings: ['backgroundClass'],

  title: null,

  color: null,

  hex: null,

  sass: null,

  didInsertElement() {
    run.schedule('afterRender', this, function() {
      if (window.Clipboard) {
        this.clipboard = new window.Clipboard('code', {
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
