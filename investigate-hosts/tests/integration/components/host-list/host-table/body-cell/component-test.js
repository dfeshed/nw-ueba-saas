import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

moduleForComponent('host-list/host-table/body-cell', 'Integration | Component | host list/host table/body cell', {
  integration: true,
  resolver: engineResolverFor('investigate-hosts'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
  }
});

test('it renders the checkbox column', function(assert) {
  this.set('column', { componentClass: true });
  this.set('item', { selected: true, id: 1, checked: true });
  this.set('checkBoxAction', (id) => {
    assert.equal(id, 1);
  });
  this.render(hbs`{{host-list/host-table/body-cell column=column item=item checkBoxAction=(action checkBoxAction 1)}}`);

  assert.equal(this.$('.rsa-form-checkbox').length, 1);
  assert.equal(this.$('.rsa-form-checkbox:checked').length, 1, 'Expecting to select the checkbox');
  this.$('.rsa-form-checkbox').click();
  assert.equal(this.$('.rsa-form-checkbox:checked').length, 0, 'Expecting to un-select the checkbox');
});

test('it should render non-zero risk score for host', function(assert) {
  this.set('column', { field: 'score' });
  this.set('item', { score: 80, id: 1 });

  this.render(hbs`{{host-list/host-table/body-cell column=column item=item}}`);
  assert.equal(this.$('.rsa-risk-score').length, 1, 'Expected to render risk score component');
  assert.equal(this.$('.rsa-risk-score')[0].textContent.trim(), 80, 'Expected to render risk score of 80 for host');
});

test('it should render zero risk score for host', function(assert) {
  this.set('column', { field: 'score' });

  this.render(hbs`{{host-list/host-table/body-cell column=column}}`);
  assert.equal(this.$('.rsa-risk-score').length, 1, 'Expected to render risk score component');
  assert.equal(this.$('.rsa-risk-score')[0].textContent.trim(), 0, 'Expected to render risk score of 0 for host');
});

test('it should render the anchor tag for machine name', function(assert) {
  this.set('column', { field: 'machine.machineName' });
  this.set('item', { machine: { machineName: 'Test' }, id: 1 });
  this.render(hbs`{{host-list/host-table/body-cell column=column item=item}}`);
  assert.equal(this.$('.host-name > a').length, 1, 'Expected to render machine name with anchor tag');
});

test('it should render agent status component', function(assert) {
  this.set('column', { field: 'agentStatus.scanStatus' });
  this.set('item', { agentStatus: { scanStatus: 'Idle' }, id: 1, machine: { agentVersion: '11.1.0.0' } });
  this.render(hbs`{{host-list/host-table/body-cell column=column item=item}}`);
  assert.equal(this.$('.rsa-agent-scan-status').length, 1, 'Expected to render agent scan status component');
});

test('it should render disable text css when host is migrated', function(assert) {
  this.set('column', { field: 'agentStatus.scanStatus' });
  this.set('item', { agentStatus: { scanStatus: 'Idle' }, id: 1, machine: { agentVersion: '11.1.0.0' }, groupPolicy: { managed: false } });
  this.render(hbs`{{host-list/host-table/body-cell column=column item=item}}`);
  assert.equal(this.$('.host-disable-text').length, 1, 'Expected to render host-disable-text');
});

test('it should render disable text css when host is managed', function(assert) {
  this.set('column', { field: 'agentStatus.scanStatus' });
  this.set('item', { agentStatus: { scanStatus: 'Idle' }, id: 1, machine: { agentVersion: '11.1.0.0' }, groupPolicy: { managed: true } });
  this.render(hbs`{{host-list/host-table/body-cell column=column item=item}}`);
  assert.equal(this.$('.host-disable-text').length, 0);
});
