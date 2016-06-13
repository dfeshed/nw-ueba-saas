import { test, skip } from 'qunit';
import moduleForAcceptance from 'sa/tests/helpers/module-for-acceptance';
import asyncFixtures from 'sa/mirage/scenarios/async-fixtures';
import config from 'sa/config/environment';
import selectors from 'sa/tests/selectors';
import Ember from 'ember';

let oldFeatureFlags;

moduleForAcceptance('Acceptance | respond', {
  beforeEach() {
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

test('enable respond feature flag, visiting /do/respond and check DOM ', function(assert) {
  config.featureFlags = {
    'show-respond-route': true
  };

  withFeature('show-respond-route');

  andThen(function() {
    return asyncFixtures(server, ['incident', 'alerts']);
  });

  visit('/do/respond');

  andThen(function() {
    assert.equal(currentPath(), 'protected.respond.index');
    assert.equal(find('.rsa-header-nav-respond').length, 1, '.rsa-header-nav-respond should be in dom');
  });
});

test('Landing Page card components should be displayed on load', function(assert) {
  asyncFixtures(server, ['incident', 'alerts']);
  visit('/do/respond');
  andThen(function() {
    assert.equal(currentPath(), selectors.pages.respond.path);
    let el = find(selectors.pages.respond.incTile.editButton);
    andThen(function() {
      assert.notOk(el.hasClass('hide'), 'Edit button is invisible by default');
      triggerEvent(el[0], 'mouseover');
      andThen(function() {
        assert.notOk(el.hasClass('hide'), 'Edit button is visible on hover');
        click(el[0]);
        andThen(function() {
          assert.notOk(el.hasClass('active'), 'Edit button is active on click');
        });
      });
    });
  });
});

test('Selectors should be visible on click', function(assert) {
  asyncFixtures(server, ['incident', 'alerts']);
  assert.expect(3);
  visit('/do/respond');
  andThen(function() {
    let el = find(selectors.pages.respond.incTile.editButton);
    triggerEvent(el[0], 'mouseover');
    andThen(function() {
      click(el[0]);
      andThen(function() {
        el = find(selectors.pages.respond.incTile.statusSelect);
        andThen(function() {
          assert.notEqual(el.length, 0, 'Status select box is visible on click');
          el = find(selectors.pages.respond.incTile.assigneeSelect);
          assert.notEqual(el.length, 0, 'Asignee select box is visible on click');
          el = find(selectors.pages.respond.incTile.prioritySelect);
          assert.notEqual(el.length, 0, 'Priority select box is visible on click');
        });
      });
    });
  });
});

skip('User should be able to setStatus, Assignee and Priority', function(assert) {

  asyncFixtures(server, ['incident', 'alerts']);
  visit('/do/respond');
  andThen(function() {
    let editBtn = find(selectors.pages.respond.incTile.editButton);
    andThen(function() {
      triggerEvent(editBtn[0], 'mouseover');
      andThen(function() {
        click(editBtn[0]);
        andThen(function() {
          let el = find(selectors.pages.respond.incTile.statusSelect);
          let assignee = find(selectors.pages.respond.incTile.assigneeSelect);
          let priority = find(selectors.pages.respond.incTile.prioritySelect);
          andThen(function() {
            Ember.Logger.debug('Setting the Status');
            fillIn(el[0], 2);
            Ember.Logger.debug('Setting the Assignee');
            fillIn(assignee[0], 1);
            Ember.Logger.debug('Setting the Priority');
            fillIn(priority[0], 1);
            andThen(function() {
              click(selectors.pages.respond.incTile.editButton);
              andThen(function() {
                el = find(selectors.pages.respond.incTile.statusLabel);
                andThen(function() {
                  let status = el[0].innerHTML.trim();
                  assert.equal(status.toLowerCase(), 'new');
                  Ember.Logger.debug('Verified that status is set correctly');
                  let currPriority = find(selectors.pages.respond.incTile.priorityLabel);
                  assert.equal(currPriority[0].innerText.indexOf('Medium'), -1);
                  Ember.Logger.debug('Verified that Priority field is set correctly');
                });
              });
            });
          });
        });
      });
    });
  });
});
