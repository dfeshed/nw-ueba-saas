import Ember from 'ember';
import RsaApplicationRoute from 'component-lib/routes/application';

const {
  $,
  run
} = Ember;

export default RsaApplicationRoute.extend({

  /**
   * Retrieves the records of all components to be included in this style guide.
   * The entire catalog of records is actually defined as a big JSON API document, which this method caches into
   * the Ember Data store, so that it can be accessed by component throughout the entire style guide app.
   * @todo Consider moving the JSON API document to some other file (maybe a vendor JSON without components-lib addon).
   * @returns {object[]} Records of component models.
   * @public
   */
  model() {
    let me = this;
    return $.get('vendor/component-lib.json')
      .then(function(result) {
        run(function() {
          me.store.push(result);
        });
        return me.store.peekAll('spec');
      });
  }

});
