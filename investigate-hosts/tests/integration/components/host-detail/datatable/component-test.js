import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolver from '../../../../helpers/engine-resolver';
import Data from '../../../../data/subscriptions/file-context/data';

const sampleData = {
  'fileName': 'systemd-journald.service',
  'state': 'loaded-active-running',
  'timeModified': '2015-09-15T13:21:10.000Z',
  'type': 'Systemds',
  'path': '/usr/lib/systemd/system',
  'signature':
  {
    'timeStamp': '2016-09-14T09:43:27.000Z',
    'thumbprint': '4a14668158d79df2ac08a5ee77588e5c6a6d2c8f',
    'features': ['signed', 'valid'],
    'signer': 'Martin Prikryl'
  }
};

moduleForComponent('host-detail/utils/datatable', 'Integration | Component | Endpoint Host Detail/Data Table', {
  integration: true,
  resolver: engineResolver('investigate-hosts'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
  }
});

test('Testing datatable component - RSA Loader', function(assert) {
  this.set('Status', 'wait');
  this.render(hbs`{{host-detail/utils/datatable status=Status}}`);
  assert.equal(this.$('.rsa-loader__wheel').attr('class'), 'rsa-loader__wheel', 'RSA loader wheel loaded successfully for wait status');
});

test('Testing datatable component - Importing Items and ColumnsConfig - Length of the datatable', function(assert) {
  const config = [
    {
      field: 'timeModified',
      title: 'LAST MODIFIED TIME',
      format: 'DATE'
    },
    {
      field: 'fileName',
      title: 'SERVICE NAME'
    }
  ];
  this.set('items', Data);
  this.set('columnsConfig', config);
  this.set('Status', false);
  this.render(hbs`{{host-detail/utils/datatable items=items columnsConfig=columnsConfig status=Status}}`);
  assert.equal(this.$('.rsa-data-table-body-row').length, 1, 'It should return the number of rows/length of the table');
});

test('Testing datatable component - Importing Items and ColumnsConfig - Date&Time', function(assert) {
  const config = [
    {
      field: 'timeModified',
      title: 'LAST MODIFIED TIME',
      format: 'DATE'
    },
    {
      field: 'fileName',
      title: 'SERVICE NAME'
    }
  ];
  this.set('items', Data);
  this.set('columnsConfig', config);
  this.set('Status', false);
  this.render(hbs`{{host-detail/utils/datatable items=items columnsConfig=columnsConfig status=Status}}`);
  const dateTime = this.$('.rsa-data-table-body-cell').find('.rsa-content-datetime');
  assert.equal(dateTime.length, 1, 'Date and Time should be displayed in correct format');
});

test('Testing datatable component - Importing Items and ColumnsConfig - Signature', function(assert) {
  const config = [
    {
      field: 'fileName',
      title: 'SERVICE NAME'
    },
    {
      field: 'signature',
      format: 'SIGNATURE'
    }
  ];
  this.set('items', Data);
  this.set('columnsConfig', config);
  this.set('Status', false);
  this.set('sampleData', sampleData);
  this.render(hbs`{{host-detail/utils/datatable items=items columnsConfig=columnsConfig status=Status}}`);
  assert.equal(this.$(this.$('.rsa-data-table-body-cell')[1]).text().trim(), 'unsigned', 'Testing of signature when it is not signed');
});
