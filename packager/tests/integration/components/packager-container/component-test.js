import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('packager-container', 'Integration | Component | packager Container', {
  integration: true,
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
  }
});

test('it renders', function(assert) {
  this.render(hbs`{{packager-container}}`);
  const $el = this.$('.packager-container');
  assert.equal($el.length, 1, 'Expected to find packager container root element in DOM.');
});