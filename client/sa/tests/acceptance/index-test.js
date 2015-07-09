import Ember from 'ember';
import { module, test } from 'qunit';
import startApp from 'sa/tests/helpers/start-app';

var application;

module('Acceptance | index', {
  beforeEach: function() {
    application = startApp();
  },

  afterEach: function() {
    Ember.run(application, 'destroy');
  }
});

test('visiting /index and check text', function(assert) {
    visit('/');

    andThen(function() {
        assert.equal(currentPath(), 'index');
        var content = find(".app-body .liquid-child");
        assert.ok(content.length, "Could not find the explorer container DOM.");
        assert.equal(content.text().trim(), "Home contents go here.", "Unexpected contents in DOM.");
    });

});
