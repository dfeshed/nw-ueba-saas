import Ember from 'ember';
import { module, test } from 'qunit';
import startApp from 'sa/tests/helpers/start-app';

var application;

module('Acceptance | explorer', {
  beforeEach: function() {
    application = startApp();
  },

  afterEach: function() {
    Ember.run(application, 'destroy');
  }
});

test('visiting /explorer and check text', function(assert) {
    visit('/explorer');

    andThen(function() {
        assert.equal(currentPath(), 'explorer');
        var content = find('div.sa-content');
        assert.equal(content.text().trim(), 'Threat details');
    });

});
