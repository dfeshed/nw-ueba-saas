/**
 * @file Context helper utilities
 * @public
 */
import Iioc from 'sa/context/iioc';

export default {

  getIocs(iocsData) {
    let iiocs = [];
    if (iocsData === undefined || iocsData == null || iocsData.length === 0) {
      return null;
    }
    let iioc = Iioc.create({ 'iiocLevel0': [], 'iiocLevel1': [], 'iiocLevel2': [], 'iiocLevel3': [] });

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
  }
};