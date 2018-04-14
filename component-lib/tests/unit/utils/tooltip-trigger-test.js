import Service from '@ember/service';
import Evented from '@ember/object/evented';
import $ from 'jquery';
import {
  sendTetherEvent,
  wireTriggerToHover,
  unwireTriggerToHover,
  wireTriggerToClick,
  unwireTriggerToClick
} from 'component-lib/utils/tooltip-trigger';
import { module, test } from 'qunit';
import wait from 'ember-test-helpers/wait';

module('Unit | Utility | tooltip trigger');

const eventBus = Service.extend(Evented, {}).create();
const panelId = 'panel1';
const model = 'model1';
const elId = 'id1';
const el = document.createElement('div');
el.id = elId;
const $el = $(el);

test('sendTetherEvent triggers event handlers with the correctly payload', function(assert) {
  const eventType = 'eventType1';
  const eventName = `rsa-content-tethered-panel-${eventType}-${panelId}`;
  eventBus.on(eventName, function(height, width, id, model) {
    assert.equal(id, elId);
    assert.equal(model, model);
  });

  assert.expect(2);
  sendTetherEvent(el, panelId, eventBus, eventType, model);

  eventBus.off(eventName);
});

test('wireTriggerToClick attaches event handlers, unwireTriggerToClick detaches them', function(assert) {
  const eventType = 'toggle';
  const eventName = `rsa-content-tethered-panel-${eventType}-${panelId}`;
  eventBus.on(eventName, function() {
    assert.ok('eventBus handler was triggered');
  });

  assert.expect(1);

  wireTriggerToClick(el, panelId, eventBus);
  $el.trigger('click');

  unwireTriggerToClick(el);
  $el.trigger('click'); // shouldn't fire any more asserts

  eventBus.off(eventName);
});

test('wireTriggerToClick supports an option for triggering only for right clicks', function(assert) {
  const eventType = 'toggle';
  const eventName = `rsa-content-tethered-panel-${eventType}-${panelId}`;
  let spyCounter = 0;
  eventBus.on(eventName, function() {
    spyCounter++;
    assert.ok('eventBus handler was triggered');
  });

  assert.expect(3);

  wireTriggerToClick(el, panelId, eventBus, { rightClick: true });
  $el.trigger('click'); // shouldn't fire an event to eventBus
  assert.equal(spyCounter, 0, 'Expected no eventBus triggers from click event');
  $el.trigger('contextmenu'); // should fire an event to eventBus
  assert.equal(spyCounter, 1, 'Expected 1 eventBus trigger from contextmenu event');

  unwireTriggerToClick(el);
  $el.trigger('contextmenu'); // shouldn't fire any more asserts
  $el.trigger('click'); // shouldn't fire any more asserts

  eventBus.off(eventName);

});

test('wireTriggerToHover attaches event handlers, unwireTriggerToHover detaches them', function(assert) {
  const mouseenterEventName = `rsa-content-tethered-panel-display-${panelId}`;
  const mouseleaveEventName = `rsa-content-tethered-panel-hide-${panelId}`;

  eventBus
    .on(mouseenterEventName, function() {
      assert.ok('eventBus display handler was triggered');
    })
    .on(mouseleaveEventName, function() {
      assert.ok('eventBus hide handler was triggered');
    });

  assert.expect(2);

  wireTriggerToHover(el, panelId, eventBus);
  $el.trigger('mouseenter');
  $el.trigger('mouseleave');

  unwireTriggerToHover(el);
  $el.trigger('mouseenter'); // shouldn't fire any more asserts
  $el.trigger('mouseleave'); // shouldn't fire any more asserts

  eventBus
    .off(mouseenterEventName)
    .off(mouseleaveEventName);
});

test('wireTriggerToHover supports delays', function(assert) {
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

  assert.expect(2);

  wireTriggerToHover(el, panelId, eventBus, { displayDelay, hideDelay });
  $el.trigger('mouseenter');
  return wait().then(() => {
    $el.trigger('mouseleave');
    return wait();
  }).then(() => {
    unwireTriggerToHover(el);
    return wait();
  }).then(() => {
    $el.trigger('mouseenter'); // shouldn't fire any more asserts
    return wait();
  }).then(() => {
    $el.trigger('mouseleave'); // shouldn't fire any more asserts
    return wait();
  }).then(() => {
    eventBus
      .off(mouseenterEventName)
      .off(mouseleaveEventName);
  });
});

test('wireTriggerToHover aborts a pending hide event if interrupted by a display event on the same trigger element', function(assert) {
  const mouseenterEventName = `rsa-content-tethered-panel-display-${panelId}`;
  const mouseleaveEventName = `rsa-content-tethered-panel-hide-${panelId}`;
  const displayDelay = 0;
  const hideDelay = 1000;

  eventBus
    .on(mouseenterEventName, function() {
      assert.ok(true, 'eventBus display handler was triggered as expected');
    })
    .on(mouseleaveEventName, function() {
      assert.notOk(true, 'eventBus hide handler was triggered but should not have been');
    });

  assert.expect(2);

  wireTriggerToHover(el, panelId, eventBus, { displayDelay, hideDelay });
  $el.trigger('mouseenter');  // should trigger a `display` event in eventBus immediately
  return wait().then(() => {
    $el.trigger('mouseleave');  // should wait for `hideDelay` before triggering eventBus
    $el.trigger('mouseenter');  // should abort the eventBus trigger from mouseleave above and instead trigger another `display` event
    return wait();
  }).then(() => {
    unwireTriggerToHover(el);
    eventBus
      .off(mouseenterEventName)
      .off(mouseleaveEventName);
  });
});
