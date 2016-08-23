import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('rsa-investigate/meta/keys-panel', 'Integration | Component | rsa investigate/meta/keys panel', {
  integration: true
});

test('it renders', function(assert) {
  const keys = [{
    id: 1,
    name: 'Key 1',
    type: 'key'
  }, {
    id: 2,
    name: 'Key 2',
    type: 'key'
  }];
  this.set('keys', keys);
  this.render(hbs`{{rsa-investigate/meta/keys-panel keys=keys}}`);

  assert.equal(this.$('.rsa-investigate-meta-keys-panel').length, 1);
  assert.equal(this.$('li').length, keys.length, 'Expected one list item for each key.');
});
