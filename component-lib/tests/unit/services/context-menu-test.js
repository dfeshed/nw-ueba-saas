import { module, test } from 'ember-qunit';
import $ from 'jquery';
import ContextMenuService from 'component-lib/services/context-menu';
import _ from 'lodash';

let contextMenuService;
module('Unit | Service | context menu', {
  beforeEach() {
    contextMenuService = ContextMenuService.create();
  },
  afterEach() {
    $(document.body).off('contextmenu', '**'); // unregister all handlers
  }
});

test('test removeDeactivateHandler', function(assert) {
  const deactivate = function() {
    assert.notOk(true, 'deactivate handler should not be called');
  };
  $(document.body).one('contextmenu', deactivate);
  contextMenuService.set('deactivate', deactivate);
  assert.ok(_.get($._data($(document.body)[0], 'events'), 'contextmenu'), 'event handler should be registered');
  contextMenuService.removeDeactivateHandler();
  $(document.body).contextmenu();
  assert.notOk(_.get($._data($(document.body)[0], 'events'), 'contextmenu'), 'event handler should not be registered');
});

test('test addDeactivateHandler', function(assert) {
  contextMenuService.set('isActive', true);
  contextMenuService.addDeactivateHandler();
  assert.equal($._data($('body')[0], 'events').contextmenu.length, 1, 'event handler should be registered');
  $(document.body).contextmenu();
  assert.notOk(contextMenuService.get('isActive'), 'deactivate must be called');
});

test('test that deactivate is not called when right-clicked in a content-context-menu classed span', function(assert) {
  contextMenuService.set('isActive', true);
  contextMenuService.addDeactivateHandler();
  assert.equal($._data($(document.body)[0], 'events').contextmenu.length, 1, 'event handler should be registered');

  $('body').prepend('<span class="content-context-menu"></span>');

  $('.content-context-menu').contextmenu();
  assert.ok(contextMenuService.get('isActive'), 'deactivate must not be called');
});