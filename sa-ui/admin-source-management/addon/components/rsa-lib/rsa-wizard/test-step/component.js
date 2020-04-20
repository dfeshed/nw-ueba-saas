import Component from '@ember/component';

const TestStep1 = Component.extend({
  tagName: 'vbox',
  classNames: ['scroll-box', 'rsa-wizard-step'],
  classNameBindings: ['step.id'],

  // passed in by the wizard
  step: undefined
});

export default TestStep1;
