import { accordianLabel } from 'sa/helpers/accordian-label';
import { module, test } from 'qunit';
import columnData from 'sa/context/tree-table';

module('Unit | Helper | accordian label');

test('it works', function(assert) {
  const columns = columnData;
  const list = { 'dataSourceName': 'list1',
    'dataSourceDescription': 'Black Listed IP','resultList': [
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
    ] };

  const result = accordianLabel([list,columns.columns]);
  assert.equal(result.length, 3);
});