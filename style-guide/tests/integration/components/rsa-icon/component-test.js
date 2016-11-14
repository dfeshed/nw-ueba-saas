import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('/rsa-icon', 'Integration | Component | rsa-icon', {
  integration: true
});

test('it renders', function(assert) {
  this.render(hbs `{{rsa-icon name='account-circle-1'}}`);
  const iconCount = this.$().find('.rsa-icon.is-filled.rsa-icon-account-circle-1').length;
  assert.equal(iconCount, 1);
});

test('it includes the proper classes when isLined is true', function(assert) {
  this.render(hbs `{{rsa-icon style='lined' name='account-circle-1'}}`);
  const iconCount = this.$().find('.rsa-icon.is-lined').length;
  assert.equal(iconCount, 1);
});

test('it includes the proper classes when isLarge is true', function(assert) {
  this.render(hbs `{{rsa-icon size='large' name='account-circle-1'}}`);
  const iconCount = this.$().find('.rsa-icon.is-large').length;
  assert.equal(iconCount, 1);
});

test('it includes the proper classes when isLarger is true', function(assert) {
  this.render(hbs `{{rsa-icon size='larger' name='account-circle-1'}}`);
  const iconCount = this.$().find('.rsa-icon.is-larger').length;
  assert.equal(iconCount, 1);
});

test('it includes the proper classes when isLargest is true', function(assert) {
  this.render(hbs `{{rsa-icon size='largest' name='account-circle-1'}}`);
  const iconCount = this.$().find('.rsa-icon.is-largest').length;
  assert.equal(iconCount, 1);
});

test('it includes the proper title', function(assert) {
  this.render(hbs `{{rsa-icon size='largest' name='account-circle-1' title='Foo'}}`);
  const title = this.$().find('.rsa-icon').attr('title');
  assert.equal(title, 'Foo');
});
