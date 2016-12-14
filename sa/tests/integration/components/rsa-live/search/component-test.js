import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('rsa-live/search', 'Integration | Component | rsa live/search', {
  integration: true
});

test('it renders', function(assert) {
  this.render(hbs`{{rsa-live/search}}`);
  assert.equal(this.$('.rsa-live-search').length, 1, 'The live search wrapper element is rendered to the page');
});
