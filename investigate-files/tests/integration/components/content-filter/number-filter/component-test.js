import { moduleForComponent, skip } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';


moduleForComponent('content-filter/number-filter', 'Integration | Component | content filter/number filter', {
  integration: true,
  resolver: engineResolverFor('investigate-files')
});

skip('it renders', function(assert) {

  // Set any properties with this.set('myProperty', 'value');
  // Handle any actions with this.on('myAction', function(val) { ... });

  this.render(hbs`{{content-filter/number-filter}}`);

  assert.equal(this.$().text().trim(), '');

  // Template block usage:
  this.render(hbs`
    {{#content-filter/number-filter}}
      template block text
    {{/content-filter/number-filter}}
  `);

  assert.equal(this.$().text().trim(), 'template block text');
});
