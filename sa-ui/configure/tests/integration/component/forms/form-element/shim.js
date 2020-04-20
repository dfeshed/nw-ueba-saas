import { get } from '@ember/object';
import FormElementComponent from 'configure/components/forms/form-element/component';

const ENTER_KEY = 13;

// When the enter key is pressed from an input element, to submit the form, triggering the
// keyboard event is only part of the required solution to simulate what happens in browser.
// An event handler also needs to be added to the form component in order for the enter keyboard
// event triggered by triggerKeyEvent to fire (required for tests to simulate the browser enviornment)
const FormElement = FormElementComponent.extend({
  keyDown(e) {
    if (e.keyCode === ENTER_KEY) {
      const changeset = get(this, 'changeset');
      this.send('save', changeset);
    }
  }
});

export { FormElement };
