import Stream from '../../../../utils/stream/helpers';
import { module, test } from 'qunit';
import Ember from 'ember';

const { typeOf } = Ember;

module('Unit | Utility | stream/helpers');

test('it accepts subscribers of type object', function(assert) {
  assert.expect(2);

  let stream = Stream.findSocketConfig('test', 'stream');
  assert.notEqual(typeOf(stream), 'undefined', 'Socket configuration undefined');
  stream = Stream.findSocketConfig('dummy', 'stream');
  assert.equal(typeOf(stream), 'undefined', 'Unexpected socket configuration');
});
