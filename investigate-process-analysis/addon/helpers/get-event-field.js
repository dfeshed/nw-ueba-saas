import Helper from '@ember/component/helper';
import moment from 'moment';

/**
 * To get the event data from an object based on its type.
 * @param {*} params
 * @public
 */
export function getEventField(params) {
  let result;
  const [object, column, timezone, dateFormat, timeFormat, localeLanguage ] = params;
  result = object[column.field];
  if (column.type == 'date') {
    moment.locale(localeLanguage ? localeLanguage : 'en'); // Setting the language to the value of i18n.locale, if sent, or 'en'
    const date = moment(result);
    const selectedZoneId = timezone._selected.zoneId;
    const dateForm = date.tz(selectedZoneId).format(dateFormat._selected.format);
    const timeForm = date.tz(selectedZoneId).format(timeFormat._selected.format);
    result = `${ dateForm } ${ timeForm }`;
  }
  return result;
}

export default Helper.helper(getEventField);