import { module, test } from 'qunit';
import { formatResponse } from 'recon/actions/util/meta-util';
import { data } from '../../data/subscriptions/reconstruction-meta/query/data';

module('Unit | Util | Recon Meta Util', function() {

  test('returns correct formatted response for data', function(assert) {
    const unformattedData = [
      {
        name: 'someMeta1',
        type: 'aaa',
        value: 'someValue1',
        count: 0
      },
      {
        name: 'someMeta2',
        type: 'bbb',
        value: 'someValue2',
        count: 0
      },
      {
        name: 'someMeta3',
        type: 'ccc',
        value: 'someValue3',
        count: 0
      }
    ];

    assert.expect(unformattedData.length * 3 + 4);

    const formatted = formatResponse(unformattedData);
    assert.ok(typeof(formatted) === 'object' && formatted.length === 1, 'returns an array of length 1');
    assert.equal(typeof(formatted[0]), 'object', 'returned array contains one element that is of type object');
    assert.ok(formatted[0].hasOwnProperty('metas'), 'returned array contains object with metas property');
    assert.equal(formatted[0].metas.length, unformattedData.length, 'metas array is of correct length');
    formatted[0].metas.forEach((item, i) => {
      assert.ok(typeof(item) === 'object' && item.length === 2, 'returns metas array contains elements that are arrays of length 2');
      assert.ok(item[0] === unformattedData[i].name, 'returned metas array has correct meta name for each element');
      assert.ok(item[1] === unformattedData[i].value, 'returned metas array has correct meta value for each element');
    });
  });

  test('returns correct formatted response for data', function(assert) {
    assert.expect(data.length * 3 + 4);
    const formatted = formatResponse(data);
    assert.ok(typeof(formatted) === 'object' && formatted.length === 1, 'returns an array of length 1');
    assert.equal(typeof(formatted[0]), 'object', 'returned array contains one element that is of type object');
    assert.ok(formatted[0].hasOwnProperty('metas'), 'returned array contains object with metas property');
    assert.equal(formatted[0].metas.length, data.length, 'metas array is of correct length');
    formatted[0].metas.forEach((item, i) => {
      assert.ok(typeof(item) === 'object' && item.length === 2, 'returns metas array contains elements that are arrays of length 2');
      assert.ok(item[0] === data[i].name, 'returned metas array has correct meta name for each element');
      assert.ok(item[1] === data[i].value, 'returned metas array has correct meta value for each element');
    });
  });

  test('returns nothing if data passed in is null or undefined', function(assert) {
    const responseNull = formatResponse(null);
    const responseUndefined = formatResponse(undefined);
    assert.equal(responseNull, undefined, 'returns undefined if data passed in is null');
    assert.equal(responseUndefined, undefined, 'returns undefined if data passed in is undefined');
  });


  test('returns formatted reponse with empty metas array if data passed in is an empty array', function(assert) {
    const emptyArrayFormatted = formatResponse([]);
    assert.ok(typeof(emptyArrayFormatted) === 'object' && emptyArrayFormatted.length === 1, 'returns an array of length 1');
    assert.equal(typeof(emptyArrayFormatted[0]), 'object', 'returned array contains one element that is of type object');
    assert.ok(emptyArrayFormatted[0].hasOwnProperty('metas'), 'returned array contains object with metas property');
    assert.equal(emptyArrayFormatted[0].metas.length, 0, 'metas array is of correct length');
  });
});
