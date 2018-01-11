import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';
import engineResolverFor from '../../../../../helpers/engine-resolver';

moduleForComponent('host-detail/process/summary-items', 'Integration | Component | endpoint host-detail/process/summary-items', {
  integration: true,
  resolver: engineResolverFor('investigate-hosts'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
  }
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
  path: 'C:\Windows\System32',
  signature: 'signed',
  launchArguments: 'xxx',
  creationTime: '12/12/2018'
};

test('this is to test the summary items present in the process tab', function(assert) {
  this.setProperties({ config, data });
  this.render(hbs`{{host-detail/process/summary-items data=data config=config}}`);
  return wait().then(() => {
    assert.equal(this.$('.header-item').length, 8, 'Eight summary items loaded');
  });
});

test('this is to test the hasBlock', function(assert) {
  this.setProperties({ config, data });
  this.render(hbs`{{#host-detail/process/summary-items
    data=data 
    config=config as |summary|}} 
     {{#summary.property as |label value|}}
       {{label}}{{value}}
     {{/summary.property}}
    {{/host-detail/process/summary-items}}`);
  return wait().then(() => {
    assert.equal(this.$('.header-item').length, 8, 'Eight summary items loaded within hasBlock');
  });
});
