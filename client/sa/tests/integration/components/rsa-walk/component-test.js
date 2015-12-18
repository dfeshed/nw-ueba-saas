import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import initializer from 'sa/instance-initializers/ember-i18n';

moduleForComponent('rsa-walk', 'Integration | Component | rsa walk', {
  integration: true,

  beforeEach() {

    // Initializes the locale so that i18n content in the component will work.
    // See: https://github.com/jamesarosen/ember-i18n/wiki/Doc:-Testing
    initializer.initialize(this);
  }
});

test('it renders', function(assert) {
  assert.expect(3);

  let firstStep = { type: 'incidents-queue-test', value: null };
  this.set('myFirstStep', firstStep);
  this.set('myPath', [firstStep]);
  this.render(hbs`{{#rsa-walk path=myPath as |walk|}}
          <button class='js-test-step-fwd' {{action 'forward' myFirstStep 'incident-info' null target=walk}}>Step Forward</button>
      {{/rsa-walk}}
  `);

  assert.ok(this.$('.rsa-walk').length, 'Could not find component\'s root DOM element.');

  let btn = this.$('.js-test-step-fwd');
  assert.equal(btn.length, 1, 'Could not find component\'s yielded DOM element.');
  btn.trigger('click');

  let content2 = this.$('.rsa-incident-info');
  assert.equal(content2.length, 1, 'Could not find the second child component\'s root DOM element.');
});
