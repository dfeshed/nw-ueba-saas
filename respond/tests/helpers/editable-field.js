import wait from 'ember-test-helpers/wait';
import $ from 'jquery';

export function updateEditableField(scopeSelector, newValue) {
  const fieldSelector = `${scopeSelector} .editable-field .editable-field__value`;
  const inputSelector = `${scopeSelector} .editable-field input, ${scopeSelector} .editable-field textarea`;
  const confirmButtonSelector = `${scopeSelector} .editable-field .confirm-changes button`;
  $(fieldSelector).click();
  return wait().then(() => {
    $(inputSelector).val(newValue).change();
    return wait();
  })
  .then(() => {
    $(confirmButtonSelector).click();
    return wait();
  });
}