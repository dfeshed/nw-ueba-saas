import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';

moduleForComponent('rsa-stat-sign', 'Integration | Component | Stat Sign', {
  integration: true,
  resolver: engineResolverFor('respond')
});

test('it renders', function(assert) {
  const caption = 'foo';
  const body = 'bar';
  this.setProperties({ caption, body });
  this.render(hbs`{{#rsa-stat-sign caption=caption}}{{body}}{{/rsa-stat-sign}}`);

  const $el = this.$('.rsa-stat-sign');
  assert.equal($el.length, 1, 'Expected to find root element in DOM.');

  const $caption = $el.find('.rsa-stat-sign__caption');
  assert.equal($caption.text().trim(), caption, 'Expected to find caption in DOM.');

  const $body = $el.find('.rsa-stat-sign__body');
  assert.equal($body.text().trim(), body, 'Expected to find caption in DOM.');
});