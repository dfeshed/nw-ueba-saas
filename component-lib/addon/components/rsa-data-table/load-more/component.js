/**
 * @file Data Table Load More component
 * This component renders a (configure) block of markup in the {{body-rows}} of its parent {{rsa-data-table}}.
 * It is useful for rendering a "Load More" button beneath the last body row.
 * @public
 */
import Component from '@ember/component';
import { warn } from '@ember/debug';
import { run } from '@ember/runloop';
import HasTableParent from '../mixins/has-table-parent';
import safeCallback from 'component-lib/utils/safe-callback';
import layout from './template';

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
      const rowsElement = this.get('table.body.rows.element');
      if (!rowsElement) {
        warn('Unable to insert load-more into data-table.body.rows; DOM was not found.', { id: 'component-lib.components.rsa-data-table.load-more.component' });
      } else {
        this.$().appendTo(rowsElement);
      }

      const tableBody = this.$().closest('.rsa-data-table-body');
      tableBody.on('scroll', () => {
        run.debounce(() => {
          const left = tableBody.scrollLeft();
          this.$().css({
            left,
            right: `-${left}px`
          });
        }, 0);
      });
    });
  },

  willDestroyElement() {
    this._super(...arguments);

    const tableBody = this.$().closest('.rsa-data-table-body');
    tableBody.off('scroll');
  }
});
