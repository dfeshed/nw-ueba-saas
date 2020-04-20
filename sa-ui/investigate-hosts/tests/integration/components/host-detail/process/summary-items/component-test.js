import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, settled, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

module('Integration | Component | endpoint host-detail/process/summary-items', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  const config = [
    { field: 'fileName', label: 'investigateHosts.process.fileName' },
    { field: 'pid', label: 'investigateHosts.process.pid' },
    { field: 'parentPid', label: 'investigateHosts.process.parentPid' },
    { field: 'owner', label: 'investigateHosts.process.owner' },
    { field: 'signature', label: 'investigateHosts.process.signature' },
    { field: 'path', label: 'investigateHosts.process.path' },
    { field: 'launchArguments', label: 'investigateHosts.process.launchArguments' },
    { field: 'creationTime', label: 'investigateHosts.process.creationTime' }
  ];
  const data = {
    parentPid: 1,
    owner: 'test',
    fileName: 'ntoskrnl.exe',
    pid: 'user1',
    path: 'C:\\Windows\\System32',
    signature: 'signed',
    launchArguments: 'xxx',
    creationTime: '12/12/2018'
  };

  test('this is to test the summary items present in the process tab', async function(assert) {
    this.setProperties({ config, data });
    await render(hbs`{{host-detail/process/summary-items data=data config=config}}`);
    return settled().then(() => {
      assert.equal(findAll('.header-item').length, 8, 'Eight summary items loaded');
    });
  });
  test('this is to test fileName should have link', async function(assert) {
    this.setProperties({ config, data });
    await render(hbs`{{host-detail/process/summary-items data=data config=config}}`);
    return settled().then(() => {
      assert.equal(findAll('.value a').length, 1, 'Link added to file name');
    });
  });

  test('this is to test the hasBlock', async function(assert) {
    this.setProperties({ config, data });
    await render(hbs`{{#host-detail/process/summary-items
      data=data
      config=config as |summary|}}
       {{#summary.property as |label value|}}
         {{label}}{{value}}
       {{/summary.property}}
      {{/host-detail/process/summary-items}}`);
    return settled().then(() => {
      assert.equal(findAll('.header-item').length, 8, 'Eight summary items loaded within hasBlock');
    });
  });
});
