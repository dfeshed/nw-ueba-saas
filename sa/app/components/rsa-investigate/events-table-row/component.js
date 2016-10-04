import Ember from 'ember';
import RowMixin from 'component-lib/components/rsa-data-table/mixins/is-row';
import columnUtil from './column-util';
import { UI_KEY_LOG_DATA_STATUS } from 'sa/protected/investigate/actions/helpers/log-utils';

import d3 from 'd3';

const {
  computed,
  get,
  inject: {
    service
  },
  run,
  observer,
  Component
} = Ember;

// Default column width if none given.
const DEFAULT_WIDTH = 100;

export default Component.extend(RowMixin, {
  classNames: 'rsa-investigate-events-table-row',

  timezone: service(),

  // Triggers a repaint when timezone changes to update time value formatting.
  timezoneDidChange: observer('timezone.selected', function() {
    this._renderCells();
  }),

  didInsertElement() {
    this._super(...arguments);
    run.schedule('afterRender', this, this.afterRender);
  },

  // Triggers the initial rendering of cell contents.  Ensures that this is done first, before
  // any inherited `afterRender` logic from `_super`. Why? Because the `_super` in this case is a data table row,
  // which measures its own height once the DOM is ready. We want to render the cell contents before that happens.
  afterRender() {
    this._renderCells();
    this._super(...arguments);
  },

  // Formatting configuration options. Passed to utils that generate cell DOM.
  _opts: computed('i18n', 'table.aliases.data', 'timezone.selected', function() {
    const i18n = this.get('i18n');
    return {
      defaultWidth: DEFAULT_WIDTH,
      aliases: this.get('table.aliases.data'),
      timeZone: this.get('timezone.selected'),
      i18n: {
        size: {
          bytes: i18n.t('investigate.size.bytes'),
          KB: i18n.t('investigate.size.KB'),
          MB: i18n.t('investigate.size.MB'),
          GB: i18n.t('investigate.size.GB'),
          TB: i18n.t('investigate.size.TB')
        },
        medium: {
          '1': i18n.t('investigate.medium.network'),
          '32': i18n.t('investigate.medium.log'),
          '33': i18n.t('investigate.medium.correlation')
        }
      }
    };
  }),

  // Builds the DOM for the table cells.
  // Typically in Ember this is handled declaratively by defining DOM in a template.hbs file. But that approach
  // yields to sluggish performance when lazy rendering is turned on, and the user scrolls rapidly through hundreds
  // of records.  As Ember creates & destroys components on scroll, performance degrades proportionally as the
  // number & complexity of the components increases. As a @workaround, we implement this component's DOM content
  // imperatively using D3 rather than template bindings. The performance improvement is quite significant.
  _renderCells() {
    if (!this.element) {
      return;
    }
    const $el = d3.select(this.element);
    const item = this.get('item');

    // Clear any prior rendered cells. It's important to specify the class name here because we don't
    // want to accidentally remove non-cell DOM (for example, the hidden resizer element!).
    $el.selectAll('.rsa-data-table-body-cell').remove();

    // For each column, build a cell DOM element.
    (this.get('table.columns') || []).forEach((column) => {
      this._renderCell($el, column, item);
    });
  },

  // Builds the DOM for an individual table cell.
  // Responsible for building the root cell element, then invoking utils for setting the element's width & inner DOM.
  _renderCell($row, column, item) {
    const field = get(column, 'field');
    const opts = this.get('_opts');

    let $cell = $row.append('div')
      .classed('rsa-data-table-body-cell', true)
      .attr('data-field', field);

    columnUtil.applyCellWidth($cell, column, opts);

    columnUtil.buildCellContent($cell, field, item, opts);
  },

  // Updates the widths of the existing cell DOMs (if any) to match those in the column models.
  // Normally this could be done declaratively via Ember's template bindings, but due to performance issues this
  // component builds the cells' DOM imperatively rather than declaratively.
  _repaintCellWidths() {
    if (!this.element) {
      return;
    }
    const cells = this.$('.rsa-data-table-body-cell');
    const opts = this.get('_opts');
    (this.get('table.columns') || []).forEach((column, index) => {
      let $cell = d3.select(cells[index]);
      columnUtil.applyCellWidth($cell, column, opts);
    });
  },

  // Triggers a redraw of the cells whenever either (1) the data item changes, (2) the columns model changes, or
  // (3) the status of the item's log data changes (log data is fetched async on-demand as the user scrolls).
  _columnsOrDataDidChange: observer('item', 'table.columns.[]', `item.${UI_KEY_LOG_DATA_STATUS}`, function() {
    run.once(this, this._renderCells);
  }),

  // Triggers an update of the cell DOM widths whenever the column model's width changes.
  _columnWidthDidChange: observer('table.columns.@each.width', function() {
    run(this, this._repaintCellWidths);
  })
});
