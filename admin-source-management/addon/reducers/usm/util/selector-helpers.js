import { isPresent, isBlank, isEmpty } from '@ember/utils';
import { isArray } from '@ember/array';
import _ from 'lodash';

const exceedsLength = (name, maxLength) => {
  if (isPresent(name) && isPresent(maxLength)) {
    return (name.length > maxLength);
  } else {
    return false;
  }
};

const isNameInList = (list, id, name) => {
  if (!isEmpty(list) && isPresent(name)) {
    const lowerCaseName = name.toLocaleLowerCase();
    for (let l = 0; l < list.length; l++) {
      const listItem = list[l];
      if ((!id || (listItem.id !== id)) && (listItem.name.toLocaleLowerCase() === lowerCaseName)) {
        return true;
      }
    }
  }
  return false;
};

// Validators for group expressions
// -------------------------------------------------------------------------------
// hostname sources:
// http://regexlib.com/Search.aspx?k=host
// https://en.wikipedia.org/wiki/Hostname
// const VALID_HOSTNAME_REGEX = /(?=^.{1,254}$)(^(?:(?!\d+\.|-)[a-zA-Z0-9_\-]{1,63}(?<!-)\.?)+(?:[a-zA-Z]{2,})$)/;  // orginal required 3 chars min
const VALID_HOSTNAME_REGEX = /^([a-zA-Z0-9]([a-zA-Z0-9\-]{0,61}[a-zA-Z0-9])?\.)*[a-zA-Z0-9]([a-zA-Z0-9\-]{0,61}[a-zA-Z0-9])?$/;
const VALID_HOSTNAMECHARS_REGEX = /^[a-zA-Z0-9\-\.]{1,63}$/;

// Ipv4 sources:
// https://www.oreilly.com/library/view/regular-expressions-cookbook/9781449327453/ch08s16.html
const VALID_IPV4_REGEX = /^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/;

// IPv6 sources:
// http://shop.oreilly.com/product/0636920023630.do
// https://stackoverflow.com/questions/23483855/javascript-regex-to-validate-ipv4-and-ipv6-address-no-hostnames
const VALID_IPV6_REGEX = /^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$|^(([a-zA-Z]|[a-zA-Z][a-zA-Z0-9\-]*[a-zA-Z0-9])\.)*([A-Za-z]|[A-Za-z][A-Za-z0-9\-]*[A-Za-z0-9])$|^\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:)))(%.+)?$/;

const groupExpressionValidator = (value, validation, visited) => {
  let inputValid = false;

  if (isPresent(validation) && (validation === 'none')) {
    // No value needs to be present for 'none'
    inputValid = true;

  } else if (isPresent(validation) && isPresent(value) && isArray(value)) {
    // value is an array of more than one value, so validate each item
    for (let index = 0; index < value.length; index++) {
      inputValid = !groupExpressionValidator(value[index], validation, visited).isError;
      if (!inputValid) {
        break;
      }
    }
  } else if (isPresent(validation) && isPresent(value) && !isArray(value) && !isEmpty(value)) {
    // These items all require a value to be a single value and not empty
    switch (validation) {
      case 'none':
        inputValid = true;
        break;
      case 'notEmpty':
        inputValid = !isBlank(value);
        break;
      case 'maxLength256':
        inputValid = (value.length <= 256);
        break;
      case 'validHostname':
        inputValid = (VALID_HOSTNAME_REGEX.test(value));
        break;
      case 'validHostnameChars':
        inputValid = (VALID_HOSTNAMECHARS_REGEX.test(value));
        break;
      case 'validHostnameList':
        {
          const items = value.split(/[ ,\t\n]+/);
          for (let index = 0; index < items.length; index++) {
            if (!isBlank(items[index])) {
              inputValid = VALID_HOSTNAME_REGEX.test(items[index]);
              if (!inputValid) {
                break;
              }
            }
          }
        }
        break;
      case 'validIPv4':
        inputValid = (VALID_IPV4_REGEX.test(value));
        break;
      case 'validIPv4List':
        {
          // ex. value[0] = "1.2.3.4,2.3.4.5     3.4.5.6,,,,, 4.5.6.7\n5.6.7.8"
          const items = value.split(/[ ,\t\n]+/);
          for (let index = 0; index < items.length; index++) {
            if (!isBlank(items[index])) {
              inputValid = VALID_IPV4_REGEX.test(items[index]);
              if (!inputValid) {
                break;
              }
            }
          }
        }
        break;
      case 'validIPv6':
        inputValid = (VALID_IPV6_REGEX.test(value));
        break;
      case 'validIPv6List':
        {
          const items = value.split(/[ ,\t\n]+/);
          for (let index = 0; index < items.length; index++) {
            if (!isBlank(items[index])) {
              inputValid = VALID_IPV6_REGEX.test(items[index]);
              if (!inputValid) {
                break;
              }
            }
          }
        }
        break;
      default:
        break;
    }
  }
  return {
    isError: !inputValid,
    showError: visited ? !inputValid : false
  };
};

/**
 * Takes a complete description and returns an object containing two truncated versions of the description
 * {
 *   truncated : 'Truncated to 256 characters without an omission indicator',
 *   truncatedWithEllipsis 'Truncated to 256 characters with an ellipsis as the omission indicator...'
 * }
 * @param {*} description
 * @public
 */
const descriptionsForDisplay = (description) => {
  return {
    truncated: description ? _.truncate(description, { length: 256, omission: '' }) : null,
    truncatedWithEllipsis: description ? _.truncate(description, { length: 256, omission: '...' }) : null
  };
};

export {
  exceedsLength,
  isNameInList,
  groupExpressionValidator,
  descriptionsForDisplay
};
