/**
 * @file Utilities related to time ranges and units.
 * @public
 */

// Enumeration of time units.
const enumUNITS = {
  HOUR: 'H',
  DAY: 'D',
  WEEK: 'W',
  MONTH: 'M'
};

// Number of millisec in an hour.
const MS_PER_HOUR = 60 * 60 * 1000,

// Maps time units to number of millisec.
  MS_PER_UNIT = {
    H: MS_PER_HOUR,
    D: MS_PER_HOUR * 24,
    W: MS_PER_HOUR * 24 * 7,
    M: MS_PER_HOUR * 24 * 30
  };

export default {
  /**
   * Enumeration of time 'units'; i.e. commonly-used deltas of time.
   * @private
   * @type {{HOUR: string, DAY: string, WEEK: string, MONTH: string}}
   */
  UNITS: enumUNITS,

  /**
   * Maps a given time interval to an enumerated unit of time from this.UNITS.
   * The interval can be given as either a single Number (milliseconds), or a set of two
   * Numbers which correspond to two Dates (in milliseconds).
   * If the given interval is less than 2 hours, returns 'h';
   * otherwise if the given interval is less than 2 days, returns 'd';
   * otherwise if the given interval is less than 8 days, returns 'w';
   * otherwise returns 'm'.
   * @returns {String} A value from the enumeration this.UNITS.
   * @public
   */
  toUnit(fromVal, toVal) {
    fromVal = parseInt(fromVal, 10) || 0;

    let interval = fromVal;
    if (toVal) {
      toVal = parseInt(toVal, 10) || 0;
      interval = Math.abs(toVal - fromVal);
    }
    if (interval < MS_PER_UNIT.H * 2) {
      return enumUNITS.HOUR;
    } else if (interval < MS_PER_UNIT.D * 2) {
      return enumUNITS.DAY;
    } else if (interval < MS_PER_UNIT.D * 8) {
      return enumUNITS.WEEK;
    } else {
      return enumUNITS.MONTH;
    }
  },

  /**
   * Maps a given unit to a time interval in milliseconds.
   * @param {String} unit A value from the enumeration this.UNITS. If some other value is given, this function
   * defaults to DAY unit.
   * @returns {Number} The time interval in milliseconds.
   * @public
   */
  toMillisec(unit) {
    return MS_PER_UNIT[unit] || MS_PER_UNIT.D;
  }
};
