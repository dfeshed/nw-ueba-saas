import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | card-lc', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders', async function(assert) {
    // Set any properties with this.set('myProperty', 'value');
    // Handle any actions with this.set('myAction', function(val) { ... });

    await render(hbs`{{card-lc}}`);
    assert.equal(this.element.querySelectorAll('.rsa-data-table').length, 1);
    // assert.equal(this.element.querySelectorAll('.rsa-data-table .rsa-data-table-header-cell').length, 7);

    const headers = this.element.querySelectorAll('.rsa-data-table .rsa-data-table-header-cell');
    let headersActual = [];
    headers.forEach((x) => headersActual.push(x.textContent.trim()));
    headersActual = headersActual.filter(function(el) {
      return el != '';
    });
    const headersExpected = ['Protocol', 'Event Rate', 'Total Events', 'Byte Rate', 'Total Bytes',
      'Error Rate', 'Error Count'];
    assert.deepEqual(headersExpected, headersActual);

    const noEventSourcesMsg = this.element.querySelectorAll('.rsa-data-table .no-results-message').length;
    assert.equal(noEventSourcesMsg, 1);
  });

});
