import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolver from '../../../helpers/engine-resolver';

moduleForComponent('host-table-footer', 'Integration | Component | host table footer', {
  integration: true,
  resolver: engineResolver('investigate-hosts'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
  }
});

test('Should show count of hosts displayed out of total hosts', function(assert) {
  const hostCount = 4;
  const hostItems = ['Harp', 'WIN10x64', 'WIN8x64', 'server.local', 'CentOS'];
  this.set('hostCount', hostCount);
  this.set('hostItemsLength', hostItems.length);
  this.set('label', 'hosts');
  this.render(hbs`{{host-table-footer total=hostItemsLength index=hostCount label=label}}`);
  const expected = `${hostCount} of ${hostItems.length} hosts`;
  assert.equal(this.$('div.file-info').text().trim(), expected, 'When count of hosts displayed is less than total hosts length');

  const hostItems2 = ['Harp', 'WIN10x64', 'WIN8x64', 'server.local'];
  this.set('hostItems2Length', hostItems2.length);
  this.render(hbs`{{host-table-footer total=hostItems2Length index=hostCount label=label}}`);
  const expected2 = `${hostCount} of ${hostItems2.length} hosts`;
  assert.equal(this.$('div.file-info').text().trim(), expected2, 'When count of hosts displayed is equal to total hosts length');
});
