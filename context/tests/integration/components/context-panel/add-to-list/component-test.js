import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { find, findAll, render, waitUntil } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import Evented from '@ember/object/evented';
import Service from '@ember/service';
const eventBusStub = Service.extend(Evented, {});

let eventBus;

const timeout = 15000;

module('Integration | Component | context-panel/add-to-list', function(hooks) {

  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    this.owner.register('service:event-bus', eventBusStub);
    eventBus = this.owner.lookup('service:event-bus');
  });

  test('it renders', async function(assert) {

    const entity = {
      type: 'IP',
      id: '10.10.10.10'
    };

    await render(hbs`
      <div id='modalDestination'></div>
      {{context-panel/add-to-list}}
    `);

    await eventBus.trigger('rsa-application-modal-open-addToList', entity);

    await waitUntil(() => {
      return find('.rsa-context-tree-table__listDetails').textContent.trim().length > 0;
    }, { timeout });

    assert.ok(findAll('.modal-content').length);
  });
});
