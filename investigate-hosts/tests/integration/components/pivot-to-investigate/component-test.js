import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, settled, findAll, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import sinon from 'sinon';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | Pivot to investigate', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.timezone = this.owner.lookup('service:timezone');
    this.get('timezone').set('selected', { zoneId: 'UTC' });
  });

  test('it renders', async function(assert) {
    await render(hbs`{{pivot-to-investigate}}`);
    assert.equal(findAll('.pivot-to-investigate').length, 1, 'should rend the the component');
  });

  test('on clicking the icon renders the service modal', async function(assert) {
    this.set('serviceList', new Array(5));
    this.set('metaName', 'checksum');
    this.set('itemList', { checksum: 123 });
    await render(hbs`{{pivot-to-investigate serviceList=serviceList metaName=metaName item=itemList}}`);
    click('.rsa-icon');
    return settled().then(() => {
      assert.equal(document.querySelectorAll('#modalDestination .service-modal').length, 1, 'Expected to render service modal');
    });
  });

  test('it shows context menu on right clicking the yielded text', async function(assert) {
    this.set('showAsRightClick', true);
    await render(hbs`{{#pivot-to-investigate showAsRightClick=showAsRightClick}}
                      <span id="right-click-target">Right click here</span>
                    {{/pivot-to-investigate}}`);
    assert.equal(findAll('.content-context-menu').length, 1, 'Context menu trigger rendered');
  });

  test('should render the service list', async function(assert) {
    this.set('serviceList', [
      { 'id': 'e90bd2a2-a768-4cb9-a19d-37cd9f47fdcc', 'displayName': 'loki-concentrator', 'name': 'CONCENTRATOR' },
      { 'id': 'e90bd2a2-a768-4cb9-a19d-37cd9f47fdcc', 'displayName': 'loki-broker', 'name': 'BROKER' }
    ]);
    this.set('metaName', 'checksum');
    this.set('itemList', { checksum: 123 });
    await render(hbs`{{pivot-to-investigate serviceList=serviceList metaName=metaName item=itemList}}`);
    click('.rsa-icon');
    return settled().then(() => {
      assert.equal(document.querySelectorAll('#modalDestination .service-modal').length, 1, 'Expected to render service modal');
      assert.equal(document.querySelectorAll('#modalDestination .rsa-data-table').length, 1, 'Expected to render rsa data table');
      assert.equal(document.querySelectorAll('#modalDestination .rsa-data-table-body-row').length, 2, 'Expected to render 2 services');
    });
  });

  test('should enable the navigate button on selecting the service', async function(assert) {
    this.set('serviceList', [
      { 'id': 'e90bd2a2-a768-4cb9-a19d-37cd9f47fdcc', 'displayName': 'loki-concentrator', 'name': 'CONCENTRATOR' },
      { 'id': 'e90bd2a2-a768-4cb9-a19d-37cd9f47fdcc', 'displayName': 'loki-broker', 'name': 'BROKER' }
    ]);
    this.set('metaName', 'checksum');
    this.set('itemList', { 'machine.machineName': 123 });
    await render(hbs`{{pivot-to-investigate metaName='machine.machineName' item=itemList serviceList=serviceList}}`);
    click('.rsa-icon');
    return settled().then(() => {
      assert.equal(document.querySelectorAll('#modalDestination .service-modal').length, 1, 'Expected to render service modal');
      assert.equal(document.querySelectorAll('#modalDestination .rsa-data-table').length, 1, 'Expected to render rsa data table');
      assert.equal(document.querySelectorAll('#modalDestination .rsa-data-table-body-row').length, 2, 'Expected to render 2 services');
      assert.equal(document.querySelectorAll('#modalDestination .is-disabled').length, 2, 'Expected to disable the navigate button');
      click(findAll('.rsa-data-table .rsa-data-table-body-row')[0]);
      return settled().then(() => {
        assert.equal(document.querySelectorAll('#modalDestination .is-disabled').length, 0, 'Expected to enable the navigate button');
      });
    });
  });

  test('should open the investigate page in new window', async function(assert) {
    const actionSpy = sinon.spy(window, 'open');
    this.set('serviceList', [
      { 'id': 'e90bd2a2-a768-4cb9-a19d-37cd9f47fdcc', 'displayName': 'loki-concentrator', 'name': 'CONCENTRATOR' },
      { 'id': 'e90bd2a2-a768-4cb9-a19d-37cd9f47fdcc', 'displayName': 'loki-broker', 'name': 'BROKER' }
    ]);
    this.set('item', { machine: { machineName: 'test' } });
    await render(hbs`{{pivot-to-investigate metaName='machine.machineName' item=item serviceList=serviceList}}`);
    click('.rsa-icon');
    return settled().then(() => {
      assert.equal(document.querySelectorAll('#modalDestination .service-modal').length, 1, 'Expected to render service modal');
      assert.equal(document.querySelectorAll('#modalDestination .rsa-data-table').length, 1, 'Expected to render rsa data table');
      click(findAll('.rsa-data-table .rsa-data-table-body-row')[0]);
      return settled().then(() => {
        assert.equal(document.querySelectorAll('#modalDestination .is-disabled').length, 0, 'Expected to enable the navigate button');
        click(findAll('.is-primary')[0]);
        return settled().then(() => {
          assert.ok(actionSpy.calledOnce);
          actionSpy.resetHistory();
          actionSpy.restore();
        });
      });
    });
  });

  test('should open the investigate page in new window', async function(assert) {
    assert.expect(3);
    const actionSpy = sinon.spy(window, 'open');
    this.set('serviceId', '123456');
    this.set('timeRange', { value: 7, unit: 'days' });
    this.set('item', { machine: { machineName: 'test' } });
    await render(hbs`{{pivot-to-investigate metaName='machine.machineName' serviceId=serviceId item=item timeRange=timeRange}}`);
    click('.rsa-icon');
    return settled().then(() => {
      assert.equal(document.querySelectorAll('#modalDestination .service-modal').length, 0, 'Service modal not rendered');
      assert.ok(actionSpy.calledOnce);
      assert.ok(actionSpy.args[0][0].includes('123456'));
      actionSpy.resetHistory();
      actionSpy.restore();
    });
  });
});
