import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolver from '../../../helpers/engine-resolver';

moduleForComponent('host-scan-status', 'Integration | Component | host scan status', {
  integration: true,
  resolver: engineResolver('investigate-hosts')
});

test('Should be styled appropriately if scanStatus is set idle', function(assert) {

  const hostDetails = {
    agentStatus: {
      scanStatus: 'idle',
      hasScanStatus: true,
      canStartScan: true
    },
    machine: {
      agentVersion: '11.1.0.1'
    }
  };

  this.set('hostDetails', hostDetails);
  this.render(hbs`{{host-scan-status agent=hostDetails}}`);
  assert.equal(this.$('.rsa-agent-scan-status').get(0).innerText.trim(), 'Idle', 'When scan status is idle');
});

test('Should be styled appropriately if scanStatus is set scanning', function(assert) {

  const hostDetails = {
    agentStatus: {
      scanStatus: 'scanning',
      hasScanStatus: true,
      canStartScan: true
    },
    machine: {
      agentVersion: '11.1.0.1'
    }
  };

  this.set('hostDetails', hostDetails);
  this.render(hbs`{{host-scan-status agent=hostDetails}}`);
  assert.equal(this.$('.rsa-agent-scan-status').get(0).innerText.trim(), 'Scanning', 'When scan status is scanning');
});

test('Should be styled appropriately if scanStatus is set cancelPending', function(assert) {

  const hostDetails = {
    agentStatus: {
      scanStatus: 'cancelPending',
      hasScanStatus: true,
      canStartScan: true
    },
    machine: {
      agentVersion: '11.1.0.1'
    }
  };

  this.set('hostDetails', hostDetails);
  this.render(hbs`{{host-scan-status agent=hostDetails}}`);
  assert.equal(this.$('.rsa-agent-scan-status').get(0).innerText.trim(), 'Stopping scan', 'When scan status is cancelPending');
});

test('Should be styled appropriately if scanStatus is set scanPending', function(assert) {

  const hostDetails = {
    agentStatus: {
      scanStatus: 'scanPending',
      hasScanStatus: true,
      canStartScan: true
    },
    machine: {
      agentVersion: '11.1.0.1'
    }
  };

  this.set('hostDetails', hostDetails);
  this.render(hbs`{{host-scan-status agent=hostDetails}}`);
  assert.equal(this.$('.rsa-agent-scan-status').get(0).innerText.trim(), 'Starting scan', 'When scan status is scanPending');
});

test('Should be styled appropriately if agentVersion is 4.4', function(assert) {

  const hostDetails = {
    agentStatus: {
      scanStatus: 'idle',
      hasScanStatus: true,
      canStartScan: true
    },
    machine: {
      agentVersion: '4.4.0.1'
    }
  };

  this.set('hostDetails', hostDetails);
  this.render(hbs`{{host-scan-status agent=hostDetails}}`);
  assert.equal(this.$('.rsa-agent-scan-status').get(0).innerText.trim(), 'N/A', 'When scan status is idle but agentVersion is 4.4');
});

test('Should be styled appropriately if scanStatus is undefined', function(assert) {

  const hostDetails = {
    agentStatus: {},
    machine: {
      agentVersion: '4.4.0.1'
    }
  };

  this.set('hostDetails', hostDetails);
  this.render(hbs`{{host-scan-status agent=hostDetails}}`);
  assert.equal(this.$('.rsa-agent-scan-status').get(0).innerText.trim(), 'N/A', 'When scan status is undefined');
});