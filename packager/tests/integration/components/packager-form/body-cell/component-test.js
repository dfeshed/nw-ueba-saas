import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('packager-form/body-cell', 'Integration | Component | packager form/body-cell', {
  integration: true
});

test('it renders packager form body cell', function(assert) {
  this.render(hbs`{{packager-form/body-cell}}`);
  const $el = this.$('.body-cell');
  assert.equal($el.length, 1, 'Expected to find packager form root element in DOM.');
});
