/**
 * @file Investigate Route Results Actions
 * Route actions related to fetching the overall results of a Core query.  Results have several pieces to them, each
 * fetched from corresponding server calls. The actions here manage the fetching of those various pieces.
 * These actions assume that the state is accessible via `this.get('state')`.
 * @public
 */
import Ember from 'ember';

const { Mixin } = Ember;

export default Mixin.create({
  /**
   * Kicks off the getting of results for whichever UI pieces are currently presented (meta, events, timeline, etc).
   * Invoked whenever user executes an action that may require the query results to be retrieved (e.g., execute new
   * search, drill down in search results, show/hide meta panel).
   * @param {object} queryNode
   * @param {boolean} [forceReload=true]
   * @public
   */
  actions: {
    resultsGet(queryNode, forceReload = false) {
      // Only load data for the panel(s) currently shown.
      const metaPanelSize = this.get('state.meta.panelSize');

      // If the meta panel is minimized, its data is not needed.
      if (metaPanelSize !== 'min') {
        this.send('metaGet', queryNode, forceReload);
      }

      // If the meta panel is maximized, the events & timeline are hidden, so data not needed.
      if (metaPanelSize !== 'max') {
        this.send('eventsGetFirst', queryNode, forceReload);
        this.send('eventCountGet', queryNode, forceReload);
        this.send('eventTimelineGet', queryNode, forceReload);
      }
    },

    /**
     * In order to reduce memory footprint, clear events list & timeline from the given node.
     * Don't clear event count; it's just a single number and it's useful to have around even for historical queries.
     * @param {object} queryNode
     * @public
     */
    resultsClear(queryNode) {
      this.send('eventsClear', queryNode);
      this.send('eventTimelineClear', queryNode);
    }
  }
});
