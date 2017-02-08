import { accordionLabel } from 'context/helpers/accordion-label';
import { module, skip } from 'qunit';
import columnData from 'context/config/tree-table';

module('Unit | Helper | accordion label');

// TODO: skipping this test for now, as either the test data or component implementation is incorrect
// please find me on slack as @rwwagner90 to discuss these issues
skip('it works', function(assert) {
  const columns = columnData;
  const list = { 'dataSourceName': 'list1',
    'dataSourceDescription': 'Black Listed IP', 'resultList': [
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

  const result = accordionLabel([list, columns.columns]);
  assert.equal(result.length, 3);
});