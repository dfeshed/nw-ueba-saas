import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render } from '@ember/test-helpers';
import { hbs } from 'ember-cli-htmlbars';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

const selectors = {
  footer: '.springboard-footer'
};

module('Integration | Component | springboard-footer', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('springboard')
  });

  test('it renders the springboard footer', async function(assert) {
    await render(hbs`<SpringboardFooter/>`);
    assert.dom(selectors.footer).exists('Footer section is present');
  });

});
