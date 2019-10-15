import Component from '@ember/component';
import { next, once, schedule } from '@ember/runloop';
import { inject as service } from '@ember/service';
import { get, observer } from '@ember/object';
import RowMixin from 'component-lib/components/rsa-data-table/mixins/is-row';
import computed from 'ember-computed-decorators';
import HighlightsEntities from 'context/mixins/highlights-entities';
import columnUtil from './column-util';
import { select } from 'd3-selection';
import { isEmpty } from '@ember/utils';

// Default column width if none given.
const DEFAULT_WIDTH = 100;

export default Component.extend(RowMixin, HighlightsEntities, {
  classNames: 'rsa-investigate-events-table-row',

  dateFormat: service(),
  timeFormat: service(),
  timezone: service(),
  entityEndpointId: 'CORE',
  autoHighlightEntities: true,

  @computed('item')
  isChild(item) {
    return item.tuple && (!isEmpty(item['session.split']) || item.groupedWithoutSplit);
  },

  @computed('item.presentAsParent')
  isParent(presentAsParent) {
    return presentAsParent;
  },

  @computed('item.sessionId', 'table.searchScrollIndex', 'table.searchMatches')
  isScrollMatch(id, searchScrollIndex = -1, matches = []) {
    return id === matches[searchScrollIndex];
  },

  @computed('item.sessionId', 'table.searchMatches', 'table.searchMatches.[]', 'table.searchTerm')
  isSearchMatch(id, matches) {
    if (matches && matches.includes(id)) {
      this._highlightSearchMatch();
      return true;
    } else {
      return false;
    }
  },

  // Formatting configuration options. Passed to utils that generate cell DOM.
  @computed('parentView.parentView.selectedItems', 'parentView.parentView.allItemsSelected', 'item.sessionId', 'index')
  isChecked(selectedItems, allItemsSelected, sessionId, index) {
    return allItemsSelected || (selectedItems && selectedItems[index]);
  },

  // Formatting configuration options. Passed to utils that generate cell DOM.
  @computed('table.aliases', 'dateFormat.selected.format', 'timeFormat.selected.format', 'i18n.locale', 'timezone.selected.zoneId')
  _opts(aliases, dateFormat, timeFormat, locale, timeZone) {
    const i18n = get(this, 'i18n');
    return {
      aliases,
      defaultWidth: DEFAULT_WIDTH,
      dateTimeFormat: `${dateFormat} ${timeFormat}`,
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
          '33': i18n.t('investigate.medium.correlation'),
          'endpoint': i18n.t('investigate.medium.endpoint'),
          'undefined': i18n.t('investigate.medium.undefined')
        }
      },
      locale,
      timeZone
    };
  },

  /**
   * Triggers a repaint when timezone changes to update time value formatting or when locale is updated
   * @private
   */
  _datetimeDidChange: observer('i18n.locale', 'timezone.selected', 'dateFormat.selected.format', 'timeFormat.selected.format', function() {
    this._renderCells();
  }),

  _selectionsDidChange: observer('isChecked', function() {
    this._renderCells();
  }),

  _highlightEntities() {
    next(this, 'highlightEntities');
  },

  /**
   * Triggers a redraw of the cells when either:
   * 1. The data item changes
   * 2. The columns model changes
   * @private
   */
  _columnsOrDataDidChange: observer('item', 'table.visibleColumns.[]', function() {
    once(this, this._renderCells);
    once(this, this._highlightEntities);
    if (this.get('isSearchMatch')) {
      once(this, this._highlightSearchMatch);
    }
  }),

  /**
   * Triggers an update of the cell DOM widths whenever the column model's width
   * changes.
   * @private
   */
  _columnWidthDidChange: observer('table.visibleColumns.@each.width', function() {
    once(this, this._repaintCellWidths);
  }),

  didInsertElement() {
    this._super(...arguments);
    schedule('afterRender', this, this._afterRender);
  },

  _highlightSearchMatch() {
    schedule('afterRender', () => {
      const el = this.get('element');
      if (!el) {
        return;
      }

      const matchEls = el.querySelectorAll('.search-match-text');

      matchEls.forEach((el) => {
        el.outerHTML = el.innerHTML;
      });

      const cells = el.querySelectorAll('.rsa-data-table-body-cell .content');
      for (let cell = 0; cell < cells.length; cell++) {
        const root = cells[cell].children[0] ? cells[cell].children[0] : cells[cell];
        const markup = root.innerHTML;
        const newMarkup = markup.replace(new RegExp(this.get('table.searchTerm'), 'gi'), '<span class=\'search-match-text\'>$&</span>');
        root.innerHTML = newMarkup;
      }
    });
  },

  /**
   * Triggers the initial rendering of cell contents.  Ensures that this is done
   * first, before any inherited `afterRender` logic from `_super`. Why? Because
   * the `_super` in this case is a data table row, which measures its own
   * height once the DOM is ready. We want to render the cell contents before
   * that happens.
   * @private
   */
  _afterRender() {
    this._renderCells();
    this._super(...arguments);
    if (this.get('isSearchMatch')) {
      this._highlightSearchMatch();
    }
  },

  /**
   * Builds the DOM for the table cells. Typically in Ember this is handled
   * declaratively by defining DOM in a template.hbs file. But that approach
   * yields to sluggish performance when lazy rendering is turned on, and the
   * user scrolls rapidly through hundreds of records.  As Ember creates &
   * destroys components on scroll, performance degrades proportionally as the
   * number & complexity of the components increases. As a @workaround, we
   * implement this component's DOM content imperatively using D3 rather than
   * template bindings. The performance improvement is quite significant.
   * @private
   */
  _renderCells() {
    if (!this.element) {
      return;
    }
    const $el = select(this.element);
    const item = this.get('item');

    // Clear any prior rendered cells. It's important to specify the class name here because we don't
    // want to accidentally remove non-cell DOM (for example, the hidden resizer element!).
    $el.selectAll('.rsa-data-table-body-cell').remove();

    if (item.tuple) {
      if (!isEmpty(item['session.split'])) {
        $el.append('i')
          .attr('class', 'session-split-decorator grouped-with-split rsa-icon rsa-icon-layers-stacked')
          .attr('title', this.get('i18n').t('investigate.splitSessionLabels.withSplit', {
            split: item['session.split'],
            tuple: item.tuple
          }));
      } else if (item.groupedWithoutSplit) {
        $el.append('i')
          .attr('class', 'session-split-decorator grouped-without-split rsa-icon rsa-icon-layers-stacked')
          .attr('title', this.get('i18n').t('investigate.splitSessionLabels.onlyGrouped', {
            tuple: item.tuple
          }));
      }
    }

    // For each column, build a cell DOM element.
    (this.get('table.visibleColumns') || []).forEach((column, columnIndex) => {
      this._renderCell($el, column, item, columnIndex);
    });
  },

  /**
   * Builds the DOM for an individual table cell. Responsible for building the
   * root cell element, then invoking utils for setting the element's width &
   * inner DOM.
   * @private
   */
  _renderCell($row, column, item, columnIndex) {
    const field = get(column, 'field');
    const _opts = this.get('_opts');
    const isEndpoint = !!item['nwe.callback_id'];

    const opts = Object.assign(_opts, { isEndpoint }, {
      isChecked: this.get('isChecked')
    });

    const $cell = $row.append('div')
      .classed('rsa-data-table-body-cell', true)
      .classed(`column-index-${columnIndex}`, true)
      .attr('data-field', field);
    columnUtil.applyCellWidth($cell, column, opts);
    columnUtil.buildCellContent($cell, field, item, opts);
  },

  /**
   * Updates the widths of the existing cell DOMs (if any) to match those in the
   * column models. Normally this could be done declaratively via Ember's
   * template bindings, but due to performance issues this component builds the
   * cells' DOM imperatively rather than declaratively.
   * @private
   */
  _repaintCellWidths() {
    if (!this.element) {
      return;
    }
    const { element } = this;
    const cells = element.querySelectorAll('.rsa-data-table-body-cell');
    const opts = this.get('_opts');
    (this.get('table.visibleColumns') || []).forEach((column, index) => {
      const $cell = select(cells.item(index));
      columnUtil.applyCellWidth($cell, column, opts);
    });
  }
});
