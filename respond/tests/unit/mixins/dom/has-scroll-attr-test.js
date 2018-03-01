import EmberObject from '@ember/object';
import HasScrollAttr from 'respond/mixins/dom/has-scroll-attr';
import { module, skip } from 'qunit';
import wait from 'ember-test-helpers/wait';

module('Unit | Mixin | dom/has scroll attr');

const MockClass = EmberObject.extend(HasScrollAttr);

skip('it works with default selector', function(assert) {
  assert.expect(5);

  const element = document.createElement('div');
  element.style.height = '30px';
  element.style.width = '30px';
  element.style.overflow = 'auto';
  document.body.appendChild(element);
  const child = document.createElement('div');
  child.style.height = '1000px';
  child.style.width = '1000px';
  child.style.overflow = 'hidden';
  element.appendChild(child);

  const initialScroll = { top: 10, left: 20 };
  element.scrollTop = initialScroll.top;
  element.scrollLeft = initialScroll.left;

  const subject = MockClass.create({
    element,
    scrollThrottle: 0
  });
  assert.ok(subject);

  subject.didInsertElement();
  return wait()
    .then(() => {
      assert.equal(subject.get('scroll.top'), initialScroll.top, 'Expected scroll.top to initialize');
      assert.equal(subject.get('scroll.left'), initialScroll.left, 'Expected scroll.left to initialize');

      // Manually scroll the DOM
      element.scrollTop = initialScroll.top * 2;
      element.scrollLeft = initialScroll.left * 2;

      return wait();
    })
    .then(() => {
      // Check if the scroll attrs were updated
      assert.equal(subject.get('scroll.top'), initialScroll.top * 2, 'Expected scroll.top to update');
      assert.equal(subject.get('scroll.left'), initialScroll.left * 2, 'Expected scroll.left to update');
      subject.willDestroyElement();
    });
});
