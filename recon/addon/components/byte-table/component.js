import Ember from 'ember';
import Drag from 'recon/utils/drag';
import d3 from 'd3';
import layout from './template';
const { $, Component, K, observer, run } = Ember;

export default Component.extend({
  layout,
  tagName: 'section',
  classNames: 'rsa-byte-table',
  classNameBindings: ['byteFormat'],
  cellClass: 'byte-cell',
  headerCellClass: 'header',
  byteRows: null,
  bytes: null,
  byteFormat: null,
  packet: null,
  packetFields: null,

  /**
   * Indicates a range of packet bytes currently selected, if any; null otherwise.
   * If some bytes are selected, `selection` will be an object with properties:
   * `packet`: the packet data object which contains the selection;
   * `bytes`: {array} the entire list of byte values for `packet`;
   * `start` and `end`: {number} the indices of the first and last selected bytes, respectively, in `packet`.
   * @type {object}
   * @readonly
   * @public
   */
  selection: null,

  /**
   * Configurable callback to be invoked whenever `selection` changes.
   * The callback will be passed `selection` as its single single argument.
   * @type function
   * @public
   */
  onselect: K,

  /**
   * Information that corresponds to the table cell that the user is currently hovering over, if any;
   * null otherwise.
   * @type {object}
   * @public
   */
  hoverData: null,

  didInsertElement() {
    this._scheduleAfterRenderTasks();
  },

  willDestroyElement() {
    this._cells = null;
    this.detachDomListeners();
  },

  _scheduleAfterRenderTasks: observer('byteRows', function() {
    run.schedule('afterRender', this, 'afterRender');
  }),

  afterRender() {
    this.renderTable();
    this.selectionDidChange();
    this.attachDomListeners();
  },

  renderTable() {
    let { cellClass, headerCellClass, byteFormat } = this.getProperties('cellClass', 'headerCellClass', 'byteFormat');
    let  el = d3.select(this.element);

    el.select('table').remove();

    let table = el.append('table');
    let cells = this._cells = [];

    this.get('byteRows').forEach((byteRow) => {
      let tr = table.append('tr');
      byteRow.forEach((byte) => {
        let td = tr.append('td')
          .attr('class', `${cellClass} ${byte.packetField ? byte.packetField.roles : ''} ${byte.isHeader ? headerCellClass : ''}`)
          .datum(byte);

        if (byte.isHeader) {
          td.on('mouseenter', (d) => {
            let packetField = d && d.packetField;
            this.set('hoverData', !packetField ? null : {
              field: packetField.field,
              index: packetField.index,
              values: packetField.values,
              position: { x: d3.event.pageX, y: d3.event.pageY }
            });
          })
          .on('mouseleave', () => {
            this.set('hoverData', null);
          });
        }

        td.append('span')
          .text(byte[byteFormat]);

        cells.push(td.nodes()[0]);
      });
    });
  },

  attachDomListeners() {
    this._mousedownCallback = this._clearSelection.bind(this);
    $(document).on('mousedown', this._mousedownCallback);
  },

  detachDomListeners() {
    $(document).off('mousedown', this._mousedownCallback);
    this._mousedownCallback = null;
  },

  /**
   * Handles changes in `hoverData` by highlighting/unhighlighting the targeted field's bytes.
   * @private
   */
  hoverDataDidChange: observer('hoverData', function() {
    let tds = this.$(`.${this.get('headerCellClass')}`);
    let toggleHover = function(isHover, position, length) {
      [].slice.apply(tds, [position, position + length])
          .forEach((td) => {
            td.setAttribute('data-is-hover', isHover);
          });
    };

    // Unhighlight the table cells corresponding to the last hover data, if any.
    let d = this._lastHoverData;
    if (d && d.field) {
      toggleHover(false, d.field.position, d.field.length);
    }

    // Highlight the table cells corresponding to the current hover data, if any.
    d = this.get('hoverData');
    if (d && d.field) {
      toggleHover(true, d.field.position, d.field.length);
    }

    // Cache the current hover data for future reference (i.e., to unhighlight later).
    this._lastHoverData = d;
  }),

  /**
   * Handles DOM mousedown events in this component by watching for a drag to select byte values in DOM.
   * Tracks which bytes are selected (if any), and updates the corresponding values in `bytes[i].selected`.
   * This is done in order to allow other byte table(s) showing these same bytes to synchronize their selection.
   * @param {object} e The DOM mousedown event object.
   * @private
   */
  mouseDown(e) {
    let drag = this._drag = this._drag || Drag.create({
      on: {
        dragmove: () => {
          this._updateSelection();
        }
      }
    });

    drag.mousedown(e);
    return true;  // Allow browser to perform default behavior (selection highlighting).
  },

  _updateSelection() {
    let selection = window.getSelection && window.getSelection();
    if (!selection || selection.isCollapsed) {

      // Nothing selected by user.
      this._clearSelection();

    } else {

      // Something selected by user.
      let bytes = this.get('bytes');
      let cells = this._cells || [];
      let start = -1;
      let end = -1;

      // Loop through our DOM elements that display byte values.
      // Note that we are using jQuery's `.each()` not Ember's `Array.forEach()`.
      // For each element..
      cells.forEach((cell, index) => {

        // Check if the DOM element is within the user's selection.
        let selected = selection.containsNode(cell, true /* true = partlyContained*/);

        // Update the `isSelected` property of the corresponding item in the `bytesMeta` array.
        // Ember.set(bytes[index], 'isSelected', selected);

        // Track the start & end indices (i.e., min & max) of the selected cells.
        if (selected) {
          if (start === -1) {
            start = index;
          }
          end = index;
        }
      });

      // Update our `selection` attr to reflect which `bytes` are now selected, if any.
      // The `selection` attr is useful shorthand for external observers that listen for changes in the selection.
      // It's also used by _clearSelection to quickly find existing selections without walking all the bytes.
      this.set('selection', (start === -1) ? null : {
        start,
        end,
        bytes,
        packet: this.get('packet')
      });
    }
  },

  /**
   * If any bytes are currently selected, unselects them.
   * Typically called upon hearing a mousedown anywhere in the HTML doc.
   * @private
   */
  _clearSelection() {
    let selection = this.get('selection');
    if (selection) {
      // this.get('bytes').slice(selection.start, selection.end + 1).forEach((byte) => {
      //  Ember.set(byte, 'isSelected', false);
      // });
      this.set('selection', null);
    }
  },

  /**
   * Listens for changes in `selection`, updates the DOM highlighting, and invokes the `onselect` callback in response.
   * @private
   */
  selectionDidChange: observer('selection', function() {
    let tds = this.$(`.${this.get('cellClass')}`);
    let  toggleSelected = function(isSelected, start, end) {
      [].slice.apply(tds, [start, end + 1]).forEach((td) => {
        td.setAttribute('data-is-selected', isSelected);
      });
    };

    // Unhighlight the previous selection, if any.
    let d = this._lastSelection;
    if (d) {
      toggleSelected(false, d.start, d.end);
    }

    // Highlight the current selection, if any.
    d = this.get('selection');
    if (d) {
      toggleSelected(true, d.start, d.end);
    }

    // Cache the current selection for future reference (i.e., unhighlighting).
    this._lastSelection = d;

    this.get('onselect')(this.get('selection'));
  })
});
