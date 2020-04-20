import { click, fillIn } from '@ember/test-helpers';

export async function updateEditableField(scopeSelector, newValue) {
  const fieldSelector = `${scopeSelector} .editable-field .editable-field__value`;
  const inputSelector = `${scopeSelector} .editable-field input, ${scopeSelector} .editable-field textarea`;
  const confirmButtonSelector = `${scopeSelector} .editable-field .confirm-changes button`;
  await click(fieldSelector);
  await fillIn(inputSelector, newValue);
  await click(confirmButtonSelector);
}