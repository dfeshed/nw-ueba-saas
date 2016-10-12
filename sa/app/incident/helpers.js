/**
 * @file Incident helper utilities
 * @public
 */
import Ember from 'ember';
import { incidentRiskThreshold } from 'sa/incident/constants';

const {
  isArray,
  isNone
} = Ember;

const _SOURCES_MAP = [
  {
    test(source) {
      return String(source).match(/event stream analysis/i);
    },
    short() {
      return 'ESA';
    }
  },
  {
    test(source) {
      return String(source).match(/ecat/i);
    },
    short() {
      return 'ECAT';
    }
  }];

export default {
  /**
   * @name sourceShortName
   * @description returns a parametrized shortname for each source, if there is no configuration for the source, the
   * initials are returned.
   * @public
   */
  sourceShortName(source) {
    let matchedRule = _SOURCES_MAP.find((rule) => rule.test(source));
    return matchedRule ? matchedRule.short(source) : source.match(/\b\w/g).join('');
  },

  /**
   * @name riskScoreToBadgeLevel
   * @description define the badge style [low, medium, high, danger] based on the incident' risk score
   * @public
   */
  riskScoreToBadgeLevel(riskScore) {
    if (riskScore < incidentRiskThreshold.LOW) {
      return 'low';
    } else if (riskScore < incidentRiskThreshold.MEDIUM) {
      return 'medium';
    } else if (riskScore < incidentRiskThreshold.HIGH) {
      return 'high';
    } else {
      return 'danger';
    }
  },

  /**
   * @description transforms a one dimension categories array into a 2 dimension array
   * @public
   */
  normalizeCategoryTags(categoryTags = []) {
    let normalizedCategories = [];

    categoryTags.forEach((category) => {
      let objParent = normalizedCategories.findBy('name', category.parent);

      if (!objParent) {
        objParent = {
          name: category.parent,
          children: []
        };
        normalizedCategories.pushObject(objParent);
      }

      objParent.children.pushObject(category);

    });

    return normalizedCategories;
  },

  /**
   * @description It returns a printable version of a IP array based on the input size:
   * - If zero elements or null reference is passed, it returns a '-'
   * - If the array has 1 element, it returns its value
   * - If more than 1 element is in the array, the size of the array is returned
   * @param array
   * @public
   */
  groupByIp: (ipList) => {
    if (isNone(ipList) || !isArray(ipList) || ipList.length === 0) {
      return '-';
    } else {
      // temporarily hardcoding string `IPs`. It will removed when using another component to display list of IPs
      return ipList.length === 1 ? ipList.get('firstObject') : `(${ ipList.length } IPs)`;
    }
  }
};
