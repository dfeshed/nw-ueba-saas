import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('/rsa-application-footer', 'Integration | Component | rsa-application-footer', {
  integration: true
});

test('it includes the proper classes', function(assert) {
  this.render(hbs `{{rsa-application-footer}}`);
  let footer = this.$().find('.rsa-application-footer').length;
  assert.equal(footer, 1);
});

test('it includes the label', function(assert) {
  this.render(hbs `{{rsa-application-footer title='Foo' version='1.0'}}`);
  let label = this.$().find('.application-name').text().trim(),
      version = this.$().find('.application-version').text().trim();
  assert.equal(label, 'Foo');
  assert.equal(version, '1.0');
});
