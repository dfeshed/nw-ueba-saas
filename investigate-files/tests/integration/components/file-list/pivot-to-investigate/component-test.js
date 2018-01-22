import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import wait from 'ember-test-helpers/wait';
import $ from 'jquery';
import sinon from 'sinon';

moduleForComponent('file-list/pivot-to-investigate', 'Integration | Component | Pivot to investigate', {
  integration: true,
  resolver: engineResolverFor('investigate-files'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
  }
});

test('it renders', function(assert) {
  this.render(hbs`{{file-list/pivot-to-investigate}}`);
  assert.equal(this.$('.pivot-to-investigate').length, 1, 'should rend the the component');
});

test('on clicking the yielded text renders the service modal', function(assert) {
  this.render(hbs`{{file-list/pivot-to-investigate}}`);
  this.$('.rsa-icon').trigger('click');
  return wait().then(() => {
    assert.equal($('#modalDestination .service-modal').length, 1, 'Expected to render service modal');
  });
});

test('should render the service list', function(assert) {
  this.set('serviceList', [
    { 'id': 'e90bd2a2-a768-4cb9-a19d-37cd9f47fdcc', 'displayName': 'loki-concentrator', 'name': 'CONCENTRATOR' },
    { 'id': 'e90bd2a2-a768-4cb9-a19d-37cd9f47fdcc', 'displayName': 'loki-broker', 'name': 'BROKER' }
  ]);
  this.render(hbs`
    {{file-list/pivot-to-investigate serviceList=serviceList}}`);
  this.$('.rsa-icon').trigger('click');
  return wait().then(() => {
    assert.equal($('#modalDestination .service-modal').length, 1, 'Expected to render service modal');
    assert.equal($('#modalDestination .rsa-data-table').length, 1, 'Expected to render rsa data table');
    assert.equal($('#modalDestination .rsa-data-table-body-row').length, 2, 'Expected to render 2 services');
  });
});

test('should enable the navigate button on selecting the service', function(assert) {
  this.set('serviceList', [
    { 'id': 'e90bd2a2-a768-4cb9-a19d-37cd9f47fdcc', 'displayName': 'loki-concentrator', 'name': 'CONCENTRATOR' },
    { 'id': 'e90bd2a2-a768-4cb9-a19d-37cd9f47fdcc', 'displayName': 'loki-broker', 'name': 'BROKER' }
  ]);
  this.render(hbs`{{file-list/pivot-to-investigate metaName='machine.machineName' serviceList=serviceList}}`);
  this.$('.rsa-icon').trigger('click');
  return wait().then(() => {
    assert.equal($('#modalDestination .service-modal').length, 1, 'Expected to render service modal');
    assert.equal($('#modalDestination .rsa-data-table').length, 1, 'Expected to render rsa data table');
    assert.equal($('#modalDestination .rsa-data-table-body-row').length, 2, 'Expected to render 2 services');
    assert.equal($('#modalDestination .is-disabled').length, 2, 'Expected to disable the navigate button');
    $('.rsa-data-table .rsa-data-table-body-row').first().click();
    return wait().then(() => {
      assert.equal($('#modalDestination .is-disabled').length, 0, 'Expected to enable the navigate button');
    });
  });
});

test('should open the investigate page in new window', function(assert) {
  const actionSpy = sinon.spy(window, 'open');
  this.set('serviceList', [
    { 'id': 'e90bd2a2-a768-4cb9-a19d-37cd9f47fdcc', 'displayName': 'loki-concentrator', 'name': 'CONCENTRATOR' },
    { 'id': 'e90bd2a2-a768-4cb9-a19d-37cd9f47fdcc', 'displayName': 'loki-broker', 'name': 'BROKER' }
  ]);
  this.set('item', { machine: { machineName: 'test' } });
  this.render(hbs`{{file-list/pivot-to-investigate metaName='machine.machineName' item=item serviceList=serviceList}}`);
  this.$('.rsa-icon').trigger('click');
  return wait().then(() => {
    assert.equal($('#modalDestination .service-modal').length, 1, 'Expected to render service modal');
    assert.equal($('#modalDestination .rsa-data-table').length, 1, 'Expected to render rsa data table');
    $('.rsa-data-table .rsa-data-table-body-row').first().click();
    return wait().then(() => {
      assert.equal($('#modalDestination .is-disabled').length, 0, 'Expected to enable the navigate button');
      $('.is-primary:eq(0)').trigger('click');
      assert.ok(actionSpy.calledOnce);
      actionSpy.reset();
      actionSpy.restore();
    });
  });
});
