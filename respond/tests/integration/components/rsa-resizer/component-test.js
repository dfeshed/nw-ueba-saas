import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';
import wait from 'ember-test-helpers/wait';
import $ from 'jquery';

moduleForComponent('rsa-resizer', 'Integration | Component | rsa resizer', {
  integration: true,
  resolver: engineResolverFor('respond')
});

test('it renders', function(assert) {
  this.render(hbs`{{rsa-resizer}}`);

  return wait().then(() => {
    assert.equal(this.$('.rsa-resizer').length, 1, 'Expected to find root DOM node.');
  });
});

test('it fires its resizeAction when a dragmove occurs', function(assert) {
  assert.expect(2);

  const x = 100;
  const y = 200;
  const deltaX = 3;
  const deltaY = 4;
  const done = assert.async();
  let $el;

  const resizeAction = (a, b) => {
    assert.equal(a, x + deltaX, 'Expected resizeAction to receive a numeric x param');
    assert.equal(b, y + deltaY, 'Expected resizeAction to receive a numeric y param');
  };

  this.setProperties({
    x,
    y,
    resizeAction
  });

  this.render(hbs`{{rsa-resizer x=x y=y resizeAction=resizeAction}}`);

  return wait()
    .then(() => {
      $el = this.$('.rsa-resizer');
      // Simulate a mousedown to start the drag.
      // eslint-disable-next-line new-cap
      const evt = $.Event('mousedown');
      evt.pageX = x;
      evt.pageY = y;
      $el.trigger(evt);
      return wait();
    })
    .then(() => {
      // Simulate a dragmove by moving the mouse.
      // eslint-disable-next-line new-cap
      const evt2 = $.Event('mousemove');
      $(document.body).trigger(evt2);

      // eslint-disable-next-line new-cap
      const evt3 = $.Event('mousemove');
      evt3.pageX = x + deltaX;
      evt3.pageY = y + deltaY;
      $(document.body).trigger(evt3);
      return wait();
    })
    .then(() => {
      // Teardown test.
      $(document.body).trigger('mouseup');
      $(document.body).off('mousemove');
      done();
    });
});
