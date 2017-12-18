import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';

moduleForComponent('files-toolbar/export-button', 'Integration | Component | files toolbar/export button', {
  integration: true,
  resolver: engineResolverFor('investigate-files'),
  beforeEach() {
    this.inject.service('redux');
    this.registry.injection('component', 'i18n', 'service:i18n');
  }
});

test('it renders export button', function(assert) {
  // Set any properties with this.set('myProperty', 'value');
  // Handle any actions with this.on('myAction', function(val) { ... });

  this.render(hbs`{{files-toolbar/export-button}}`);
  assert.equal(this.$('.export-button').text().trim(), 'Export to CSV', 'Make sure button is present');
});
