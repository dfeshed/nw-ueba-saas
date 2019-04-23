import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, settled, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import sinon from 'sinon';

module('Integration | Component | endpoint/pivot-to-investigate/modal', function(hooks) {
  setupRenderingTest(hooks);

  test('The service modal is rendered', async function(assert) {
    this.set('serviceList', [
      { 'id': 'e90bd2a2-a768-4cb9-a19d-37cd9f47fdcc', 'displayName': 'loki-concentrator', 'name': 'CONCENTRATOR' },
      { 'id': 'e90bd2a2-a768-4cb9-a19d-37cd9f47fdcc', 'displayName': 'loki-broker', 'name': 'BROKER' }
    ]);
    this.set('item', [{ 'id': '12345', 'firstFilename': 'test', 'checksumSha256': '8789689685' }]);
    this.set('onClose', function() {});
    await render(hbs`
      <div id='modalDestination'></div>
      {{endpoint/pivot-to-investigate/modal
        serviceList=serviceList
        item=item
        metaName='checksumSha256'
        onClose=(action onClose)
      }}
    `);
    return settled().then(async() => {
      assert.equal(findAll('#modalDestination .service-modal').length, 1, 'Expected to render service modal');
    });
  });

  test('should render the service list', async function(assert) {
    this.set('serviceList', [
      { 'id': 'e90bd2a2-a768-4cb9-a19d-37cd9f47fdcc', 'displayName': 'loki-concentrator', 'name': 'CONCENTRATOR' },
      { 'id': 'e90bd2a2-a768-4cb9-a19d-37cd9f47fdcc', 'displayName': 'loki-broker', 'name': 'BROKER' }
    ]);
    this.set('item', [{ 'id': '12345', 'firstFilename': 'test', 'checksumSha256': '8789689685' }]);
    this.set('onClose', function() {});
    await render(hbs`
      <div id='modalDestination'></div>
      {{endpoint/pivot-to-investigate/modal
        serviceList=serviceList
        item=item
        metaName='checksumSha256'
      }}
    `);
    return settled().then(async() => {
      assert.equal(findAll('#modalDestination .service-modal .rsa-data-table').length, 1, 'Expected to render rsa data table');
      assert.equal(findAll('#modalDestination .service-modal .rsa-data-table-body-row').length, 2, 'Expected to render 2 services');
    });
  });

  test('should enable the navigate button on selecting the service', async function(assert) {
    const timezone = {
      selected: 'America/Los_Angeles'
    };
    const actionSpy = sinon.spy(window, 'open');
    this.set('tz', timezone);
    this.set('serviceList', [
      { 'id': 'e90bd2a2-a768-4cb9-a19d-37cd9f47fdcc', 'displayName': 'loki-concentrator', 'name': 'CONCENTRATOR' },
      { 'id': 'e90bd2a2-a768-4cb9-a19d-37cd9f47fdcc', 'displayName': 'loki-broker', 'name': 'BROKER' }
    ]);
    this.set('itemList', [{ checksum: 8789689685 }]);
    this.set('item', [{ 'id': '12345', 'firstFilename': 'test', 'checksumSha256': '8789689685' }]);
    this.set('onClose', function() {});
    await render(hbs`
      <div id='modalDestination'></div>
      {{endpoint/pivot-to-investigate/modal
        serviceList=serviceList
        item=item
        itemList=itemList
        metaName='checksumSha256'
        onClose=(action onClose) timezone=tz
      }}
    `);
    return settled().then(async() => {
      assert.equal(findAll('#modalDestination .service-modal').length, 1, 'Expected to render service modal');
      assert.equal(findAll('#modalDestination .service-modal .rsa-data-table').length, 1, 'Expected to render rsa data table');
      assert.equal(findAll('#modalDestination .service-modal .rsa-data-table-body-row').length, 2, 'Expected to render 2 services');
      assert.equal(findAll('#modalDestination .service-modal .is-disabled').length, 2, 'Expected to disable the navigate button');
      await click(findAll('.rsa-data-table .rsa-data-table-body-row .rsa-data-table-body-cell')[0]);
      assert.equal(findAll('.service-modal .is-disabled').length, 0, 'Expected to enable the navigate button');
      await click(findAll('.modal-footer-buttons .rsa-form-button')[1]);
      assert.ok(actionSpy.calledOnce, 'Window.open is called');
      actionSpy.restore();
    });
  });

  test('should enable the event analysis button on selecting the service', async function(assert) {
    const timezone = {
      selected: 'America/Los_Angeles'
    };
    const actionSpy = sinon.spy(window, 'open');
    this.set('tz', timezone);
    this.set('serviceList', [
      { 'id': 'e90bd2a2-a768-4cb9-a19d-37cd9f47fdcc', 'displayName': 'loki-concentrator', 'name': 'CONCENTRATOR' },
      { 'id': 'e90bd2a2-a768-4cb9-a19d-37cd9f47fdcc', 'displayName': 'loki-broker', 'name': 'BROKER' }
    ]);
    this.set('itemList', [{ checksum: 8789689685 }]);
    this.set('item', [{ 'id': '12345', 'firstFilename': 'test', 'checksumSha256': '8789689685' }]);
    this.set('onClose', function() {});
    await render(hbs`
      <div id='modalDestination'></div>
      {{endpoint/pivot-to-investigate/modal
        serviceList=serviceList
        item=item
        itemList=itemList
        metaName='checksumSha256'
        onClose=(action onClose)
        timezone=tz
      }}
    `);
    return settled().then(async() => {
      assert.equal(findAll('#modalDestination .service-modal').length, 1, 'Expected to render service modal');
      assert.equal(findAll('#modalDestination .service-modal .rsa-data-table').length, 1, 'Expected to render rsa data table');
      assert.equal(findAll('#modalDestination .service-modal .rsa-data-table-body-row').length, 2, 'Expected to render 2 services');
      assert.equal(findAll('#modalDestination .service-modal .is-disabled').length, 2, 'Expected to disable the navigate button');
      await click(findAll('.rsa-data-table .rsa-data-table-body-row .rsa-data-table-body-cell')[0]);
      assert.equal(findAll('.service-modal .is-disabled').length, 0, 'Expected to enable the Event analysis button');
      await click(findAll('.modal-footer-buttons .rsa-form-button')[2]);
      assert.ok(actionSpy.calledOnce, 'Window.open is called');
      actionSpy.restore();
    });
  });

  test('modal is closed on clicking cancel button', async function(assert) {
    this.set('serviceList', [
      { 'id': 'e90bd2a2-a768-4cb9-a19d-37cd9f47fdcc', 'displayName': 'loki-concentrator', 'name': 'CONCENTRATOR' },
      { 'id': 'e90bd2a2-a768-4cb9-a19d-37cd9f47fdcc', 'displayName': 'loki-broker', 'name': 'BROKER' }
    ]);
    this.set('item', [{ 'id': '12345', 'firstFilename': 'test', 'checksumSha256': '8789689685' }]);
    this.set('closeModal', function() {
      assert.ok(true);
    });
    await render(hbs`
      <div id='modalDestination'></div>
      {{endpoint/pivot-to-investigate/modal
        serviceList=serviceList
        item=item
        metaName='checksumSha256'
        closeModal=(action closeModal)
      }}
    `);
    await click(findAll('.modal-footer-buttons .rsa-form-button')[0]);
  });

});
