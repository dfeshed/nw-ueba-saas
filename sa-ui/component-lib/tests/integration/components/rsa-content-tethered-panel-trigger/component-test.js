import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { findAll, render, triggerEvent, waitUntil } from '@ember/test-helpers';
import Service from '@ember/service';
import hbs from 'htmlbars-inline-precompile';

let called = false;
let calledArgs;
const eventBusStub = Service.extend({
  trigger() {
    called = true;
    calledArgs = arguments;
  }
});

module('Integration | Component | rsa-content-tethered-panel-trigger', function(hooks) {
  setupRenderingTest(hooks, {});

  hooks.beforeEach(function() {
    this.owner.register('service:event-bus', eventBusStub);
    called = false;
    calledArgs = undefined;
  });

  test('it includes the proper classes', async function(assert) {
    await render(hbs`
      {{#rsa-content-tethered-panel-trigger
        panel="foo-class"
      }}
        Trigger
      {{/rsa-content-tethered-panel-trigger}}
    `);
    assert.equal(findAll('.foo-class').length, 1, 'the panel param turns into class');
  });

  test('it emits the rsa-content-tethered-panel-display event on mouseenter', async function(assert) {
    await render(hbs`
      {{#rsa-content-tethered-panel-trigger
        panel="foo"
      }}
        Trigger
      {{/rsa-content-tethered-panel-trigger}}
    `);

    await triggerEvent('.rsa-content-tethered-panel-trigger', 'mouseover');
    await waitUntil(() => called === true);
    assert.ok(calledArgs[0] === 'rsa-content-tethered-panel-display-foo', 'correct message is sent to event trigger');
  });

  test('it emits the rsa-content-tethered-panel-hide event on mouseleave', async function(assert) {
    await render(hbs`
      {{#rsa-content-tethered-panel-trigger
        panel="foo"
      }}
        Trigger
      {{/rsa-content-tethered-panel-trigger}}
    `);

    await triggerEvent('.rsa-content-tethered-panel-trigger', 'mouseout');
    await waitUntil(() => called === true);
    assert.ok(calledArgs[0] === 'rsa-content-tethered-panel-hide-foo', 'correct message is sent to event trigger');
  });
});
