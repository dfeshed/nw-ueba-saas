import Ember from 'ember';
import { moduleForComponent, test, skip } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import { clickTrigger } from '../../../../helpers/ember-power-select';
import wait from 'ember-test-helpers/wait';
import sinon from 'sinon';

let dispatchSpy;

const { $, getOwner } = Ember;

const options = [
  {
    a: 'h.granger',
    b: 'Hermione Granger'
  },
  {
    a: 'r.weasley',
    b: 'Ron Weasley'
  },
  {
    a: 'h.potter',
    b: 'Harry Potter'
  }
];

moduleForComponent('rsa-incident-toolbar', 'Integration | Component | Incident Update Action Button', {
  integration: true,
  resolver: engineResolverFor('respond'),
  beforeEach() {
    // inject and handle redux
    this.inject.service('redux');
    const redux = this.get('redux');
    dispatchSpy = sinon.spy(redux, 'dispatch');

    const typesUsed = ['info', 'warning', 'success', 'error'];
    getOwner(this).lookup('service:flash-messages').registerTypes(typesUsed);
  },
  afterEach() {
    dispatchSpy.restore();
  }
});

function triggerMouseEvent(node, eventType) {
  const clickEvent = document.createEvent('MouseEvents');
  clickEvent.initEvent(eventType, true, true);
  node.dispatchEvent(clickEvent);
}

test('it renders', function(assert) {
  this.render(hbs`{{rsa-incident/update-action-button}}`);
  return wait().then(() => {
    const $el = this.$('.incident-action-button');
    assert.equal($el.length, 1, 'Expected to find toolbar root element in DOM.');
    const $embeddedButton = this.$('.incident-action-button .ember-power-select-trigger .rsa-form-button-wrapper.is-standard');
    assert.equal($embeddedButton.length, 1, 'A standard styled embedded button is the trigger');
  });
});


test('it renders the label property as the button text', function(assert) {
  this.render(hbs`{{rsa-incident/update-action-button label='Do not press this button'}}`);
  return wait().then(() => {
    const $el = this.$('.incident-action-button .rsa-form-button');
    assert.equal($el.text().trim(), 'Do not press this button', 'The label property dictates the button text');
  });
});

test('the label is not rendered when isIconOnly is true', function(assert) {
  this.render(hbs`{{rsa-incident/update-action-button isIconOnly=true label='Do not press this button'}}`);
  return wait().then(() => {
    const $el = this.$('.incident-action-button .rsa-form-button');
    assert.equal($el.text().trim(), '', 'The label is not shown in the button when isIconOnly is true');
  });
});

test('it opens the ember-power-select dropdown on click', function(assert) {
  this.render(hbs`{{rsa-incident/update-action-button}}`);
  return wait().then(() => {
    clickTrigger();
    assert.equal($('.ember-power-select-option.ember-power-select-option--no-matches-message').length, 1,
      'The Power Select dropdown opens with 1 item indicating no results were found, since there are no options in the dropdwon');
    assert.equal($('.ember-power-select-search').length, 1,
      'The dropdown has the search box');
  });
});

test('it does not open a dropdown on click when isDisabled is true ', function(assert) {
  this.render(hbs`{{rsa-incident/update-action-button isDisabled=true}}`);
  return wait().then(() => {
    clickTrigger();
    assert.equal($('.ember-power-select-option').length, 0,
      'The dropdown does not open when attribute isDisabled is true');
  });
});

test('the search box in the dropdown can be decativated using searchEnabled = false', function(assert) {
  this.set('searchEnabled', false);
  this.render(hbs`{{rsa-incident/update-action-button searchEnabled=searchEnabled}}`);
  return wait().then(() => {
    clickTrigger();
    assert.equal($('.ember-power-select-search').length, 0,
      'The dropdown does not have the search box');
  });
});

test('options are shown on click in the dropdown using the optionLabelProperty as the lookup', function(assert) {
  this.set('options', options);
  this.render(hbs`{{rsa-incident/update-action-button options=options optionLabelProperty='b'}}`);
  return wait().then(() => {
    clickTrigger();
    const selectOptions = $('.ember-power-select-option');
    assert.equal(selectOptions.length, 3, 'The dropdown has 3 options in it');
    assert.equal($(selectOptions[0]).text().trim(), 'Hermione Granger', 'The dropdown option shows the correct name');
    assert.equal($(selectOptions[1]).text().trim(), 'Ron Weasley', 'The dropdown option shows the correct name');
    assert.equal($(selectOptions[2]).text().trim(), 'Harry Potter', 'The dropdown option shows the correct name');
  });
});

test('selectedValue preselects the associated option (via optionValueProperty)', function(assert) {
  this.set('options', options);
  this.render(hbs`
    {{rsa-incident/update-action-button
      options=options
      optionLabelProperty='b'
      optionValueProperty='a'
      selectedValue='h.potter'}}`);
  return wait().then(() => {
    clickTrigger();
    const [firstOption, secondOption, thirdOption] = $('.ember-power-select-option'); // the third option should be harry potter
    assert.equal($(firstOption).attr('aria-selected'), 'false', 'The first option (Hermione Granger) is not preselected');
    assert.equal($(secondOption).attr('aria-selected'), 'false', 'The third option (Ron Weasley) is not preselected');
    assert.equal($(thirdOption).attr('aria-selected'), 'true', 'The third option (Harry Potter) is preselected');
  });
});

skip('clicking on an option dispatches an action', function(assert) {
  this.set('options', options);
  this.render(hbs`
    {{rsa-incident/update-action-button
      fieldName="priority"
      incidentId='INC-123'
      options=options
      optionValueProperty='a'
      optionLabelProperty='b'}}`);
  return wait().then(() => {
    clickTrigger();
    const [ option ] = $('.ember-power-select-option').first();
    triggerMouseEvent(option, 'mouseover');
    triggerMouseEvent(option, 'mousedown');
    triggerMouseEvent(option, 'mouseup');
    triggerMouseEvent(option, 'click');

    assert.ok(dispatchSpy.callCount);
  });
});