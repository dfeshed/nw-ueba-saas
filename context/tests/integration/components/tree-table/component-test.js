import { moduleForComponent, skip } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import columnData from 'context/config/tree-table';
moduleForComponent('tree-table', 'Integration | Component | tree-table', {
  integration: true
});

// TODO: skipping this test for now, as either the test data or component implementation is incorrect
// please find me on slack as @rwwagner90 to discuss these issues
skip('it renders', function(assert) {
  const columns = columnData;
  const list = [
    {
      'resultList': [
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
        }, {
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
            'createdByUser': 'Tony'
          }
        }
      ]
    },
    {
      'resultList': [
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
        }, {
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

      ]
    }];
  this.set('list', list);
  this.set('columns', columns);

  this.render(hbs`{{context-panel/tree-table data=list title=(t columns.title) columnsConfig=columns.columns headerdata=columns.header footerdata=columns.footer}}`);
  assert.equal(this.$('.rsa-content-accordion h3').length, 2, 'Testing to see tree table  headers are rendered');
  assert.equal(this.$('.rsa-content-accordion .content').length, 2, 'Testing to see tree table content is rendered');
  assert.equal(this.$('.rsa-context-tree-table__content-header').length, 39, 'Testing to see tree table columns are rendered ');
});