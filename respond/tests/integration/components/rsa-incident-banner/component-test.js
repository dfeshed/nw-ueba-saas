import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';
import info from '../../data/incident';

moduleForComponent('rsa-incident-banner', 'Integration | Component | Incident Banner', {
  integration: true,
  resolver: engineResolverFor('respond')
});

test('it renders', function(assert) {
  this.set('info', info);
  this.render(hbs`{{rsa-incident-banner incidentId=info.id info=info}}`);

  const $el = this.$('.rsa-incident-banner');
  assert.equal($el.length, 1, 'Expected to find root element in DOM.');
});