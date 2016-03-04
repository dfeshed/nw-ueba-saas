import Ember from 'ember';
import getOwner from 'ember-getowner-polyfill';

export default Ember.Service.extend({

  /**
   * Name of the currently selected theme.
   * @type String
   * @default ''
   * @public
   */
  selected: Ember.computed({
    get() {
      return this.selected;
    },

    set(key, val) {
      let was = this.get('selected') || '';

      if (was !== val) {
        let config = getOwner(this).resolveRegistration('config:environment'),
            rootEl = null,
            $root = null;

        if (config && config.APP && config.APP.rootElement) {
          rootEl = config.APP.rootElement;
        } else {
          rootEl = 'body';
        }

        $root = Ember.$(rootEl);

        if (was) {
          $root.removeClass(`rsa-${was}`);
        }

        $root.addClass(`rsa-${val}`);
      }
      return val;
    }
  })

});
