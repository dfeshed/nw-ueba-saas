import Ember from 'ember';
import { module, test } from 'qunit';
import startApp from 'sa/tests/helpers/start-app';

var application;

module('Acceptance | investigation', {
  beforeEach: function() {
    application = startApp();
  },

  afterEach: function() {
    Ember.run(application, 'destroy');
  }
});

test('visiting /investigation and check text', function(assert) {
    visit('/investigation');

    andThen(function() {
        assert.equal(currentPath(), 'investigation');
        var content = find('div.sa-content');
        assert.equal(content.text().trim(), 'Investigation details');
    });

});
