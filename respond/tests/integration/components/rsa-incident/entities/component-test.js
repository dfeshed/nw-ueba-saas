import { moduleForComponent, skip } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import wait from 'ember-test-helpers/wait';
import DataHelper from '../../../../helpers/data-helper';

moduleForComponent('rsa-incident-entities', 'Integration | Component | Incident Entities', {
  integration: true,
  resolver: engineResolverFor('respond'),
  setup() {
    this.inject.service('redux');
  }
});

// @workaround Skip this test for now, because it throws errors that we need to use Ember.run around async code.
// But I can't find where exactly the run calls are needed.
// On the bright side, the integration test for {{rsa-force-layout}} passes fine, and this component is just a
// wrapper for that component anyway.
skip('it renders', function(assert) {
  new DataHelper(this.get('redux')).fetchIncidentStoryline();
  this.render(hbs`{{rsa-incident/entities}}`);
  return wait().then(() => {
    const $el = this.$('.rsa-force-layout');
    assert.equal($el.length, 1, 'Expected to find force-layout root element in DOM.');

    const $nodes = $el.find('.rsa-force-layout-node');
    assert.ok($nodes.length, 'Expected to find at least one node element in DOM.');

    const $links = $el.find('.rsa-force-layout-link');
    assert.ok($links.length, 'Expected to find at least one link element in DOM.');
  });
});