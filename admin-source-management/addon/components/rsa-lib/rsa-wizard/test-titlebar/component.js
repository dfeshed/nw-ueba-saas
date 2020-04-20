import Component from '@ember/component';

const TestTitlebar = Component.extend({
  tagName: 'vbox',
  classNames: ['test-titlebar'],
  // step object required to be passed in
  step: null
});

export default TestTitlebar;
