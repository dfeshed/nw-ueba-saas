import Ember from 'ember';
import moment from 'moment';

const { isArray, isEmpty, merge } = Ember;

const aKB = 1024;
const aMB = aKB * 1024;
const aGB = aMB * 1024;
const aTB = aGB * 1024;

/**
 * Computes a formatted value for the given field of a given Core event record.
 * If a single field name is given, only that field's value is inspected in the record. Otherwise, if an array of field
 * names is given, will loop thru the fields until a value that is not `undefined` is found.
 * @param {string|string[]} field Either a single field or array of fields whose values are to be inspected.
 * @param {object} item The Core event record to be inspected.
 * @param {object} [opts] Optional hash of configuration settings.
 * @returns {{ raw: *, alias: string, rawAndAlias: string }}
 * @public
 */
function value(field, item, opts) {
  // If given an array of fields, field the first one with a non-`undefined` value.
  let definedField;
  if (!isArray(field)) {
    definedField = field;
  } else {
    definedField = field.find((fieldName) => item[fieldName] !== undefined) || field[0];
  }

  let raw = item[definedField];
  return {
    raw,
    alias: text(definedField, raw, opts),
    textAndAlias: tooltip(definedField, raw, opts)
  };
}

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
  let hash, valueLookup;

  if (field === 'medium') {
    hash = opts.i18n && opts.i18n[field];
    valueLookup = _hashLookup(hash, value);
  }

  if (valueLookup === undefined) {
    hash = opts.aliases && opts.aliases[field];
    valueLookup = _hashLookup(hash, value);
  }

  if (valueLookup === undefined) {
    return (value === undefined) ? '' : String(value);
  } else if (opts.appendRawValue) {
    return `${valueLookup} [${value}]`;
  } else {
    return valueLookup;
  }
}

// Lookup a given key in a given hash. Returns `undefined` if hash not given or key not found in hash.
function _hashLookup(hash, key) {
  return hash && hash[key];
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
  let w = _parseNumberAndUnits(value) || _parseNumberAndUnits(opts.defaultWidth);
  return w.auto ? 'auto' : `${w.num}${w.units || 'px'}`;
}

// Formats a given number of bytes into a string with units (e.g., 'bytes', 'KB', 'MB', 'GB', 'TB').
// The unit labels and the decimal precision can be configured via the `opts` hash argument.
function _size(value, opts = {}, dontAggregate = false) {
  let precision = opts.precision || 0;
  let i18nSize = (opts.i18n && opts.i18n.size) || {};

  if (dontAggregate || (value < aKB)) {
    return `${value} ${i18nSize.bytes || 'bytes'}`;
  } else if (value < aMB) {
    return `${Number(value / aKB).toFixed(precision)} ${i18nSize.KB || 'KB'}`;
  } else if (value < aGB) {
    return `${Number(value / aMB).toFixed(precision)} ${i18nSize.MB || 'MB'}`;
  } else if (value < aTB) {
    return `${Number(value / aGB).toFixed(precision)} ${i18nSize.GB || 'GB'}`;
  } else {
    return `${Number(value / aTB).toFixed(precision)} ${i18nSize.TB || 'TB'}`;
  }
}

// Formats a given timestamp value into a string using given (optional) format.
function _time(value, opts = {}) {
  let mom = moment(value);
  if (opts.timeZone) {
    mom.tz(opts.timeZone);
  }
  return mom.format(opts.dateTimeFormat || 'YYYY-MM-DD[T]HH:mm:ss');
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
  value,
  text,
  tooltip,
  width
};
