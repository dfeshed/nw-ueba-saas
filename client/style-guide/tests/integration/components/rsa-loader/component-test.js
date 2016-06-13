import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('/rsa-loader', 'Integration | Component | rsa-loader', {
  integration: true
});

test('The Loader component loads properly with all of the required elements.', function(assert) {
  this.render(hbs `{{rsa-loader}}`);
  assert.equal(this.$('.rsa-loader').length, 1, 'Testing to see if the .rsa-loader__container class exists.');
  assert.equal(this.$('.rsa-loader__wheel').length, 1, 'Testing to see if the .rsa-loader__wheel class exists.');
  assert.equal(this.$('.rsa-loader__text').length, 1, 'Testing to see if the .rsa-loader__text class exists.');
});

test('The Loader component properly renders the proper size class given a size attribute.', function(assert) {
  this.render(hbs `{{rsa-loader}}`);
  assert.equal(this.$('.is-small').length, 1, 'Testing to see if the loader is rendered using the small class.');

  this.render(hbs `{{rsa-loader size='medium'}}`);
  assert.equal(this.$('.is-medium').length, 1, 'Testing to see if the loader is rendered using the medium class.');

  this.render(hbs `{{rsa-loader size='large'}}`);
  assert.equal(this.$('.is-large').length, 1, 'Testing to see if the loader is rendered using the large class.');

  this.render(hbs `{{rsa-loader size='larger'}}`);
  assert.equal(this.$('.is-larger').length, 1, 'Testing to see if the loader is rendered using the larger class.');

  this.render(hbs `{{rsa-loader size='largest'}}`);
  assert.equal(this.$('.is-largest').length, 1, 'Testing to see if the loader is rendered using the largest class.');
});

test('The Loader component properly renders a label given the label attribute.', function(assert) {
  this.render(hbs `{{rsa-loader label='Gathering Data'}}`);
  assert.equal(this.$('.rsa-loader__text').html(), 'Gathering Data', 'Testing to see if the proper label is applied to the loader.');
});

test('The Loader component properly renders properly given empty attribute values.', function(assert) {
  this.render(hbs `{{rsa-loader size='' label=''}}`);
  assert.equal(this.$('.is-small').length, 1, 'Testing to see if the loader is rendered using the small class.');
  assert.equal(this.$('.rsa-loader__text').html(), '<!---->', 'Testing to see if the label attribute is ignored.');
});
