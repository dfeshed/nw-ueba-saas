import Component from '@ember/component';
import { observer } from '@ember/object';
import computed from 'ember-computed-decorators';

import HasTableParent from 'component-lib/components/rsa-data-table/mixins/has-table-parent';
import layout from './template';
import { isNumeric } from 'component-lib/utils/jquery-replacement';
import { next } from '@ember/runloop';

export default Component.extend(HasTableParent, {
  layout,
  tagName: 'header',
  classNames: 'rsa-data-table-header',
  classNameBindings: ['enableColumnSelector'],

  searchTerm: '',

  /**
   * Whether or not to show the column filter in column chooser
   * @public
   */
  enableColumnSearch: true,

  /**
   * CSS selector for the (optional) DOM nodes that can be used to drag-move header cells.
   * If not specified, or if matching node cannot be found, then moving will be disabled.
   * @type {string}
   * @public
   */
  moveHandleSelector: '.js-move-handle', // Selector for the drag-move handle.

  /**
   * Whether or not to translate header titles from locale
   * @public
   */
  translateTitles: false,

  /**
   * @name enableColumnSelector
   * @description Whether or not to display the column selector icon
   * @public
   */
  enableColumnSelector: false,

  /**
   * Moves this component horizontally so that the children header cells will horizontally align with the
   * rsa-data-table/body's cells as the user scrolls horizontally.
   * @private
   */
  _tableBodyScrollLeftDidChange: observer('table.body.scrollLeft', function() {
    if (this._row) {
      const left = this.get('table.body.scrollLeft');
      if (isNumeric(left)) {
        this._row.style.transform = `translate(-${left}px,0)`;
      }
    }
  }),

  /**
   * Filter the column chooser based column name with contains search
   * @public
   */
  @computed('table.sortedColumns', 'searchTerm')
  filterColumnChooser(allFilters, searchTerm) {
    const list = [ ...allFilters ];
    const { i18n, translateTitles } = this.getProperties('i18n', 'translateTitles');
    if (searchTerm) {
      return list.filter((item) => {
        let name = item.title;
        if (translateTitles) {
          name = i18n.t(item.title) || '';
        }
        return name.toString().toUpperCase().includes(searchTerm.toUpperCase());
      });
    }
    return list;
  },

  /**
   * Initialize this element's horizontal translation when it first renders.
   * This ensures its aligns horizontally with the table body's scrollLeft initially, even if this component finishes
   * rendering AFTER the table.body, which is not typical but nevertheless possible.
   * @private
   */
  didInsertElement() {
    this._super(...arguments);
    this._row = document.querySelector('.js-header-row');
    this._tableBodyScrollLeftDidChange();
  },

  willDestroyElement() {
    this._row = null;
    this._super(...arguments);
  },

  actions: {
    toggleColumn(col) {
      col.toggleProperty('selected');
      const fn = this.get('onToggleColumn');
      if (typeof fn === 'function') {
        const columns = this.get('table.visibleColumns');
        next(() => {
          fn.apply(this, [col, columns]);
        });
      }
    },

    clearSearchTerm() {
      this.set('searchTerm', '');
    }
  }
});
