/**
 * @file Context helper utilities
 * @public
 */
import { isEmpty } from '@ember/utils';

const THIRTY_DAYS = 1000 * 60 * 60 * 24 * 30;

export default {

  /**
   * Filters last 30 days' data
   * @param data Array containing points to be plotted on chart (each element is an object with time and percentage)
   * @returns {Array} filtered array
   * @public
   */
  filterLast30Days(data) {
    const filtered = [];
    if (isEmpty(data)) {
      return filtered;
    }
    const last30Days = Date.now() - THIRTY_DAYS;
    for (let i = data.length - 1; i >= 0; i--) {
      const point = data[i];
      if (point.time > last30Days) {
        filtered.unshift(point);
      } else {
        // array is sorted by asc order of time,
        // so if we encounter a point which lies outside the 30-day window, break
        break;
      }
    }
    return filtered;
  }
};