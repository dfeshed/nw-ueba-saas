import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

import { testSetupConfig } from './util';

moduleForComponent(
  'query-filter-fragment',
  'Integration | Component | query-filter-fragment basic-interaction',
  testSetupConfig
);

test('it is selectable', function(assert) {
  this.render(hbs`{{query-filter-fragment}}`);
  const $fragment = this.$('.rsa-query-fragment');
  const $fragmentMeta = $fragment.find('.meta');
  $fragmentMeta.click();
  assert.ok($fragment.hasClass('selected'), 'Expected fragment to be selected.');
});