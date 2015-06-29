import Ember from 'ember';
import { module, test } from 'qunit';
import startApp from 'sa/tests/helpers/start-app';

var application;

module('Acceptance | incidents', {
  beforeEach: function() {
    application = startApp();
  },

  afterEach: function() {
    Ember.run(application, 'destroy');
  }
});

test('visiting /incidents and check text', function(assert) {
    visit('/incidents');

    andThen(function() {
        assert.equal(currentPath(), 'incidents');
        var content = find('div.sa-content');
        assert.equal(content.text().trim(), 'Incident details');
    });

});
