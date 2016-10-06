import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('rsa-content-memsize', 'Integration | Component | rsa content memsize', {
  integration: true
});

test('it renders with the correct class', function(assert) {
  this.render(hbs`{{rsa-content-memsize}}`);
  assert.equal(this.$('.rsa-content-memsize').length, 1);
});

test('it renders less than 1024 bytes in bytes', function(assert) {
  this.render(hbs`{{rsa-content-memsize size=10}}`);
  assert.equal(this.$('.size').text().trim(), 10);
  assert.equal(this.$('.rsa-content-memsize').attr('title').trim(), '10 bytes');
});

test('it renders 1024 bytes or more in various sizes', function(assert) {
  let bytes = 1024;
  this.set('size', bytes);
  this.render(hbs`{{rsa-content-memsize size=size}}`);
  assert.equal(this.$('.size').text().trim().indexOf('1.'), 0);
  assert.equal(this.$('.rsa-content-memsize').attr('title').trim(), `${bytes} bytes`);

  bytes *= 1025 * 2;
  this.set('size', bytes);
  assert.equal(this.$('.size').text().trim().indexOf('2.'), 0);
  assert.equal(this.$('.rsa-content-memsize').attr('title').trim(), `${bytes} bytes`);

  bytes *= 1025;
  this.set('size', bytes);
  assert.equal(this.$('.size').text().trim().indexOf('2.'), 0);
  assert.equal(this.$('.rsa-content-memsize').attr('title').trim(), `${bytes} bytes`);

  bytes *= 2;
  this.set('size', bytes);
  assert.equal(this.$('.size').text().trim().indexOf('4.'), 0);
  assert.equal(this.$('.rsa-content-memsize').attr('title').trim(), `${bytes} bytes`);
});

