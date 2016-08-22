/**
 * @file Investigate Route Meta Actions
 * Route actions related to fetching/manipulating the meta data for the current Core query.
 * These actions assume that the state is accessible via `this.get('state')`.
 * @public
 */
import Ember from 'ember';

const { Mixin } = Ember;

export default Mixin.create({
  actions: {
    /**
     * Updates the meta panel size state to a given value.
     * @param size
     * @public
     */
    metaPanelSize(size) {
      this.set('state.meta.panelSize', size);
    }
  }
});
