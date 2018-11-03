import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolver from 'ember-engines/test-support/engine-resolver-for';

module('Integration | Component | host table footer', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('Should show count of hosts displayed out of total hosts', async function(assert) {
    const hostCount = 4;
    const selectedHosts = 1;
    const hostItems = ['Harp', 'WIN10x64', 'WIN8x64', 'server.local', 'CentOS'];
    this.set('hostCount', hostCount);
    this.set('hostItemsLength', hostItems.length);
    this.set('selectedHosts', selectedHosts);
    this.set('label', 'hosts');
    await render(
      hbs`{{host-table-footer total=hostItemsLength index=hostCount label=label selectedItems=selectedHosts}}`
    );
    const expected = `Showing ${hostCount} of ${hostItems.length} hosts | ${selectedHosts} selected`;
    assert.equal(find('div.file-info').textContent.trim(), expected, 'When count of hosts displayed is less than total hosts length');

    const hostItems2 = ['Harp', 'WIN10x64', 'WIN8x64', 'server.local'];
    this.set('hostItems2Length', hostItems2.length);
    await render(
      hbs`{{host-table-footer total=hostItems2Length index=hostCount label=label selectedItems=selectedHosts}}`
    );
    const expected2 = `Showing ${hostCount} of ${hostItems2.length} hosts | ${selectedHosts} selected`;
    assert.equal(find('div.file-info').textContent.trim(), expected2, 'When count of hosts displayed is equal to total hosts length');
  });
});
