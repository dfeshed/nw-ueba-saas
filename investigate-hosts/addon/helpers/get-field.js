import Ember from 'ember';
import moment from 'moment';


const {
  Helper
} = Ember;

export function getField(params) {


  let result;
  const [object, column, timezone, dateFormat, timeFormat ] = params;
  const { dataType, displayFormat } = column;
  const keys = column.get('field').split('.');

  const parseValue = function(value) {
    let val;
    if (value instanceof Array) {
      val = value;
    } else if (dataType === 'DATE') {
      const date = moment(value);
      const selectedZoneId = timezone._selected.zoneId;
      const dateForm = date.tz(selectedZoneId).format(dateFormat._selected.format);
      const timeForm = date.tz(selectedZoneId).format(timeFormat._selected.format);
      val = `${ dateForm } ${ timeForm }`;
      val = val.includes('Invalid') ? '' : val;
    } else if (displayFormat === 'HEX') {
      val = `0x${value.toString(16)}`;
    } else {
      val = value;
    }
    return val;
  };

  for (let i = 0; i < keys.length; i++) {
    let obj = object;
    for (let j = 0; j <= i; j++) {
      const val = obj[keys[j]];
      const val1 = (val !== undefined && val.length !== 0) ? val : '';
      obj = (i === j) ? parseValue(val1) : val1;
    }
    if (obj instanceof Array) {
      const list = [];
      obj = obj ? obj : '';
      obj.forEach((arr) => {
        const val1 = (keys[i + 1] !== undefined) ? arr[keys[i + 1]] : arr[keys[i]];
        const val = (arr instanceof Object) ? val1 : arr;
        if (val !== undefined && val.length !== 0) {
          list.push(parseValue(val));
        }
      });
      result = list;
      break;
    }
    result = obj;
  }

  return result;
}

export default Helper.helper(getField);
