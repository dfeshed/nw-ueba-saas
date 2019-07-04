import { module, test } from 'ember-qunit';
import ContextMenuService from 'rsa-context-menu/services/context-menu';
import { triggerEvent } from '@ember/test-helpers';
import { htmlStringToElement } from 'component-lib/utils/jquery-replacement';

let contextMenuService;

module('Unit | Service | context menu', {
  beforeEach() {
    contextMenuService = ContextMenuService.create();
  },
  afterEach() {
    // [ {element, 'event': {eventType, handler, options}}, {}, {}, ]
    const existingEventHandlers = contextMenuService.getEventHandlerStorageArray();
    existingEventHandlers.forEach((item) => {
      if (item.element === document.body) {
        document.body.removeEventListener(item.event.eventType, item.event.handler, item.event.options);
      }
    });
  }
});

test('test removeDeactivateHandler', async function(assert) {
  let count = 0;
  const deactivate = function() {
    if (count < 1) {
      assert.ok(true, 'event handler should be registered');
    } else {
      assert.notOk(true, 'deactivate handler should not be called');
    }
    count++;
  };
  document.body.addEventListener('contextmenu', deactivate, { once: true });
  // deactivate() should be executed once
  document.body.dispatchEvent(new MouseEvent('contextmenu'));
  contextMenuService.set('deactivate', deactivate);
  contextMenuService.removeDeactivateHandler();

  await triggerEvent(document.body, 'contextmenu');

  // check that deactivate() did not execute more than once
  // since listener was removed
  assert.equal(count, 1, 'event handler should not be registered');
});

test('test addDeactivateHandler', async function(assert) {

  contextMenuService.set('isActive', true);
  contextMenuService.addDeactivateHandler();
  // [ {element, 'event': {eventType, handler, options}}, {}, {}, ]
  const existingEventHandlers = contextMenuService.getEventHandlerStorageArray();
  const oneHandlerExists = existingEventHandlers.length === 1;
  const eventTypeIsContextmenu = existingEventHandlers[0] &&
    existingEventHandlers[0].event.eventType === 'contextmenu';

  assert.equal(oneHandlerExists && eventTypeIsContextmenu, true, 'event handler should be registered');

  document.body.dispatchEvent(new MouseEvent('contextmenu'));

  await triggerEvent(document.body, 'contextmenu');
  assert.notOk(contextMenuService.get('isActive'), 'deactivate must be called');
});

test('test that deactivate is not called when right-clicked in a content-context-menu classed span', async function(assert) {
  contextMenuService.set('isActive', true);
  contextMenuService.addDeactivateHandler();
  // [ {element, 'event': {eventType, handler, options}}, {}, {}, ]
  const existingEventHandlers = contextMenuService.getEventHandlerStorageArray();
  const oneHandlerExists = existingEventHandlers.length === 1;
  const eventTypeIsContextmenu = existingEventHandlers[0] &&
    existingEventHandlers[0].event.eventType === 'contextmenu';

  assert.equal(oneHandlerExists && eventTypeIsContextmenu, true, 'event handler should be registered');
  const spanToInsert = htmlStringToElement('<span class="content-context-menu"></span>');
  document.body.insertBefore(spanToInsert, document.body.firstChild);

  await triggerEvent(document.body.querySelector('.content-context-menu'), 'contextmenu');
  assert.ok(contextMenuService.get('isActive'), 'deactivate must not be called');
});
