import { helper } from '@ember/component/helper';
import moment from 'moment';

export function parseTimestamp(params) {
  const [value, timezone, dateFormat, timeFormat, localeLanguage ] = params;
  moment.locale(localeLanguage ? localeLanguage : 'en'); // Setting the language to the value of i18n.locale, if sent, or 'en'
  const date = moment(value);
  const selectedZoneId = timezone._selected.zoneId;
  const dateForm = date.tz(selectedZoneId).format(dateFormat._selected.format);
  const timeForm = date.tz(selectedZoneId).format(timeFormat._selected.format);
  let val = `${ dateForm } ${ timeForm }`;
  val = val.includes('Invalid') ? '' : val;
  return val;
}

export default helper(parseTimestamp);
