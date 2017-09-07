import { moduleForComponent, skip } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';

moduleForComponent('content-filter/list-filter', 'Integration | Component | content filter/list filter', {
  integration: true,
  resolver: engineResolverFor('investigate-files')
});

skip('it renders', function(assert) {

  // Set any properties with this.set('myProperty', 'value');
  // Handle any actions with this.on('myAction', function(val) { ... });

  this.render(hbs`{{content-filter/list-filter}}`);

  assert.equal(this.$().text().trim(), '');

  // Template block usage:
  this.render(hbs`
    {{#content-filter/list-filter}}
      template block text
    {{/content-filter/list-filter}}
  `);

  assert.equal(this.$().text().trim(), 'template block text');
});
