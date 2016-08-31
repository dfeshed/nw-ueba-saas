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
     * @param {string} size Either 'min', 'max' or 'default'.
     * @public
     */
    metaPanelSize(size) {
      // When expanding meta panel from its minimized state, ensure recon panel is closed.
      if ((this.get('state.meta.panelSize') === 'min') && (size !== 'min')) {
        this.send('reconClose', false);
      }
      this.set('state.meta.panelSize', size);
    }
  }
});
