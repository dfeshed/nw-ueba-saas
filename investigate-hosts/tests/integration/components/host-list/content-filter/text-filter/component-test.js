import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../../helpers/engine-resolver';

import filterConfig from '../../../state/filter.config';

moduleForComponent('host-list/content-filter/text-filter', 'Integration | Component | host list/content filter/text filter', {
  integration: true,
  resolver: engineResolverFor('investigate-hosts'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
    this.inject.service('redux');
  }
});

test('Text-filter reders', function(assert) {
  // Set any properties with this.set('myProperty', 'value');
  // Handle any actions with this.on('myAction', function(val) { ... });
  this.set('config', filterConfig[0]);
  this.render(hbs`{{host-list/content-filter/text-filter config=config}}`);
  const textFilterComponent = this.$().find('.text-filter');
  assert.equal(this.$(textFilterComponent).length, 1);
});
