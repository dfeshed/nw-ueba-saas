import Ember from 'ember';
import { module, test } from 'qunit';
import startApp from 'sa/tests/helpers/start-app';


var application;

module('Acceptance | i18n', {
  beforeEach: function() {
    application = startApp();
  },

  afterEach: function() {
    Ember.run(application, 'destroy');
  }
});

test('visiting / and changing locale', function(assert) {
    visit('/');

    andThen(function() {
        var content = find('div.sa-content');
        assert.equal(content.text().trim(), 'Index');

        var li = find('.locale_jp');
        li.trigger('click');
        content = find('div.sa-content');
        assert.equal(content.text().trim(), 'jp_Index');
    });
});
