import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { interpolateColors } from 'component-lib/utils/chart-utils';
import { interpolateRainbow } from 'd3-scale-chromatic';

module('Unit | Utils | sanitizeHtml', function(hooks) {
  setupTest(hooks);

  test('it returns the colour array based on data', async function(assert) {
    const data = [
      {
        name: 'Test',
        count: 1
      },
      {
        name: 'Test 2',
        count: 10
      }
    ];
    const color = interpolateColors(data.length, interpolateRainbow);
    assert.equal(color.length, data.length, 'Returns required number of color');
  });
});
