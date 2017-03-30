import {
  sendTetherEvent,
  wireTriggerToHover,
  unwireTriggerToHover,
  wireTriggerToClick,
  unwireTriggerToClick
} from 'component-lib/utils/tooltip-trigger';
import { module, test } from 'qunit';
import Ember from 'ember';

const {
  Service,
  Evented,
  $
} = Ember;

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
