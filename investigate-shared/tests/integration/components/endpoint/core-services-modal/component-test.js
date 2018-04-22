import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, click, find, settled } from '@ember/test-helpers';
import Evented from '@ember/object/evented';
import hbs from 'htmlbars-inline-precompile';
import Service from '@ember/service';
const eventBusStub = Service.extend(Evented, {});
let eventBus;

module('Integration | Component | endpoint/core-services-modal', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    this.owner.register('service:event-bus', eventBusStub);
    eventBus = this.owner.lookup('service:event-bus');
  });

  test('it renders the services modal', async function(assert) {
    await render(hbs`{{endpoint/core-services-modal}}`);
    await eventBus.trigger('rsa-application-modal-open-service-modal');
    return settled().then(() => {
      assert.equal(document.querySelectorAll('#modalDestination .service-modal').length, 1, 'Expected to render service modal');
    });
  });

  test('it renders the footer buttons', async function(assert) {
    assert.expect(1);
    this.set('pivot', function(params) {
      assert.equal(params.name, 'test-1');
    });
    this.set('serviceList', [
      {
        displayName: 'test',
        name: 'test-1'
      },
      {
        displayName: 'test-2',
        name: 'test-2'
      }
    ]);
    await render(hbs`{{#endpoint/core-services-modal serviceList=serviceList as |selectedService|}}
      {{#rsa-form-button class='test' style='primary' click=(action pivot selectedService)}}
        {{t 'investigateHosts.pivotToInvestigate.buttonText2'}}
      {{/rsa-form-button}}
    {{/endpoint/core-services-modal}}`);
    await eventBus.trigger('rsa-application-modal-open-service-modal');
    await click(find('.rsa-data-table .rsa-data-table-body-row'));
    await click(find('.test'));

  });

  test('it renders the loading indicator when service list is null', async function(assert) {
    await render(hbs`{{endpoint/core-services-modal}}`);
    await eventBus.trigger('rsa-application-modal-open-service-modal');
    return settled().then(() => {
      assert.equal(document.querySelectorAll('#modalDestination .rsa-loader').length, 1);
    });
  });

  test('it renders the service list with two columns', async function(assert) {
    this.set('serviceList', [
      {
        displayName: 'test',
        name: 'test-1'
      },
      {
        displayName: 'test-2',
        name: 'test-2'
      }
    ]);
    await render(hbs`{{endpoint/core-services-modal showServiceModal=showServiceModal serviceList=serviceList}}`);
    await eventBus.trigger('rsa-application-modal-open-service-modal');
    return settled().then(() => {
      assert.equal(document.querySelectorAll('#modalDestination .rsa-data-table-body-row').length, 2, 'two rows');
      assert.equal(document.querySelectorAll('#modalDestination .rsa-data-table-header-cell').length, 2, 'two columns');
    });

  });
});
