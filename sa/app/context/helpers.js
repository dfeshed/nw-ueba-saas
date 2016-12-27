/**
 * @file Context helper utilities
 * @public
 */
import Ember from 'ember';
import Iioc from 'sa/context/iioc';

const { isEmpty } = Ember;

const THIRTY_DAYS = 1000 * 60 * 60 * 24 * 30;

export default {

  getIocs(iocsData) {
    const iiocs = [];
    if (iocsData === undefined || iocsData == null || iocsData.length === 0) {
      return null;
    }
    const iioc = Iioc.create({ 'iiocLevel0': [], 'iiocLevel1': [], 'iiocLevel2': [], 'iiocLevel3': [] });

    iocsData.forEach(function(entry) {
      if ('0' === entry.IOCLevel) {
        iioc.iiocLevel0.push(entry.Description);
      } else if ('1' === entry.IOCLevel) {
        iioc.iiocLevel1.push(entry.Description);
      } else if ('2' === entry.IOCLevel) {
        iioc.iiocLevel2.push(entry.Description);
      } else if ('3' === entry.IOCLevel) {
        iioc.iiocLevel3.push(entry.Description);
      }
    });

    iiocs.push(iioc);
    return iiocs;
  },

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