import Service from '@ember/service';
import Evented from '@ember/object/evented';
import {
  sendTetherEvent,
  wireTriggerToHover,
  unwireTriggerToHover,
  wireTriggerToClick,
  unwireTriggerToClick
} from 'component-lib/utils/tooltip-trigger';
import { module, test } from 'qunit';
import { triggerEvent } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';

module('Unit | Utility | tooltip trigger', function(hooks) {

  setupRenderingTest(hooks);

  const eventBus = Service.extend(Evented, {}).create();
  const panelId = 'panel1';
  const model = 'model1';
  const elId = 'id1';
  const el = document.createElement('div');
  el.id = elId;

  test('sendTetherEvent triggers event handlers with the correctly payload', function(assert) {
    assert.expect(2);
    const eventType = 'eventType1';
    const eventName = `rsa-content-tethered-panel-${eventType}-${panelId}`;

    eventBus.on(eventName, function(height, width, id, model) {
      assert.equal(id, elId);
      assert.equal(model, model);
    });

    sendTetherEvent(el, panelId, eventBus, eventType, model);
    eventBus.off(eventName);
  });

  test('wireTriggerToClick attaches event handlers, unwireTriggerToClick detaches them', async function(assert) {
    assert.expect(1);
    const eventType = 'toggle';
    const eventName = `rsa-content-tethered-panel-${eventType}-${panelId}`;

    eventBus.on(eventName, function() {
      assert.ok('eventBus handler was triggered');
    });

    wireTriggerToClick(el, panelId, eventBus);
    await triggerEvent(el, 'click');

    unwireTriggerToClick(el, panelId, eventBus);
    await triggerEvent(el, 'click'); // should not fire assert

    eventBus.off(eventName);
  });

  test('wireTriggerToClick supports an option for triggering only for right clicks', async function(assert) {
    assert.expect(4);
    const eventType = 'toggle';
    const eventName = `rsa-content-tethered-panel-${eventType}-${panelId}`;
    let spyCounter = 0;

    eventBus.on(eventName, function() {
      spyCounter++;
      assert.ok('eventBus handler was triggered');
    });

    wireTriggerToClick(el, panelId, eventBus, { rightClick: true });
    // shouldn't fire an event to eventBus
    await triggerEvent(el, 'click');
    assert.equal(spyCounter, 0, 'Expected no eventBus triggers from click event');
    // should fire an event to eventBus
    await triggerEvent(el, 'contextmenu');
    assert.equal(spyCounter, 1, 'Expected 1 eventBus trigger from contextmenu event');

    unwireTriggerToClick(el);
    // shouldn't fire any more asserts
    await triggerEvent(el, 'contextmenu');
    // shouldn't fire any more asserts
    await triggerEvent(el, 'click');
    assert.equal(spyCounter, 1, 'Expected no eventBus trigger from click event');

    eventBus.off(eventName);
  });

  test('wireTriggerToHover attaches event handlers, unwireTriggerToHover detaches them', async function(assert) {
    assert.expect(2);
    const mouseenterEventName = `rsa-content-tethered-panel-display-${panelId}`;
    const mouseleaveEventName = `rsa-content-tethered-panel-hide-${panelId}`;

    eventBus
      .on(mouseenterEventName, function() {
        assert.ok('eventBus display handler was triggered');
      })
      .on(mouseleaveEventName, function() {
        assert.ok('eventBus hide handler was triggered');
      });

    wireTriggerToHover(el, panelId, eventBus);
    await triggerEvent(el, 'mouseenter');
    await triggerEvent(el, 'mouseleave');

    unwireTriggerToHover(el, panelId, eventBus);
    // shouldn't fire any more asserts
    await triggerEvent(el, 'mouseenter');
    await triggerEvent(el, 'mouseenter');
    // shouldn't fire any more asserts
    await triggerEvent(el, 'mouseleave');
    await triggerEvent(el, 'mouseleave');

    eventBus
      .off(mouseenterEventName)
      .off(mouseleaveEventName);
  });

  test('wireTriggerToHover supports delays', async function(assert) {
    assert.expect(2);
    const mouseenterEventName = `rsa-content-tethered-panel-display-${panelId}`;
    const mouseleaveEventName = `rsa-content-tethered-panel-hide-${panelId}`;
    const displayDelay = 10;
    const hideDelay = 10;

    eventBus
      .on(mouseenterEventName, function() {
        assert.ok('eventBus display handler was triggered');
      })
      .on(mouseleaveEventName, function() {
        assert.ok('eventBus hide handler was triggered');
      });

    wireTriggerToHover(el, panelId, eventBus, { displayDelay, hideDelay });
    await triggerEvent(el, 'mouseenter');
    await triggerEvent(el, 'mouseleave');

    unwireTriggerToHover(el, panelId, eventBus, { displayDelay, hideDelay });
    // shouldn't fire any more asserts
    await triggerEvent(el, 'mouseenter');
    // shouldn't fire any more asserts
    await triggerEvent(el, 'mouseleave');

    eventBus
      .off(mouseenterEventName)
      .off(mouseleaveEventName);
  });

  test('wireTriggerToHover aborts a pending hide event if interrupted by a display event on the same trigger element', async function(assert) {
    assert.expect(2);
    const mouseenterEventName = `rsa-content-tethered-panel-display-${panelId}`;
    const mouseleaveEventName = `rsa-content-tethered-panel-hide-${panelId}`;
    const displayDelay = 0;
    const hideDelay = 1000;
    const mouseleaveEvent = new MouseEvent('mouseleave');
    const mouseenterEvent = new MouseEvent('mouseenter');

    eventBus
      .on(mouseenterEventName, function() {
        assert.ok(true, 'eventBus display handler was triggered as expected');
      })
      .on(mouseleaveEventName, function() {
        assert.notOk(true, 'eventBus hide handler was triggered but should not have been');
      });

    wireTriggerToHover(el, panelId, eventBus, { displayDelay, hideDelay });

    // should trigger a `display` event in eventBus immediately
    await triggerEvent(el, 'mouseenter');
    // should wait for `hideDelay` before triggering eventBus
    el.dispatchEvent(mouseleaveEvent);
    // should abort the eventBus trigger from mouseleave above and instead trigger another `display` event
    el.dispatchEvent(mouseenterEvent);

    unwireTriggerToHover(el, panelId, eventBus, { displayDelay, hideDelay });

    eventBus
      .off(mouseenterEventName)
      .off(mouseleaveEventName);
  });
});
