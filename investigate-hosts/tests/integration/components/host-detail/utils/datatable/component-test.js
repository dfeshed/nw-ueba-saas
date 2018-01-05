import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';
import engineResolver from '../../../../../helpers/engine-resolver';


const dataItems = [
  {
    'fileName': 'systemd-journald.service',
    'signature': {
      'timeStamp': '2016-09-14T09:43:27.000Z',
      'thumbprint': '4a14668158d79df2ac08a5ee77588e5c6a6d2c8f',
      'features': ['signed', 'valid'],
      'signer': 'ABC'
    }
  },
  {
    'fileName': 'vmwgfx.ko',
    'signature': {
      'timeStamp': '2016-10-14T07:43:39.000Z',
      'thumbprint': '4a14668158d79df2ac08a5ee77588e5c6a6d2c8f',
      'features': ['signed', 'valid'],
      'signer': 'XYZ'
    }
  }
];

moduleForComponent('host-detail/utils/datatable', 'Integration | Component | host-detail/utils/datatable', {
  integration: true,
  resolver: engineResolver('investigate-hosts'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
  }
});

test('Should apply appropriate class label for rsa-loader', function(assert) {
  this.set('value', 'true');
  this.render(hbs`{{host-detail/utils/datatable isDataLoading=value}}`);
  assert.equal(this.$('.rsa-loader').hasClass('is-larger'), true, 'Appropriate class label is applied for rsa-loader');
});

test('Should return the length of items in the datatable', function(assert) {
  const config = [
    {
      field: 'fileName',
      title: 'File Name'
    },
    {
      field: 'signature',
      title: 'Signature',
      format: 'SIGNATURE'
    }
  ];

  this.set('data', dataItems);
  this.set('columnsConfig', config);
  this.set('value', false);
  this.render(hbs`
  <style>
      box, section {
        min-height: 1000px
      }
    </style>
  {{host-detail/utils/datatable items=data columnsConfig=columnsConfig isDataLoading=value}}`);
  return wait().then(() => {
    assert.equal(this.$('.rsa-data-table-body-row').length, 2, 'It should return the number of rows/length of the datatable');
  });
});