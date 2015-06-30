import { moduleFor, test } from 'ember-qunit';
import Ember from 'ember';

var container, application;

moduleFor('controller:i18n', {
    // Specify the other units that are required for this test.
    // needs: ['controller:foo']
});

container = new Ember.Container();
application = {
    localeStream: {
        value: function() {
            return 'en';
        },
        subscribe: function () {}
    }
};

container.register('application:main', application, { instantiate: false });

test('i18n actions', function(assert) {
    assert.expect(1);
    var ctrl = this.subject();
    ctrl.send('changeLocale', 'jp', application);
    assert.equal(Ember.get(application, 'locale'), 'jp');
});
