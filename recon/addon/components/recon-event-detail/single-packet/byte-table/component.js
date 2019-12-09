import Component from '@ember/component';
import { connect } from 'ember-redux';
import { scaleQuantize } from 'd3-scale';
import { event, select } from 'd3-selection';
import {
  showPacketTooltip,
  hidePacketTooltip
} from 'recon/actions/interaction-creators';
import { offset } from 'component-lib/utils/jquery-replacement';
import InViewportMixin from 'ember-in-viewport';
import { setProperties } from '@ember/object';
import { ROW_HEIGHT } from 'recon/components/recon-event-detail/single-packet/util';

// Number of rows to use as a buffer for determining if this component is
// in or near the viewport.
const BUFFER_ROWS = 20;

// A quantize scale will map a continuous domain to a discrete range
const scale = scaleQuantize()
  .domain([0, 255])
  .range([1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16]);

const compact = (array) => {
  const length = array == null ? 0 : array.length;
  const result = [];
  let index = -1;
  let resIndex = 0;

  while (++index < length) {
    const value = array[index];
    if (value) {
      result[resIndex++] = value;
    }
  }
  return result;
};

const dispatchToActions = {
  showPacketTooltip,
  hidePacketTooltip
};

const ByteTableComponent = Component.extend(InViewportMixin, {
  tagName: 'section',
  classNames: 'rsa-byte-table',
  classNameBindings: ['byteFormat'],
  cellClass: 'byte-cell',
  headerCellClass: 'header',

  byteFormat: null,
  hasSignaturesHighlighted: false,
  isPayloadOnly: false,
  packet: null,

  init() {
    this._super(...arguments);
    const buffer = ROW_HEIGHT * BUFFER_ROWS;
    // Configure InViewportMixin
    setProperties(this, {
      viewportTolerance: {
        top: buffer,
        bottom: buffer
      }
    });
  },

  didInsertElement() {
    this._super(...arguments);
    const { byteRows } = this.packet;
    // Figure out what the height of this component will be so that
    // didEnterViewport is not prematurely invoked because the height is 0
    // (because we haven't rendered the table yet).
    const heightInPx = `${byteRows.length * ROW_HEIGHT}px`;
    this.element.style.height = heightInPx;
  },

  didEnterViewport() {
    this.renderTable();
  },

  didExitViewport() {
    this.removeTable();
  },

  renderTable() {
    const { cellClass, headerCellClass, byteFormat } = this;
    const el = select(this.element);
    const table = el.append('table');
    const cells = this._cells = [];
    const byteRows = this.packet?.byteRows ?? [];

    byteRows.forEach((byteRow) => {
      const tr = table.append('tr');
      byteRow.forEach((byte) => {
        const td = tr.append('td')
          .attr('class', this.getByteClass(byte, cellClass, headerCellClass))
          .datum(byte);

        if (byte.isHeader || byte.isKnown) {
          td.on('mousemove', (d) => {
            this.mousemoveHandler(d, byte);
          }).on('mouseleave', () => {
            this.send('hidePacketTooltip');
          });
        }

        td.append('span')
          .attr('class', (d) => (!byte.isHeader && !byte.isFooter) ? `shade-${scale(d.int)}` : null)
          .text(byte[byteFormat]);

        cells.push(td.nodes()[0]);
      });
      // Add a fill cell if there is 1 row with less than 16 cells. This helps
      // align the single-row packet to the other packets with multiple rows.
      if (byteRows.length === 1 && byteRow.length < 16) {
        this._addFillCell(byteRow, byteFormat, tr);
      }
    });
  },

  removeTable() {
    const { element } = this;
    if (element) {
      const table = element.querySelector('table');
      if (table) {
        table.remove();
      }
    }
    this._cells = null;
  },

  /**
   * Adds a `td` cell to a table row that acts as a space filler.
   * @param {Object[]} byteRow - The row of data
   * @param {String} byteFormat - The format of the data
   * @param {Object} tr - A D3 <tr> selection
   * @private
   */
  _addFillCell(byteRow, byteFormat, tr) {
    const fillCount = 16 - byteRow.length;
    const octets = Math.ceil(byteRow.length / 4);
    const padding = 6; // equal to margin + border + padding from CSS
    let fill = '&emsp;';
    if (byteFormat === 'hex') {
      // For some reason, ascii is fine with a single space, but hex
      // wouldn't layout appropriately unless there were a number of spaces
      // equal to what's needed to fill in 16 spaces
      for (let i = 0; i < fillCount; i++) {
        fill += '&emsp;';
      }
    }
    tr.append('td')
      .attr('style', `padding: 0 ${fillCount * padding + (4 - octets)}px`)
      .html(fill);
  },

  mousemoveHandler(d, byte) {
    const hasSignaturesHighlighted = this.get('hasSignaturesHighlighted');
    let tooltipData = null;
    if (byte.isHeader) {
      const packetField = d && d.packetField;
      tooltipData = !packetField ? null : {
        field: packetField.field,
        values: packetField.values,
        position: { x: event.pageX, y: offset(event.target).top },
        packetId: this.get('packet.id')
      };
    } else if (hasSignaturesHighlighted && byte.isKnown) {
      tooltipData = {
        field: { name: 'signature', type: 'sig' },
        values: byte.isKnown.type,
        position: { x: event.pageX, y: offset(event.target).top },
        packetId: this.get('packet.id')
      };
    }
    this.send('showPacketTooltip', tooltipData);
  },

  /**
   * Appends appropriate cell, role, header, footer, and known classes to the
   * byteRow.
   * @param {Object} byte The byte object
   * @param {String} cellClass A class name
   * @param {String} headerCellClass A class to designate byte as a header item
   * @returns {{String}}
   * @public
   */
  getByteClass(byte, cellClass, headerCellClass) {
    const role = byte.packetField ? byte.packetField.roles : false;
    const header = byte.isHeader ? headerCellClass : false;
    const footer = byte.isFooter ? 'footer' : false;
    const known = byte.isKnown ? 'is-known' : false;
    return compact([cellClass, role, header, footer, known]).join(' ');
  }
});

export default connect(null, dispatchToActions)(ByteTableComponent);
