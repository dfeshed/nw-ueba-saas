import { setupRenderingTest } from 'ember-qunit';
import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { render, triggerEvent, findAll } from '@ember/test-helpers';

module('Integration | Component | rsa resizer', function(hooks) {
  setupRenderingTest(hooks, {
    integration: true,
    resolver: engineResolverFor('respond')
  });

  test('it renders', async function(assert) {
    await render(hbs`{{rsa-resizer}}`);
    assert.equal(findAll('.rsa-resizer').length, 1, 'Expected to find root DOM node.');
  });

  test('it fires its resizeAction when a dragmove occurs', async function(assert) {
    assert.expect(2);

    const x = 100;
    const y = 200;
    const deltaX = 3;
    const deltaY = 4;

    const resizeAction = (a, b) => {
      assert.equal(a, x + deltaX, 'Expected resizeAction to receive a numeric x param');
      assert.equal(b, y + deltaY, 'Expected resizeAction to receive a numeric y param');
    };

    this.setProperties({
      x,
      y,
      resizeAction
    });

    await render(hbs`{{rsa-resizer x=x y=y resizeAction=(action resizeAction)}}`);

    // Simulate a mousedown to start the drag.
    // eslint-disable-next-line new-cap
    await triggerEvent('.rsa-resizer', 'mousedown', { clientX: x, clientY: y });


    // Simulate a dragmove by moving the mouse.
    // eslint-disable-next-line new-cap
    await triggerEvent(document.body, 'mousemove');

    // eslint-disable-next-line new-cap
    await triggerEvent(document.body, 'mousemove', { clientX: x + deltaX, clientY: y + deltaY });
    await triggerEvent(document.body, 'mouseup');
    // $(document.body).off('mousemove');

  });
});