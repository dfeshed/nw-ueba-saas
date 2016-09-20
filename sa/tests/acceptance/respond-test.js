/* global server */
/* global withFeature */

import { test, skip } from 'qunit';
import moduleForAcceptance from 'sa/tests/helpers/module-for-acceptance';
import config from 'sa/config/environment';
import selectors from 'sa/tests/selectors';
import Ember from 'ember';

const { Logger } = Ember;

let oldFeatureFlags;

/**
 * @description navigates to /monitor route in order to generate a willTransition action call into respond route to
 * close existing streams
 * @private
 */
function navigateToMonitor() {
  Logger.log(' Navigating to /monitor...');
  visit('/do/monitor');

  andThen(function() {
    Logger.log(' Landed to /monitor');
  });
}

moduleForAcceptance('Acceptance | respond', {
  beforeEach() {
    server.createList('incidents', 3);
    oldFeatureFlags = config.featureFlags;
  },
  // After each test, destroy the MockServer instances we've created (if any), so that the next test will not
  // throw an error when it tries to re-create them.
  afterEach() {
    config.featureFlags = oldFeatureFlags;
    (window.MockServers || []).forEach((server) => {
      server.close();
    });
  }
});

test('disable respond feature flag, visiting /do/respond and check DOM ', function(assert) {
  config.featureFlags = {
    'show-respond-route': false
  };

  visit('/do/monitor');

  andThen(function() {
    assert.equal(find('.rsa-header-nav-respond').length, 0, '.rsa-header-nav-respond should not be in dom');
  });

});

test('ensure journal-trigger is added on the respond incident route', function(assert) {
  assert.expect(1);
  visit('/do/respond');
  andThen(function() {
    click('.rsa-incident-tile:first-of-type');

    andThen(function() {
      assert.equal(find('.rsa-application-action-bar .incident-journal-trigger').length, 1);
    });
  });
});

test('enable respond feature flag, visiting /do/respond and check DOM ', function(assert) {
  config.featureFlags = {
    'show-respond-route': true
  };

  withFeature('show-respond-route');

  visit('/do/respond');

  andThen(function() {
    assert.equal(find('.rsa-header-nav-respond').length, 1, '.rsa-header-nav-respond should be in dom');

    navigateToMonitor();
  });
});

test('Landing Page card components should be displayed on load by default', function(assert) {
  visit('/do/respond');
  andThen(function() {
    assert.equal(currentPath(), selectors.pages.respond.path);

    let respondHeader = find(selectors.pages.respond.toggleViewHeader).first();
    assert.equal(respondHeader.length, 1, 'Respond header is visible');

    let el = find(selectors.pages.respond.card.incTile.editButton);

    assert.notOk(el.hasClass('hide'), 'Edit button is invisible by default');
    triggerEvent(el[0], 'mouseover');

    andThen(function() {
      assert.notOk(el.hasClass('hide'), 'Edit button is visible on hover');
      click(el[0]);
      andThen(function() {
        assert.notOk(el.hasClass('active'), 'Edit button is active on click');

        navigateToMonitor();

      });
    });
  });
});

test('Selectors should be visible on click', function(assert) {
  assert.expect(3);
  visit('/do/respond');
  andThen(function() {
    let el = find(selectors.pages.respond.card.incTile.editButton);
    triggerEvent(el[0], 'mouseover');
    andThen(function() {
      click(el[0]);
      andThen(function() {
        el = find(selectors.pages.respond.card.incTile.statusSelect);
        assert.notEqual(el.length, 0, 'Status select box is visible on click');
        el = find(selectors.pages.respond.card.incTile.assigneeSelect);
        assert.notEqual(el.length, 0, 'Assignee select box is visible on click');
        el = find(selectors.pages.respond.card.incTile.prioritySelect);
        assert.notEqual(el.length, 0, 'Priority select box is visible on click');

        navigateToMonitor();
      });
    });
  });
});

test('User should be able to setStatus, Assignee and Priority', function(assert) {
  visit('/do/respond');
  andThen(() => {
    let editBtn = find(selectors.pages.respond.card.incTile.editButton).first();
    andThen(function() {
      triggerEvent(editBtn, 'mouseover');

      andThen(() => {
        click(editBtn);
        andThen(() => {

          Logger.debug('Setting the Status');
          click(find(selectors.pages.respond.card.incTile.statusLabel).first());
          find(selectors.pages.respond.card.incTile.statusSelect).first().val(2);
          triggerEvent(find(selectors.pages.respond.card.incTile.statusSelect).first(), 'change');

          Logger.debug('Setting the Priority');
          click(find(selectors.pages.respond.card.incTile.priorityLabel).first());
          find(selectors.pages.respond.card.incTile.prioritySelect).first().val(1);
          triggerEvent(find(selectors.pages.respond.card.incTile.prioritySelect).first(), 'change');

          andThen(()=> {

            let statusLabel = find(selectors.pages.respond.card.incTile.statusLabel).first();
            let statusVal = statusLabel.text().trim();
            assert.equal(statusVal.toLowerCase(), 'in progress', 'Verified that status is set correctly');

            let priorityLabel = find(selectors.pages.respond.card.incTile.priorityLabel).first();
            let priorityVal = priorityLabel.text().trim();
            assert.equal(priorityVal.toLowerCase(), 'medium', 'Verified that priority is set correctly');

            navigateToMonitor();
          });
        });
      });
    });
  });
});

/* @TODO these asserts fail in phantomJS environment with an error
  not ok 123 PhantomJS 2.1 - Global error: TypeError: undefined is not an object (evaluating 'expand.firstElementChild')
  this is coming from javascript-detect-element-resize
  https://github.com/sdecima/javascript-detect-element-resize/blob/master/detect-element-resize.js#L31
  Once we've integrated flexi hopefully we'll be able to replace this addon with flexi helpers
  and uncomment this test.*/
skip('Toggle list button renders incidents list view with right number of columns', function(assert) {

  visit('/do/respond');
  andThen(() => {
    let listViewBtn = find(selectors.pages.respond.listViewBtn);
    click(listViewBtn);

    andThen(() => {

      let table = find(selectors.pages.respond.list.table);
      assert.equal(table.length, 1, 'Table with incidents is displayed');

      let columns = find(selectors.pages.respond.list.columns);
      assert.ok(columns, 'Table has columns');
      assert.equal(columns.length, 9, 'Table displays proper number of columns');

      navigateToMonitor();
    });
  });
});
