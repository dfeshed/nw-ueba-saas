import Ember from 'ember';

export default Ember.Controller.extend({
    actions: {
        changeLocale: function(locale, application) {
            application = application || this.container.lookup('application:main');
            Ember.set(application, 'locale', locale);
        }
    }
});
