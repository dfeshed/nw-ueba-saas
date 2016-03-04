import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('/rsa-icon', 'Integration | Component | rsa-icon', {
  integration: true
});

test('it renders', function(assert) {
  this.render(hbs `{{rsa-icon name='atomic-bomb'}}`);
  let iconCount = this.$().find('.rsa-icon.is-filled.rsa-icon-atomic-bomb').length;
  assert.equal(iconCount, 1);
});

test('it includes the proper classes when isLined is true', function(assert) {
  this.render(hbs `{{rsa-icon style='lined' name='atomic-bomb'}}`);
  let iconCount = this.$().find('.rsa-icon.is-lined').length;
  assert.equal(iconCount, 1);
});

test('it includes the proper classes when isLarge is true', function(assert) {
  this.render(hbs `{{rsa-icon size='large' name='atomic-bomb'}}`);
  let iconCount = this.$().find('.rsa-icon.is-large').length;
  assert.equal(iconCount, 1);
});

test('it includes the proper classes when isLarger is true', function(assert) {
  this.render(hbs `{{rsa-icon size='larger' name='atomic-bomb'}}`);
  let iconCount = this.$().find('.rsa-icon.is-larger').length;
  assert.equal(iconCount, 1);
});

test('it includes the proper classes when isLargest is true', function(assert) {
  this.render(hbs `{{rsa-icon size='largest' name='atomic-bomb'}}`);
  let iconCount = this.$().find('.rsa-icon.is-largest').length;
  assert.equal(iconCount, 1);
});
