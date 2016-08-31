import Ember from 'ember';
import moment from 'moment';

const { $, isEmpty, merge } = Ember;

/**
 * Formats a given value for a given field into a user friendly string for display.
 * @param {string} field The identifier of the field (e.g., 'ip.src').
 * @param {*} value The data value corresponding to the field.
 * @param {object} [opts] Optional hash of configuration settings. May be field-specific.
 * @returns {string}
 * @public
 */
function text(field, value, opts) {
  switch (field) {
    case 'size':
      return _size(value, opts);
    case 'time':
      return _time(value, opts);
    default:
      return _alias(field, value, opts);
  }
}

/**
 * Maps a given field value pair to the corresponding alias value in a given lookup hash.
 * @param {string} field The identifier of the field (e.g., 'ip.src').
 * @param {*} value The data value corresponding to the field.
 * @param {object} [opts] Optional hash of configuration settings.
 * @param {object} [opts.aliases] Optional hash of lookup tables, keyed by field name.
 * @param {boolean} [opts.appendRawValue=false] If true, raw value is appended after aliased value e.g., "foo (1)"
 * @returns {string}
 * @private
 */
function _alias(field, value, opts = {}) {
  const lookups = opts.aliases;
  const fieldLookup = lookups && lookups[field];
  const valueLookup = fieldLookup && fieldLookup[value];
  if (valueLookup === undefined) {
    return String(value);
  } else if (opts.appendRawValue) {
    return `${valueLookup} (${value})`;
  } else {
    return valueLookup;
  }
}

/**
 * Formats a given value for a given field into a user friendly string for tooltips.
 * Extends the `value()` method to include field-specific logic; for example, for the `size` field, the `value()`
 * method may aggregate & round the given value, but this method will not.
 * @param {string} field The identifier of the field (e.g., 'ip.src').
 * @param {*} value The data value corresponding to the field.
 * @param {object} [opts] Optional hash of configuration settings. May be field-specific.
 * @returns {string}
 * @public
 */
function tooltip(field, value, opts = {}) {
  if (field === 'size') {
    return _size(value, opts, true);
  } else {
    return text(field, value, merge({ appendRawValue: true }, opts));
  }
}

/**
 * Formats a given width value into a CSS string value.
 * If no valid value is given, a default can be applied from the given (optional) options hash.
 * @param {number} value The width.
 * @param {object} [opts] Optional configuration settings.
 * @param {number|string} [opts.defaultWidth] The default width to be applied if `value` is invalid.
 * @returns string
 * @public
 */
function width(value, opts = {}) {
  let w = _parseNumberAndUnits(value) ||  _parseNumberAndUnits(opts.defaultWidth);
  return w.auto ? 'auto' : `${w.num}${w.units || 'px'}`;
}

// Formats a given number of bytes into a string with units (either 'bytes' or 'KB').
// If bytes < 1 KB, uses 'bytes'; otherwise, uses KB and rounds to the first decimal.
function _size(value, opts = {}, dontAggregate = false) {
  let showAsKb = !dontAggregate && $.isNumeric(value) && (value >= 1024);
  if (showAsKb) {
    let valuesAsKb = (value / 1024).toFixed(1);
    return `${valuesAsKb} ${opts.kbLabel || 'KB'}`;
  } else {
    return `${value} ${opts.bytesLabel || 'bytes'}`;
  }
}

// Formats a given timestamp value into a string using given (optional) format.
function _time(value, opts = {}) {
  return moment(value).format(opts.dateTimeFormat || 'YYYY-MM-DD HH:mm:ss');
}

// Parses a given width value into a number and units (if any).
// If the value 'auto' is given, returns `{ auto: true }`.
// Example: parses '24.5%' into `{ number: 24.5, units: '%' }`.
function _parseNumberAndUnits(value) {
  if (!isEmpty(value)) {
    if (value === 'auto') {
      return { auto: true };
    } else {
      let match = String(value).match(/([\d\.]+)([^\d]*)/);
      let num = match && Number(match[1]);
      let units = (match && match[2]) || '';

      if (!isNaN(num)) {
        return { num, units };
      }
    }
  }
  return null;
}

export default {
  text,
  tooltip,
  width
};
