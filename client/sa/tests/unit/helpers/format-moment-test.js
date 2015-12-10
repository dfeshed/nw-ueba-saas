import { formatMoment } from '../../../helpers/format-moment';
import { module, test } from 'qunit';

module('Unit | Helper | format moment');

test('it works', function(assert) {
  let result = formatMoment([new Date(), 'M/D/YY h:mm:ss a']);
  assert.ok(result);
});
