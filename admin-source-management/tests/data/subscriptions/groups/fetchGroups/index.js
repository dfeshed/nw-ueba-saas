import _ from 'lodash';
import data from './data';

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

/*
  - multiple expressions are AND'd
  - values within expressions are OR'd

  example - policyType is edr or file AND publishStatus is published or unpublished_edits
*/
const applyFilter = (dataArray, criteria) => {
  const { expressionList } = criteria;
  const filteredArray = dataArray.filter((dataElement) => {
    for (let i = 0; i < expressionList.length; i++) {
      const fltrPropName = expressionList[i].propertyName;

      if (fltrPropName === 'sourceType') {
        const dataPolicyTypes = Object.keys(dataElement.assignedPolicies);
        const fltrPolicyTypes = expressionList[i].propertyValues.map((pVal) => pVal.value);
        const diffPolicyTypes = _.difference(dataPolicyTypes, fltrPolicyTypes);
        // difference returns dataPolicyTypes that are not in fltrPolicyTypes,
        // so if the lengths are the same there are no matches
        if (dataPolicyTypes.length === diffPolicyTypes.length) {
          return false;
        }
      }

      if (fltrPropName === 'publishStatus') {
        let dataPublishStatus = 'published';
        if (dataElement.lastPublishedOn === 0) {
          dataPublishStatus = 'unpublished';
        } else if (dataElement.dirty === true) {
          dataPublishStatus = 'unpublished_edits';
        }
        const fltrPublishStatusTypes = expressionList[i].propertyValues.map((pVal) => pVal.value);
        if (_.includes(fltrPublishStatusTypes, dataPublishStatus) === false) {
          return false;
        }
      }
    }
    // return true since this must have matched each expression in the criteria
    return true;
  });

  return filteredArray;
};

export default {
  subscriptionDestination: '/user/queue/usm/groups/search',
  requestDestination: '/ws/usm/groups/search',
  message(frame) {
    /*
      - this function mocks some of the sorting using single column
        and works on the non-composite sorted columns: name, description
      - this function also mocks filtering
    */
    const body = JSON.parse(frame.body);
    let fetchedData = data;
    /* eslint-disable */
    const sortColumn = body.data.sort.keys[0];
    const descending = body.data.sort.descending;
    const criteria = body.data.criteria;
    /* eslint-disable no-console */
    console.log('sortColumn=', sortColumn);
    console.log('descending=', descending);
    /* eslint-enable */
    switch (sortColumn) {
      case 'name':
      case 'description':
        fetchedData = data.sort(sortBy(sortColumn, descending, function(a) {
          return a.toUpperCase();
        }));
        break;
      case 'sourceCount':
        fetchedData = data.sort(sortBy(sortColumn, descending, parseInt));
        break;
      default:
        break;
    }
    // apply filter(s)
    if (criteria) {
      fetchedData = applyFilter(fetchedData, criteria);
    }
    return {
      data: {
        items: fetchedData,
        totalItems: fetchedData.length
      }
    };
  }
};
