import wait from 'ember-test-helpers/wait';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('context-container', 'Integration | Component | context container', {
  integration: true
});

test('it renders a message', function(assert) {
  this.render(hbs`{{context-container message="whatwhat"}}`);
  return wait().then(() => {
    const str = this.$().text().trim();
    assert.equal(str, 'whatwhatwhat');
  });
});
