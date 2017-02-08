
// will be completed soon
import { topRecords } from 'context/helpers/top-records';
import { module, test } from 'qunit';

module('Unit | Helper | top records');

// Replace this with your real tests.
test('it works', function(assert) {

  const list = [
    {
      'id': '030afa5c-ae5d-49a6-9dff-0b4f6ac1998a',
      'dataSourceEntryMeta': {
        'createdTimeStamp': 1475225722540,
        'lastModifiedTimeStamp': 1475225722540,
        'createdByUser': 'Tony',
        'lastModifiedByUser': null
      },
      'data': {
        'DestinationIP': '23.99.221.178',
        'Time': 1478093501237,
        'SourceIP': '17.191.140.16',
        'location': 'Bangalore',
        'email': 'tony@emc.com',
        'phone': '9879776682',
        'createdTimeStamp': 1475225722540,
        'lastModifiedTimeStamp': 1475225722540,
        'createdByUser': 'Tony',
        'lastModifiedByUser': 'Duke'
      }
    }
  ];

  const result = topRecords([list, 'overview']);
  assert.equal(result.length, 1);


});

