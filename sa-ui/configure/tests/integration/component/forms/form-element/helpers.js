import { click } from '@ember/test-helpers';
import { selectors } from './selectors';

export const fieldsetSync = function(index) {
  const fieldsets = document.querySelectorAll(selectors.fieldset);
  const fieldset = fieldsets[index - 1];
  const legend = fieldset.querySelector(selectors.legend);
  const fieldsetInputs = fieldset.querySelectorAll(selectors.fieldsetInput);
  const [ inputGroupOne, inputGroupTwo ] = fieldsetInputs;
  const labelOne = inputGroupOne.querySelector(selectors.fieldsetLabel);
  const radioShapeOne = labelOne.querySelector(selectors.radioShape);
  const radioOne = inputGroupOne.querySelector(selectors.radioInput);
  const labelTwo = inputGroupTwo.querySelector(selectors.fieldsetLabel);
  const radioShapeTwo = labelTwo.querySelector(selectors.radioShape);
  const radioTwo = inputGroupTwo.querySelector(selectors.radioInput);
  return {
    legend,
    labelOne,
    radioOne,
    inputGroupOne,
    radioShapeOne,
    labelTwo,
    radioTwo,
    inputGroupTwo,
    radioShapeTwo
  };
};

export const formGroupExists = function(index) {
  const groups = document.querySelectorAll(`${selectors.formValidation} > :not(fieldset)`);
  return groups[index - 1] !== undefined;
};

export const formGroupSync = function(index) {
  const groups = document.querySelectorAll(`${selectors.formValidation} > :not(fieldset)`);
  const group = groups[index - 1];
  const validation = group.parentElement;
  const inlineMessages = group.querySelectorAll(selectors.formGroupValidationMessage);
  const label = group.querySelector(selectors.formLabel);
  const input = group.querySelector(selectors.formInput);
  const groupValidation = group.querySelector(selectors.formGroupValidation);
  return {
    group,
    validation,
    inlineMessages,
    label,
    input,
    groupValidation
  };
};

export const formGroup = async function(index) {
  const { group, validation, inlineMessages, label, input, groupValidation } = formGroupSync(index);
  const select = group.querySelector(selectors.powerSelect);
  let options = [];
  let selectedItem;
  if (select) {
    selectedItem = group.querySelector(selectors.powerSelectItem);
    await click(select);
    options = document.querySelectorAll(`${selectors.powerSelectOptions} li`);
  }
  return {
    group,
    validation,
    inlineMessages,
    groupValidation,
    label,
    input,
    select,
    options,
    selectedItem
  };
};
