import { module, test } from 'qunit';
import EmberObject from '@ember/object';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { find, findAll, click, render } from '@ember/test-helpers';

module('Integration | Component | host list/host table/body cell', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    this.owner.inject('component', 'dateFormat', 'service:dateFormat');
    this.owner.inject('component', 'timeFormat', 'service:timeFormat');
    this.owner.inject('component', 'timezone', 'service:timezone');
  });

  test('it renders the checkbox column', async function(assert) {
    this.set('column', { componentClass: true });
    this.set('selections', [ { id: 1 }]);
    this.set('item', { selected: true, id: 1, checked: true });
    this.set('checkBoxAction', (id) => {
      assert.equal(id, 1);
    });
    await render(hbs`{{host-list/host-table/body-cell selections=selections column=column item=item checkBoxAction=(action checkBoxAction 1)}}`);

    assert.equal(findAll('.rsa-form-checkbox').length, 1);
    assert.equal(findAll('.rsa-form-checkbox:checked').length, 1, 'Expecting to select the checkbox');
    await click('.rsa-form-checkbox');
    assert.equal(findAll('.rsa-form-checkbox:checked').length, 0, 'Expecting to un-select the checkbox');
  });

  test('it should render non-zero risk score for host', async function(assert) {
    this.set('column', { field: 'score' });
    this.set('item', { score: 80, id: 1 });

    await render(hbs`{{host-list/host-table/body-cell column=column item=item}}`);
    assert.equal(findAll('.rsa-risk-score').length, 1, 'Expected to render risk score component');
    assert.equal(find('.rsa-risk-score').textContent.trim(), 80, 'Expected to render risk score of 80 for host');
  });

  test('it should render zero risk score for host', async function(assert) {
    this.set('column', { field: 'score' });

    await render(hbs`{{host-list/host-table/body-cell column=column}}`);
    assert.equal(findAll('.rsa-risk-score').length, 1, 'Expected to render risk score component');
    assert.equal(find('.rsa-risk-score').textContent.trim(), 0, 'Expected to render risk score of 0 for host');
  });

  test('it should render the anchor tag for machine name', async function(assert) {
    this.set('column', { field: 'machineIdentity.machineName' });
    this.set('item', { machine: { machineName: 'Test' }, id: 1 });
    await render(hbs`{{host-list/host-table/body-cell column=column item=item}}`);
    assert.equal(findAll('.host-name > a').length, 1, 'Expected to render machine name with anchor tag');
  });

  test('it should render agent status component', async function(assert) {
    this.set('column', { field: 'agentStatus.scanStatus' });
    this.set('item', { agentStatus: { scanStatus: 'Idle' }, id: 1, machine: { agentVersion: '11.1.0.0' } });
    await render(hbs`{{host-list/host-table/body-cell column=column item=item}}`);
    assert.equal(findAll('.rsa-agent-scan-status').length, 1, 'Expected to render agent scan status component');
  });

  test('it should render disable text css when host is migrated', async function(assert) {
    this.set('column', { field: 'agentStatus.scanStatus' });
    this.set('item', { agentStatus: { scanStatus: 'Idle' }, id: 1, machine: { agentVersion: '11.1.0.0' }, groupPolicy: { managed: false } });
    await render(hbs`{{host-list/host-table/body-cell column=column item=item}}`);
    assert.equal(findAll('.host-disable-text').length, 1, 'Expected to render host-disable-text');
  });

  test('it should render disable text css when host is managed', async function(assert) {
    this.set('column', { field: 'agentStatus.scanStatus' });
    this.set('item', { agentStatus: { scanStatus: 'Idle' }, id: 1, machine: { agentVersion: '11.1.0.0' }, groupPolicy: { managed: true } });
    await render(hbs`{{host-list/host-table/body-cell column=column item=item}}`);
    assert.equal(findAll('.host-disable-text').length, 0);
  });

  test('should render RAR icon', async function(assert) {
    const column = EmberObject.create({ field: 'agentStatus.lastSeenTime' });

    this.set('column', column);
    this.set('item', { agentStatus: {
      agentId: '93B4DD24',
      lastSeenTime: '2019-04-04T08:51:14.734+0000',
      scanStatus: 'Idle',
      lastSeen: 'RelayServer'
    },
    isAgentRoaming: true,
    id: '93B4DD24',
    machine: { agentVersion: '11.1.0.0' },
    groupPolicy: { managed: true }
    });
    await render(hbs`{{host-list/host-table/body-cell column=column item=item}}`);
    assert.equal(findAll('.rar-icon').length, 1, 'RAR icon present');
  });

  test('should not render RAR icon', async function(assert) {
    const column = EmberObject.create({ field: 'agentStatus.lastSeenTime' });

    this.set('column', column);
    this.set('item', { agentStatus: {
      agentId: '93B4DD24',
      lastSeenTime: '2019-04-04T08:51:14.734+0000',
      scanStatus: 'Idle',
      lastSeen: 'EndpointServer'
    },
    isAgentRoaming: false,
    id: '93B4DD24',
    machine: { agentVersion: '11.1.0.0' },
    groupPolicy: { managed: true }
    });
    await render(hbs`{{host-list/host-table/body-cell column=column item=item}}`);
    assert.equal(findAll('.rar-icon').length, 0, 'RAR icon not present');
  });
});
