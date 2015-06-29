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
        var content = find('div.sa-content');
        assert.equal(content.text().trim(), 'Index');
    });

});
