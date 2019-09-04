import { next } from '@ember/runloop';

import { filterValidMeta } from 'investigate-events/util/meta';
import {
  NO_RESULTS_MESSAGE_SELECTOR,
  LOADING_SPINNER_SELECTOR,
  POWER_SELECT_OPTIONS
} from 'investigate-events/constants/pill';

const LEADING_SPACES = /^[\s\uFEFF\xA0]+/;
/**
 * Static Object. Useful for assigning properties from query-pill.
 * Based on the what type of pill { meta, operator, value } is formed while toggling
 * from recentQueries tab, we need to activate the right components with either string (prepopulatedText)
 * or proper objects (selected).
 * There are a set number of use cases that would need these different types of properties sprinkled with.
 * Read more about these cases @determineNewComponentPropsFromPillData
 */
const propertiesMap = {
  meta: {
    object: {
      selectedMeta: null,
      isMetaActive: false,
      isMetaAutoFocused: true
    },
    string: {
      prepopulatedMetaText: null,
      selectedMeta: null,
      isMetaAutoFocused: true,
      isMetaActive: true,
      selectedOperator: null,
      isOperatorActive: false,
      valueString: null,
      isValueActive: false
    }
  },
  operator: {
    object: {
      selectedOperator: null,
      isOperatorActive: false
    },
    string: {
      prepopulatedOperatorText: null,
      selectedOperator: null,
      isOperatorActive: true,
      valueString: null,
      isValueActive: false

    }
  },
  value: {
    string: {
      valueString: null,
      isValueActive: true
    }
  }
};

/**
 *
 * @param {String} key: meta/operator/value
 * @param {Object/String} propObject: either a selected object or a string
 */
const _getProps = (key, propObject) => {
  const props = propertiesMap[key][typeof propObject];
  const porpsArr = Object.keys(props);
  // adds either selectedMeta, selectedOperator, valueString,
  // prepopulatedMetaText, prepopulatedOperatorText
  props[porpsArr[0]] = propObject;

  return props;
};

/**
 *
 * @param {Object} pillData
 * Constructs a property object based on pillData provided.
 * Cases covered:
 * 1. operator object, value
 * 2. meta object, operator object, value
 * 3. meta string, no operator, no value
 * 4. operator object, no value
 * 5. operator string, no value
 * 6. meta object, operator object, no value
 * 7. meta object, operator string, no value
 * 8. meta object, no operator, no value
 */
export const determineNewComponentPropsFromPillData = (pillData) => {
  let propertyObject = {};
  Object.keys(pillData).forEach((k) => {
    propertyObject = {
      ...propertyObject,
      ..._getProps(k, pillData[k])
    };
  });

  // Put them in pill-operator component as users typed
  // `action` and flipped
  if (typeof pillData.meta === 'object' && !pillData.operator) {
    propertyObject.isOperatorActive = true;
  }
  // Put them in pill-value component as users typed
  // `action =` and flipped
  // Exception are operators that do not accept values
  if (typeof pillData.operator === 'object' && !pillData.value) {
    if (pillData.operator.hasValue) {
      propertyObject.isValueActive = true;
      propertyObject.valueString = '';
    } else {
      propertyObject.isOperatorActive = true;
    }
  }

  return propertyObject;
};

/**
   * Function that power-select uses to make an autosuggest match. This function
   * looks at the meta's `metaName` and `displayName` properties for a match.
   * If it finds a match anywhere within those two strings, it's considered a
   * match.
   * @param {Object} meta A meta object
   * @param {string} input The search string
   * @return {number} The index of the string match
   * @private
   */
export const matcher = (meta, input) => {
  const _input = input.toLowerCase().replace(LEADING_SPACES, '');
  const _metaName = meta.metaName.toLowerCase();
  const _displayName = meta.displayName.toLowerCase();
  return _metaName.indexOf(_input) & _displayName.indexOf(_input);
};

/**
 * Function that takes in a string and returns a count of possible matches
 * excluding meta that are `isIndexedByNone` or metaName is `sessionid`
 * from the metaOptions array.
 * @param {Array} metaOptions Language
 * @param {String} input String that was typed inside query-pill
 */
export const resultsCount = (metaOptions, input) => {
  if (input.trim().length === 0) {
    return 0;
  }
  const count = metaOptions.filter(filterValidMeta).reduce((acc, meta) => {
    const num = matcher(meta, input);
    // do not include isIndexedByNone in count
    if (num >= 0) {
      acc++;
    }
    return acc;
  }, 0);
  return count;
};


// We need them here because the button has multiple
// clickable points
export const isSubmitClicked = (target) => {
  return target.classList.contains('execute-query-button') ||
    target.classList.contains('execute-query-spyglass') ||
    target.classList.contains('query-button-wrapper') ||
    (target.parentElement && isSubmitClicked(target.parentElement));
};

// ************************ DOM Manipulation *************************  //

export function removeNoResultsMessage(context) {
  addNoResultsMessage(' ', context);
}

export function addNoResultsMessage(message, context) {

  next(context, () => {
    if (context.get('isActive')) {
      const targetElement = document.querySelector(NO_RESULTS_MESSAGE_SELECTOR);
      const powerSelectOptions = document.querySelector(POWER_SELECT_OPTIONS);
      if (targetElement && targetElement.firstChild) {
        const newEl = targetElement.firstChild;
        const oldTextElement = targetElement.firstChild;

        newEl.textContent = message;
        targetElement.replaceChild(newEl, oldTextElement);
      } else if (powerSelectOptions) {
        // Insert a li node with the message
        // This is a case where value suggestions will always have a default option,
        // but we still need to display no results message.
        const fragment = createNoMessageFragment(message);
        powerSelectOptions.appendChild(fragment);
      }
    }
  });
}

const createNoMessageFragment = (message) => {
  const frag = document.createDocumentFragment();
  const textNode = document.createTextNode(message);

  const li = document.createElement('li');
  li.setAttribute('class', 'ember-power-select-option ember-power-select-option--no-matches-message');
  li.setAttribute('role', 'option');
  li.appendChild(textNode);

  frag.appendChild(li);
  return frag;

};

/**
 * Appends a spinner to power-select-options in dom
 */
export function addSpinnerToDOM(context) {

  next(context, () => {
    const targetElement = document.querySelector(POWER_SELECT_OPTIONS);

    if (targetElement) {
      const fragment = document.createDocumentFragment();

      const span = document.createElement('span');
      span.setAttribute('class', 'ember-power-select-loading-options-spinner');

      const div = document.createElement('div');
      div.setAttribute('class', 'rsa-loader is-medium');

      const divWheel = document.createElement('div');
      divWheel.setAttribute('class', 'rsa-loader__wheel');

      div.appendChild(divWheel);

      span.appendChild(div);

      fragment.appendChild(span);

      targetElement.appendChild(fragment);
    }
  });
}

export function addAndRemoveElements(propertyFlag, context) {
  if (context.get('isActive')) {
    const loadingSpinner = document.querySelector(LOADING_SPINNER_SELECTOR);
    if (propertyFlag) {

      removeNoResultsMessage(context);

      // call in progress, but there is no spinner
      // probably the first time, so add one
      if (!loadingSpinner) {
        addSpinnerToDOM(context);
      } else {
        // call in progress, but there is already a spinner present.
        // just style it.
        loadingSpinner.style.display = 'inline';
      }
    } else {
      // Hide the spinner when call gets completed.
      if (loadingSpinner) {
        loadingSpinner.style.display = 'none';
      }
      if (context.hasOwnProperty('triggerRecentQueryMaintenance')) {
        context.toggleProperty('triggerRecentQueryMaintenance');
      }
    }
  }
}
// **********************************************************************  //