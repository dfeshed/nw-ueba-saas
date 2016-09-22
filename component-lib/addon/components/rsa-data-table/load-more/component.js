/**
 * @file Data Table Load More component
 * This component renders a (configure) block of markup in the {{body-rows}} of its parent {{rsa-data-table}}.
 * It is useful for rendering a "Load More" button beneath the last body row.
 * @public
 */
import Ember from 'ember';
import HasTableParent from '../mixins/has-table-parent';
import safeCallback from 'component-lib/utils/safe-callback';

import layout from './template';

const {
  run,
  Component,
  Logger
} = Ember;

export default Component.extend(HasTableParent, {
  layout,
  classNames: 'rsa-data-table-load-more',
  classNameBindings: ['status'],

  /**
   * The status of the data fetch. Either 'idle', 'streaming', 'error' or 'complete'.
   * By default, this template will only render content if status is 'idle' or 'streaming'.
   * @type {string}
   * @public
   */
  status: undefined,

  /**
   * Configurable callback to be invoked when user clicks on Load More button.
   * @type {function}
   * @public
   */
  clickAction: undefined,

  actions: {
    safeCallback
  },

  // Appends (moves) this component's DOM into the DOM of it's table's `body.rows`.
  didInsertElement() {
    this._super(...arguments);
    run.schedule('afterRender', () => {
      let rowsElement = this.get('table.body.rows.element');
      if (!rowsElement) {
        Logger.warn('Unable to insert load-more into data-table.body.rows; DOM was not found.');
      } else {
        this.$().appendTo(rowsElement);
      }
    });
  }
});

