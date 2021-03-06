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
// const VALID_HOSTNAME_REGEX = /(?=^.{1,254}$)(^(?:(?!\d+\.|-)[a-zA-Z0-9_\-]{1,63}(?<!-)\.?)+(?:[a-zA-Z]{2,})$)/;
const VALID_HOSTNAME_REGEX = /^([^~`!@#$%^&*()[\]{}:;'"?/<>.\-_\s,][^~`!@#$%^&*()[\]{}:;'"?/<>\s,]{0,253}){0,1}[^~`!@#$%^&*()[\]{}:;'"?/<>.\-_\s,]$/;
const VALID_HOSTNAME_REGEX_STARTS_WITH = /^[^~`!@#$%^&*()[\]{}:;'"?/<>.\-_\s,][^~`!@#$%^&*()[\]{}:;'"?/<>\s,]{0,254}$/;
const VALID_HOSTNAME_REGEX_ENDS_WITH = /^[^~`!@#$%^&*()[\]{}:;'"?/<>\s,]{0,254}[^~`!@#$%^&*()[\]{}:;'"?/<>.\-_\s,]$/;
const VALID_HOSTNAME_REGEX_CONTAINS = /^[^~`!@#$%^&*()[\]{}:;'"?/<>\s,]{1,255}$/;

// Ipv4 sources:
// https://www.oreilly.com/library/view/regular-expressions-cookbook/9781449327453/ch08s16.html
const VALID_IPV4_REGEX = /^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/;

// IPv6 sources:
// http://shop.oreilly.com/product/0636920023630.do
// https://stackoverflow.com/questions/23483855/javascript-regex-to-validate-ipv4-and-ipv6-address-no-hostnames
const VALID_IPV6_REGEX = /^((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:)))(%.+)?$/;

const groupExpressionValidator = (value, validation, trimInput, visited) => {
  let inputValid = false;

  if (isPresent(validation) && (validation === 'none')) {
    // No value needs to be present for 'none'
    inputValid = true;

  } else if (isPresent(validation) && isPresent(value) && isArray(value)) {
    // value is an array of more than one value, so validate each item
    for (let index = 0; index < value.length; index++) {
      inputValid = !groupExpressionValidator(value[index], validation, trimInput, visited).isError;
      if (!inputValid) {
        break;
      }
    }
  } else if (isPresent(validation) && isPresent(value) && !isArray(value) && !isEmpty(value)) {
    // These items all require a value to be a single value and not empty

    // trim the input if required
    if (trimInput) {
      value = value.trim();
    }

    // Validate based on the type of validation
    switch (validation) {
      case 'none':
        inputValid = true;
        break;
      case 'notEmpty':
        inputValid = !isBlank(value);
        break;
      case 'maxLength255':
        inputValid = (value.length <= 255);
        break;
      case 'validHostname':
        // PO request to keep hostname validation simplier
        // so we just match likely characters
        inputValid = (VALID_HOSTNAME_REGEX.test(value));
        break;
      case 'validHostnameStartsWith':
        inputValid = (VALID_HOSTNAME_REGEX_STARTS_WITH.test(value));
        break;
      case 'validHostnameEndsWith':
        inputValid = (VALID_HOSTNAME_REGEX_ENDS_WITH.test(value));
        break;
      case 'validHostnameContains':
        inputValid = (VALID_HOSTNAME_REGEX_CONTAINS.test(value));
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

const sortBy = function(field, descending, primer) {
  const key = primer ?
    function(x) {
      return primer(x[field]);
    } :
    function(x) {
      return x[field];
    };
  descending = !descending ? 1 : -1;
  return function(a, b) {
    return a = key(a), b = key(b), descending * ((a > b) - (b > a));
  };
};

export {
  exceedsLength,
  isNameInList,
  groupExpressionValidator,
  descriptionsForDisplay,
  sortBy,
  VALID_HOSTNAME_REGEX,
  VALID_IPV4_REGEX,
  VALID_IPV6_REGEX
};
