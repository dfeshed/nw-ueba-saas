import { moduleForComponent, skip } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';

moduleForComponent('custom-filters/custom-filter-list', 'Integration | Component | custom filters/custom filter list', {
  integration: true,
  resolver: engineResolverFor('investigate-files')
});

skip('it renders', function(assert) {
  // Set any properties with this.set('myProperty', 'value');
  // Handle any actions with this.on('myAction', function(val) { ... });

  this.render(hbs`{{custom-filters/custom-filter-list}}`);

  assert.equal(this.$().text().trim(), '');

  // Template block usage:
  this.render(hbs`
    {{#custom-filters/custom-filter-list}}
      template block text
    {{/custom-filters/custom-filter-list}}
  `);

  assert.equal(this.$().text().trim(), 'template block text');
});
