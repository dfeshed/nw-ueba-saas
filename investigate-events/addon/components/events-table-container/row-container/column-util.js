import { get } from '@ember/object';
import formatUtil from './format-util';
import { isLogEvent } from 'component-lib/utils/log-utils';
import { select } from 'd3-selection';
import { lookup } from 'ember-dependency-lookup';
import { handleInvestigateErrorCode } from 'component-lib/utils/error-codes';
import { SUMMARY_COLUMN_KEYS } from 'investigate-events/reducers/investigate/data-selectors';

/**
 * Applies the width currently specified by a given column model to a given d3 cell.
 * The width is set on the cell element's `style.width`. Additionally, a CSS class `auto-width` is applied only if
 * the width is currently `auto`.
 * @private
 */
function applyCellWidth($cell, column, opts) {
  let width = get(column, 'width');
  const hasAutoWidth = width === 'auto';
  if (!hasAutoWidth) {
    width = formatUtil.width(width, opts);
  } else {
    width = '100%';
  }

  $cell
    .classed('auto-width', hasAutoWidth)
    .style('width', width);
}

/**
 * Builds the inner HTML of a given cell element.
 * Responsible for knowing how to build cells for "custom" columns, i.e. columns that display not just simply
 * the value of a given meta key, but rather a composite of values derived from some app-specific logic.  Such
 * columns were supported in 10.6 and need to be supported here.
 * @private
 */
function buildCellContent($cell, field, item, opts) {
  const $content = $cell.append('div')
    .classed('content', true);

  switch (field) {
    case 'checkbox':
      buildCheckbox($content, item, opts);
      break;
    case 'custom.meta-summary':
      buildMetaSummaryContent($content, item, opts);
      break;
    case 'time':
      buildTimeContent($content, item, opts);
      break;
    case 'custom.theme':
      buildThemeContent($content, field, item, opts);
      break;
    default:
      buildDefaultCellContent($content, field, item, opts);
  }
}

/**
 * Builds the inner HTML for the custom "meta summary" column.
 * Basically just source & destination IPs & ports, but with some logic to check a few different meta keys for
 * the necessary meta values.
 * @private
 */
function buildMetaSummaryContent($content, item, opts) {
  function addMetaSummaryRow(elRowContent) {
    if (elRowContent) {
      $content.node().appendChild(elRowContent);
    }
  }
  if (isLogEvent(item)) {
    const keys = SUMMARY_COLUMN_KEYS.generic.concat(SUMMARY_COLUMN_KEYS.log);
    addMetaSummaryRow(buildLogContent(item));
    addMetaSummaryRow(
      buildMetaKeyValuePairs(keys, item, opts)
    );
  } else {
    const keys = SUMMARY_COLUMN_KEYS.generic.concat(SUMMARY_COLUMN_KEYS.network);
    addMetaSummaryRow(
      buildMetaKeyValuePairs(keys, item, opts)
    );
  }
}

function buildMetaKeyValuePairs(keys, item, opts) {
  const pairs = document.createElement('div');
  const $pairs = select(pairs).attr('class', 'meta-key-value-pairs');
  const htmlPairs = [];
  keys.forEach((key) => {
    const value = formatUtil.value(key, item, opts);
    if (value.raw !== undefined) {
      // special handling of certain log meta
      const originalKey = value.key; // preserve original key
      if (value.key === 'event.cat.name' || value.key === 'ec.theme') {
        value.key = 'event.theme';
      }
      htmlPairs.push(`<span class="key">${value.key} =</span><span class="value entity" metaname="${originalKey}" metavalue="${value.alias}" title="${value.textAndAlias}" data-meta-key='${originalKey}' data-entity-id='${value.alias}'>${value.alias}</span>`);
    }
  });
  $pairs.html(htmlPairs.join(' | '));
  return pairs;
}

function buildCheckbox($content, item, opts) {
  const elClass = opts.isChecked ? 'rsa-form-checkbox-label checked' : 'rsa-form-checkbox-label';
  $content.append('label').attr('class', elClass);
}

/**
 * Build inner HTML for the "time" column.
 * @private
 */
function buildTimeContent($content, item, opts) {
  const tooltip = formatUtil.tooltip('time', item.time, opts);
  const text = formatUtil.text('time', item.time, opts);

  $content
    .attr('title', tooltip)
    .append('div')
    .attr('class', 'time')
    .text(text);
}

/**
 * Builds the inner HTML for the custom "Theme" column.
 * Has logic to populate "Theme" column based on Event Type (Endpoint, Network, Log etc)
 * @private
 */
function buildThemeContent($content, field, item, opts) {
  let value;
  if (isLogEvent(item)) {
    // Use category for an endpoint event and device.type for a log event
    value = opts.isEndpoint ? item.category : item['device.type'];
  } else {
    // Use service for any event that is not log based
    value = item.service;
  }

  const tooltip = formatUtil.tooltip(field, value, opts);
  const text = formatUtil.text(field, value, opts);

  $content
    .attr('title', tooltip)
    .text(text);
}

/**
 * Builds the inner HTML for a standard single-field column's cell.
 * @private
 */
function buildDefaultCellContent($content, field, item, opts) {
  // Important: Don't use Ember get to read the field value from the data item, because the field could have a dot
  // ('.') in its name (e.g., 'ip.src'). Ember get would mistake such a field name for a property path (`item.ip.src`).
  const value = item[field];
  const tooltip = formatUtil.tooltip(field, value, opts);
  const text = formatUtil.text(field, value, opts);
  const htmlWrapper = `<span class="entity" data-meta-key="${field}" data-entity-id="${value}" metaname="${field}" metavalue="${value}">${text}</span>`;
  $content
    .attr('title', tooltip)
    .html(htmlWrapper);
}

/**
 * Builds the inner HTML for a log data column's cell.
 * @private
 */
function buildLogContent(item) {
  const status = item.logStatus || '';
  const data = item.log || '';

  let errorObj;
  let text = '';
  let tooltip = '';

  switch (status) {
    case '':
    case 'wait':
      text = lookup('service:i18n').t('investigate.generic.loading');
      break;
    case 'rejected':
      errorObj = handleInvestigateErrorCode(item);
      text = lookup('service:i18n').t(errorObj.messageLocaleKey, { errorCode: errorObj.errorCode, type: errorObj.type });
      break;
    default:
      tooltip = data;
      text = data;
  }

  const el = document.createElement('div');
  select(el)
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
