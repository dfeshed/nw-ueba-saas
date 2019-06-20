
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
    } else if (!pillData.operator.hasValue) {
      propertyObject.isOperatorActive = true;
    }
  }

  return propertyObject;
};