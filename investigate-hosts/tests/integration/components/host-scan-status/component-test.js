import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolver from 'ember-engines/test-support/engine-resolver-for';

module('Integration | Component | host scan status', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });

  test('Should be styled appropriately if scanStatus is set idle', async function(assert) {

    const hostDetails = {
      agentStatus: {
        scanStatus: 'idle',
        hasScanStatus: true,
        canStartScan: true
      },
      machineIdentity: {
        agentVersion: '11.1.0.1'
      }
    };

    this.set('hostDetails', hostDetails);
    await render(hbs`{{host-scan-status agent=hostDetails}}`);
    assert.equal(findAll('.rsa-agent-scan-status')[0].innerText.trim(), 'Idle', 'When scan status is idle');
  });

  test('Should be styled appropriately if scanStatus is set scanning', async function(assert) {

    const hostDetails = {
      agentStatus: {
        scanStatus: 'scanning',
        hasScanStatus: true,
        canStartScan: true
      },
      machineIdentity: {
        agentVersion: '11.1.0.1'
      }
    };

    this.set('hostDetails', hostDetails);
    await render(hbs`{{host-scan-status agent=hostDetails}}`);
    assert.equal(findAll('.rsa-agent-scan-status')[0].innerText.trim(), 'Scanning', 'When scan status is scanning');
  });

  test('Should be styled appropriately if scanStatus is set cancelPending', async function(assert) {

    const hostDetails = {
      agentStatus: {
        scanStatus: 'cancelPending',
        hasScanStatus: true,
        canStartScan: true
      },
      machineIdentity: {
        agentVersion: '11.1.0.1'
      }
    };

    this.set('hostDetails', hostDetails);
    await render(hbs`{{host-scan-status agent=hostDetails}}`);
    assert.equal(findAll('.rsa-agent-scan-status')[0].innerText.trim(), 'Cancelling', 'When scan status is cancelPending');
  });

  test('Should be styled appropriately if scanStatus is set scanPending', async function(assert) {

    const hostDetails = {
      agentStatus: {
        scanStatus: 'scanPending',
        hasScanStatus: true,
        canStartScan: true
      },
      machineIdentity: {
        agentVersion: '11.1.0.1'
      }
    };

    this.set('hostDetails', hostDetails);
    await render(hbs`{{host-scan-status agent=hostDetails}}`);
    assert.equal(findAll('.rsa-agent-scan-status')[0].innerText.trim(), 'Pending', 'When scan status is scanPending');
  });

  test('Should be styled appropriately if agentVersion is 4.4', async function(assert) {

    const hostDetails = {
      agentStatus: {
        scanStatus: 'idle',
        hasScanStatus: true,
        canStartScan: true
      },
      machineIdentity: {
        agentVersion: '4.4.0.1'
      }
    };

    this.set('hostDetails', hostDetails);
    await render(hbs`{{host-scan-status agent=hostDetails}}`);
    assert.equal(findAll('.rsa-agent-scan-status')[0].innerText.trim(), 'N/A', 'When scan status is idle but agentVersion is 4.4');
  });

  test('Should be styled appropriately if scanStatus is undefined', async function(assert) {

    const hostDetails = {
      agentStatus: {},
      machineIdentity: {
        agentVersion: '4.4.0.1'
      }
    };

    this.set('hostDetails', hostDetails);
    await render(hbs`{{host-scan-status agent=hostDetails}}`);
    assert.equal(findAll('.rsa-agent-scan-status')[0].innerText.trim(), 'N/A', 'When scan status is undefined');
  });
});