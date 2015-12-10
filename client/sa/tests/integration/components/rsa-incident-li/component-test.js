import Ember from 'ember';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import initializer from 'sa/instance-initializers/ember-i18n';

moduleForComponent('rsa-incident-li', 'Integration | Component | rsa incident li', {
  integration: true,

  beforeEach() {

    // Initializes the locale so that i18n content in the component will work.
    // See: https://github.com/jamesarosen/ember-i18n/wiki/Doc:-Testing
    initializer.initialize(this);
  }
});

test('it renders', function(assert) {
  assert.expect(2);

  this.set('incident', Ember.Object.create({
    name: 'My Test Incident Name'
  }));
  this.render(hbs`{{rsa-incident-li model=incident}}`);

  assert.ok(this.$('.rsa-incident-li').length, 'Could not find the component DOM element.');
  assert.equal(this.$('.rsa-incident-li .rsa-name span').text().trim(), 'My Test Incident Name', 'Unexpected DOM output.');
});
