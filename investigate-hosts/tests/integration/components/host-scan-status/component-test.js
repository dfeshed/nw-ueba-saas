import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolver from '../../../helpers/engine-resolver';

moduleForComponent('host-scan-status', 'Integration | Component | host scan status', {
  integration: true,
  resolver: engineResolver('investigate-hosts')
});

test('Should be styled appropriately if scanStatus is set idle', function(assert) {

  const agentStatus = {
    scanStatus: 'idle',
    hasScanStatus: true,
    canStartScan: true
  };

  this.set('hostDetails', agentStatus);
  this.render(hbs`{{host-scan-status agent=hostDetails}}`);
  assert.equal(this.$('.rsa-agent-scan-status').find('i').hasClass('rsa-green-color'), true, 'When scan status is idle, rsa-green-color class is used');
});

test('Should be styled appropriately if scanStatus is set scanning', function(assert) {

  const agentStatus = {
    scanStatus: 'scanning',
    hasScanStatus: true,
    canStartScan: false
  };

  this.set('hostDetails', agentStatus);
  this.render(hbs`{{host-scan-status agent=hostDetails}}`);
  assert.equal(this.$('.rsa-agent-scan-status').find('i').hasClass('rsa-red-color'), true, 'When scan status is idle, rsa-red-color class is used');
});

test('Should be styled appropriately if scanStatus is set cancelPending', function(assert) {

  const agentStatus = {
    scanStatus: 'cancelPending',
    hasScanStatus: true,
    canStartScan: true
  };

  this.set('hostDetails', agentStatus);
  this.render(hbs`{{host-scan-status agent=hostDetails}}`);
  assert.equal(this.$('.rsa-agent-scan-status').find('i').hasClass('rsa-green-color'), true, 'When scan status is cancelPending, rsa-green-color class is used');
});

test('Should be styled appropriately if scanStatus is set scanPending', function(assert) {

  const agentStatus = {
    scanStatus: 'scanPending',
    hasScanStatus: true,
    canStartScan: false
  };

  this.set('hostDetails', agentStatus);
  this.render(hbs`{{host-scan-status agent=hostDetails}}`);
  assert.equal(this.$('.rsa-agent-scan-status').find('i').hasClass('rsa-red-color'), true, 'When scan status is scanPending, rsa-red-color class is used');
});