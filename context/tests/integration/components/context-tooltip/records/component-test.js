import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';
import { later } from 'ember-runloop';

moduleForComponent('context-tooltip-records', 'Integration | Component | context tooltip records', {
  integration: true
});

const model = { type: 'IP', id: '10.20.30.40' };

test('it renders', function(assert) {
  assert.expect(4);

  this.set('model', model);
  this.render(hbs`{{context-tooltip/records model=model}}`);
  return wait().then(() => {
    assert.equal(this.$('.rsa-context-tooltip-records').length, 1);

    // Wait long enough for some data to start streaming in before checking for data in the DOM.
    const done = assert.async();
    later(() => {
      assert.ok(this.$('.rsa-context-tooltip-records__record').length, 'Expected to find one or more records in the DOM');
      assert.ok(this.$('.rsa-context-tooltip-records__record .value').text().trim(), 'Expected to find record value');
      assert.ok(this.$('.rsa-context-tooltip-records__record .text').text().trim(), 'Expected to find record name');
      done();
    }, 1000);
  });
});
