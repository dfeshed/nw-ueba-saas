import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('rsa-application-layout-panel', 'Integration | Component | rsa application layout panel', {
  integration: true
});

test('it includes the proper classes', function(assert) {
  this.render(hbs `{{rsa-application-layout-panel}}`);
  assert.equal(this.$().find('vbox.rsa-application-layout-panel').length, 1);
});
