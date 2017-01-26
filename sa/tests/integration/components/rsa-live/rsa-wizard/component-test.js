import Ember from 'ember';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';
import WizardStepMixin from 'sa/components/rsa-live/rsa-wizard/wizard-step';
import { and } from 'ember-computed-decorators';

moduleForComponent('rsa-live/rsa-wizard', 'Integration | Component | rsa wizard', {
  integration: true
});

const {
    Component,
    assign,
    getOwner,
    observer
} = Ember;

function nextStep($) {
  $('.next-step button').click();
}

function previousStep($) {
  $('.previous-step button').click();
}

function registerInvalidSteppableComponent(context, name = 'invalid-step', opts = {}) {
  const owner = getOwner(context);
  const options = assign({ tagName: 'article' }, opts);

  const InvalidStepComponent = Component.extend(WizardStepMixin, options, {
    isCool: true,
    isNice: true,
    visibleChanged: observer('isVisible', function() {
      this.set('isCool', false);
    }),

    @and('isCool', 'isNice') isValid: null, // false
    didInsertElement() {
      this.$().append('<div>Invalid</div>');
    }
  });

  unregisterSteppableComponent(context, name = 'invalid-step');
  owner.register(`component:${name}`, InvalidStepComponent);
}

function registerSteppableComponent(context, name = 'wizard-component', appendedContent = 'hello world', opts = {}) {
  const owner = getOwner(context);
  const options = assign({ tagName: 'article' }, opts);
  const DummyComponent = Component.extend(WizardStepMixin, options, {
    didInsertElement() {
      this.$().append(`<div>${appendedContent}</div>`);
    }
  });

  unregisterSteppableComponent(context);
  owner.register(`component:${name}`, DummyComponent);
}

function unregisterSteppableComponent(context, name = 'wizard-component') {
  const owner = getOwner(context);

  if (owner.resolveRegistration(`component:${name}`)) {
    owner.unregister(`component:${name}`);
  }
}

test('The Wizard component is rendered with basic expected DOM elements', function(assert) {
  registerSteppableComponent(this);
  this.render(hbs`
  {{#rsa-live/rsa-wizard as |workflow|}}
    {{wizard-component name="Review Resources" stepNumber=1 workflow=workflow }}
    {{wizard-component name="Select Services" stepNumber=2 workflow=workflow }}
  {{/rsa-live/rsa-wizard}}`);

  return wait().then(() => {
    assert.equal(this.$('.rsa-wizard').length, 1, 'The wizard wrapper is rendered');
    assert.equal(this.$('article').length, 2, 'Each wizard step component is rendered');
    assert.equal(this.$('button').length, 2, 'There are two buttons');
  });
});

test('The Wizard\'s cancel action is executed via the cancel button', function(assert) {
  assert.expect(1);
  registerSteppableComponent(this);

  this.actions = {};
  this.actions.externalAction = () => {
    assert.ok(true);
  };
  this.render(hbs`
  {{#rsa-live/rsa-wizard cancel=(action 'externalAction') as |workflow|}}
    {{wizard-component name="Review Resources" stepNumber=1 workflow=workflow }}
    {{wizard-component name="Select Services" stepNumber=2 workflow=workflow }}
  {{/rsa-live/rsa-wizard}}`);

  return wait().then(() => {
    this.$('.cancel-wizard button').click();
  });
});

test('The Wizard progress bar shows the proper number of steps', function(assert) {
  registerSteppableComponent(this);
  this.render(hbs`
  {{#rsa-live/rsa-wizard as |workflow|}}
    {{wizard-component name="Buy Ingredients" stepNumber=1 workflow=workflow }}
    {{wizard-component name="Prepare Ingredients" stepNumber=2 workflow=workflow }}
    {{wizard-component name="Cook" stepNumber=3 workflow=workflow }}
  {{/rsa-live/rsa-wizard}}`);

  return wait().then(() => {
    const progressBreadCrumbSteps = this.$('.rsa-wizard-progress li > span');
    assert.equal(progressBreadCrumbSteps.length, 3, 'There are three steps in the progress breadcrumb.');
    assert.equal(this.$(progressBreadCrumbSteps[0]).text().trim(), 'Buy Ingredients', 'The appropriate name is displayed for the step');
    assert.equal(this.$(progressBreadCrumbSteps[1]).text().trim(), 'Prepare Ingredients', 'The appropriate name is displayed for the step');
    assert.equal(this.$(progressBreadCrumbSteps[2]).text().trim(), 'Cook', 'The appropriate name is displayed for the step');
  });
});

test('The Wizard progress bar shows each step as visited on arrival', function(assert) {
  registerSteppableComponent(this);
  this.render(hbs`
  {{#rsa-live/rsa-wizard as |workflow|}}
    {{wizard-component name="Buy Ingredients" stepNumber=1 workflow=workflow }}
    {{wizard-component name="Prepare Ingredients" stepNumber=2 workflow=workflow }}
    {{wizard-component name="Cook" stepNumber=3 workflow=workflow }}
  {{/rsa-live/rsa-wizard}}`);

  return wait().then(() => {
    const progressBreadCrumbSteps = this.$('.rsa-wizard-progress li');
    assert.equal(this.$(progressBreadCrumbSteps[0]).hasClass('visited'), true, 'The first step has the "visited" class by default');
    assert.equal(this.$(progressBreadCrumbSteps[1]).hasClass('visited'), false, 'By default, non-first steps do not have the "visited" class');
    assert.equal(this.$(progressBreadCrumbSteps[2]).hasClass('visited'), false, 'By default, non-first steps do not have the "visited" class');

    nextStep(this.$);

    assert.equal(this.$(progressBreadCrumbSteps[0]).hasClass('visited'), true, 'The first step has the "visited" class');
    assert.equal(this.$(progressBreadCrumbSteps[1]).hasClass('visited'), true, 'Second step has the visited class');
    assert.equal(this.$(progressBreadCrumbSteps[2]).hasClass('visited'), false, 'Third step does not have the visited class');

    nextStep(this.$);

    assert.equal(this.$(progressBreadCrumbSteps[0]).hasClass('visited'), true, 'The first step has the "visited" class');
    assert.equal(this.$(progressBreadCrumbSteps[1]).hasClass('visited'), true, 'Second step has the visited class');
    assert.equal(this.$(progressBreadCrumbSteps[2]).hasClass('visited'), true, 'Third step has the visited class');
  });
});

test('The Wizard buttons appear with the default labels', function(assert) {
  registerSteppableComponent(this);
  this.render(hbs`
  {{#rsa-live/rsa-wizard as |workflow|}}
    {{wizard-component name="Buy Ingredients" stepNumber=1 workflow=workflow }}
    {{wizard-component name="Prepare Ingredients" stepNumber=2 workflow=workflow }}
    {{wizard-component name="Cook" stepNumber=3 workflow=workflow }}
  {{/rsa-live/rsa-wizard}}`);


  return wait().then(() => {
    assert.equal(this.$('.next-step button').text().trim(), 'Next', 'The default label for the next button is "Next"');
    assert.equal(this.$('.cancel-wizard button').text().trim(), 'Cancel', 'The default label for the cancel button is "Cancel"');
    nextStep(this.$);
    assert.equal(this.$('.previous-step button').text().trim(), 'Previous', 'The default label for the previous button is "Previous"');
    nextStep(this.$);
    assert.equal(this.$('.finish-wizard button').text().trim(), 'Finish', 'The default label for the finish button is "Finish"');
  });
});

test('The Wizard buttons appear with the default labels', function(assert) {
  registerSteppableComponent(this);
  this.render(hbs`
  {{#rsa-live/rsa-wizard nextLabel="Keep Going" previousLabel="Back!" cancelLabel="Stop!" finishLabel="Done" as |workflow|}}
    {{wizard-component name="Buy Ingredients" stepNumber=1 workflow=workflow }}
    {{wizard-component name="Prepare Ingredients" stepNumber=2 workflow=workflow }}
    {{wizard-component name="Cook" stepNumber=3 workflow=workflow }}
  {{/rsa-live/rsa-wizard}}`);


  return wait().then(() => {
    assert.equal(this.$('.next-step button').text().trim(), 'Keep Going', 'The override label for the next button is "Keep Going"');
    assert.equal(this.$('.cancel-wizard button').text().trim(), 'Stop!', 'The override label for the cancel button is "Stop!"');
    nextStep(this.$);
    assert.equal(this.$('.previous-step button').text().trim(), 'Back!', 'The override label for the previous button is "Back!"');
    nextStep(this.$);
    assert.equal(this.$('.finish-wizard button').text().trim(), 'Done', 'The override label for the finish button is "Done!"');
  });
});

test('The Wizard buttons appear and disappear appropriately based on current step', function(assert) {
  registerSteppableComponent(this);
  this.render(hbs`
  {{#rsa-live/rsa-wizard as |workflow|}}
    {{wizard-component name="Buy Ingredients" stepNumber=1 workflow=workflow }}
    {{wizard-component name="Prepare Ingredients" stepNumber=2 workflow=workflow }}
    {{wizard-component name="Cook" stepNumber=3 workflow=workflow }}
  {{/rsa-live/rsa-wizard}}`);

  const stepOneButtonAssertions = () => {
    assert.equal(this.$('button').length, 2, 'There are two buttons on the first step');
    assert.equal(this.$('.next-step button').length, 1, 'There is one "Next" button');
    assert.equal(this.$('.cancel-wizard button').length, 1, 'There is one "Cancel" button');
  };

  const stepTwoButtonAssertions = () => {
    assert.equal(this.$('button').length, 3, 'There are three buttons on the second step');
    assert.equal(this.$('.next-step button').length, 1, 'There is one "Next" button');
    assert.equal(this.$('.previous-step button').length, 1, 'There is one "Previous" button');
    assert.equal(this.$('.cancel-wizard button').length, 1, 'There is one "Cancel" button');
  };

  const stepThreeButtonAssertions = () => {
    assert.equal(this.$('button').length, 3, 'There are three buttons on the third step');
    assert.equal(this.$('.finish-wizard button').length, 1, 'There is one "Finish" button');
    assert.equal(this.$('.previous-step button').length, 1, 'There is one "Previous" button');
    assert.equal(this.$('.cancel-wizard button').length, 1, 'There is one "Cancel" button');
  };

  return wait().then(() => {
    stepOneButtonAssertions();
    nextStep(this.$);
    stepTwoButtonAssertions();
    nextStep(this.$);
    stepThreeButtonAssertions();
    previousStep(this.$);
    stepTwoButtonAssertions();
    previousStep(this.$);
    stepOneButtonAssertions();
  });
});

test('The Wizard only shows the content for each step', function(assert) {
  registerSteppableComponent(this, 'wizard-component-one', '<div class="wizard-one">wizard one</div>');
  registerSteppableComponent(this, 'wizard-component-two', '<div class="wizard-two">wizard two</div>');
  registerSteppableComponent(this, 'wizard-component-three', '<div class="wizard-three">wizard three</div>');
  this.render(hbs`
  {{#rsa-live/rsa-wizard as |workflow|}}
    {{wizard-component-one name="Buy Ingredients" stepNumber=1 workflow=workflow }}
    {{wizard-component-two name="Prepare Ingredients" stepNumber=2 workflow=workflow }}
    {{wizard-component-three name="Cook" stepNumber=3 workflow=workflow }}
  {{/rsa-live/rsa-wizard}}`);

  return wait().then(() => {
    assert.equal(this.$('.wizard-one').is(':visible'), true, 'The first step\'s content is visible by default');
    assert.equal(this.$('.wizard-two').is(':visible'), false, 'The second step\'s content is NOT visible by default');
    assert.equal(this.$('.wizard-three').is(':visible'), false, 'The third step\'s content is NOT visible by default');

    nextStep(this.$);

    assert.equal(this.$('.wizard-one').is(':visible'), false, 'The first step\'s content is NOT visible');
    assert.equal(this.$('.wizard-two').is(':visible'), true, 'The second step\'s content is visible');
    assert.equal(this.$('.wizard-three').is(':visible'), false, 'The third step\'s content is NOT visible');

    nextStep(this.$);

    assert.equal(this.$('.wizard-one').is(':visible'), false, 'The first step\'s content is NOT visible');
    assert.equal(this.$('.wizard-two').is(':visible'), false, 'The second step\'s content is NOT visible');
    assert.equal(this.$('.wizard-three').is(':visible'), true, 'The third step\'s content is visible');
  });
});

test('A Wizard Step that is invalid has a disabled "Next" button', function(assert) {
  registerSteppableComponent(this);
  registerInvalidSteppableComponent(this);
  this.render(hbs`
  {{#rsa-live/rsa-wizard as |workflow|}}
    {{wizard-component name="Buy Ingredients" stepNumber=1 workflow=workflow }}
    {{invalid-step name="Prepare Ingredients" stepNumber=2 workflow=workflow }}
    {{wizard-component name="Cook" stepNumber=3 workflow=workflow }}
  {{/rsa-live/rsa-wizard}}`);

  return wait().then(() => {
    assert.equal(this.$('.next-step button').is(':disabled'), false, 'The first step\'s next button is not disabled.');
    nextStep(this.$);
    assert.equal(this.$('.next-step button').is(':disabled'), true, 'The second step\'s next button is disabled.');
  });
});

test('The Wizard progress bar shows an invalid step with the class "invalid"', function(assert) {
  registerSteppableComponent(this);
  this.render(hbs`
  {{#rsa-live/rsa-wizard as |workflow|}}
    {{wizard-component name="Buy Ingredients" stepNumber=1 workflow=workflow }}
    {{wizard-component isValid=false name="Prepare Ingredients" stepNumber=2 workflow=workflow }}
    {{wizard-component name="Cook" stepNumber=3 workflow=workflow }}
  {{/rsa-live/rsa-wizard}}`);

  return wait().then(() => {
    const invalidProgressSteps = this.$('.rsa-wizard-progress li.invalid');
    assert.equal(invalidProgressSteps.length, 1, 'There is one invalid step/class on the breadcrumb.');
  });
});
