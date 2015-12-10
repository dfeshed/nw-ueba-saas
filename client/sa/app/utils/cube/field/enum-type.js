/**
 * @file Field Type Enumeration
 * Categorizes fields by how their corresponding values are structured and should be grouped.
 * @public
 */

export default {
  DEFAULT: 'DEFAULT',
  ARRAY: 'ARRAY',   // Field values are arrays of primitives, each of which should be treated separately.
  CSV: 'CSV'      // Field values are strings of comma-separated-values, each of which should be treated separately.
};
