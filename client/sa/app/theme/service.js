import Ember from 'ember';
import config from 'sa/config/environment';

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
        let rootSelector = config.APP.rootElement || 'body',
          $root = Ember.$(rootSelector);
        if (was) {
          $root.removeClass(`rsa-${was}`);
        }
        $root.addClass(`rsa-${val}`);
      }
      return val;
    }
  })
});
