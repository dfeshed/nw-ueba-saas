import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('packager-info', 'Integration | Component | packager info', {
  integration: true,
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
  }
});

test('it renders packager information', function(assert) {
  this.render(hbs`{{packager-info}}`);
  const $header = this.$('.packager-information__header');
  assert.equal($header.length, 1, 'Expected to find information header element in DOM.');
  assert.equal($header.text().trim(), 'How to install ?', 'Expected to match the text.');
});

test('it renders warning message', function(assert) {
  this.render(hbs`{{packager-info}}`);
  const $warning = this.$('.packager-information__content__caution__text');
  assert.equal($warning.length, 1, 'Expected to find warning information element in DOM.');
});