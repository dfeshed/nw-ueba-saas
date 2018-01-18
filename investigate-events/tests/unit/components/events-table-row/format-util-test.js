import { module, test } from 'qunit';
import moment from 'moment';
import formatUtil from 'investigate-events/components/events-table-row/format-util';

module('Unit | Events Table Row | format util', {
  beforeEach() {
    moment.tz.add([
      'Asia/Kolkata|MMT IST +0630|-5l.a -5u -6u|012121|-2zOtl.a 1r2LP.a 1un0 HB0 7zX0|15e6',
      'America/Los_Angeles|PST PDT|80 70|0101|1Lzm0 1zb0 Op0'
    ]);
  }
});

test('test time formatting', function(assert) {
  const opts = {
    locale: 'en',
    dateTimeFormat: 'YYYY/MM/DD[T]HH:mm:ss',
    timeZone: 'Asia/Kolkata'
  };
  assert.equal(formatUtil.text('time', 1516199601341, opts), '2018/01/17T20:03:21', 'time should be formatted properly in Asia/Kolkata timezone');

  opts.timeZone = 'America/Los_Angeles';
  assert.equal(formatUtil.text('time', 1516199601341, opts), '2018/01/17T07:33:21', 'time should be formatted properly in America/Los_Angeles timezone');

  opts.timeZone = null;
  assert.equal(formatUtil.text('time', 1516199601341, opts), '2018/01/17T14:33:21', 'time should be formatted properly in UTC');
});