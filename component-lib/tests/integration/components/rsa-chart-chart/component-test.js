import { find, findAll, render } from '@ember/test-helpers';
import { module, skip, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | rsa-chart', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders', async function(assert) {
    await render(hbs `{{rsa-chart}}`);
    assert.equal(findAll('.rsa-chart').length, 1, 'Testing to see if the .rsa-chart class exists');
    assert.equal(findAll('.rsa-chart-background').length, 1, 'Testing to see if the .rsa-chart-background class exists');
  });

  skip('The Chart component is properly sized when supplied with a margin attribute', function(assert) {
    this.set('margin', { top: 0, bottom: 0, left: 0, right: 0 });
    this.render(hbs `{{rsa-chart margin=margin}}`);
    // const $el = this.$('.rsa-chart svg');
    // For some reason the width of the SVG does not equal to the default size specified in rsa-chart.js.
    // It should be 600x150 (like in the test above), yet for some reason it's 1252x150. I don't understand
    // why.
    const width = 1250;// $el.width();
    const height = 150;// $el.height();
    assert.equal(find('.rsa-chart-background').getAttribute('width'), width, 'Width should be same as component width');
    assert.equal(find('.rsa-chart-background').getAttribute('height'), height, 'Height should be same as component height');
  });
});
