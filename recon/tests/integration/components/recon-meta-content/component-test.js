import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import DataActions from 'recon/actions/data-creators';

moduleForComponent('recon-meta-content', 'Integration | Component | recon meta content', {
  integration: true,
  setup() {
    this.inject.service('redux');
  }
});

test('several meta items render correctly', function(assert) {
  // const done = assert.async();
  const meta = [
    [
      'size',
      62750
    ],
    [
      'payload',
      56460
    ],
    [
      'medium',
      1
    ],
    [
      'eth.src',
      '70:56:81:9A:94:DD'
    ],
    [
      'eth.dst',
      '10:0D:7F:75:C4:C8'
    ]
  ];
  this.get('redux').dispatch(DataActions.initializeRecon({ meta }));
  this.render(hbs`{{recon-meta-content}}`);
  assert.equal(this.$('.recon-meta-content-item').length, 5);
});

test('zero meta items render correctly', function(assert) {
  this.set('meta', []);
  this.render(hbs`{{recon-meta-content meta=meta}}`);
  assert.equal(this.$('.recon-meta-content-item').length, 0);
});
