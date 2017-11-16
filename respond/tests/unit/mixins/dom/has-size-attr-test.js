import EmberObject from 'ember-object';
import HasSizeAttr from 'respond/mixins/dom/has-size-attr';
import { module, skip } from 'qunit';
import wait from 'ember-test-helpers/wait';
import { later } from 'ember-runloop';

module('Unit | Mixin | dom/has size attr');

const MockClass = EmberObject.extend(HasSizeAttr);

// Pause (in millisec) required by the 3rd party `javascript-detect-element-resize` library before
// notifying listeners of a resize event.  That library apparently uses `requestAnimationFrame` to notify listeners,
// which means the listener callbacks are fired are async (and without calling `Ember.run` of course). So we
// have to workaround that by waiting with `Ember.run.later`.
const laterInterval = 2000;

skip('it works with default selector', function(assert) {
  assert.expect(5);

  const initialWidth = 100;
  const initialHeight = 200;
  const element = document.createElement('div');
  element.style.height = `${initialHeight}px`;
  element.style.width = `${initialWidth}px`;
  element.style.overflow = 'hidden';
  element.style.padding = '0px';
  element.style.border = '0px none';
  document.body.appendChild(element);

  const subject = MockClass.create({
    element,
    sizeThrottle: 0
  });
  assert.ok(subject);

  subject.didInsertElement();
  const done = assert.async();
  return wait()
    .then(() => {
      assert.equal(subject.get('size.innerHeight'), initialHeight, 'Expected innerHeight to initialize');
      assert.equal(subject.get('size.innerWidth'), initialWidth, 'Expected innerWidth to initialize');

      // Resize
      element.style.height = `${initialHeight * 2}px`;
      element.style.width = `${initialWidth * 2}px`;

      return wait();
    })
    .then(() => {
      later(() => {
        // Check if the client attrs were updated
        assert.equal(subject.get('size.innerHeight'), element.clientHeight, 'Expected innerHeight to update');
        assert.equal(subject.get('size.innerWidth'), element.clientWidth, 'Expected innerWidth to update');
        subject.willDestroyElement();
        done();

      }, laterInterval);
    });
});
