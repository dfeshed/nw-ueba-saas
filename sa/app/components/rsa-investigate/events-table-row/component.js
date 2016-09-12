import Ember from 'ember';
import RowMixin from 'sa/components/rsa-data-table/mixins/is-row';
import format from './format-util';
import d3 from 'd3';

const {
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
    run.schedule('afterRender', this, this._renderCells);
  },

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
    const i18n = this.get('i18n');
    const opts = {
      defaultWidth: DEFAULT_WIDTH,
      timeZone: this.get('timezone.selected'),
      aliases: this.get('table.aliases.data'),
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

    // Clear any prior rendered cells. It's important to specify the class name here because we don't
    // want to accidentally remove non-cell DOM (for example, the hidden resizer element!).
    $el.selectAll('.rsa-data-table-body-cell').remove();

    // For each column, build a cell DOM element.
    (this.get('table.columns') || []).forEach((column) => {
      const field = get(column, 'field');
      // Important: Don't use Ember get to read the field value from the data item, because the field could have a dot
      // ('.') in its name (e.g., 'ip.src'). Ember get would mistake such a field name for a property path (`item.ip.src`).
      const value = item[field];
      const tooltip = format.tooltip(field, value, opts);
      const text = format.text(field, value, opts);
      const width = format.width(get(column, 'width'));
      $el.append('div')
        .classed('rsa-data-table-body-cell', true)
        .attr('data-field', field)
        .style('width', width)
        .append('span')
        .classed('content', true)
        .attr('title', tooltip)
        .text(text);
    });
  },


  // Updates the widths of the existing cell DOMs (if any) to match those in the column models.
  // Normally this could be done declaratively via Ember's template bindings, but due to performance issues this
  // component builds the cells' DOM imperatively rather than declaratively.
  _repaintCellWidths() {
    if (!this.element) {
      return;
    }
    let cells = this.$('.rsa-data-table-body-cell');
    (this.get('table.columns') || []).forEach((column, index) => {
      d3.select(cells[index])
        .style(
          'width',
          format.width(get(column, 'width'), { defaultWidth: DEFAULT_WIDTH })
        );
    });
  },

  // Triggers a redraw of the cells whenever the data item or columns model changes.
  _columnsDidChange: observer('item', 'table.columns.[]', function() {
    run.once(this, this._renderCells);
  }),

  // Triggers an update of the cell DOM widths whenever the column model's width changes.
  _columnWidthDidChange: observer('table.columns.@each.width', function() {
    run(this, this._repaintCellWidths);
  })
});
