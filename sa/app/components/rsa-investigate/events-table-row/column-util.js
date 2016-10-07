import Ember from 'ember';
import formatUtil from './format-util';
import { isLogEvent, getEventLogData, getEventLogDataStatus } from 'sa/protected/investigate/actions/helpers/log-utils';
import d3 from 'd3';

const { get, isArray } = Ember;

const RESERVED_KEYS = [ 'eth.src', 'eth.dst', 'ip.src', 'ipv6.src', 'ip.dst', 'ipv6.dst', 'tcp.srcport', 'tcp.dstport', 'udp.srcport', 'udp.dstport', 'session.split'];
const RESERVED_KEY_HASH = {};
RESERVED_KEYS.forEach((key) => {
  RESERVED_KEY_HASH[key] = true;
});

// Applies the width currently specified by a given column model to a given d3 cell.
// The width is set on the cell element's `style.width`. Additionally, a CSS class `auto-width` is applied only if
// the width is currently `auto`.
function applyCellWidth($cell, column, opts) {
  let width = get(column, 'width');
  let hasAutoWidth = (width === 'auto');
  if (!hasAutoWidth) {
    width = formatUtil.width(width, opts);
  }
  $cell
    .classed('auto-width', hasAutoWidth)
    .style('width', width);
}

// Builds the inner HTML of a given cell element.
// Responsible for knowing how to build cells for "custom" columns, i.e. columns that display not just simply
// the value of a given meta key, but rather a composite of values derived from some app-specific logic.  Such
// columns were supported in 10.6 and need to be supported here.
function buildCellContent($cell, field, item, opts) {
  let $content = $cell.append('div')
    .classed('content', true);

  switch (field) {
    case 'custom.meta-summary':
      buildMetaSummaryContent($content, item, opts);
      break;
    case 'custom.meta-details':
      buildMetaDetailsContent($content, item, opts);
      break;
    default:
      buildDefaultCellContent($content, field, item, opts);
  }
}

// Builds the inner HTML for the custom "meta summary" column.
// Basically just source & destination IPs & ports, but with some logic to check a few different meta keys for
// the necessary meta values.
function buildMetaSummaryContent($content, item, opts) {
  function addMetaSummaryRow(elRowContent) {
    if (elRowContent) {
      $content.node().appendChild(elRowContent);
    }
  }

  if (isLogEvent(item)) {

    addMetaSummaryRow(
      buildLogContent(item)
    );

  } else {

    addMetaSummaryRow(
      buildMetaSrcDstPair(['ip.src', 'ipv6.src'], ['ip.dst', 'ipv6.dst'], item, opts)
    );

    addMetaSummaryRow(
      buildMetaSrcDstPair(['tcp.srcport', 'udp.srcport'], ['tcp.dstport', 'udp.dstport'], item, opts)
    );

    addMetaSummaryRow(
      buildMetaKeyAndValue('session.split', item, opts)
    );

  }

}

// Builds the inner HTML for the custom "meta details" column.
// Basically a multi-column ordered list (top->bottom, left->right) of all meta key-value pairs; but for 10.6 parity,
// a few keys get special treatment, as follows.
// * The following are assumed to be in other columns and thus not included here:
// sessionId, eth.type, ip.proto, ipv6.proto, service, size, time, log-data, sessionId.
// * The following are bumped up to the front of the list:
// eth.src, eth.dst, ip[v6].src, ip[v6].dst, [tcp|udp].srcport, [tcp|udp].dstport, session.split
function buildMetaDetailsContent($content, item, opts) {
  const maxRowCount = 10;
  let rowCount = 0;
  let $column;

  function addMetaDetailsColumn() {
    return $content.append('div')
      .classed('meta-details-column', true);
  }

  function addMetaDetailsRow(elRowContent) {
    if (!elRowContent) {
      return;
    }
    if (rowCount % maxRowCount === 0) {
      $column = addMetaDetailsColumn();
    }
    $column.node().appendChild(elRowContent);
    rowCount++;
  }

  addMetaDetailsRow(
    buildMetaSrcDstPair('eth.src', 'eth.dst', item, opts)
  );

  addMetaDetailsRow(
    buildMetaSrcDstPair(['ip.src', 'ipv6.src'], ['ip.dst', 'ipv6.dst'], item, opts)
  );

  addMetaDetailsRow(
    buildMetaSrcDstPair(['tcp.srcport', 'udp.srcport'], ['tcp.dstport', 'udp.dstport'], item, opts)
  );

  addMetaDetailsRow(
    buildMetaKeyAndValue('session.split', item, opts)
  );

  (item.metas || []).forEach(([ metaKey ]) => {
    if (RESERVED_KEY_HASH[metaKey]) {
      return;
    }
    addMetaDetailsRow(
      buildMetaKeyAndValue(metaKey, item, opts)
    );
  });
}

// Builds the DOM for a pair of meta values, one from a source key & one from a destination key.
// Renders an arrow pointing from source to destination.
// Only the meta values are rendered, not the meta keys.
function buildMetaSrcDstPair(srcMetaKey, dstMetaKey, item, opts) {
  let srcValue = formatUtil.value(srcMetaKey, item, opts);
  let dstValue = formatUtil.value(dstMetaKey, item, opts);
  if ((srcValue.raw == undefined) && (dstValue.raw === undefined)) {
    return null;
  }
  let pair = document.createElement('div');
  let $pair = d3.select(pair)
    .classed('meta-src-dst-pair', true)
    .attr('data-field', (isArray(srcMetaKey) ? srcMetaKey[0] : srcMetaKey) || '');
  if (srcValue.raw !== undefined) {
    $pair.append('span')
      .classed('src', true)
      .attr('title', srcValue.textAndAlias)
      .text(srcValue.alias);
  }
  if (dstValue.raw !== undefined) {
    $pair.append('span')
      .classed('arrow', true)
      .html('&rarr;');
    $pair.append('span')
      .classed('dst', true)
      .attr('title', dstValue.textAndAlias)
      .text(dstValue.alias);
  }
  return pair;
}

// Builds the DOM for the meta key & value of a given meta key.
function buildMetaKeyAndValue(metaKey, item, opts) {
  let value = formatUtil.value(metaKey, item, opts);
  if (value.raw === undefined) {
    return null;
  }

  let pair = document.createElement('div');
  let $pair = d3.select(pair)
    .classed('meta-key-and-value', true)
    .attr('data-field', (isArray(metaKey) ? metaKey[0] : metaKey) || '');
  $pair.append('span')
    .classed('key', true)
    .text(metaKey);
  $pair.append('span')
    .classed('value', true)
    .attr('title', value.textAndAlias)
    .text(value.alias);
  return pair;
}

// Builds the inner HTML for a standard single-field column's cell.
function buildDefaultCellContent($content, field, item, opts) {
  // Important: Don't use Ember get to read the field value from the data item, because the field could have a dot
  // ('.') in its name (e.g., 'ip.src'). Ember get would mistake such a field name for a property path (`item.ip.src`).
  const value = item[field];
  const tooltip = formatUtil.tooltip(field, value, opts);
  const text = formatUtil.text(field, value, opts);

  $content
    .attr('title', tooltip)
    .text(text);
}

// Builds the inner HTML for a log data column's cell.
function buildLogContent(item) {
  const status = getEventLogDataStatus(item) || '';
  const data = getEventLogData(item) || '';
  let text = '';
  let tooltip = '';

  switch (status) {
    case 'wait':
      text = 'Loading logs...';
      break;
    case 'rejected':
      text = 'Error loading logs.';
      break;
    default:
      tooltip = data;
      text = data;
  }

  const el = document.createElement('div');
  d3.select(el)
    .classed('log-data', true)
    .attr('title', tooltip)
    .attr('data-status', status)
    .text(text);

  return el;
}

export default {
  applyCellWidth,
  buildCellContent
};
