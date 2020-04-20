import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render } from '@ember/test-helpers';
import { hbs } from 'ember-cli-htmlbars';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

const selectors = {
  header: '.springboard-header'
};

module('Integration | Component | springboard-header', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('springboard')
  });

  test('it renders the springboard header', async function(assert) {
    await render(hbs`<SpringboardHeader/>`);
    assert.dom(selectors.header).exists('Header section is present');
  });

});
