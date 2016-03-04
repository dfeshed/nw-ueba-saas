import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('/rsa-application-header', 'Integration | Component | rsa-application-header', {
  integration: true
});

test('it includes the proper classes', function(assert) {
  this.render(hbs `{{rsa-application-header}}`);
  let header = this.$().find('.rsa-application-header').length;
  assert.equal(header, 1);
});

test('it hides locales when hideLocales', function(assert) {
  this.render(hbs `{{rsa-application-header hideLocales=true}}`);
  let select = this.$().find('.rsa-application-select-locale').length;
  assert.equal(select, 0);
});

test('it hides themes when hideThemes', function(assert) {
  this.render(hbs `{{rsa-application-header hideThemes=true}}`);
  let select = this.$().find('.rsa-application-select-theme').length;
  assert.equal(select, 0);
});

test('it provides theme and locale toggle', function(assert) {
  this.render(hbs `{{rsa-application-header}}`);

  let themeBtn = this.$().find('.rsa-application-select-theme'),
      localeBtn = this.$().find('.rsa-application-select-locale');

  assert.equal(themeBtn.length, 1);
  assert.equal(localeBtn.length, 1);
});

